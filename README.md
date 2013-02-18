MondoCommand
============

MondoCommand is a library for Bukkit applications which lets you create a command which dispatches a number of sub-commands along with generating pretty-looking help pages, checking arguments, and so on.

It also abstracts out a lot of the annoying things about doing subcommands, like return values, argument handling, and so on to help you write shorter, cleaner code (and split it up nicely too).

Basic usage:
```java
	// Basic setup and registration
	MondoCommand base = new MondoCommand("MyHouseBuilder");
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
				String houseName = call.getArg(1);
				if (houseMap.containsKey(houseName) {
					houseMap.remove(houseName)
					// MondoCommand allows you to add messages with color formatting
					call.append("{GREEN}House {GOLD}%s{GREEN} removed", houseName);
				} else {
					call.append("{RED}House %s not found", houseName);
				}
			}
		});
```

**Coming soon**:

 * Sub-Sub-commands support
 * Comprehensive Documentation
 * An "installer" to repackage this into your java project's namespace.
