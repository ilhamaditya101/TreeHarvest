package id.yeue.treeharvest;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;

public final class TreeManager {
    private final JavaPlugin plugin;
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();
    private static final Set<Material> LOGS = EnumSet.of(Material.OAK_LOG,Material.SPRUCE_LOG,Material.BIRCH_LOG,Material.JUNGLE_LOG,Material.ACACIA_LOG,Material.DARK_OAK_LOG,Material.MANGROVE_LOG,Material.CHERRY_LOG);
    private static final Set<Material> LEAVES = EnumSet.of(Material.OAK_LEAVES,Material.SPRUCE_LEAVES,Material.BIRCH_LEAVES,Material.JUNGLE_LEAVES,Material.ACACIA_LEAVES,Material.DARK_OAK_LEAVES,Material.MANGROVE_LEAVES,Material.CHERRY_LEAVES,Material.AZALEA_LEAVES,Material.FLOWERING_AZALEA_LEAVES);
    public TreeManager(JavaPlugin p){plugin=p;}
    public boolean isLog(Material m){return LOGS.contains(m);}
    public boolean isLeaf(Material m){return LEAVES.contains(m);}
    public Set<Block> collectTree(Block origin){
        if(!isLog(origin.getType())&&!isLeaf(origin.getType())) return Set.of();
        Set<Block> out=new HashSet<>(); ArrayDeque<Block> q=new ArrayDeque<>(); q.add(origin);
        int max=plugin.getConfig().getInt("tree-detection.max-blocks",512);
        while(!q.isEmpty()&&out.size()<max){
            Block b=q.poll(); if(!out.add(b))continue;
            for(int x=-1;x<=1;x++)for(int y=-1;y<=1;y++)for(int z=-1;z<=1;z++){
                if(x==0&&y==0&&z==0)continue;
                Block n=b.getRelative(x,y,z); if(isLog(n.getType())||isLeaf(n.getType()))q.add(n);
            }
        }
        return out;
    }
    public Set<Block> collectLogs(Block origin){Set<Block> r=new HashSet<>();for(Block b:collectTree(origin))if(isLog(b.getType()))r.add(b);return r;}
    public String treeId(Block origin){
        Set<Block> t=collectTree(origin); if(t.isEmpty())return null;
        Block b=t.stream().min(Comparator.comparingInt(Block::getY).thenComparingInt(Block::getX).thenComparingInt(Block::getZ)).orElse(origin);
        return b.getWorld().getUID()+":"+b.getX()+":"+b.getY()+":"+b.getZ();
    }
    public boolean cooldown(UUID p,String id){return cooldowns.getOrDefault(p,Map.of()).getOrDefault(id,0L)>System.currentTimeMillis();}
    public long remaining(UUID p,String id){long u=cooldowns.getOrDefault(p,Map.of()).getOrDefault(id,0L);return Math.max(0,(u-System.currentTimeMillis()+999)/1000);}
    public void setCooldown(UUID p,String id){cooldowns.computeIfAbsent(p,k->new HashMap<>()).put(id,System.currentTimeMillis()+plugin.getConfig().getLong("leaves.cooldown-seconds",60)*1000L);}
}
