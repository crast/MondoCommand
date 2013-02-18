package mondocommand;

import org.bukkit.command.CommandSender;


public final class SubCommand {
	private final String name;
    private final String permission;
	private boolean allow_console = false;
	private int minArgs = 0;
	private SubHandler handler = null;
	private String description;
	private String usage = null;
	
	public SubCommand(String name, String permission) {
		this.name = name;
		this.permission = permission;
	}

	public SubCommand(String name, String permission, SubHandler handler) {
		this(name, permission);
		this.handler = handler;
	}
	
	public SubCommand allowConsole() {
		this.allow_console = true;
		return this;
	}
	
	public boolean isConsoleAllowed() {
		return this.allow_console;
	}

	public int getMinArgs() {
		return minArgs;
	}

	public SubCommand setMinArgs(int minArgs) {
		this.minArgs = minArgs;
		return this;
	}

	public SubHandler getHandler() {
		return handler;
	}

	public SubCommand setHandler(SubHandler handler) {
		this.handler = handler;
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public String getUsage() {
		return this.usage;
	}

    public String getPermission() {
        return permission;
    }
	
	public SubCommand setUsage(String usage) {
		this.usage = usage;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public SubCommand setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public boolean checkPermission(CommandSender sender) {
	    return sender.hasPermission(permission);
	}
}
