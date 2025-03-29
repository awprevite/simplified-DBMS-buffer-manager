import java.util.LinkedList;

public class BufferPool {

    // each index corresponds to one file, represents the main memory space
    private Frame[] buffers;

    // list to keep track of the fifo order of being entered into main memory, supports the fifo replacement policy
    private LinkedList<Integer> fifo;

    // full list of files, represents the disk space
    private Frame[] allFrames;

    // constructor
    public BufferPool(int bufferSize){
        buffers = new Frame[bufferSize];
        fifo = new LinkedList<Integer>();
    }

    // set all the files in disk
    public void initializeFrames(Frame[] frames){
        allFrames = frames;
    }

    // check if the file is in main memory / the buffer pool
    // returns the index of the file if it is in the pool, -1 otherwise
    public int inPool(int fileNumber){
        // for each frame in the buffer
        for(int i = 0; i < buffers.length; i++){
            // if the current index is full
            if(buffers[i] != null){
                // check if the given file is there
                if(buffers[i].getFileNumber() == fileNumber){
                    // if it's there
                    return i;
                }
            }
        }
        // if it was not found
        return -1;
    }

    // display the contents of a record, try to move it to memory to access the record first
    public void GET(int recordNumber){
        // if record number does not exist
        if(recordNumber < 1 || recordNumber > 700){
            System.out.println("Invalid record number (1-700)");
            return;
        }

        // calculate file number
        int fileNumber = (recordNumber - 1) / 100 + 1;

        // set file based on record number
        Frame frame = null;
        for (Frame currentFrame : allFrames) {
            if (currentFrame.getFileNumber() == fileNumber) {
                frame = currentFrame;
                break;
            }
        }

        // add frame to buffer
        boolean added = addToBufferPool(frame);

        // if successful add then get/display the record contents
        if(added){
            for(int i = 0; i < buffers.length; i++){
                if(buffers[i].getFileNumber() == fileNumber){
                    buffers[i].printContent(recordNumber);
                    break;
                }
            }
        }
    }

    // alter the contents of a record, try to move it to memory to access the record first
    public void SET(int recordNumber, byte[] content){
        // if record number does not exist
        if(recordNumber < 1 || recordNumber > 700){
            System.out.println("Invalid record number (1-700)");
            return;
        }

        // calculate file number
        int fileNumber = (recordNumber - 1) / 100 + 1;

        // set frame based on frame number
        Frame frame = null;
        for (Frame currentFrame : allFrames) {
            if (currentFrame.getFileNumber() == fileNumber) {
                frame = currentFrame;
                break;
            }
        }

        // add the file to the buffer
        boolean added = addToBufferPool(frame);

        // if successful add then set the record
        if(added){
            for(int i = 0; i < buffers.length; i++){
                if(buffers[i].getFileNumber() == fileNumber){
                    // save the current file content
                    byte[] currentContent = buffers[i].getContent();

                    // calculate the bytes the record is in
                    int startByte = (recordNumber - (100 * (fileNumber - 1)) - 1) * 40;
                    int endByte = startByte + 40;
                    int k = 0;

                    // replace the old record contents with the new ones
                    for(int j = startByte; j < endByte; j++){
                        currentContent[j] = content[k];
                        k++;
                    }
                    // apply the new content
                    buffers[i].setContent(currentContent);
                    // mark the file as dirty
                    buffers[i].setDirty(true);

                    int x = i + 1;
                    System.out.println("Write was successful to file " + frame.getFileNumber() + " record " + recordNumber + " in frame " + x);
                    break;
                }
            }
        }else{
            System.out.println("Write unsuccessful");
        }

    }

    // try to pin a file, if it is not in the buffer try to add it first
    public void PIN(int fileNumber) {
        // if invalid file number
        if(fileNumber < 1 || fileNumber > 7){
            System.out.println("Invalid Block ID / File Number (1-7)");
            return;
        }
        // set frame based on frame number
        Frame frame = null;
        for (Frame currentFrame : allFrames) {
            if (currentFrame.getFileNumber() == fileNumber) {
                frame = currentFrame;
                break;
            }
        }

        // add frame to memory
        boolean added = addToBufferPool(frame);

        // if successful add then pin
        if (added) {
            // get the file
            for(int i = 0; i < buffers.length; i++){
                if(buffers[i].getFileNumber() == fileNumber){
                    // if already pinned
                    if(buffers[i].getPinned() == true){
                        int x = i + 1;
                        System.out.println("File " + buffers[i].getFileNumber() + " is already pinned in frame " + x);
                    // if not already pinned
                    }else{
                        // pin it
                        buffers[i].setPinned(true);
                        int x = i + 1;
                        System.out.println("File " + buffers[i].getFileNumber() + " pinned in frame " + x);
                    }
                    break;
                }
            }
        }else{
            System.out.println("The file could not be pinned because the memory buffers are full");
        }
    }

    // if the file is in the buffer unpins it
    public void UNPIN ( int fileNumber){
        // if invalid file number
        if(fileNumber < 1 || fileNumber > 7){
            System.out.println("Invalid Block ID / File Number (1-7)");
            return;
        }

        // check if the file is in the buffer
        for(int i = 0; i < buffers.length; i++){
            if(buffers[i] != null){
                if(buffers[i].getFileNumber() == fileNumber) {
                    // if it is, check if its pinned
                    if (buffers[i].getPinned() == true) {
                        // if it is, unpin it
                        buffers[i].setPinned(false);
                        int x = i + 1;
                        System.out.println("Unpinned file " + buffers[i].getFileNumber() + " in frame " + x);
                        // if not pinned
                    } else {
                        int x = i + 1;
                        System.out.println("File " + buffers[i].getFileNumber() + " in frame " + x + " is already unpinned");
                    }
                    // if found end the loop
                    return;
                }
            }
        }
        // if not found
        System.out.println("File " + fileNumber + " Cannot be unpinned, it's not in the buffer");
    }

    // adds a file to the buffer pool if its able to
    // if it's able to add, or is already in, return true, otherwise return false
    public boolean addToBufferPool(Frame frame){
        // keep track if the file has been added yet
        boolean added = false;

        // if it is already in the buffer
        int index = inPool(frame.getFileNumber());
        if(index >= 0){
            int x = index + 1;
            System.out.println("File " + frame.getFileNumber() + " is already in memory; Located in frame " + x);
            return true;
        }

        // if not already in the buffer, check for empty frame, -1 means none available
        // if no empty frames, use fifo replacement policy
        int empty = this.findEmptyFrame();
        int currentIndex = 0;
        if(empty == -1){
            // for each item of the fifo list
            for(int i = 0; i < fifo.size(); i++) {
                // get the index in the fifo
                currentIndex = fifo.get(i);
                int fifoIndex = i;
                // get the corresponding file
                Frame currentFrame = buffers[currentIndex];
                // if it's not pinned
                if (!currentFrame.getPinned()){
                    // remove it from the fifo
                    fifo.remove(fifoIndex);
                    // re-add the index to the end of the fifo
                    fifo.add(currentIndex);
                    int removedFile = buffers[currentIndex].getFileNumber();
                    // check if dirty
                    if(buffers[currentIndex].getDirty()){
                        // write it to disk
                    }
                    // replace the new file with the old
                    buffers[currentIndex] = frame;
                    int x = currentIndex + 1;
                    System.out.println("Brought file " + buffers[currentIndex].getFileNumber() + " from disk; Placed in frame " + x + "; Evicted file " + removedFile);
                    // set added flag
                    added = true;
                    // end the loop
                    break;
                }
            }
        }
        // if empty frame, add to that frame
        else {
            buffers[empty] = frame;
            // add to fifo
            fifo.add(empty);
            // set added flag
            int x = empty + 1;
            System.out.println("Brought file " + buffers[empty].getFileNumber() + " from disk; Placed in frame " + x);
            added = true;
        }

        // if the file was added
        if(added){
            return true;
        } else {
            // add to queue
            System.out.println("File " + frame.getFileNumber() + " cannot be accessed from disk because the memory buffers are full and pinned");
            return false;
        }

    }

    // checks if there is an empty frame in the buffer
    // returns -1 if there is not, if there is it returns the index of the first empty frame
    public int findEmptyFrame(){
        // for each frame in the buffer
        for(int i = 0; i< buffers.length; i++){
            // check if it is empty
            if(buffers[i] == null){
                // if it is empty
                return i;
            }
        }
        // if no empty frames
        return -1;
    }

    // visualize the current state of the buffer, populated with the file numbers or empty
    public void displayBuffer(){
        System.out.println("---------- Buffer ----------");
        for(int i = 0; i < buffers.length; i++){
            if(buffers[i] == null){
                System.out.print(" | empty ");
            }else{
                System.out.print(" |   " + buffers[i].getFileNumber() + "   ");
            }
        }
        System.out.print("| \n");
        for(int i = 0; i < buffers.length; i++){
            if(buffers[i] == null){
                System.out.print(" |       ");
            }else{
                if(buffers[i].getPinned() == true){
                    System.out.print(" |   o   ");
                }else{
                    System.out.print(" |       ");
                }
            }
        }
        System.out.print("| \n");
        System.out.println("---------- Pinned ----------");
    }
}
