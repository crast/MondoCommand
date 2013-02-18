package mondocommand;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MondoCommandTest {
    private static String[] EMPTY_ARGS = new String[0];
    private MockCommandSender sender = new MockCommandSender();
    private MondoCommand cmd;
    public MondoCommandTest() {
        cmd = new MondoCommand();
        cmd.addSub("default").setDescription("desc").allowConsole();
        cmd.addSub("permissioned", "fooplugin.perm").setDescription("permissioned!").allowConsole();
    }

    @Test
    public void testInitialize() {
        assertEquals("foo", cmd.addSub("foo").getName());
    }
    
    @Test
    public void testCommandHelp() {
        cmd.onCommand(sender, null, "foo", EMPTY_ARGS);
        assertEquals(2, sender.messages.size());
        assertEquals("Usage: foo <command> [<args>]", sender.stripMessage(0));
        assertEquals("foo default desc", sender.stripMessage(1));
    }
    
    @Test
    public void testCommandHelpPermissioned() {
        sender.permissions.add("fooplugin.perm");
        cmd.onCommand(sender, null, "foo", EMPTY_ARGS);
        assertEquals(3, sender.messages.size());
    }
    
    @Test
    public void testCommandHelpPlayerOnly() {
        MockPlayer player = new MockPlayer();
        cmd.addSub("player-only").setDescription("foo");
        cmd.onCommand(player, null, "foo", EMPTY_ARGS);
        assertEquals(3, player.messages.size());
        assertEquals("Usage: /foo <command> [<args>]", player.stripMessage(0));
        assertEquals("/foo default desc", player.stripMessage(1));
        assertEquals("/foo player-only foo", player.stripMessage(2));
        player.permissions.add("fooplugin.perm");
        cmd.onCommand(player, null, "foo", EMPTY_ARGS);
        assertEquals(7, player.messages.size());
        assertEquals("/foo permissioned permissioned!", player.stripMessage(5));
    }
    
    @Test
    public void testCommandDefault() {
        String[] args = new String[] {"default", "misc"};
        cmd.onCommand(sender, null, "foo", args);
        assertEquals(1, sender.messages.size());
        assertEquals("This SubHandler does not have an appropriate handler registered.", sender.stripMessage(0));
    }
}
