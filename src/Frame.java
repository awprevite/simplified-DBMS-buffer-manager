public class Frame {

    // actual data
    private byte[] content;

    // true if content is modified in main memory
    private boolean dirty;

    // if true, cannot be removed from main memory
    private boolean pinned;

    // corresponds to src/Project1/F1.txt F7.txt (1-7)
    private int fileNumber;

    // constructor
    public Frame(byte[] content, boolean dirty, boolean pinned, int fileNumber){
        this.content = content;
        this.dirty = dirty;
        this.pinned = pinned;
        this.fileNumber = fileNumber;
    }

    public int getFileNumber(){
        return this.fileNumber;
    }

    public byte[] getContent() {
        return this.content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public boolean getDirty() {
        return this.dirty;
    }

    public void setDirty(boolean dirty){
        this.dirty = dirty;
    }

    public boolean getPinned() {
        return this.pinned;
    }

    public void setPinned(boolean pinned){
        this.pinned = pinned;
    }

    // Prints the content of a given record (40 Bytes)
    public void printContent(int recordNumber){

        int startByte = (recordNumber - (100 * (fileNumber - 1)) - 1) * 40;

        int endByte = startByte + 40;

        for(int i = startByte; i < endByte; i++){
            char c = (char) content[i];
            System.out.print(c);
        }

        System.out.print("\n");
    }
}
