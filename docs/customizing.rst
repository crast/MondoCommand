Customizing Formatting
======================

If you don't like the default formatting of MondoCommand, the formatting can be customized in various ways:

Color Roles
-----------

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

.. code-block:: java

    ChatMagic.registerAlias("{HEADER}", ChatColor.GOLD);
    ChatMagic.registerAlias("{USAGE}", ChatColor.LIGHT_PURPLE);


You can also use any of these roles in your own code:

.. code-block:: java

	String action_text = "expand";
	call.reply("About to {VERB}%s{RESET} the house belonging to {NOUN}%s",
	           action_text, playerName);


Format Usage Strings
--------------------

In addition, many of the usage strings can be customized using the FormatConfig class:

.. code-block:: java

	FormatConfig fmt = new FormatConfig()
	    .setUsageHeading("{GOLD}Usage: ")
	    .setUsageCommandSuffix(" {GREEN}<command> [arg...]")
	    .setReplyPrefix("{RED}My{GREEN}App: ")
	    .setPermissionWarning("{RED}No permissions to perform this action.");

	MondoCommand base = new MondoCommand(fmt);
