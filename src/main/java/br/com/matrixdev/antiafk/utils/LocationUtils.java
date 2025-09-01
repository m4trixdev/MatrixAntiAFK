package br.com.matrixdev.antiafk.utils;

import org.bukkit.Location;
public class LocationUtils {

    public static boolean isSimilarLocation(Location loc1, Location loc2, double threshold) {
        if (loc1 == null || loc2 == null) {
            return false;
        }

        if (!loc1.getWorld().equals(loc2.getWorld())) {
            return false;
        }

        double distanceSquared = getDistanceSquared(loc1, loc2);
        double thresholdSquared = threshold * threshold;

        return distanceSquared <= thresholdSquared;
    }

    public static double getDistanceSquared(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) {
            return Double.MAX_VALUE;
        }

        double deltaX = loc1.getX() - loc2.getX();
        double deltaY = loc1.getY() - loc2.getY();
        double deltaZ = loc1.getZ() - loc2.getZ();

        return (deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ);
    }

    public static boolean hasSignificantLookChange(Location loc1, Location loc2, float threshold) {
        if (loc1 == null || loc2 == null) {
            return false;
        }

        float yawDiff = Math.abs(loc1.getYaw() - loc2.getYaw());
        float pitchDiff = Math.abs(loc1.getPitch() - loc2.getPitch());

        if (yawDiff > 180) {
            yawDiff = 360 - yawDiff;
        }

        return yawDiff > threshold || pitchDiff > threshold;
    }

    public static Location parseLocation(String locationString, org.bukkit.World world) {
        if (locationString == null || locationString.trim().isEmpty()) {
            return null;
        }

        try {
            String[] coords = locationString.split(",");

            if (coords.length >= 3) {
                double x = Double.parseDouble(coords[0].trim());
                double y = Double.parseDouble(coords[1].trim());
                double z = Double.parseDouble(coords[2].trim());

                Location location = new Location(world, x, y, z);

                if (coords.length >= 5) {
                    float yaw = Float.parseFloat(coords[3].trim());
                    float pitch = Float.parseFloat(coords[4].trim());
                    location.setYaw(yaw);
                    location.setPitch(pitch);
                }

                return location;
            }
        } catch (NumberFormatException e) {
            return null;
        }

        return null;
    }

    public static String locationToString(Location location) {
        if (location == null) {
            return "";
        }

        return String.format("%.2f,%.2f,%.2f,%.2f,%.2f",
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch());
    }
}