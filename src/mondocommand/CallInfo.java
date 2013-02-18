package mondocommand;

import java.util.ArrayList;
import java.util.List;

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

	private List<String> args;
	private List<String> messages;

	/**
	 * Create a new CallInfo representing one command invocaion.
	 * @param sender The CommandSender who invoked this (can be a console)
	 * @param player The Player who invoked this (will be null if a console)
	 * @param baseCommand The label of the base command being executed (for reference)
	 * @param subCommand The SubCommand we're executing.
	 * @param args The command arguments.
	 */
	public CallInfo(CommandSender sender, Player player, String baseCommand, SubCommand subCommand, List<String> args) {
	    Validate.notNull(sender);
        Validate.notEmpty(baseCommand);
	    Validate.notNull(subCommand);
		this.sender = sender;
		this.player = player;
		this.args = args;
		this.baseCommand = baseCommand;
		this.subCommand = subCommand;
		this.messages = new ArrayList<String>();
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
	 * How many arguments we got.
	 * @return Number of arguments
	 */
	public int numArgs() {
	    return this.args.size();
	}

	/**
	 * Add a message to the call, interpolating colors and variables.
	 * @param template A string template. see @class ColorMagic documentation.
	 * @param args Zero or more arguments to interpolate the template
	 */
	public void append(String template, Object ... args) {
		messages.add(ChatMagic.colorize(template, args));
	}
	
	/**
	 * Add a plain message to this call
	 * @param message a simple string.
	 */
	public void appendPlain(String message) {
	    messages.add(message);
	}

	/**
	 * Get the list of messages for this call.
	 * @return The list of messages.
	 */
	public List<String> getMessages() {
		return messages;
	}

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
}
