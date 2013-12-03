package mondocommand;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Allow customizing response strings from MondoCommand.
 *
 * Creating a FormatConfig is similar to doing so for MondoCommand: use argument chaining.
 * */
public class FormatConfig {
    private String permissionWarning = "{WARNING}You do not have permissions for this command.";
    private String usageHeading = "{HEADER}Usage: ";
    private String usageCommandSuffix = " <command> [<args>]";
    private String replyPrefix = "";

    public FormatConfig() {

    }

    public String getPermissionWarning() {
        return permissionWarning;
    }

    public String getUsageCommandSuffix() {
        return usageCommandSuffix;
    }

    public String getUsageHeading() {
        return usageHeading;
    }

    public String getReplyPrefix() {
        return replyPrefix;
    }


    /**
     * Set the permission warning to be displayed when a user cannot access a command.
     * @param permissionWarning a String which accepts color codes.
     * @return the same FormatConfig, for chaining.
     */
    public FormatConfig setPermissionWarning(String permissionWarning) {
        Validate.notNull(permissionWarning);
        this.permissionWarning = permissionWarning;
        return this;
    }

    /**
     * Set the suffix shown when displaying command usage.
     * @param usageCommandSuffix A string which accepts color codes.
     * @return the same FormatConfig, for chaining.
     */
    public FormatConfig setUsageCommandSuffix(String usageCommandSuffix) {
        Validate.notNull(usageCommandSuffix);
        this.usageCommandSuffix = usageCommandSuffix;
        return this;
    }

    /**
     * Set the heading to be shown when displaying command usage.
     * @param usageHeading A string which accepts color codes.
     * @return the same FormatConfig, for chaining.
     */
    public FormatConfig setUsageHeading(String usageHeading) {
        Validate.notNull(usageHeading);
        this.usageHeading = usageHeading;
        return this;
    }

    /**
     * Set the prefix to be placed before all command replies.
     * @param replyPrefix a String which accepts color codes.
     * @return the same FormatConfig, for chaining
     */
    public FormatConfig setReplyPrefix(String replyPrefix) {
        Validate.notNull(replyPrefix);
        this.replyPrefix = replyPrefix;
        return this;
    }


    /**
     * Register MondoCommand color aliases upon initialization of the MondoCommand.
     *
     * This method doesn't make much sense to be called by the user right now,
     * it's here to support future functionality and/or user customization where colors
     * are concerned.
     */
    public void registerColorAliases() {
        ChatMagic.registerDefaultAlias("{HEADER}", ChatColor.GOLD);
        ChatMagic.registerDefaultAlias("{USAGE}", ChatColor.LIGHT_PURPLE);
        ChatMagic.registerDefaultAlias("{WARNING}", ChatColor.DARK_RED);
        ChatMagic.registerDefaultAlias("{ERROR}", ChatColor.RED);
        ChatMagic.registerDefaultAlias("{NOUN}", ChatColor.AQUA);
        ChatMagic.registerDefaultAlias("{VERB}", ChatColor.GRAY);
        ChatMagic.registerDefaultAlias("{MCMD}", ChatColor.GREEN);
        ChatMagic.registerDefaultAlias("{DESCRIPTION}", ChatColor.BLUE);
    }

    /**
     * Write out one entry of the usage output.
     *
     * <p>This method exists so that users can subclass it and do their own
     * formatting of MondoCommand's usage output. It's the responsibility of
     * this method to also send the usage output, this way customized line wrapping,
     * multi-line messages, etc can be implemented.
     *
     * @param sender The CommandSender requesting usage.
     * @param commandLabel The current base command label. Will be prefixed with a / if a player command.
     * @param sub The SubCommand we're generating a usage screen for.
     * @return A formatted single usage line.
     */
    public void writeUsageLine(CommandSender sender, String commandLabel, SubCommand sub) {
        String usage = "";
        if (sub.getUsage() != null) {
            usage = ChatMagic.colorize(" {USAGE}%s", sub.getUsage());
        }
        ChatMagic.send(sender,
            "{MCMD}%s {VERB}%s%s {DESCRIPTION}%s",
            commandLabel,
            sub.getName(),
            usage,
            sub.getDescription()
        );
    }
}
