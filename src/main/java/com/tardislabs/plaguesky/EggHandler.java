package com.tardislabs.plaguesky;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EggHandler
{
	public EggHandler()
	{
        MinecraftForge.EVENT_BUS.register( this );
	}
	@SubscribeEvent
	public void onPlaceBlock( BlockEvent.EntityPlaceEvent event )
	{
		// Make sure we're running on the server side
		if( event.getWorld().isRemote() ) return;
		
		int x = event.getPos().getX();
		int y = event.getPos().getY();
		int z = event.getPos().getZ();
		
		
		ServerWorld world = (ServerWorld) event.getWorld();
		if( !world.getDimensionKey().equals( World.OVERWORLD )) return;
		if( event.getPlacedBlock().getBlock().equals( Blocks.DRAGON_EGG ))
		{
			Data data = world.getSavedData().getOrCreate( Data::create, PlagueSky.MODID );
			if( data.isHealing() ) return;
			data.setHealing();
			List<ServerPlayerEntity> players = world.getServer().getPlayerList().getPlayers();
			for( ServerPlayerEntity player: players ) 
			{
				player.sendStatusMessage( new StringTextComponent( "The healing has begun." ), false );
			}
		}
		else if( event.getPlacedBlock().getBlock().equals( Blocks.BEACON ))
		{
			for( int y1 = y + 1; y1 < 256; y1++ )
			{
				Block b = world.getBlockState( new BlockPos( x, y1, z )).getBlock();
				// PlagueSky.mutter( "Checking " + b.toString() + "at Y" + y1 );
				if( b.equals( BlockRegistry.dragonSkin.get() ))
				{
					// PlagueSky.mutter( "Hit dragonskin" );
					
					int r = Config.COMMON.beaconBlastRadius.get();
					for( int x1 = x - r; x1 < x + r; x1++ )
					{
						for( int z1 = z - r; z1 < z + r; z1++ )
						{
							// PlagueSky.mutter( "Checking " + x1 + "x" + z1 );
							if( Math.pow( x1 - x, 2 ) + Math.pow( z1 - z , 2 ) < Math.pow( r, 2 ))
							{
								Block b1 = world.getBlockState( new BlockPos( x1, y1, z1 ) ).getBlock();
								// PlagueSky.mutter( "In circle: " + b1.toString() );
								if( b1.equals( BlockRegistry.dragonSkin.get() ))
								{
									// PlagueSky.mutter( "Is dragonskin" );
									world.setBlockState( new BlockPos( x1, y1, z1 ),
											Blocks.AIR.getDefaultState() );
								}
										
							}
						}
						
					}
					return;
				}
				else if( !b.equals( Blocks.AIR ))
				{
					// PlagueSky.mutter( "Hit a block" );
					return;
				}
			}
			// PlagueSky.mutter( "Hit sky" );
		}
	}

	@SubscribeEvent
	public void onBreakBlock( BlockEvent.BreakEvent event )
	{
		// Make sure we're running on the server side
		if( event.getWorld().isRemote() ) return;

		ServerWorld world = (ServerWorld) event.getWorld();
		if( !world.getDimensionKey().equals( World.OVERWORLD )) return;
		if( !event.getState().getBlock().equals( Blocks.DRAGON_EGG )) return;

		Data data = world.getSavedData().getOrCreate( Data::create, PlagueSky.MODID );
		if( !data.isHealing() ) return;
		data.setHealing( false );
		List<ServerPlayerEntity> players = world.getServer().getPlayerList().getPlayers();
		for( ServerPlayerEntity player: players ) 
		{
			player.sendStatusMessage( new StringTextComponent( "The healing has stopped." ), false );
		}
	}

}
