package fr.lucidiax.wondara.arena;

import com.google.common.collect.Lists;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class Arenas {

    private static final List<Arena> ARENAS = Lists.newArrayList();

    public static void addArena(Arena arena) {
        if(ARENAS.contains(arena))
            return;
        ARENAS.add(arena);
    }

    public static void removeArena(Arena arena) {
        ARENAS.remove(arena);
    }

    public static Arena getArenaBySign(Sign sign) {
        return ARENAS.stream().filter(arena -> arena.getSignLocation().getWorld() == sign.getWorld() && arena.getSignLocation().distance(sign.getLocation()) < 1).findFirst().orElse(null);
    }

    public static List<Arena> getArenas() {
        return Collections.unmodifiableList(ARENAS);
    }

    public static Arena getArenaByPlayer(Player player) {
        return ARENAS.stream().filter(arena -> arena.hasPlayer(player)).findFirst().orElse(null);
    }
}
