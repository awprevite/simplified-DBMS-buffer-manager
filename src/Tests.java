import org.junit.Assert;
import org.junit.Test;

public class Tests {

    @Test
    public void getDirtyTest(){

        byte[] content = new byte[40];

        Frame frame = new Frame(content, false, false, 1);

        Assert.assertEquals(false, frame.getDirty());

        frame.setDirty(true);

        Assert.assertEquals(true, frame.getDirty());
    }
}