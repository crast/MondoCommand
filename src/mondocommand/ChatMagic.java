package mondocommand;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;

/** 
 * I got tired of doing ChatColor.GREEN all over the place.
 * 
 * This class lets you do a format string that looks like:
 *  colorize("{BLUE}Welcome to the server, {GREEN}%s{BLUE}! You are an %s", player.getName, "Admin");
 *
 */
public final class ChatMagic {
	private static HashMap<String, String> colorMap = new HashMap<String, String>();
	private static HashMap<String, String> translationMap = new HashMap<String, String>();
	static {
		for (ChatColor c: ChatColor.values()) {
			colorMap.put("{" + c.name() + "}", c.toString());
			colorMap.put("{" + c.name().toLowerCase() + "}", c.toString());
		}
	}
	
	public static void registerAlias(String verb, ChatColor color) {
	    colorMap.put(verb, color.toString());
	}
	
	public static String colorize(String template, Object ... args) {
		String translated = translationMap.get(template);
		if (translated == null) {
			translated = template;
			for (Map.Entry<String, String> e: colorMap.entrySet()) {
				translated = translated.replace(e.getKey(), e.getValue());
			}
			translationMap.put(template, translated);
		}
		if (args.length > 0) {
			return String.format(translated, args);
		} else {
			return translated;
		}
		
	}
	
	public static void send(CommandSender sender, String template, Object ... args) {
	    sender.sendMessage(colorize(template, args));
	}
	
	public static void send(Conversable c, String template, Object ... args) {
		c.sendRawMessage(colorize(template, args));
	}

}
