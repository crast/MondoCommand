MondoCommand
============

MondoCommand is a library for Bukkit applications which lets you create a command which dispatches a number of sub-commands along with generating pretty-looking help pages, checking arguments, and so on.

It also abstracts out a lot of the annoying things about doing subcommands, like return values, argument handling, and so on to help you write shorter, cleaner code (and split it up nicely too).

Basic usage:
```java
// Basic setup and registration
MondoCommand base = new MondoCommand();
getCommand("housebuilder").setExecutor(base);

/** Add sub-commands and their handlers. */

// Add sub-command build which requires permission "housebuilder.build"
base.addSub("build", "housebuilder.build")
	.setDescription("Build a House")       // Description is shown in command help
	.setMinArgs(2)                         // Won't run your command without this many args
	.setUsage("<name> <owner>")            // Sets argument usage information
	.setHandler(new HouseBuildHandler());


// Add a sub-command destroy which requires permissions "housebuilder.destroy"
base.addSub("destroy", "housebuilder.destroy")
	.setDescription("Destroy a House")
	.setMinArgs(1)
	.setUsage("<name>")
	.setHandler(new SubHandler() {
		// This is an example of how to do handlers in-line.
		public void handle(CallInfo call) {
			String houseName = call.getArg(0);
			if (houseMap.containsKey(houseName)) {
				houseMap.remove(houseName);
				// MondoCommand allows you to add messages with color formatting
				call.reply("{GREEN}House {GOLD}%s{GREEN} removed", houseName);
			} else {
				call.reply("{RED}House %s not found", houseName);
			}
		}
	});

// Example of a command which works on the console
base.addSub("version")
	.allowConsole()
	.setDescription("Get HouseBuilder version")
	.setHandler(new SubHandler() {
		public void handle(CallInfo call) {
			call.reply("HouseBuilder Version {RED}1.0.5");
		}
	});
```

MondoCommand now also supports doing sub-sub commands by nesting one MondoCommand as a sub of the base (and in theory can handle as many levels as you want)

```java
MondoCommand colorSub = new MondoCommand();
base.addSub("color")
	.setDescription("Manage colors")
	.setUsage("[add/remove] <color>")
	.setHandler(colorSub);

colorSub.addSub("add")
	.setDescription("Add Colors");

colorSub.addSub("remove")
	.setDescription("Remove colors");
// and so on
```

**Coming soon**:

 * Comprehensive Documentation
 * Support for easily customizing colors in MondoCommand
 * Support for long-help screens for commands.
 * An "installer" to repackage this into your java project's namespace.
