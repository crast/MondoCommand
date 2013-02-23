MondoCommand
============

MondoCommand is a library for Bukkit applications which lets you create a command which dispatches a number of sub-commands along with generating pretty-looking help pages, checking arguments, and so on.

It also abstracts out a lot of the annoying things about doing subcommands, like return values, argument handling, and so on to help you write shorter, cleaner code (and split it up nicely too).


Basic usage
-----------

Do this in your onEnable or something similar:

```java
// Basic setup and registration
MondoCommand base = new MondoCommand();
base.autoRegisterFrom(this);
getCommand("housebuilder").setExecutor(base);
```

Now you can add some handlers:

```java
@Sub(description="Build a House", minArgs=2, usage="<owner> <name>")
public void build(CallInfo call) {
    String owner = call.getArg(0);
    String name = call.getArg(1);
    if (houseMap.containsKey(name)) {
        call.reply("House with name %s already exists", name);
    } else {
        // TODO add code to actually make a house
        call.reply("House %s made!", name);
    }
}

@Sub(description="Destroy a House", permission="housebuilder.destroy",
     minArgs=1, usage="<name>", allowConsole=false)
public void destroy(CallInfo Call) {
    // We don't need to check number of args, becaus we registered the
    // command with minArgs = 1.
    String name = call.getArg(0);
    if (houseMap.containsKey(name)) {
        houseMap.remove(name);
        call.reply("{GREEN}House {GOLD}%s{GREEN} removed", name);
    } else {
        call.reply("{RED}House %s not found", name);
    }
});
```

This creates an output which looks like this:

![Usage example](https://dl.dropbox.com/u/14941058/Screenshots/MondoCommand_Usage2.png)


But I want more control!
------------------------

If you want more control over MondoCommand, or you hate dynamic registration via reflection, worry not, you can add all your commands explicitly with the core API for subcommand adding:

```java
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



Nested Sub-Commands
-------------------

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

The output of running sub-sub commands looks lke this:

![Usage example 3](http://dl.dropbox.com/u/14941058/Screenshots/MondoCommand_Usage3.png)


Customizing formatting
----------------------

If you don't like the default formatting of MondoCommand, the formatting can be customized:
```java
FormatConfig fmt = new FormatConfig()
    .setUsageHeading("{GOLD}Usage: ")
    .setReplyPrefix("{RED}My{GREEN}App: ")
    .setPermissionWarning("{RED}You do not have any permissions to perform this action.");

MondoCommand base = new MondoCommand(fmt);
```

Using with Maven
----------------

Add this repository:
```xml
    <repository>
      <id>crast-repo</id>
      <url>http://maven.crast.us</url>
    </repository>
```

And this dependency:
```xml
    <dependency>
      <groupId>us.crast</groupId>
      <artifactId>MondoCommand</artifactId>
      <version>0.2</version>
      <type>jar</type>
      <scope>compile</scope>
    </dependency>
```

And also remember to shade the package so that there's no conflicts with other plugins.
```xml
<relocation>
  <pattern>mondocommand</pattern>
  <shadedPattern>me.something.your-project-name.mondocommand</shadedPattern>
</relocation>
```

Coming soon
-----------

 * Comprehensive Documentation
 * Support for easily customizing colors in MondoCommand
 * Support for long-help screens for commands.
 * An "installer" to repackage this into your java project's namespace.
