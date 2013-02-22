package mondocommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import mondocommand.dynamic.SubCommandFinder;

import org.apache.commons.lang.Validate;
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
    private static final FormatConfig BASE_FORMAT = new FormatConfig();
    private static final SubHandler fallbackHandler = new FallbackHandler();

    private final Map<String, SubCommand> subcommands = new LinkedHashMap<String, SubCommand>();
    private final FormatConfig formatter;
    
    /**
     * Create a new MondoCommand with the base formatting specification.
     */
    public MondoCommand() {
        this(BASE_FORMAT);
    }
    
    /**
     * Create a new MondoCommand, used for dynamic sub command handling.
     * @param formatter Configuration on how to format responses.
     */
    public MondoCommand(FormatConfig formatter) {
        Validate.notNull(formatter);
        this.formatter = formatter;
        formatter.registerColorAliases();
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
    

    /** Implement the SubHandler interface so we can do sub-sub commands and such. */
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
            ChatMagic.send(sender, formatter.getPermissionWarning());
            return;
        } else if ((args.size() - 1) < sub.getMinArgs()) {
            String usageFormat = formatter.getUsageHeading() + "{GREEN}%s %s {USAGE}%s";
            ChatMagic.send(sender, usageFormat, commandLabel, sub.getName(), sub.getUsage());
            return;
        }
        List<String> callArgs = new ArrayList<String>(args.subList(1, args.size()));
        CallInfo call = new CallInfo(sender, player, commandLabel, sub, callArgs, formatter);
        try {
            sub.getHandler().handle(call);
        } catch (MondoFailure e) {
            call.reply("{ERROR}%s", e.getMessage());
        }
        return;
    }

    /**
     * Show the usage for this command.
     * @param sender A CommandSender who is requesting the usage.
     * @param player A Player object (can be null)
     * @param commandLabel The current command label.
     * @param slash An empty string if there should be a slash prefix, a slash otherwise.
     */
    private void showUsage(CommandSender sender, Player player, String commandLabel) {
        String headerFormat = formatter.getUsageHeading() + "%s <command> [<args>]";
        ChatMagic.send(sender, headerFormat, commandLabel);

        for (SubCommand sub: availableCommands(sender, player)) {
            formatter.writeUsageLine(sender, commandLabel, sub);
        }
    }
    
    /**
     * Add a sub-command to this MondoCommand.
     * @param name The name of this sub-command.
     * @param permission The permission string of a permission to check for this command.
     * @return a new SubCommand.
     */
    public SubCommand addSub(String name, String permission) {
        SubCommand cmd = new SubCommand(name, permission).setHandler(fallbackHandler);
        subcommands.put(name.toLowerCase(), cmd);
        return cmd;
    }
    
    /**
     * Add a sub-command to this MondoCommand.
     * @param name the name of this sub-command.
     * @return a new SubCommand.
     */
    public SubCommand addSub(String name) {
        return addSub(name, null);
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

    public void autoRegisterFrom(Object handler) {
        new SubCommandFinder(this).registerMethods(handler);
    }
    
    public List<SubCommand> listCommands() {
        return new ArrayList<SubCommand>(subcommands.values());
    }
}

final class FallbackHandler implements SubHandler {

    @Override
    public void handle(CallInfo call) throws MondoFailure {
        throw new MondoFailure("This SubHandler does not have an appropriate handler registered.");
    }
}
