package com.tardislabs.plaguesky.blocks;

import com.tardislabs.plaguesky.Config;
import com.tardislabs.plaguesky.PlagueSky;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.BlockTags;

import java.util.*;

public class DragonStone extends FallingBlock {

    Vector<BlockState> oreBlocks = null;

    public DragonStone(Properties props) {
        super(props);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {
        // Call this so the block falls
        super.tick(state, worldIn, pos, rand);

        // Don't decay if we're not on something solid
        if (pos.getY() > 0 && worldIn.getBlockState(pos.below()).getBlock() == Blocks.AIR)
            return;

        // Random chance check
        if (rand.nextInt(100) >= Config.COMMON.orePercent.get())
            return;

        // Select a random entry from the drop list
        List<String> dropList = (List<String>) Config.COMMON.dropBlocks.get();
        if (dropList.isEmpty())
            return;

        String entry = dropList.get(rand.nextInt(dropList.size()));
        PlagueSky.mutter(entry);

        BlockState newState;

        if (entry.startsWith("#")) {
            // It's a tag
            ResourceLocation tagId = ResourceLocation.tryParse(entry.substring(1)); // remove '#'
            Optional<HolderSet.Named<Block>> tag = BuiltInRegistries.BLOCK.getTag(BlockTags.create(tagId));
            newState = tag.flatMap(t -> t.getRandomElement(rand))
                    .map(holder -> holder.value().defaultBlockState())
                    .orElse(Blocks.AIR.defaultBlockState());
        } else {
            // It's a direct block ID
            ResourceLocation blockId = ResourceLocation.tryParse(entry);
            Block block = BuiltInRegistries.BLOCK.get(blockId);
            newState = block != null ? block.defaultBlockState() : Blocks.AIR.defaultBlockState();
        }

        worldIn.setBlock(pos, newState, 3);
    }
}
