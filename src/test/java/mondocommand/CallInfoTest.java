/**
 * 
 */
package mondocommand;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.junit.Before;
import org.junit.Test;

/**
 * @author james
 *
 */
public class CallInfoTest {
    private static final FormatConfig formatter = new FormatConfig().setReplyPrefix("{GREEN}HEADER: ");
    private static final String expectedHeader = ChatColor.GREEN.toString() + "HEADER: ";

    private static SubCommand sub = new MondoCommand().addSub("bar");
    private CallInfo call;
    private MockPlayer player;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        player = new MockPlayer();
        ArrayList<String> args = new ArrayList<String>();
        args.add("foo");
        args.add("bar");
        args.add("42");
        call = new CallInfo(player, player, "foo", sub, args, formatter);
    }

    /**
     * Test method for {@link mondocommand.CallInfo#getArg(int)}.
     */
    @Test
    public void testGetArg() {
        assertEquals("foo", call.getArg(0));
        assertEquals("42", call.getArg(2));
    }

    /**
     * Test method for {@link mondocommand.CallInfo#getArgs()}.
     */
    @Test
    public void testGetArgs() {
        assertEquals(3, call.getArgs().size());
    }

    /**
     * Test method for {@link mondocommand.CallInfo#getIntArg(int)}.
     */
    @Test
    public void testGetIntArg() {
        assertEquals(42, call.getIntArg(2));
    }
    
    @Test(expected=NumberFormatException.class)
    public void testIntFailure() {
        call.getIntArg(1);
    }

    /**
     * Test method for {@link mondocommand.CallInfo#getJoinedArgsAfter(int)}.
     */
    @Test
    public void testGetJoinedArgsAfter() {
        assertEquals("foo bar 42", call.getJoinedArgsAfter(0));
        assertEquals("bar 42", call.getJoinedArgsAfter(1));
    }

    /**
     * Test method for {@link mondocommand.CallInfo#numArgs()}.
     */
    @Test
    public void testNumArgs() {
        assertEquals(3, call.numArgs());
    }

    /**
     * Test method for {@link mondocommand.CallInfo#reply(java.lang.String, java.lang.Object[])}.
     */
    @Test
    public void testReply() {
        call.reply("foo {red}%d", 42);
        assertEquals(1, player.messages.size());
        assertEquals(expectedHeader + "foo " + ChatColor.RED.toString() + "42",
                     player.messages.get(0));
    }

    /**
     * Test method for {@link mondocommand.CallInfo#replySimple(java.lang.String)}.
     */
    @Test
    public void testReplySimple() {
        call.replySimple("foo bar");
        assertEquals(1, player.messages.size());
        assertEquals(expectedHeader + "foo bar", player.messages.get(0));
    }

}
