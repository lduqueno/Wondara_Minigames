package fr.lucidiax.wondara.listener;

import fr.lucidiax.wondara.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

public class ArenaListener implements Listener {

    private Arena arena;

    public ArenaListener(Arena arena) {
        this.arena = arena;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (arena.hasPlayer(player)) {
            event.setCancelled(arena.getGame().onPlayerMove(player, arena, event.getTo()));
            if (!arena.getArea().isInside(player.getLocation()))
                arena.removePlayer(player);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (arena.hasPlayer(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        if (arena.hasPlayer(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if (arena.hasPlayer((Player) event.getWhoClicked()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onItemUse(PlayerItemConsumeEvent event) {
        if (arena.hasPlayer(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLose(FoodLevelChangeEvent event) {
        if (arena.hasPlayer((Player) event.getEntity()))
            event.setFoodLevel(20);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (arena.hasPlayer(event.getPlayer())) {
            if(!arena.getArea().isInside(event.getBlockPlaced().getLocation())) {
                event.setCancelled(true);
                return;
            }
            event.setCancelled(arena.getGame().onBlockPlace(event.getPlayer(), arena, event.getBlockPlaced()));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (arena.hasPlayer(event.getPlayer())) {
            if(!arena.getArea().isInside(event.getBlock().getLocation())) {
                event.setCancelled(true);
                return;
            }
            event.setCancelled(arena.getGame().onBlockBreak(event.getPlayer(), arena, event.getBlock()));
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        System.out.println(event.getMessage());
        if (arena.hasPlayer(event.getPlayer())) {
            event.getPlayer().sendMessage("§cTu ne peux pas utiliser de commandes en jeu !");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        if (arena.hasPlayer(player))
            event.setCancelled(arena.getGame().onPlayerDamage(player, arena, null, event.getCause()));
    }

    @EventHandler
    public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        if (arena.hasPlayer(player))
            event.setCancelled(arena.getGame().onPlayerDamage(player, arena, (Player) event.getDamager(), event.getCause()));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (arena.hasPlayer(player))
            event.setCancelled(arena.getGame().onPlayerInteract(player, arena, event.getAction()));
    }

}
