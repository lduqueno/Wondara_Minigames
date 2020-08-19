package fr.lucidiax.wondara.game;

import fr.lucidiax.wondara.arena.Arena;
import fr.lucidiax.wondara.arena.ArenaRegenHandler;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;

public abstract class Game {

    protected String name;
    protected int minPlayers, maxPlayers;
    protected boolean giveColoredArmors;
    protected ArenaRegenHandler regenHandler;

    protected Game(String name, int minPlayers, int maxPlayers) {
        this.name = name;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    public abstract void onGameStart(Arena arena);
    public abstract void onSecond(Arena arena, int seconds);
    public abstract void onGameFinish(Arena arena);
    public abstract void onWin(Player player);
    public abstract boolean onPlayerMove(Player player, Arena arena, Location to);
    public abstract boolean onPlayerDamage(Player player, Arena arena, Player damager, EntityDamageEvent.DamageCause cause);
    public abstract boolean onPlayerInteract(Player player, Arena arena, Action action);
    public abstract boolean onBlockPlace(Player player, Arena arena, Block block);
    public abstract boolean onBlockBreak(Player player, Arena arena, Block block);

    public String getName() {
        return name;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public boolean shouldGiveColoredArmors() {
        return giveColoredArmors;
    }

    public ArenaRegenHandler getRegenHandler() {
        return regenHandler;
    }
}
