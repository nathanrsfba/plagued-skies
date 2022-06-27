package com.tardislabs.plaguesky;

import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;


public class DragonStone extends FallingBlock
{
	/**
	 * A list of blocks that decaying dragonskin might turn into
	 */
	Vector<BlockState> oreBlocks = null;
	
	public DragonStone()
	{
		
        super( Properties.create( Material.ROCK ));
    }

	@Override
	public boolean ticksRandomly( BlockState state ) 
	{
		return true;
	}

	@Override
	public void tick( BlockState state, ServerWorld worldIn, BlockPos pos, Random rand ) 
	{
		// Call this so the block falls
		super.tick( state, worldIn, pos, rand );
		// Don't decay if we're not on something solid
		if( pos.getY() > 0 && worldIn.getBlockState( 
						new BlockPos( pos.getX(), pos.getY() - 1, pos.getZ()) )
				.getBlock() == Blocks.AIR ) return; 

		if( rand.nextInt( 100 ) >= Config.COMMON.orePercent.get() ) return;

		getOres();
		BlockState ore = oreBlocks
				.elementAt( rand.nextInt( oreBlocks.size() ));

		worldIn.setBlockState( pos, ore );
		
	}
	
	/**
	 * Populate oreBlocks with a list of blocks that dragonskin might drop
	 */
	public void getOres()
	{
		if( oreBlocks != null ) return;
		oreBlocks = new Vector<BlockState>();

		
		List<? extends String> dropBlocks = Config.COMMON.dropBlocks.get();


		// Get a list of all tags
		List<? extends INamedTag<Block>> tags = BlockTags.getAllTags();
		Hashtable<String, INamedTag<Block>> tagList = 
				new Hashtable<String, INamedTag<Block>>();
		
		for( INamedTag<Block> tag: tags )
		{
			tagList.put( tag.getName().toString(), tag );
		}
		
		for( String b : dropBlocks )
		{
			if( tagList.containsKey( b ))
			{
				INamedTag<Block> tag = tagList.get( b );
				List<Block> blocks = tag.getAllElements();
				if( blocks.size() > 0 )
				{
					Block block = blocks.get( 0 );
					oreBlocks.add( block.getDefaultState() );
				}
				else
				{
					PlagueSky.LOGGER.warn( "Tag " + b + " does not contain any blocks" );
				}
			}
			else if( ForgeRegistries.BLOCKS
					.containsKey( new ResourceLocation( b )))
			{
				Block block =  ForgeRegistries.BLOCKS.getValue(
						new ResourceLocation( b ));
				oreBlocks.add( block.getDefaultState() );
			}
			else
			{
				PlagueSky.LOGGER.warn( "Can't resolve block " + b );
			}
		}

	}
}