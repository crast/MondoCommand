Working with SubCommands
========================

The SubCommand is the basic building block of MondoCommand. Each MondoCommand has many subcommands, and each subcommand has:

name
    name of this command

description
    text shown in the help line about this subcommand

usage 
    text explaining the parameters of this subcommand

permission (optional)
    A string which is the permission to check to run this subcommand

handler
    A SubHandler which performs the action of this subcommand


Registering SubCommands
-----------------------

.. note::

    In :doc:`getting_started`, you saw registration of subcommands using the ``@Sub`` method annotation. The annotation is a very compact and handy way to register commands, but the underlying API allows better control. In actuality, using the @Sub annotation just causes anonymous handlers to be built for the underlying API.

Let's start with a simple example, registering your first command.

.. code-block:: java

    // Add sub-command "build" which requires permission "housebuilder.build"
    base.addSub("build", "housebuilder.build")
        .setDescription("Build a House")     // Description is shown in command help
        .setMinArgs(2)                       // Won't run command without this many args
        .setUsage("<owner> <name>")          // Sets argument usage information
        .setHandler(new HouseBuildHandler());

This example references a theoretical HouseBuildHandler which we haven't created yet, but we will get into that soon.  The most important things to note are:

1. You build a sub-command by chaining, similar to some other API's like the conversations API.
2. All sub-commands can be gated by an optional permission, if the user doesn't have this permission they can't access the subcommand nor will it show to them in help.
3. You can set useful metadata like the description, usage arguments, and the minimum number of arguments accepted.
4. Sub-commands delegate the action to a SubHandler which handles the actual implementation.

So now let's get to the code of the HouseBuildHandler:

.. code-block:: java
    
    class HouseBuildHandler implements SubHandler {
        public void handle(CallInfo call) throws MondoFailure {
            String owner = call.getArg(0);
            String name = call.getJoinedArgsAfter(1);
            if (houseMap.containsKey(name)) {
                call.reply("House with name %s already exists", name);
            } else {
                // TODO add code to actually make a house
                call.reply("House %s made!", name);
            }
        }
    }

This code is basically the same as the ``build`` method we wrote in the intro tutorial, except now it's part of a handler class. You may also be wondering about the ``throws MondoFailure`` part we added. This is a feature which allows for writing code with very good error handling (a must in the world of Minecraft where situations can change rapidly) that still provides good insight to the user. For more on that, jump over to :py:ref:`error-handling`

Handlers can be anything which implement SubHandler, but if you don't want to create a new class for every handler you can also define them inline:

.. code-block:: java

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


Console Commands
----------------

Commands can be set to run on the console. When a command is run from the console, 
:meth:`getPlayer() <mondocommand.CallInfo.getPlayer()>` will return None, but you
can use :meth:`getSender() <mondocommand.CallInfo.getSender()>` instead if needed.

Example:

.. code-block:: java

    // Example of a command which works on the console
    base.addSub("version")
        .allowConsole()
        .setDescription("Get HouseBuilder version")
        .setHandler(new SubHandler() {
            public void handle(CallInfo call) {
                call.reply("HouseBuilder Version {RED}1.0.5");
            }
        });

Note you can use ``call.reply()`` as usual, including color codes (though some consoles will ignore colors)


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


.. _error-handling:

Better Error Handling
---------------------

One thing MondoCommand is able to do is provide a better error handling flow and also encourage re-usable components by using exceptions to handle the flow and provide descriptive errors back to the users. This is done by use of the `MondoFailure` exception.

To illustrate how this can change your code flow, let's begin first with some basic code which manipulates houses in our theoretical HouseBuilder plugin:

.. code-block:: java

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
    }

    @Sub(description="Expand House", minArgs=2, usage="<name> <size>")
    public void grow(CallInfo Call) {
        String name = call.getArg(0);
        if (houseMap.containsKey(name)) {
            houseMap.get(name).expand(call.getIntArg(1));
            call.reply("{GREEN}House {GOLD}%s{GREEN} expanded", name);
        } else {
            call.reply("{RED}House %s not found", name);
        }
    }

You'll notice above that you've more or less duplicated the pieces which deal with finding a house by name in both of those methods, and while you could define a helper, it makes the control flow with providing a clean message to the user much harder. There is another way you can do it, using the `MondoFailure` exception. All handlers for MondoCommand are allowed to throw a MondoFailure exception. The intention of this is to allow you to propagate sensible messages outwards.

.. code-block:: java

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

By using MondoFailure and allowing it to propagate through handlers, now your code is considerably flattened versus the original way it was designed, allowing you to have cleaner command handlers and less duplicated code.
