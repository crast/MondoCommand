package mondocommand.dynamic;

import static org.junit.Assert.*;
import mondocommand.CallInfo;
import mondocommand.MockCommandSender;
import mondocommand.MondoCommand;
import mondocommand.MondoFailure;

import org.bukkit.ChatColor;
import org.junit.Test;

public class DynamicSubcommandsTest {

    @Test
    public void testMondoFailureWithDynamicSubcommands() {
        MondoCommand cmd = new FailingCommand();
        MockCommandSender sender = new MockCommandSender();
        cmd.onCommand(sender, null, "test", new String[] { "fail" });
        assertEquals(1, sender.messages.size());
        assertEquals(sender.messages.get(0), ChatColor.RED + "fail");
    }
    
    public static class FailingCommand extends MondoCommand {
        
        public FailingCommand() {
            this.autoRegisterFrom(this);
        }
        
        @Sub
        public void fail(CallInfo call) throws MondoFailure {
            throw new MondoFailure("fail");
        }
    }

}
