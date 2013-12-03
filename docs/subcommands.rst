Working with SubCommands
========================

A basic idea of how subcommand


Nested Sub-Commands
-------------------

MondoCommand supports doing sub-sub commands by nesting one MondoCommand as a sub of the base (and in theory can handle as many levels as you want)

.. code-block:: java

    MondoCommand colorSub = new MondoCommand();
    base.addSub("color")
        .setDescription("Manage colors")
        .setUsage("[add/remove] <color>")
        .setHandler(colorSub);

    colorSub.addSub("add")
        .setDescription("Add Colors");

    colorSub.addSub("remove")
        .setDescription("Remove colors");

The output of running sub-sub commands looks lke this:

.. image:: images/MondoCommand_Usage3.png


I hate dynamic registration
---------------------------

If you want more control over MondoCommand, or you hate dynamic registration via reflection, worry not, you can add all your commands explicitly with the core API for subcommand adding:

.. code-block:: java

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
