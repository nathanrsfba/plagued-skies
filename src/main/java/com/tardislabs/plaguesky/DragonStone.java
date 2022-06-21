package com.tardislabs.plaguesky;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class DragonStone extends BlockFalling
{
	public DragonStone()
	{
		super( Material.ROCK );
		setUnlocalizedName( PlagueSky.MODID + "." + "dragonstone" );
		setCreativeTab( CreativeTabs.MISC );
		setTickRandomly( true );
		setRegistryName( "dragonstone" );
	}
	
	public void updateTick( World world, BlockPos pos, IBlockState state, 
			Random rand ) 
	{
		// Call this so the block falls
		super.updateTick( world, pos, state, rand );
		// Don't decay if we're not on something solid
		if( pos.getY() > 0 &&
				world.getBlockState( 
						new BlockPos( pos.getX(), pos.getY() - 1, pos.getZ())
						).getBlock() == Blocks.AIR ) return; 
				
		if( rand.nextInt( 100 ) >= Config.orePercent ) return;
		IBlockState ore = Config.skinOreBlocks.elementAt( rand.nextInt( Config.skinOreBlocks.size() ));
		world.setBlockState( pos, ore );
	}
}	
