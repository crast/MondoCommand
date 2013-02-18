package mondocommand;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class MondoCommand implements CommandExecutor {
    private final String appName;
	public java.util.Map<String, SubCommand> subcommands = new LinkedHashMap<String, SubCommand>();
	
	public MondoCommand(String appName) {
	    this.appName = appName;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = null;
		String slash = "";
		if (sender instanceof Player) {
			player = (Player) sender;
			slash = "/";
		}
		
		if (args.length == 0) {
			sender.sendMessage("Usage: " + slash +  commandLabel + " <command> [<args>]");

			for (SubCommand sub: availableCommands(sender, player)) {
				String usage = "";
				if (sub.getUsage() != null) {
					usage = ChatMagic.colorize(" {LIGHT_PURPLE}%s", sub.getUsage());
				}
				sender.sendMessage(ChatMagic.colorize(
						"{GREEN}%s%s %s%s {BLUE}%s", 
						slash, commandLabel, 
						sub.getName(),
						usage,
						sub.getDescription()
				));
			}
			return false;
		}
		String subcommandName = args[0].toLowerCase();
		SubCommand sub = subcommands.get(subcommandName);
		if (sub == null) {
			// TODO return usage
			return false;
		} else if (!sender.hasPermission(sub.getPermission())) {
			sender.sendMessage(ChatMagic.colorize("{GOLD}%s: {WARNING}Stop being sneaky.", appName));
			return false;
		} else if ((args.length -1 ) < sub.getMinArgs()) {
			sender.sendMessage(ChatMagic.colorize("{GOLD}Usage: {GREEN}%s%s %s {USAGE}%s", slash, commandLabel, sub.getName(), sub.getUsage()));
			return false;
		}
		CallInfo call = new CallInfo(sender, player, sub, args);
		try {
			sub.getHandler().handle(call);
		} catch (MondoFailure e) {
			call.append(ChatMagic.colorize("{FAILURE}") + e.getMessage());
		}
		for (String message: call.getMessages()) {
			sender.sendMessage(message);
		}
		return false;
	}
	
	public SubCommand addSub(String name, String permission, SubHandler handler) {
		SubCommand cmd = new SubCommand(name, permission, handler);
		subcommands.put(name, cmd);
		return cmd;
	}
	
	public SubCommand addSub(String name, String permission) {
		return addSub(name, permission, null);
	}
	
	private List<SubCommand> availableCommands(CommandSender sender, Player player) {
		ArrayList<SubCommand> items = new ArrayList<SubCommand>();
		boolean has_player = (player != null);
		for (SubCommand sub: subcommands.values()) {
			if ((has_player || sub.isConsoleAllowed()) && sub.checkPermission(sender)) {
				items.add(sub);
			}
		}
		return items;
	}
}
