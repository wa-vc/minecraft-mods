package com.worldanchor.structures.features;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.worldanchor.structures.Utility;
import com.worldanchor.structures.processors.OresStructureProcessor;
import com.worldanchor.structures.processors.RandomDeleteStructureProcessor;
import com.worldanchor.structures.processors.RuinsStructureProcessor;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
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

public class SilverfishNestFeature extends Utility.ModStructureFeature {
    public static Identifier ID = new Identifier(MODID + ":silverfish-nest");

    public static Pool<SpawnSettings.SpawnEntry> MONSTER_SPAWNS = Pool.of(new SpawnSettings.SpawnEntry(
            EntityType.SILVERFISH, 1, 1, 5));

    public static StructureProcessorList PROCESSOR_LIST = BuiltinRegistries.add(
            BuiltinRegistries.STRUCTURE_PROCESSOR_LIST, MODID + ":silverfish-nest-processor-list", new StructureProcessorList(
                    Arrays.asList(
                            new RandomDeleteStructureProcessor(0.15f, true, Arrays.asList(
                                    Blocks.SPAWNER.getDefaultState()
                            )),
                            new OresStructureProcessor(true, 0.2f, 0, 0, 0, 0,
                                    0, 0, 1, 0, 0),
                            new RuinsStructureProcessor(0, 0, 0, 0.75F)
                    ))
    );
    public static StructurePool STRUCTURE_POOLS = StructurePools.register(
            new StructurePool(
                    ID, new Identifier("empty"),
                    ImmutableList.of(
                            // Use ofProcessedSingle to add processors or just ofSingle to add elements without processors
                            Pair.of(StructurePoolElement.ofProcessedSingle(MODID + ":silverfish-nest",
                                    PROCESSOR_LIST), 1)
                    ),
                    StructurePool.Projection.RIGID
            )
    );
    public static StructureFeature<StructurePoolFeatureConfig> DEFAULT =
            new SilverfishNestFeature(StructurePoolFeatureConfig.CODEC);
    public static ConfiguredStructureFeature<StructurePoolFeatureConfig,
            ? extends StructureFeature<StructurePoolFeatureConfig>> CONFIGURED
            = DEFAULT.configure(new StructurePoolFeatureConfig(() -> STRUCTURE_POOLS, 1));
    static {
        registerStructure(ID, DEFAULT, GenerationStep.Feature.UNDERGROUND_DECORATION,
                32, 16, 502359193, false);
        Registry.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, ID, CONFIGURED);

    }

    public SilverfishNestFeature(Codec<StructurePoolFeatureConfig> codec) {
        super(codec, ID);
    }

    @Override
    public @Nullable Utility.PlacementData shouldStartAt(DynamicRegistryManager dynamicRegistryManager,
            ChunkGenerator generator, BiomeSource biomeSource, StructureManager manager, long worldSeed, ChunkPos pos,
            Biome biome, int referenceCount, ChunkRandom random, StructureConfig structureConfig,
            StructurePoolFeatureConfig config, HeightLimitView world, BlockRotation rotation, int xMod, int zMod) {
        int x = pos.getStartX(), z = pos.getStartZ();
        int randomY = random.nextInt(70) - 60;
        BlockPos structurePos = TestStructureMask(generator, world, new BlockPos(x, 29, z), xMod, zMod, randomY, randomY+1, 1,
                rotation);
        if (structurePos == null) return null;
        else return new Utility.PlacementData(structurePos, rotation);
    }

}


