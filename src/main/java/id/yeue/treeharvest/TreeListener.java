package id.yeue.treeharvest;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.*;

public final class TreeListener implements Listener {
    private final JavaPlugin plugin; private final TreeManager manager; private final Random rng=new Random();
    private final Map<UUID,Block> pending=new HashMap<>(), chopping=new HashMap<>();
    public TreeListener(JavaPlugin p,TreeManager m){plugin=p;manager=m;}

    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void breakBlock(BlockBreakEvent e){
        Player p=e.getPlayer(); Block b=e.getBlock();
        if(manager.isLeaf(b.getType())&&plugin.getConfig().getBoolean("leaves.enabled",true)){
            e.setCancelled(true); String id=manager.treeId(b); if(id==null)return;
            if(manager.cooldown(p.getUniqueId(),id)){p.sendMessage(msg("messages.leaf-cooldown").replace("%seconds%",String.valueOf(manager.remaining(p.getUniqueId(),id))));return;}
            manager.setCooldown(p.getUniqueId(),id); giveRewards(p); p.sendMessage(msg("messages.leaf-reward")); return;
        }
        if(manager.isLog(b.getType())&&plugin.getConfig().getBoolean("tree.enabled",true)){
            e.setCancelled(true); if(!pending.containsKey(p.getUniqueId())&&!chopping.containsKey(p.getUniqueId())){pending.put(p.getUniqueId(),b);confirm(p);}
        }
    }
    private void confirm(Player p){Inventory i=Bukkit.createInventory(null,9,msg("tree.minigame.confirm-title"));i.setItem(3,item(Material.LIME_STAINED_GLASS_PANE,"&aCONFIRM"));i.setItem(5,item(Material.RED_STAINED_GLASS_PANE,"&cCANCEL"));p.openInventory(i);}
    private void minigame(Player p,Block b){chopping.put(p.getUniqueId(),b);Inventory i=Bukkit.createInventory(null,9,msg("tree.minigame.title"));for(int x=0;x<9;x++)i.setItem(x,item(Material.RED_STAINED_GLASS_PANE,"&cMISS"));i.setItem(rng.nextInt(9),item(Material.LIME_STAINED_GLASS_PANE,"&aCHOP"));p.openInventory(i);}
    @EventHandler public void click(InventoryClickEvent e){
        if(!(e.getWhoClicked() instanceof Player p))return;
        String t=ChatColor.stripColor(e.getView().getTitle()); String confirm=ChatColor.stripColor(msg("tree.minigame.confirm-title"));String game=ChatColor.stripColor(msg("tree.minigame.title"));
        if(!t.equalsIgnoreCase(confirm)&&!t.equalsIgnoreCase(game))return; e.setCancelled(true);
        if(t.equalsIgnoreCase(confirm)){if(e.getRawSlot()==3){Block b=pending.remove(p.getUniqueId());p.closeInventory();if(b!=null&&manager.isLog(b.getType()))minigame(p,b);}else if(e.getRawSlot()==5){pending.remove(p.getUniqueId());p.closeInventory();}return;}
        if(e.getRawSlot()<0||e.getRawSlot()>8)return; Block b=chopping.remove(p.getUniqueId());p.closeInventory();if(b==null)return;
        if(e.getCurrentItem()==null||e.getCurrentItem().getType()!=Material.LIME_STAINED_GLASS_PANE){p.sendMessage(msg("messages.failed"));return;} chop(p,b);
    }
    private void chop(Player p,Block origin){
        Set<Block> logs=manager.collectLogs(origin); Map<Block,Material> original=new HashMap<>();for(Block b:logs)original.put(b,b.getType());
        for(Block b:logs)b.setType(Material.AIR,false);
        new BukkitRunnable(){public void run(){for(var x:original.entrySet())if(x.getKey().getType()==Material.AIR)x.getKey().setType(x.getValue(),false);}}.runTaskLater(plugin,2L);
        int xp=plugin.getConfig().getInt("tree.foraging-xp",300); giveXp(p,xp);p.sendMessage(msg("messages.success").replace("%xp%",String.valueOf(xp)));
    }
    private void giveXp(Player p,int xp){
        if(Bukkit.getPluginManager().getPlugin("AuraSkills")==null)return;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"sk xp add "+p.getName()+" foraging "+xp+" true");
    }
    private void giveRewards(Player p){
        for(String s:plugin.getConfig().getStringList("leaves.rewards")){String[] a=s.split("\\|");if(a.length<4)continue;try{if(rng.nextDouble()*100.0>Double.parseDouble(a[a.length-1]))continue;if(a[0].equalsIgnoreCase("VANILLA")){Material m=Material.matchMaterial(a[1]);if(m!=null)p.getInventory().addItem(new ItemStack(m,Math.max(1,Integer.parseInt(a[2]))));}else if(a[0].equalsIgnoreCase("MMOITEMS")){p.sendMessage(TreeHarvestPlugin.color("&eMMOItems reward configured: "+a[2]));}}catch(Exception ignored){}}
    }
    private ItemStack item(Material m,String n){ItemStack i=new ItemStack(m);ItemMeta im=i.getItemMeta();if(im!=null){im.setDisplayName(TreeHarvestPlugin.color(n));i.setItemMeta(im);}return i;}
    private String msg(String path){return TreeHarvestPlugin.color(plugin.getConfig().getString(path));}
}
