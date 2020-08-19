package fr.lucidiax.wondara.arena;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Map;

public abstract class ArenaRegenHandler {

    protected abstract Map<Location, Material> getBlocksToRegen(Arena arena);

}
