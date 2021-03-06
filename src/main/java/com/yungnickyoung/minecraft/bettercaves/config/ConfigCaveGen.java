package com.yungnickyoung.minecraft.bettercaves.config;

import com.yungnickyoung.minecraft.bettercaves.config.cave.*;
import com.yungnickyoung.minecraft.bettercaves.config.cavern.ConfigFlooredCavern;
import com.yungnickyoung.minecraft.bettercaves.config.cavern.ConfigLiquidCavern;
import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigCaveGen {
    public final ConfigCubicCave configCubicCave;
    public final ConfigSimplexCave configSimplexCave;
    public final ConfigLiquidCavern configLiquidCavern;
    public final ConfigFlooredCavern configFlooredCavern;
    public final ConfigWaterRegions configWaterRegions;
    public final ConfigVanillaCave configVanillaCave;

    public final ForgeConfigSpec.ConfigValue<String> caveRegionSize;
    public final ForgeConfigSpec.ConfigValue<String> cavernRegionSize;
    public final ForgeConfigSpec.ConfigValue<Integer> surfaceCutoff;
    public final ForgeConfigSpec.ConfigValue<Integer> maxCaveAltitude;

    public ConfigCaveGen(final ForgeConfigSpec.Builder builder) {
        builder.push("Cave Generation");

        caveRegionSize = builder
                .comment(" Determines how large cave regions are." +
                        "\n Larger Region Size = more cave interconnectivity for a given area, but possibly less variation." +
                        "\n Accepted values: Small, Medium, Large, ExtraLarge" +
                        "\n Default: Large")
                .worldRestart()
                .define("Cave Region Size", "Small");

        cavernRegionSize = builder
                .comment(" Determines how large cavern regions are. This affects the average size of caverns." +
                        "\n Accepted values: Small, Medium, Large, ExtraLarge" +
                        "\n Default: Small")
                .worldRestart()
                .define("Cavern Region Size", "Small");

        surfaceCutoff = builder
                .comment(" The depth from a given point on the surface (or the Max Cave Altitude, whichever is lower) at which caves start to close off." +
                        "\n Decrease this to create more cave openings in the sides of mountains." +
                        "\n Increase to create less above-surface openings." +
                        "\n Default: 10")
                .worldRestart()
                .defineInRange("Cave Surface Cutoff Depth", 10, 0, 255);

        maxCaveAltitude = builder
                .comment(" The maximum altitude at which caves can generate.\n Default: 128")
                .worldRestart()
                .defineInRange("Max Cave Altitude", 80, 0, 255);

        configCubicCave = new ConfigCubicCave(builder);
        configSimplexCave = new ConfigSimplexCave(builder);
        configFlooredCavern = new ConfigFlooredCavern(builder);
        configLiquidCavern = new ConfigLiquidCavern(builder);
        configVanillaCave = new ConfigVanillaCave(builder);
        configWaterRegions = new ConfigWaterRegions(builder);

        builder.pop();
    }
}