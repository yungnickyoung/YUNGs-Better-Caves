package com.yungnickyoung.minecraft.bettercaves.world.carver.cave;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseGen;
import com.yungnickyoung.minecraft.bettercaves.world.carver.CarverSettings;
import com.yungnickyoung.minecraft.bettercaves.world.carver.CarverUtils;
import com.yungnickyoung.minecraft.bettercaves.world.carver.ICarver;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CaveCarver implements ICarver {
    private CarverSettings settings;
    private NoiseGen noiseGen;
    private int surfaceCutoff;
    private int bottomY;
    private int topY;

    /**
     * Set true to perform pre-processing on noise values, adjusting them to increase ...
     * ... headroom in the y direction.
     */
    private boolean enableYAdjust;

    /** Adjustment value for the block immediately above. Must be between 0 and 1.0 */
    private float yAdjustF1;

    /** Adjustment value for the block two blocks above. Must be between 0 and 1.0 */
    private float yAdjustF2;

    public CaveCarver(final CaveCarverBuilder builder) {
        settings = builder.getSettings();
        noiseGen = new NoiseGen(
            settings.getSeed(),
            settings.isFastNoise(),
            settings.getNoiseSettings(),
            settings.getNumGens(),
            settings.getyCompression(),
            settings.getXzCompression()
        );
        surfaceCutoff = builder.getSurfaceCutoff();
        bottomY = builder.getBottomY();
        topY = builder.getTopY();
        enableYAdjust = builder.isEnableYAdjust();
        yAdjustF1 = builder.getyAdjustF1();
        yAdjustF2 = builder.getyAdjustF2();
        if (bottomY > topY) {
            BetterCaves.LOGGER.warn("Warning: Min altitude for caves should not be greater than max altitude.");
            BetterCaves.LOGGER.warn("Using default values...");
            this.bottomY = 1;
            this.topY = 80;
        }
    }

    public void carveColumn(IChunk chunk, BlockPos colPos, int topY, double[][] noises, BlockState liquidBlock, boolean flooded, BitSet carvingMask) {
        int localX = colPos.getX() & 0xF;
        int localZ = colPos.getZ() & 0xF;

        // Validate vars
        if (bottomY < 0) bottomY = 0;
        if (bottomY > 255) bottomY = 255;
        if (topY < 0) topY = 0;
        if (topY > 255) topY = 255;

        // Altitude at which caves start closing off so they aren't all open to the surface
        int transitionBoundary = topY - surfaceCutoff;

        // Validate transition boundary
        if (transitionBoundary < 1)
            transitionBoundary = 1;

        // Pre-compute thresholds to ensure accuracy during pre-processing
        Map<Integer, Float> thresholds = generateThresholds(topY, bottomY, transitionBoundary);

        // Do some pre-processing on the noises to facilitate better cave generation.
        // Basically this makes caves taller to give players more headroom.
        // See the javadoc for the function for more info.
        if (this.enableYAdjust)
            preprocessCaveNoiseCol(noises, topY, bottomY, thresholds, settings.getNumGens());

        BlockPos.Mutable localPos = new BlockPos.Mutable(localX, 1, localZ);

        // Dig out caves in this column, based on noise values
        for (int y = topY; y >= bottomY; y--) {
            if (y <= settings.getLiquidAltitude() && liquidBlock == null)
                break;

            double[] noiseBlock = noises[y - bottomY];
            boolean digBlock = true;

            for (double noise : noiseBlock) {
                if (noise < thresholds.get(y)) {
                    digBlock = false;
                    break;
                }
            }

            localPos.setPos(localX, y, localZ);

            // Dig out the block if it passed the threshold check, using the debug visualizer if enabled
            if (settings.isEnableDebugVisualizer()) {
                CarverUtils.debugCarveBlock(chunk, localPos, settings.getDebugBlock(), digBlock);
            }
            else if (digBlock) {
                if (flooded) {
                    CarverUtils.carveFloodedBlock(chunk, new Random(), localPos, liquidBlock, settings.getLiquidAltitude(), settings.isReplaceFloatingGravel(), carvingMask);
                } else {
                    CarverUtils.carveBlock(chunk, localPos, liquidBlock, settings.getLiquidAltitude(), settings.isReplaceFloatingGravel(), carvingMask);
                }
            }
        }
    }

    /**
     * Preprocessing performed on a column of noise to adjust its values before comparing them to the threshold.
     * This function adjusts the noise value of blocks based on the noise values of blocks below.
     * This has the effect of raising the ceilings of caves, giving the player more headroom.
     * Big shoutouts to the guys behind Worley's Caves for this great idea.
     * @param noises The column of noises as a map, mapping the y-coordinate of a block to its NoiseTuple
     * @param topY Top y-coordinate of the noise column
     * @param bottomY Bottom y-coordinate of the noise column
     * @param thresholds Map of y-coordinates to noise thresholds. This is the output of the generateThresholds method.
     * @param numGens Number of noise values to create per block. This is equal to the number of floats held
     *                in each NoiseTuple for each block in the noise column.
     */
    private void preprocessCaveNoiseCol(double[][] noises, int topY, int bottomY, Map<Integer, Float> thresholds, int numGens) {
        /* Adjust simplex noise values based on blocks above in order to give the player more headroom */
        for (int y = topY; y >= bottomY; y--) {
            int yIndex = y - bottomY;
            double[] noiseBlock = noises[yIndex];
            float threshold = thresholds.get(y);

            boolean valid = true;
            for (double noise : noiseBlock) {
                if (noise < threshold) {
                    valid = false;
                    break;
                }
            }

            // Adjust noise values of blocks above to give the player more head room
            if (valid) {
                float f1 = yAdjustF1;
                float f2 = yAdjustF2;

                // Adjust block one above
                if (y < topY) {
                    double[] tupleAbove = noises[yIndex + 1];
                    for (int i = 0; i < numGens; i++) {
                        tupleAbove[i] = ((1 - f1) * tupleAbove[i]) + (f1 * noiseBlock[i]);
                    }
                }

                // Adjust block two above
                if (y < topY - 1) {
                    double[] tupleTwoAbove = noises[yIndex + 2];
                    for (int i = 0; i < numGens; i++) {
                        tupleTwoAbove[i] = ((1 - f2) * tupleTwoAbove[i]) + (f2 * noiseBlock[i]);
                    }
                }
            }
        }
    }

    /**
     * Generate a map of y-coordinates to thresholds for a column of blocks.
     * This is useful because the threshold will decrease near the surface, and it is useful (and more accurate)
     * to have a precomputed threshold value when doing y-adjustments for caves.
     * @param topY Top y-coordinate of the column
     * @param bottomY Bottom y-coordinate of the column
     * @param transitionBoundary The y-coordinate at which the caves start to close off
     * @return Map of y-coordinates to noise thresholds
     */
    private Map<Integer, Float> generateThresholds(int topY, int bottomY, int transitionBoundary) {
        Map<Integer, Float> thresholds = new HashMap<>();
        for (int realY = bottomY; realY <= topY; realY++) {
            float noiseThreshold = settings.getNoiseThreshold();
            if (realY >= transitionBoundary)
                noiseThreshold *= (1 + .3f * ((float)(realY - transitionBoundary) / (topY - transitionBoundary)));
            thresholds.put(realY, noiseThreshold);
        }

        return thresholds;
    }

    public NoiseGen getNoiseGen() {
        return noiseGen;
    }

    public CarverSettings getSettings() {
        return settings;
    }

    public int getPriority() {
        return settings.getPriority();
    }

    public int getBottomY() {
        return this.bottomY;
    }

    public int getTopY() {
        return this.topY;
    }
}
