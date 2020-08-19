package fr.lucidiax.wondara.game.type;

import com.google.common.collect.Maps;
import fr.lucidiax.wondara.MinigamesPlugin;
import fr.lucidiax.wondara.arena.Arena;
import fr.lucidiax.wondara.arena.ArenaRegenHandler;
import fr.lucidiax.wondara.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SpleefGame extends Game {

    private static final ItemStack SNOWBALL_ITEM = new ItemStack(Material.SNOW_BALL, 1);
    private static final List<BlockFace> FACES = Arrays.asList(BlockFace.DOWN, BlockFace.EAST, BlockFace.WEST, BlockFace.UP,
            BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH);

    static {
        ItemMeta meta = SNOWBALL_ITEM.getItemMeta();
        meta.setDisplayName("§6Déneigeuse");
        SNOWBALL_ITEM.setItemMeta(meta);
    }

    public SpleefGame() {
        super("Spleef", 2, 8); //TODO: 4
        this.giveColoredArmors = true;
        this.regenHandler = new ArenaRegenHandler() {

            @Override
            protected Map<Location, Material> getBlocksToRegen(Arena arena) {
                Map<Location, Material> blocks = Maps.newHashMap();
                Material material = Material.getMaterial((int) arena.getProperties().get("block"));
                int r = (int) arena.getProperties().get("radius");
                String[] layers = ((String) arena.getProperties().get("yLayers")).split(";");
                int cx = arena.getSpawn().getBlockX();
                int cz = arena.getSpawn().getBlockZ();
                for(String layer : layers) {
                    int cy = Integer.parseInt(layer);
                    for (double x = cx - r; x <= cx + r; x++)
                        for (double z = cz - r; z <= cz + r; z++)
                            if ((cx - x) * (cx - x) + (cz - z) * (cz - z) <= r * r)
                                blocks.put(new Location(arena.getArea().getMaxLoc().getWorld(), x, cy, z), material);
                }
                return blocks;
            }
        };
    }

    @Override
    public void onGameStart(Arena arena) {
        Listener listener = new Listener() {

            @EventHandler
            public void onProjectileLaunch(ProjectileLaunchEvent event) {
                ProjectileSource shooter = event.getEntity().getShooter();
                if(shooter != null && shooter instanceof Player && arena.hasPlayer((Player) shooter) && event.getEntity() instanceof Snowball)
                    event.getEntity().setMetadata("spleef", new FixedMetadataValue(MinigamesPlugin.getInstance(), arena.getProperties().get("block")));
            }

            @EventHandler
            public void onProjectileHit(ProjectileHitEvent event) {
                ProjectileSource shooter = event.getEntity().getShooter();
                if (shooter != null && shooter instanceof Player && arena.hasPlayer((Player) shooter) && event.getEntity() instanceof Snowball) {
                    Block block = event.getEntity().getLocation().getBlock();
                    if (event.getEntity().hasMetadata("spleef")){
                        Material material =  Material.getMaterial(event.getEntity().getMetadata("spleef").get(0).asInt());
                        for(BlockFace face : FACES) {
                            Block adjacent = block.getRelative(face);
                            if(adjacent.getType() == material)
                                adjacent.setType(Material.AIR);
                        }
                        if(block.getType() == material)
                            block.setType(Material.AIR);
                    }
                }
            }
        };
        arena.getProperties().put("customListener", listener);
        Bukkit.getPluginManager().registerEvents(listener, MinigamesPlugin.getInstance());
        for (Player player : arena.getPlayers())
            player.getInventory().setItem(0, new ItemStack(Material.DIAMOND_SPADE));
    }

    @Override
    public void onGameFinish(Arena arena) {
        HandlerList.unregisterAll((Listener) arena.getProperties().remove("customListener"));
    }

    @Override
    public void onSecond(Arena arena, int seconds) {
        if(seconds % 15 == 0)
            arena.getPlayers().forEach(player -> player.getInventory().addItem(SNOWBALL_ITEM));
    }

    @Override
    public void onWin(Player player) {
    }

    @Override
    public boolean onPlayerMove(Player player, Arena arena, Location to) {
        return false;
    }

    @Override
    public boolean onPlayerDamage(Player player, Arena arena, Player damager, EntityDamageEvent.DamageCause cause) {
        return true;
    }

    @Override
    public boolean onPlayerInteract(Player player, Arena arena, Action action) {
        return false;
    }

    @Override
    public boolean onBlockPlace(Player player, Arena arena, Block block) {
        return true;
    }

    @Override
    public boolean onBlockBreak(Player player, Arena arena, Block block) {
        if(arena.isGameProtected() || block.getType() != Material.getMaterial((int) arena.getProperties().get("block")))
            return true;
        block.setType(Material.AIR);
        return false;
    }

}
