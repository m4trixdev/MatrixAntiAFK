package br.com.matrixdev.antiafk.data;

import org.bukkit.Location;

public class PlayerData {

    private Location lastLocation;
    private long lastActivity;
    private long lastChallengeTime;
    private boolean challengeActive;
    private int repetitiveCount;

    public PlayerData(Location lastLocation, long lastActivity) {
        this.lastLocation = lastLocation;
        this.lastActivity = lastActivity;
        this.lastChallengeTime = 0;
        this.challengeActive = false;
        this.repetitiveCount = 0;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    public long getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(long lastActivity) {
        this.lastActivity = lastActivity;
    }

    public boolean isChallengeActive() {
        return challengeActive;
    }

    public void setChallengeActive(boolean challengeActive) {
        this.challengeActive = challengeActive;
        if (challengeActive) {
            this.lastChallengeTime = System.currentTimeMillis();
        } else {
            this.lastChallengeTime = 0;
        }
    }

    public long getLastChallengeTime() {
        return lastChallengeTime;
    }

    public int getRepetitiveCount() {
        return repetitiveCount;
    }

    public void incrementRepetitiveCount() {
        this.repetitiveCount++;
    }

    public void resetRepetitiveCount() {
        this.repetitiveCount = 0;
    }

    public void updateActivity(Location currentLocation) {
        this.lastLocation = currentLocation;
        this.lastActivity = System.currentTimeMillis();
        this.repetitiveCount = 0;
    }
}