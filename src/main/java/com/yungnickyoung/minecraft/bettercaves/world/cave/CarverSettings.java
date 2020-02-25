package com.yungnickyoung.minecraft.bettercaves.world.cave;

import com.yungnickyoung.minecraft.bettercaves.noise.NoiseSettings;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

public class CarverSettings {
    private World    world;
    private long     seed;

    /* ============================== Values determined through config ============================== */
    /* ------------- Ridged Multifractal Params ------------- */
    private NoiseSettings noiseSettings = new NoiseSettings();
    private int           numGens; // Number of noise values to generate per iteration (block, sub-chunk, etc)

    /* ----------------- Turbulence Params ----------------- */
    private NoiseSettings turbulenceSettings = new NoiseSettings();
    private boolean       enableTurbulence; // Set true to enable turbulence (adds performance overhead, generally not worth it)

    /* -------------- Noise Processing Params -------------- */
    private float yCompression;   // Vertical cave gen compression
    private float xzCompression;  // Horizontal cave gen compression
    private float noiseThreshold; // Noise threshold for determining whether or not a block gets dug out

    /* ------------------ Worldgen Params ------------------ */
    private int liquidAltitude;
    private boolean replaceFloatingGravel;

    /* -------------------- Debug Params ------------------- */
    private IBlockState debugBlock;             // Block used to represent this cave/cavern type in the debug visualizer
    private boolean     enableDebugVisualizer;  // Set true to enable debug visualization for this carver

    public CarverSettings(World world) {
        this.world = world;
        this.seed = world.getSeed();
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public NoiseSettings getNoiseSettings() {
        return noiseSettings;
    }

    public void setNoiseSettings(NoiseSettings noiseSettings) {
        this.noiseSettings = noiseSettings;
    }

    public int getNumGens() {
        return numGens;
    }

    public void setNumGens(int numGens) {
        this.numGens = numGens;
    }

    public NoiseSettings getTurbulenceSettings() {
        return turbulenceSettings;
    }

    public void setTurbulenceSettings(NoiseSettings turbulenceSettings) {
        this.turbulenceSettings = turbulenceSettings;
    }

    public boolean isEnableTurbulence() {
        return enableTurbulence;
    }

    public void setEnableTurbulence(boolean enableTurbulence) {
        this.enableTurbulence = enableTurbulence;
    }

    public float getyCompression() {
        return yCompression;
    }

    public void setyCompression(float yCompression) {
        this.yCompression = yCompression;
    }

    public float getXzCompression() {
        return xzCompression;
    }

    public void setXzCompression(float xzCompression) {
        this.xzCompression = xzCompression;
    }

    public float getNoiseThreshold() {
        return noiseThreshold;
    }

    public void setNoiseThreshold(float noiseThreshold) {
        this.noiseThreshold = noiseThreshold;
    }

    public int getLiquidAltitude() {
        return liquidAltitude;
    }

    public void setLiquidAltitude(int liquidAltitude) {
        this.liquidAltitude = liquidAltitude;
    }

    public boolean isReplaceFloatingGravel() {
        return replaceFloatingGravel;
    }

    public void setReplaceFloatingGravel(boolean replaceFloatingGravel) {
        this.replaceFloatingGravel = replaceFloatingGravel;
    }

    public IBlockState getDebugBlock() {
        return debugBlock;
    }

    public void setDebugBlock(IBlockState debugBlock) {
        this.debugBlock = debugBlock;
    }

    public boolean isEnableDebugVisualizer() {
        return enableDebugVisualizer;
    }

    public void setEnableDebugVisualizer(boolean enableDebugVisualizer) {
        this.enableDebugVisualizer = enableDebugVisualizer;
    }
}
