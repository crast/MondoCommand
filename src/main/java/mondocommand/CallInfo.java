package mondocommand;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Represents all the arguments that go along with a call.
 * @author James Crasta
 *
 */
public class CallInfo {
    private final CommandSender sender;
    private final Player player;
    private final String baseCommand;
    private final SubCommand subCommand;
    private final String replyPrefix;

    private List<String> args;

    /**
     * Create a new CallInfo representing one command invocation.
     * @param sender The CommandSender who invoked this (can be a console)
     * @param player The Player who invoked this (will be null if a console)
     * @param baseCommand The label of the base command being executed (for reference)
     * @param subCommand The SubCommand we're executing.
     * @param args The command arguments.
     * @param formatter The formatter being used by our MondoCommand.
     */
    public CallInfo(CommandSender sender, Player player, String baseCommand, SubCommand subCommand, List<String> args, FormatConfig formatter) {
        Validate.notNull(sender);
        Validate.notEmpty(baseCommand);
        Validate.notNull(subCommand);
        this.sender = sender;
        this.player = player;
        this.args = args;
        this.baseCommand = baseCommand;
        this.subCommand = subCommand;
        this.replyPrefix = ChatMagic.colorize(formatter.getReplyPrefix());
    }

    /**
     * Get the player who invoked this. Can be null if running at the console.
     * @return a Player, or null if this is a console command
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get the CommandSender who invoked this.
     * @return a CommandSender.
     */
    public CommandSender getSender() {
        return sender;
    }

    /**
     * Get a specific argument.
     * @param index The argument number.
     * @return The specific argument requested.
     */
    public String getArg(int index) {
        return this.args.get(index);
    }

    /**
     * Get the whole list of command arguments.
     * @return List of arguments.
     */
    public List<String> getArgs() {
        return this.args;
    }
    
    /**
     * Get an argument coerced into an int. 
     * @param index the location in the arguments array.
     * @return The argumnt
     */
    public int getIntArg(int index) {
        return Integer.parseInt(getArg(index));
    }
    
    /**
     * Get all the arguments after the specified index joined into a single string.
     * 
     * This is useful if one of your last arguments is a free-form text entry 
     * (like for a chat message, or editing a sign/book text)
     * @param index The index to start at (inclusive)
     * @return A single string containing all the arguments till the end
     */
    public String getJoinedArgsAfter(int index) {
        return StringUtils.join(args.subList(index, args.size()), " ");
    }

    /**
     * How many arguments we got.
     * @return Number of arguments
     */
    public int numArgs() {
        return this.args.size();
    }

    /**
     * Get the base command which was called for this sub-command call.
     * @return A base command string.
     */
    public String getBaseCommand() {
        return baseCommand;
    }

    /**
     * Get the SubCommand that invoked this call.
     * @return a SubCommand.
     */
    public SubCommand getSubCommand() {
        return subCommand;
    }

    /**
     * Respond to the call, interpolating colors and variables.
     * @param template A string template. See {@link ChatMagic} documentation for more info.
     * @param args Zero or more arguments to interpolate the template.
     * @see ChatMagic#colorize
     */
    public void reply(String template, Object ... args) {
        reply(true, template, args);
    }

    /**
     * Re
     * @param prefix if True, prefix the message with the formater's prefix.
     * @param template A string template. See {@link ChatMagic} documentation for more info.
     * @param args Zero or more arguments to interpolate the template.
     */
    public void reply(boolean prefix, String template, Object ... args) {
        if (prefix) {
            ChatMagic.send(sender, replyPrefix + template, args);
        } else {
            ChatMagic.send(sender, template, args);
        }
    }
}
