package mondocommand.dynamic;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import mondocommand.CallInfo;
import mondocommand.MondoCommand;
import mondocommand.SubCommand;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import static org.junit.Assert.*;

public class SubCommandFinderTest {
    private static final String NL = System.getProperty("line.separator");
    private static final String[] ERROR_LINES = new String[] {
        "MondoCommand: @Sub marked on  'broken1' from class mondocommand.dynamic.SubCommandFinderTest$FakeHandler, must receive only one argument of type CallInfo.",
        "MondoCommand: @Sub marked on  'broken2' from class mondocommand.dynamic.SubCommandFinderTest$FakeHandler, must receive only one argument of type CallInfo." + NL
     
    };
    private static final String[] sortedCommands = new String[]{ "bar", "baz1", "foo", "quux" };
    @Test
    public void test() {
        MondoCommand base = new MondoCommand();
        ByteArrayOutputStream fakeStream = new ByteArrayOutputStream();
        SubCommandFinder finder = new OverrideFinder(base, fakeStream);
        finder.registerMethods(new FakeHandler());
        assertEquals(StringUtils.join(ERROR_LINES, NL), fakeStream.toString());
        List<SubCommand> commands = base.listCommands();
        
        assertEquals(4, commands.size());
        // Make sure ordering works
        for (int i = 0; i < sortedCommands.length; i++) {
            assertEquals(sortedCommands[i], commands.get(i).getName());
        }
        
        SubCommand bar = commands.get(0);
        assertEquals("BAR!", bar.getDescription());
        assertEquals("foo!", bar.getUsage());
        assertEquals(0, bar.getMinArgs());
        assertEquals(true, bar.isConsoleAllowed());

    }
    
    class OverrideFinder extends SubCommandFinder {
        public OverrideFinder(MondoCommand base, OutputStream stream) {
            super(base);
            this.logOutput = new PrintStream(stream);
        }
    }
    
    class FakeHandler {
        @Sub
        public void foo(CallInfo call) {
            
        }
        
        @Sub(description="BAR!", usage="foo!")
        public void bar(CallInfo call) {
            
        }
        
        @Sub(name="baz1")
        public void baz(CallInfo call) {
            
        }
        
        @Sub
        public void quux(CallInfo call) {
            
        }
        
        // TODO some broken method registration
        @Sub
        public void broken1() {
            
        }
        
        @Sub
        public void broken2(CallInfo call, Object foo) {
            
        }
    }
}
