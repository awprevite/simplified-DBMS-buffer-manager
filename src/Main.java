import java.io.*;
import java.util.*;
import java.nio.file.*;

public class Main {

    // all files, used to populate the disk
    private Frame[] frames;

    // contains the contents of the working disk and main memory buffer
    private BufferPool bufferPool;

    // constructor
    public Main(int bufferSize){
        // new frame for each file
        frames = new Frame[7];
        //buffer size determined by user
        bufferPool = new BufferPool(bufferSize);
    }

    // main function runs when the program starts
    public static void main(String[] args) {

        // initialize keyboard
        Scanner scanner = new Scanner(System.in);
        // valid size flag
        boolean validSize = false;
        // prompt user to enter buffer size
        System.out.println("Enter size of the buffer (1 - 100)");

        // parse user input
        String size;
        int bufferSize = 0;
        while(!validSize){
            size = scanner.nextLine();
            try{
                bufferSize = Integer.parseInt(size);
                if (bufferSize >= 1 && bufferSize <= 100) {
                    validSize = true;
                } else {
                    System.out.println("Enter a valid integer (1 - 100)");
                }
            } catch (NumberFormatException e){
                System.out.println("Enter a valid integer (1 - 100)");
            }
        }

        // create space to store data
        Main main = new Main(bufferSize);
        System.out.println("Initialized with buffer size " + bufferSize);
        // load the data from the files
        main.loadData();
        // initialize the disk
        main.bufferPool.initializeFrames(main.frames);
        System.out.println("Data loaded");

        System.out.println("Valid commands include: GET, SET, PIN, UNPIN, EXIT");
        // command loop
        while(true){
            System.out.println("The Program is ready for the next command");
            // listen for input
            String command = scanner.nextLine();
            // issue command
            int exit = main.issueCommand(command);
            // exit if EXIT command
            if(exit == -1){
                break;
            }
        }
    }

    // runs the given command, returns -1 for exit, 1 to listen for a new command
    public int issueCommand(String input){

        // exit
        if(input.equals("EXIT")){
            return -1;
        }

        // separate the arguments
        String[] parts = input.split(" ");
        // isolate the command
        String command = parts[0];

        // check for valid command
        if(parts.length != 2){
            if(!parts[0].equals("SET")){
                System.out.println("Enter the command followed by its arguments, or EXIT");
                return 1;
            }
        }

        // get the second argument, always an int (record number or file number)
        try{
            int num = Integer.parseInt(parts[1]);

            if(command.equals("GET")){
                bufferPool.GET(num);
            } else if(command.equals("SET")){
                // recombine record info, third argument
                StringBuilder sb = new StringBuilder();
                // for the remaining part, separated by space, but still part of the record
                for (int i = 2; i < parts.length; i++) {
                    // get rid of start and end quotes around the content if present
                    String cleaned = parts[i].replaceAll("^\"|\"$", "");
                    if (!cleaned.isEmpty()) {
                        if (sb.length() > 0) {
                            // add space when necessary
                            sb.append(" ");
                        }
                        // add part to the string
                        sb.append(cleaned);
                    }
                }

                // convert string to bytes
                byte[] content = sb.toString().getBytes();

                // if the record is not 40 bytes, because this is fixed length, return an error
                if(content.length != 40){
                    System.out.println("Length of record must be 40 bytes");
                    return 1;
                }
                bufferPool.SET(num, content);
            } else if(command.equals("PIN")){
                bufferPool.PIN(num);
            } else if(command.equals("UNPIN")){
                bufferPool.UNPIN(num);
            // if not a valid command, show the user the valid commands
            } else {
                System.out.println("Valid commands are GET, SET, PIN, UNPIN, EXIT");
            }
        } catch (NumberFormatException e){
            System.out.println("Make sure second argument is an integer");
        }
        bufferPool.displayBuffer();
        return 1;
    }

    // loads the files from src/Project1
    public void loadData(){
        File folder = new File("src/Project1");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));

        // sort the files lexicographically - this ensures the proper order of the disk, caused a lot of problems when not numerically ordered
        Arrays.sort(files, Comparator.comparing(File::getName));

        // create a frame for each file
        for(int i = 0; i < files.length; i++){
            createFrame(files[i], i + 1);
        }
    }

    // creates a Frame object
    public void createFrame(File file, int fileNumber){
        try {
            Path path = file.toPath();
            byte[] data = Files.readAllBytes(path);

            Frame frame = new Frame(data, false, false, fileNumber);
            frames[fileNumber -1] = frame;

        } catch (IOException e) {
            System.out.println("Error reading file: " + file.getName());
            e.printStackTrace();
        }
    }
}