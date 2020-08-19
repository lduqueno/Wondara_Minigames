package fr.lucidiax.wondara.game;

import fr.lucidiax.wondara.game.type.SpleefGame;
import fr.lucidiax.wondara.game.type.SumoGame;
import org.bukkit.ChatColor;

import java.util.Arrays;

public enum Games {

    SUMO(new SumoGame()), SPLEEF(new SpleefGame());

    private Game game;

    Games(Game game) {
        this.game = game;
    }

    public Game get() {
        return game;
    }

    public static Games fromName(String name) {
        return Arrays.stream(values()).filter(game -> game.get().getName().equalsIgnoreCase(ChatColor.stripColor(name))).findFirst().orElse(null);
    }

}
