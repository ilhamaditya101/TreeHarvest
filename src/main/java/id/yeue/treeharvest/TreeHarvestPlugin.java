package id.yeue.treeharvest;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class TreeHarvestPlugin extends JavaPlugin {
    @Override public void onEnable() {
        saveDefaultConfig();
        TreeManager manager = new TreeManager(this);
        getServer().getPluginManager().registerEvents(new TreeListener(this, manager), this);
        getCommand("treeharvest").setExecutor((s,c,l,a) -> {
            if (!s.hasPermission("treeharvest.admin")) { s.sendMessage(color(getConfig().getString("messages.no-permission"))); return true; }
            if (a.length > 0 && a[0].equalsIgnoreCase("reload")) { reloadConfig(); s.sendMessage(color("&aTreeHarvest reloaded.")); return true; }
            s.sendMessage(color("&e/treeharvest reload")); return true;
        });
    }
    public static String color(String s) { return ChatColor.translateAlternateColorCodes('&', s == null ? "" : s); }
}
