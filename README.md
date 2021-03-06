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
    // We don't need to check number of args, because we registered the
    // command with minArgs = 1.
    String name = call.getArg(0);
    if (houseMap.containsKey(name)) {
        houseMap.remove(name);
        call.reply("{GREEN}House {GOLD}%s{GREEN} removed", name);
    } else {
        call.reply("{RED}House %s not found", name);
    }
}
```

This creates an output which looks like this:

![Usage example](https://dl.dropbox.com/u/14941058/Screenshots/MondoCommand_Usage2.png)


### Things you can do with CallInfo

All MondoCommand handlers take a single argument of type CallInfo which contains all the information you'd normally get from a command handler, plus a bunch of added bits to make writing commands much more convenient:

* `call.getPlayer()` gets a Player object (no more casting from CommandSender) and commands can be registered as allowing console or player-only.
* `call.reply(template, [...])` This is the gem of MondoCommand, it will send a message back to the user that interprets color codes embedded in the string, and lets you also interpolate variables into the string without having to do string concatenation. See below for how to use call.reply.
* `call.getArg(index)` To get a single argument, where the 0th index is the first index which comes after the sub command name (no more argument math!) and furthermore, you don't need to check the length of the args if you registered the subcommand with [B]setMinArgs()[/B], it will show the player a usage message and stop them from running your command.
* `call.getIntArg(index)` - Convenient way to get an argument coerced into an integer.
* `call.getJoinedArgsAfter(index)` - If you need to get a bunch of arguments after a certain index (like say you're accepting a text entry or chat message) this convenience method does that for you.
* `call.numArgs()` - Get the number of arguments.

### Using call.reply()

Ever write something like:
```java
player.sendMessage(ChatColor.BLUE.toString() + "Added user "
                   + ChatColor.RED.toString() + targetPlayer.getName()
                   + ChatColor.BLUE.toString() + "with role"
                   + ChatColor.GREEN.toString() + role);
```

Well with MondoCommand, that looks like:
```java
call.reply("{BLUE}Added user {RED}%s {BLUE}with role {GREEN}%s",
           targetPlayer.getName(), role);
```
You can put any of the Bukkit color codes in braces and also take advantage of string formatting as provided by *String.format* to let you smartly interpolate variables.


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

If you don't like the default formatting of MondoCommand, the formatting can be customized in various ways.

### Color Roles

Internally, MondoCommand uses color "roles" to represent various colors. This allows you to write code that, for example, has colors changeable by your users. Also, it lets you customize the colors of output from MondoCommand.

Here are the roles which MondoCommand uses:

 * `{HEADER}` Heading of any multi-line replies
 * `{USAGE}` The usage portion of command help (usually describes arguments). Default: LIGHT_PURPLE
 * `{WARNING}` Used when permissions do not match. Default: DARK_RED
 * `{ERROR}` Used when there's an error in the form of a MondoFailure
 * `{NOUN}` Used to describe an object. (currently unused by MondoCommand) Default: AQUA
 * `{VERB}` Used to describe an action. Default: GRAY
 * `{MCMD}` Used for the command portion of the usage line. Default: GREEN
 * `{DESCRIPTION}` The description of the line in the usage line. Default: BLUE

So one way to change how MondoCommand looks is by changing the role aliases:
```java
    ChatMagic.registerAlias("{HEADER}", ChatColor.GOLD);
    ChatMagic.registerAlias("{USAGE}", ChatColor.LIGHT_PURPLE);
```

You can also use any of these roles in your own code:
```java
String action_text = "expand";
call.reply("About to {VERB}%s{RESET} the house belonging to {NOUN}%s",
           action_text, playerName);
```

### FormatConfig

In addition, many of the usage strings can be customized using the FormatConfig class:
```java
FormatConfig fmt = new FormatConfig()
    .setUsageHeading("{GOLD}Usage: ")
    .setUsageCommandSuffix(" {GREEN}<command> [arg...]")
    .setReplyPrefix("{RED}My{GREEN}App: ")
    .setPermissionWarning("{RED}You do not have any permissions to perform this action.");

MondoCommand base = new MondoCommand(fmt);
```


Better Error Handling
---------------------

One thing MondoCommand is able to do is provide a better error handling flow and also encourage re-usable components by using exceptions to handle the flow and provide descriptive errors back to the users. This is done by use of the `MondoFailure` exception.

To illustrate how this can change your code flow, let's begin first with some basic code which manipulates houses in our theoretical HouseBuilder plugin:
```java
@Sub(description="Destroy a House", minArgs=1, usage="<name>")
public void destroy(CallInfo Call) {
    String name = call.getArg(0);
    if (houseMap.containsKey(name)) {
        houseMap.get(name).destroy();
        houseMap.remove(name);
        call.reply("{GREEN}House {GOLD}%s{GREEN} removed", name);
    } else {
        call.reply("{RED}House %s not found", name);
    }
};

@Sub(description="Expand House", minArgs=2, usage="<name> <size>")
public void grow(CallInfo Call) {
    String name = call.getArg(0);
    if (houseMap.containsKey(name)) {
        houseMap.get(name).expand(call.getIntArg(1));
        call.reply("{GREEN}House {GOLD}%s{GREEN} expanded", name);
    } else {
        call.reply("{RED}House %s not found", name);
    }
```

You'll notice above that you've more or less duplicated the pieces which deal with finding a house by name in both of those methods, and while you could define a helper, it makes the control flow with providing a clean message to the user much harder. There is another way you can do it, using the `MondoFailure` exception. All handlers for MondoCommand are allowed to throw a MondoFailure exception. The intention of this is to allow you to propagate sensible messages outwards.

```java
private House getHouse(String name) throws MondoFailure {
    House house = houseMap.get(name.toLowerCase());
    if (house == null) {
        throw new MondoFailure("House {NOUN}%s{ERROR} not found", name);
    }
    return house;
}

@Sub(description="Destroy a House", minArgs=1, usage="<name>")
public void destroy(CallInfo Call) throws MondoFailure {
    String name = call.getArg(0);
    // MondoFailure is propagated so we don't need to deal with not found situation
    House house = getHouse(name);
    house.destroy();
    houseMap.remove(name);
    call.reply("{GREEN}House {NOUN}%s{GREEN} removed", name);
};

@Sub(description="Expand House", minArgs=2, usage="<name> <size>")
public void grow(CallInfo Call) throws MondoFailure {
    String name = call.getArg(0);
    House house = getHouse(name);
    house.expand(call.getIntArg(1));
    call.reply("{GREEN}House {NOUN}%s{GREEN} expanded", name);
}
```

By using MondoFailure and allowing it to propagate through handlers, now your code is considerably flattened versus the original way it was designed, allowing you to have cleaner command handlers and less duplicated code.


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
      <version>0.3</version>
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
