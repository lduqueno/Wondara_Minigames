package fr.lucidiax.wondara.listener;

import fr.lucidiax.wondara.arena.Arena;
import fr.lucidiax.wondara.arena.Arenas;
import fr.lucidiax.wondara.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BaseListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType().name().contains("SIGN")) {
            Sign sign = (Sign) event.getClickedBlock().getState();
            Arena arena = Arenas.getArenaBySign(sign);
            if (arena == null)
                return;
            event.setCancelled(true);
            Arena currentArena = Arenas.getArenaByPlayer(player);
            if(currentArena != null && !arena.equals(currentArena)) {
                if(currentArena.getState() == GameState.WAITING) {
                    player.sendMessage("§cVous êtes déjà dans une file d'attente, vous êtes automatiquement désinscrit !");
                    currentArena.removePlayer(player);
                } else
                    return;
            }
            if (arena.getState() == GameState.WAITING) {
                if (arena.hasPlayer(player)) {
                    arena.removePlayer(player);
                    player.sendMessage("§cVous avez quitté la file d'attente !");
                } else if (arena.getPlayers().size() < arena.getGame().getMaxPlayers())
                    arena.addPlayer(player);
            } else
                player.sendMessage("§cLa partie n'est pas terminée !");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Arena arena = Arenas.getArenaByPlayer(event.getPlayer());
        if(arena != null)
            arena.removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Arena arena = Arenas.getArenaByPlayer(event.getEntity());
        if(arena != null)
            arena.removePlayer(event.getEntity());
    }

}
