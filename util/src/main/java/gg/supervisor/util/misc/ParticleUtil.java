package gg.supervisor.util.misc;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class ParticleUtil {

    /**
     * Spawns particles in a straight line between 2 points A and B
     *
     * @param start    Start location
     * @param end      End location
     * @param particle Particle builder which allows you to fully customize the particles spawned
     * @param stepSize How many blocks should there be in between each particle.
     */
    public static void spawnLine(Location start, Location end, ParticleBuilder particle, double stepSize) {
        Location st = start.clone();
        Vector dir = end.toVector().clone().subtract(st.toVector()).normalize();

        double distance = st.distance(end);

        for (double i = 0; i < distance; i += stepSize) {
            particle.location(dir.clone().multiply(i).toLocation(st.getWorld()).add(st));
            particle.spawn();
        }

    }

    /**
     * Spawns a circle around the center location
     *
     * @param location Center of the circle
     * @param particle Particle builder which allows you to fully customize the particles spawned
     * @param radius   How big should the circle be
     */
    public static void spawnCircle(Location location, ParticleBuilder particle, int radius) {
        for (int d = 0; d <= 30 * radius; d++) {
            Location particleLoc = new Location(location.getWorld(), location.getX() + Math.cos(d) * radius, location.getY(), location.getZ() + Math.sin(d) * radius);
            particle.location(particleLoc).spawn();
        }
    }

}
