package com.tardislabs.plaguesky;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EggHandler 
{
	@SubscribeEvent
	public void onPlayerInteract( PlaceEvent event )
	{
		World world = event.getWorld(); 
		if( world.isRemote ) return;
		if( world.provider.getDimension() != 0 ) return;
		if( event.getPlacedBlock().getBlock() == Blocks.DRAGON_EGG ) 
		{
			// PlagueSky.mutter( "Placed egg in overworld" );
			Data data = Data.get( event.getWorld() );
			if( data.isHealing() ) return;
			data.setHealing();
			for( EntityPlayerMP player: 
				event.getWorld().getMinecraftServer().getPlayerList().getPlayers() )
			{
				player.sendMessage( new TextComponentString( "The healing has begun." ));
			}
		}
		else if( event.getPlacedBlock().getBlock() == Blocks.BEACON ) 
		{
			BlockPos pos = event.getPos();
			int y = pos.getY();
			int x = pos.getX();
			int z = pos.getZ();
			
			for( int y1 = y + 1; y1 < 256; y1++ )
			{
				Block b = world.getBlockState( new BlockPos( x, y1, z )).getBlock();
				// PlagueSky.mutter( "Checking " + b.toString() + "at Y" + y1 );
				if( b.equals( com.tardislabs.plaguesky.Blocks.DRAGONSKIN ))
				{
					// PlagueSky.mutter( "Hit dragonskin" );
					
					int r = Config.beaconBlastRadius;
					for( int x1 = x - r; x1 < x + r; x1++ )
					{
						for( int z1 = z - r; z1 < z + r; z1++ )
						{
							// PlagueSky.mutter( "Checking " + x1 + "x" + z1 );
							if( Math.pow( x1 - x, 2 ) + Math.pow( z1 - z , 2 ) < Math.pow( r, 2 ))
							{
								Block b1 = world.getBlockState( new BlockPos( x1, y1, z1 ) ).getBlock();
								// PlagueSky.mutter( "In circle: " + b1.toString() );
								if( b1.equals( com.tardislabs.plaguesky.Blocks.DRAGONSKIN ))
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
}
