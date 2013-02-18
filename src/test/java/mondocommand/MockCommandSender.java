package mondocommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public class MockCommandSender implements CommandSender {
    public Set<String> permissions = new HashSet<String>();
    public List<String> messages = new ArrayList<String>();

    @Override
    public PermissionAttachment addAttachment(Plugin arg0) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin arg0, int arg1) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2, int arg3) {
        return null;
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return null;
    }

    @Override
    public boolean hasPermission(String perm) {
        return permissions.contains(perm);
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return hasPermission(perm.getName());
    }

    @Override
    public boolean isPermissionSet(String arg0) {
        return false;
    }

    @Override
    public boolean isPermissionSet(Permission arg0) {
        return false;
    }

    @Override
    public void recalculatePermissions() {
    }

    @Override
    public void removeAttachment(PermissionAttachment arg0) {
    }

    @Override
    public boolean isOp() {
        return false;
    }

    @Override
    public void setOp(boolean arg0) {
    }

    @Override
    public String getName() {
        return "BoringSender";
    }

    @Override
    public Server getServer() {
        return null;
    }

    @Override
    public void sendMessage(String message) {
        messages.add(message);
    }

    @Override
    public void sendMessage(String[] messageArray) {
        messages.addAll(Arrays.asList(messageArray));
    }
    
    public String stripMessage(int n) {
        return ChatColor.stripColor(messages.get(n));
    }
    
}
