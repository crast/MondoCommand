package mondocommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handle commands dynamically with sub-command registration.
 * @author James Crasta
 *
 */
public class MondoCommand implements CommandExecutor, SubHandler {
    private static final String PERMISSION_WARNING_TEXT = "Stop being sneaky.";
    private Map<String, SubCommand> subcommands = new LinkedHashMap<String, SubCommand>();
    
    /**
     * Create a new MondoCommand, used for dynamic sub command handling.
     */
    public MondoCommand() {
        registerColorAliases();
    }

    /** Implement onCommand so this can be registered as a CommandExecutor */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
            commandLabel = "/" + commandLabel;
        }
        List<String> callArgs = new ArrayList<String>(Arrays.asList(args));
        handleRawCommand(sender, player, commandLabel, callArgs);
        return false;
    }
    

    /** Implement the SubHandler interface so we can do sub-sub commands et al. */
    @Override
    public void handle(CallInfo call) throws MondoFailure {
        String commandLabel = call.getBaseCommand() + " " + call.getSubCommand().getName();
        handleRawCommand(call.getSender(), call.getPlayer(), commandLabel, call.getArgs());
    }
    
    /**
     * Handle a command, dispatching to the appropriate listeners.
     * @param sender A CommandSender who is the person or console who sent this command.
     * @param player A Player object (can be null)
     * @param commandLabel The current alias this command is running as
     * @param args The arguments that were passed to this command.
     */
    private void handleRawCommand(CommandSender sender, Player player, String commandLabel, List<String> args) {
        if (args.size() == 0) {
            showUsage(sender, player, commandLabel);
            return;
        }
        String subcommandName = args.get(0).toLowerCase();
        SubCommand sub = subcommands.get(subcommandName);
        if (sub == null) {
            showUsage(sender, player, commandLabel);
            return;
        } else if (!sub.checkPermission(sender)) {
            ChatMagic.send(sender, "{WARNING}" + PERMISSION_WARNING_TEXT);
            return;
        } else if ((args.size() - 1) < sub.getMinArgs()) {
            ChatMagic.send(sender, "{HEADER}Usage: {GREEN}%s %s {USAGE}%s", commandLabel, sub.getName(), sub.getUsage());
            return;
        }
        List<String> callArgs = new ArrayList<String>(args.subList(1, args.size()));
        CallInfo call = new CallInfo(sender, player, commandLabel, sub, callArgs);
        try {
            sub.getHandler().handle(call);
        } catch (MondoFailure e) {
            call.append("{ERROR}%s", e.getMessage());
        }
        for (String message: call.getMessages()) {
            sender.sendMessage(message);
        }
        return;
    }

    /**
     * Show the usage for this command.
     * @param sender
     * @param player 
     * @param commandLabel The current command label.
     * @param slash An empty string if there should be a slash prefix, a slash otherwise.
     */
    private void showUsage(CommandSender sender, Player player, String commandLabel) {
        ChatMagic.send(sender, "{HEADER}Usage: %s <command> [<args>]", commandLabel);

        for (SubCommand sub: availableCommands(sender, player)) {
            String usage = "";
            if (sub.getUsage() != null) {
                usage = ChatMagic.colorize(" {USAGE}%s", sub.getUsage());
            }
            ChatMagic.send(
                sender,
                "{GREEN}%s %s%s {BLUE}%s",
                commandLabel,
                sub.getName(),
                usage,
                sub.getDescription()
            );
        }
    }
    
    public SubCommand addSub(String name, String permission) {
        SubCommand cmd = new SubCommand(name, permission);
        subcommands.put(name.toLowerCase(), cmd);
        return cmd;
    }
    
    public SubCommand addSub(String name) {
        return addSub(name, null);
    }

    public SubCommand addSub(String name, String permission, SubHandler handler) {
        return addSub(name, permission).setHandler(handler);
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
    
    /** This is hacky and should be configurable by the library user in the future */
    private void registerColorAliases() {
        ChatMagic.registerAlias("{HEADER}", ChatColor.GOLD);
        ChatMagic.registerAlias("{USAGE}", ChatColor.LIGHT_PURPLE);
        ChatMagic.registerAlias("{WARNING}", ChatColor.DARK_RED);
        ChatMagic.registerAlias("{ERROR}", ChatColor.RED);
        ChatMagic.registerAlias("{NOUN}", ChatColor.AQUA);
        ChatMagic.registerAlias("{VERB}", ChatColor.GRAY);
    }
}
