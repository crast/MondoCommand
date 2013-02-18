package us.crast.MondoCommandExample;

import java.util.Collections;
import java.util.Map;

import mondocommand.CallInfo;
import mondocommand.MondoCommand;
import mondocommand.MondoFailure;
import mondocommand.SubHandler;

import org.bukkit.plugin.java.JavaPlugin;

public class MondoCommandExample extends JavaPlugin {

    protected Map<String, String> houseMap = Collections.emptyMap();

    @Override
    public void onEnable() {
        setupCommandHandlers();
    }

    private void setupCommandHandlers() {
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
                    String houseName = call.getArg(0);
                    if (houseMap.containsKey(houseName)) {
                        houseMap.remove(houseName);
                        // MondoCommand allows you to add messages with color formatting
                        call.append("{GREEN}House {GOLD}%s{GREEN} removed", houseName);
                    } else {
                        call.append("{RED}House %s not found", houseName);
                    }
                }
            });

        // Example of a command which works on the console
        base.addSub("version")
            .allowConsole()
            .setDescription("Get HouseBuilder version")
            .setHandler(new SubHandler() {
                public void handle(CallInfo call) {
                    call.append("HouseBuilder Version {RED}1.0.5");
                }
            });
       
        MondoCommand colorSub = new MondoCommand("MyHouseBuilder");
        base.addSub("color")
            .setDescription("Manage colors")
            .setUsage("[add/remove] <color>")
            .setHandler(colorSub);

        colorSub.addSub("add")
            .setDescription("Add Colors")
            .setHandler(new SubHandler() {
                public void handle(CallInfo call) throws MondoFailure {
                    throw new MondoFailure("Add Color failed");
                }
                
            });
        
        colorSub.addSub("remove")
            .setDescription("Remove colors")
            .setMinArgs(1)
            .setHandler(new SubHandler() {
                public void handle(CallInfo call) throws MondoFailure {
                    if (call.getArg(0).equals("HI")) {
                        call.append("Got the {GREEN}right {LIGHT_PURPLE}stuff");
                        return;
                    }
                    throw new MondoFailure("Add Color failed");
                }
            });
        // and so on
    }
}
