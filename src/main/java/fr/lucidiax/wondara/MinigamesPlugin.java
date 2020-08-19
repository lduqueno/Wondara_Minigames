package fr.lucidiax.wondara;

import com.google.common.collect.Maps;
import fr.lucidiax.wondara.util.Area;
import fr.lucidiax.wondara.arena.Arena;
import fr.lucidiax.wondara.arena.Arenas;
import fr.lucidiax.wondara.game.Games;
import fr.lucidiax.wondara.listener.BaseListener;
import fr.lucidiax.wondara.util.PlayerRestorer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Map;

public class MinigamesPlugin extends JavaPlugin {

    private static MinigamesPlugin instance;

    private String commandToExecuteOnWinning;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        if(getConfig().isSet("arenas")) {
            for(String rawId : getConfig().getConfigurationSection("arenas").getKeys(false)) {
                int id = Integer.parseInt(rawId);
                String path = "arenas." + id + ".";
                Location minLoc = locationByString(getConfig().getString(path + "minLoc").split(";"));
                Location maxLoc = locationByString(getConfig().getString(path + "maxLoc").split(";"));
                Location signLoc = locationByString(getConfig().getString(path + "signLoc").split(";"));
                Location spawnLoc = locationByString(getConfig().getString(path + "spawnLoc").split(";"));
                if(!signLoc.getBlock().getType().name().contains("SIGN")) {
                    getLogger().warning("Arena id " + id + " : there is no sign at the gived position! Skipping..");
                    continue;
                }
                Games games = Games.fromName(getConfig().getString(path + "game"));
                if(games == null) {
                    getLogger().warning("Arena id " + id + " : the gived game name doesn't exists! Skipping..");
                    continue;
                }
                Map<String, Object> properties = getConfig().isSet(path + "properties") ?
                        getConfig().getConfigurationSection(path + "properties").getValues(false) : Maps.newHashMap();
                Arena arena = new Arena(new Area(minLoc, maxLoc), spawnLoc, signLoc, properties, games);
                Arenas.addArena(arena);
                arena.updateSign();
                getLogger().info("Added one arena!");
            }
        }

        commandToExecuteOnWinning = getConfig().getString("commandToExecuteOnWinning");
        getServer().getPluginManager().registerEvents(new BaseListener(), this);
    }

    @Override
    public void onDisable() {
        for(PlayerRestorer restorer : new ArrayList<>(PlayerRestorer.getPlayers())) {
            restorer.restore();
            PlayerRestorer.removePlayer(restorer);
        }
    }

    public String getCommandToExecuteOnWinning() {
        return commandToExecuteOnWinning;
    }

    public static MinigamesPlugin getInstance() {
        return instance;
    }

    private Location locationByString(String[] rawLoc) {
        Location loc = new Location(Bukkit.getWorld(rawLoc[0]), Integer.parseInt(rawLoc[1]) + 0.5, Integer.parseInt(rawLoc[2]) + 0.5, Integer.parseInt(rawLoc[3]) + 0.5);
        if (rawLoc.length == 6) {
            loc.setYaw(Float.parseFloat(rawLoc[4]));
            loc.setPitch(Float.parseFloat(rawLoc[5]));
        }
        return loc;
    }

}
