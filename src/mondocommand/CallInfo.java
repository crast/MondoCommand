package mondocommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CallInfo {
	private final CommandSender sender;
	private final Player player;
    private final SubCommand subCommand;

	private List<String> args;
	private List<String> messages;

	public CallInfo(CommandSender sender, Player player, SubCommand subCommand, String[] args) {
		this.sender = sender;
		this.player = player;
		this.args = new ArrayList<String>(Arrays.asList(args));
		this.subCommand = subCommand;
		this.messages = new ArrayList<String>();
	}

	public Player getPlayer() {
		return player;
	}

	public CommandSender getSender() {
		return sender;
	}
	
	public String getArg(int index) {
		return this.args.get(index);
	}
	
	public List<String> getArgs() {
	    return this.args;
	}
	
	public int numArgs() {
	    return this.args.size();
	}

	public void append(String template, Object ... args) {
		messages.add(ChatMagic.colorize(template, args));
	}
	
	public void append(String message) {
	    messages.add(message);
	}

	public java.util.Collection<String> getMessages() {
		return messages;
	}

    public SubCommand getSubCommand() {
        return subCommand;
    }
}
