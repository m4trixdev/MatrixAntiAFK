package br.com.matrixdev.antiafk.data;

import org.bukkit.Material;
public class ChallengeData {

    private final int correctSlot;
    private final Material correctMaterial;
    private final long startTime;

    public ChallengeData(int correctSlot, Material correctMaterial) {
        this.correctSlot = correctSlot;
        this.correctMaterial = correctMaterial;
        this.startTime = System.currentTimeMillis();
    }

    public int getCorrectSlot() {
        return correctSlot;
    }

    public Material getCorrectMaterial() {
        return correctMaterial;
    }

    public long getStartTime() {
        return startTime;
    }
}