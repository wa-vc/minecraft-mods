package com.worldanchor.structures.features;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.worldanchor.structures.Utility;
import com.worldanchor.structures.processors.ChestLootStructureProcessor;
import com.worldanchor.structures.processors.RandomDeleteStructureProcessor;
import com.worldanchor.structures.processors.RuinsStructureProcessor;
import net.minecraft.block.Blocks;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

import static com.worldanchor.structures.Server.MODID;
import static com.worldanchor.structures.Utility.registerStructure;

public class GhastTowerFeature extends Utility.ModStructureFeature {
    public static Identifier ID = new Identifier(MODID + ":ghast-tower");

    public static final StructureProcessorList PROCESSOR_LIST = BuiltinRegistries.add(
            BuiltinRegistries.STRUCTURE_PROCESSOR_LIST, MODID + ":ghast-tower-processor-list", new StructureProcessorList(
                    Arrays.asList(
                            new RandomDeleteStructureProcessor(0.05f, true, Arrays.asList(
                                    Blocks.SPAWNER.getDefaultState()
                            )),
                            new ChestLootStructureProcessor(ID.getPath())
                    ))
    );
    public static StructurePool STRUCTURE_POOLS = StructurePools.register(
            new StructurePool(
                    ID, new Identifier("empty"),
                    ImmutableList.of(
                            // Use ofProcessedSingle to add processors or just ofSingle to add elements without processors
                            Pair.of(StructurePoolElement.ofProcessedSingle(MODID + ":ghast-tower",
                                    PROCESSOR_LIST), 1)
                    ),
                    StructurePool.Projection.RIGID
            )
    );
    public static final StructureFeature<StructurePoolFeatureConfig> DEFAULT =
            new GhastTowerFeature(StructurePoolFeatureConfig.CODEC);
    public static final ConfiguredStructureFeature<StructurePoolFeatureConfig,
            ? extends StructureFeature<StructurePoolFeatureConfig>> CONFIGURED
            = DEFAULT.configure(new StructurePoolFeatureConfig(() -> STRUCTURE_POOLS, 1));
    static {
        registerStructure(ID, DEFAULT, GenerationStep.Feature.VEGETAL_DECORATION,
                48, 32, 120915301, CONFIGURED, false);
        Registry.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, ID, CONFIGURED);
    }


    public GhastTowerFeature(Codec<StructurePoolFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public @Nullable Utility.PlacementData shouldStartAt(DynamicRegistryManager dynamicRegistryManager,
            ChunkGenerator generator, BiomeSource biomeSource, StructureManager manager, long worldSeed, ChunkPos pos,
            Biome biome, int referenceCount, ChunkRandom random, StructureConfig structureConfig,
            StructurePoolFeatureConfig config, HeightLimitView world, BlockRotation rotation, int xMod, int zMod) {
        int x = pos.getStartX(), z = pos.getStartZ();
        Structure mask = manager.getStructure(new Identifier(MODID + ":" + ID.getPath() + "-mask")).get();
        for (int y = 15; y < 35; y++) {
            if (Utility.TestStructureMask(generator, world, mask, new BlockPos(x, y, z), xMod, zMod))
                return new Utility.PlacementData(new BlockPos(x, y, z), rotation);
        }
        // Structure can't generate over here, return false
        return null;
    }
}


