package mondocommand;


import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class MondoCommandTest {
    public MondoCommandTest() {
        
    }

    @Test
    public void testInitialize() {
        MondoCommand c = new MondoCommand();
        assertEquals("foo", c.addSub("foo").getName());
    }
}
