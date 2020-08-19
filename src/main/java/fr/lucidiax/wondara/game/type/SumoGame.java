package fr.lucidiax.wondara.game.type;

import fr.lucidiax.wondara.arena.Arena;
import fr.lucidiax.wondara.game.Game;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;

public class SumoGame extends Game {

    public SumoGame() {
        super("Sumo", 2, 4);
        this.giveColoredArmors = true;
    }

    @Override
    public void onGameStart(Arena arena) {
    }

    @Override
    public void onSecond(Arena arena, int seconds) {
    }

    @Override
    public void onGameFinish(Arena arena) {

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
        if(arena.isGameProtected())
            return true;
        player.setHealth(player.getMaxHealth());
        return false;
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
        return true;
    }
}
