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
        MondoCommand cmd = new DynamicCommand();
        MockCommandSender sender = new MockCommandSender();
        cmd.onCommand(sender, null, "test", new String[] { "fail" });
        assertEquals(1, sender.messages.size());
        assertEquals(sender.messages.get(0), ChatColor.RED + "fail");
    }
    
    @Test
    public void testWorkingDynamicSubcommand() {
        MondoCommand cmd = new DynamicCommand();
        MockCommandSender sender = new MockCommandSender();
        cmd.onCommand(sender, null, "test", new String[] {"success"});
        assertEquals(1, sender.messages.size());
        assertEquals(sender.messages.get(0), ChatColor.GREEN + "success");
    }
    
    public static class DynamicCommand extends MondoCommand {
        
        public DynamicCommand() {
            this.autoRegisterFrom(this);
        }
        
        @Sub
        public void fail(CallInfo call) throws MondoFailure {
            throw new MondoFailure("fail");
        }
        
        @Sub
        public void success(CallInfo call) {
            call.reply("{GREEN}success");
        }
    }

}
