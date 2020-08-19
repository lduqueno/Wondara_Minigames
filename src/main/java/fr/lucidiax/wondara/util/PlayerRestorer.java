package fr.lucidiax.wondara.util;

import com.google.common.collect.Lists;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.List;

public class PlayerRestorer {

    private static final List<PlayerRestorer> PLAYERS = Lists.newArrayList();

    private Player player;
    private double health;
    private int food;
    private Location location;
    private Collection<PotionEffect> effects;
    private ItemStack[] inventory;
    private ItemStack[] armor;
    private GameMode gamemode;
    private boolean saved = false;

    private PlayerRestorer(Player player) {
        this.player = player;
        PLAYERS.add(this);
    }

    public void clearAndSave() {
        health = player.getHealth();
        food = player.getFoodLevel();
        location = player.getLocation();
        effects = player.getActivePotionEffects();
        inventory = player.getInventory().getContents();
        armor = player.getInventory().getArmorContents();
        gamemode = player.getGameMode();

        saved = true;

        player.setHealth(20);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setGameMode(GameMode.SURVIVAL);
        player.updateInventory();
    }

    public void restore() {
        if(!saved)
            return;
        saved = false;
        player.setHealth(health);
        player.setFoodLevel(food);
        player.teleport(location);
        effects.forEach(effect -> player.addPotionEffect(effect));
        player.getInventory().setContents(inventory);
        player.getInventory().setArmorContents(armor);
        player.setGameMode(gamemode);
        player.setFallDistance(-5);
        player.updateInventory();
    }

    public static PlayerRestorer get(Player player) {
        for(PlayerRestorer restorer : PLAYERS)
            if(restorer.player.getUniqueId().equals(player.getUniqueId()))
                return restorer;
        return new PlayerRestorer(player);
    }

    public static void removePlayer(PlayerRestorer player) {
        PLAYERS.remove(player);
    }

    public static List<PlayerRestorer> getPlayers() {
        return PLAYERS;
    }
}
