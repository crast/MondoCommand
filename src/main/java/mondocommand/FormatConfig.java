package mondocommand;

/** 
 * Allow customizing response strings from MondoCommand. 
 * 
 * Creating a FormatConfig is similar to doing so for MondoCommand: use argument chaining.
 * */
public class FormatConfig {
    private String permissionWarning = "{WARNING}You do not have permissions for this command.";
    private String usageHeading = "{HEADER}Usage: ";
    private String replyPrefix = "";
    public FormatConfig() {
        
    }
    
    public String getPermissionWarning() {
        return permissionWarning;
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
        this.permissionWarning = permissionWarning;
        return this;
    }
    
    /**
     * Set the heading to be shown when displaying command usage.
     * @param usageHeading A string which accepts color codes.
     * @return the same FormatConfig, for chaining.
     */
    public FormatConfig setUsageHeading(String usageHeading) {
        this.usageHeading = usageHeading;
        return this;
    }
    
    /**
     * Set the prefix to be placed before all command replies.
     * @param replyPrefix a String which accepts color codes.
     * @return the same FormatConfig, for chaining
     */
    public FormatConfig setReplyPrefix(String replyPrefix) {
        this.replyPrefix = replyPrefix;
        return this;
    }
}
