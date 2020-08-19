package fr.lucidiax.wondara.util;

import org.bukkit.Location;

public class Area {

    private Location minLoc, maxLoc;

    public Area(Location minLoc, Location maxLoc) {
        int x1 = Math.min(minLoc.getBlockX(), maxLoc.getBlockX());
        int y1 = Math.min(minLoc.getBlockY(), maxLoc.getBlockY());
        int z1 = Math.min(minLoc.getBlockZ(), maxLoc.getBlockZ());
        int x2 = Math.max(minLoc.getBlockX(), maxLoc.getBlockX());
        int y2 = Math.max(minLoc.getBlockY(), maxLoc.getBlockY());
        int z2 = Math.max(minLoc.getBlockZ(), maxLoc.getBlockZ());
        this.minLoc =  new Location(minLoc.getWorld(), x1, y1, z1);
        this.maxLoc = new Location(minLoc.getWorld(), x2, y2, z2);
    }

    public boolean isInside(Location loc) {
        return loc.getBlockX() >= minLoc.getBlockX() && loc.getBlockX() <= maxLoc.getBlockX()
                && loc.getBlockY() >= minLoc.getBlockY() && loc.getBlockY() <= maxLoc.getBlockY()
                && loc.getBlockZ() >= minLoc.getBlockZ() && loc.getBlockZ() <= maxLoc.getBlockZ() && loc.getWorld().equals(minLoc.getWorld());
    }

    public Location getMinLoc() {
        return minLoc;
    }

    public Location getMaxLoc() {
        return maxLoc;
    }
}
