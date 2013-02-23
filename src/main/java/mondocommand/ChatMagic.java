package mondocommand;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;

/** 
 * I got tired of doing ChatColor.GREEN all over the place.
 * 
 * <p>This class lets you do a format string that looks like:
 * 
 * <p><tt>colorize("{BLUE}Welcome, {GREEN}%s{BLUE}! You are an %s", player.getName(), "Admin");</tt>
 *
 * <p>The system is based upon two different concepts put together. Firstly, the
 * ability for string interpolation using {@code printf()} style interpolation
 * in {@link String#format} and then a simple substitution map which turns the
 * colors available in the {@link ChatColor} class into useful interpolation
 * strings like <tt>{BLUE}</tt>.
 * 
 * <p>For speed, the template strings are cached in a dictionary lookup, so the
 * color interpolation can be done once per template. For this reason, any 
 * variable strings should not be joined into the template string, but should
 *  be provided as <tt>%s</tt>-style interpolations. 
 * 
 * @see ChatColor
 * @see String#format
 */
public final class ChatMagic {
    private static final int SIZE_THRESHOLD = 5000;
    private static HashMap<String, String> colorMap = new HashMap<String, String>();
    private static HashMap<String, String> translationMap = new HashMap<String, String>();
    static {
        for (ChatColor c: ChatColor.values()) {
            colorMap.put("{" + c.name() + "}", c.toString());
            colorMap.put("{" + c.name().toLowerCase() + "}", c.toString());
        }
    }

    /**
     * Register an alias for a color.
     * 
     * <p>For example,<br><tt>registerAlias("{ERROR}", ChatColor.RED)</tt>
     * 
     * @param alias The alias string to use. Must include brackets.
     * @param color A ChatColor.
     * @see ChatColor
     */
    public static void registerAlias(String alias, ChatColor color) {
        colorMap.put(alias, color.toString());
        translationMap.clear();
    }

    /**
     * Register an alias for a color, but only if it doesn't already exist.
     * @param alias The alias string to use. Must include brackets.
     * @param color A ChatColor.
     */
    public static void registerDefaultAlias(String alias, ChatColor color) {
        if (!colorMap.containsKey(alias)) {
            registerAlias(alias, color);
        }
    }

    
    /**
     * Colorize this string with Bukkit ChatColors and interpolate any variables.
     * @param template A template string in printf-format.
     * @param args As many arguments as are expected by %-interpolations.
     * @return A string which has been color-formatted
     */
    public static String colorize(String template, Object ... args) {
        String translated = translationMap.get(template);
        if (translated == null) {
            translated = template;
            for (Map.Entry<String, String> e: colorMap.entrySet()) {
                translated = translated.replace(e.getKey(), e.getValue());
            }
            translationMap.put(template, translated);
            if (translationMap.size() > SIZE_THRESHOLD) {
                cleanup();
            }
        }
        if (args.length > 0) {
            return String.format(translated, args);
        } else {
            return translated;
        }
        
    }
    
    private static void cleanup() {
        Iterator<String> it = translationMap.values().iterator();
        int to_remove = translationMap.size() / 5;
        while (--to_remove >= 0) {
            if (!it.hasNext()) break;
            it.next();
            it.remove();
        }
    }

    /**
     * A convenience for sending messages to a player colorized. 
     * @param sender A CommandSender or player.
     * @param template A Colorizing template.
     * @param args Variadic arguments as in String.format
     * @see #colorize
     */
    public static void send(CommandSender sender, String template, Object ... args) {
        sender.sendMessage(colorize(template, args));
    }
    
    public static void sendRaw(Conversable c, String template, Object ... args) {
        c.sendRawMessage(colorize(template, args));
    }
}
