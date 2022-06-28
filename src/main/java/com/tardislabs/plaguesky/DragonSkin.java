package com.tardislabs.plaguesky;

import java.util.List;
import java.util.Random;
import java.util.Vector;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;



public class DragonSkin extends Block
{
	/**
	 * Game time when plague last actually spread
	 */
	public static long lastSpread = 0;
	/**
	 * Game time when plague was last checked for spread
	 */
	public static long lastSpreadCheck = 0;
	/**
	 * Number of growths to perform at next update
	 */
	public static long growthQueue = 0;
	/**
	 * Time of next decay
	 */
	public static long nextDecay = 0;
	/**
	 * Number of decays since decay started
	 */
	public static long decayCount = 0;

	public DragonSkin()
	{
		
        super( Properties.create( Material.ROCK )
        		.hardnessAndResistance( -1.0F, 3600000.0F ) // Unbreakable, like Bedrock
        		.noDrops()
        		.setAllowsSpawn( DragonSkin::neverAllowSpawn )
        		);
        MinecraftForge.EVENT_BUS.register( this );
    }

	/**
	 * Enable random tick updates for this block type
	 */
	@Override
	public boolean ticksRandomly( BlockState state ) 
	{
		return true;
	}

	/**
	 * Spread or decay (depending on heal mode) when the block ticks
	 */
	@Override
	public void tick( BlockState state, ServerWorld world, BlockPos pos, Random rand ) 
	{
		// Block doesn't have a .tick, so nevermind this
		// super.tick( state, worldIn, pos, rand );
		
		long now = world.getGameTime();
		PlagueSky.mutter( "Tick at " + now );

		Data data = world.getSavedData().getOrCreate( Data::create, PlagueSky.MODID );
		
		if( data.isHealing() )
		{
			if( now < nextDecay ) return;
			if( rand.nextInt( 100 ) >= Config.COMMON.decayPercent.get() ) return;

			if( rand.nextInt( 100 ) < Config.COMMON.sloughPercent.get() )
			{
				world.setBlockState( pos, BlockRegistry.dragonStone.get().getDefaultState() );
			}
			else
			{
				world.setBlockState( pos, Blocks.AIR.getDefaultState() );
			}
			return;
		}
			
			/*
			if( Config.COMMON.patchyDecay.get() )
			{
				/* When a block decays, schedule its neighbors to decay. When this
				 * happens recursively, blocks will decay in 'patches', rather than
				 * individual blocks.
				 * 
				 * There is only a 50% chance for a given neighbor to have a decay
				 * scheduled. This gives the patches a more 'organic' shape
				 * ///
				 // For now we're just going to disable all this, since the effect
				 // doesn't really seem to work properly
				 
				if( rand.nextBoolean() ) world.scheduleBlockUpdate( new BlockPos( pos.getX() + 1, pos.getY(), pos.getZ() ), this, 1, 1 );
				if( rand.nextBoolean() ) world.scheduleBlockUpdate( new BlockPos( pos.getX() - 1, pos.getY(), pos.getZ() ), this, 1, 1 );
				if( rand.nextBoolean() ) world.scheduleBlockUpdate( new BlockPos( pos.getX(), pos.getY(), pos.getZ() + 1 ), this, 1, 1 );
				if( rand.nextBoolean() ) world.scheduleBlockUpdate( new BlockPos( pos.getX(), pos.getY(), pos.getZ() - 1 ), this, 1, 1 );
				*  //
				// /fill ~-32 255 ~-32 ~32 255 ~32 plaguesky:dragonskin
			}

			decayCount++;
			if( decayCount > Config.COMMON.spreadCap.get() && 
					Config.COMMON.spreadCap.get() != 0 )
			{
				nextDecay = now + Config.COMMON.spreadDelay.get() * 20;
				decayCount = 0;
				PlagueSky.LOGGER.info( "Decay limit exceeded; waiting " + 
						Config.COMMON.spreadDelay.get() + "s" );
				
			}
			return;
			*/
		
		lastSpreadCheck = now;
		
		// int batchSize = Config.spreadBatch;
		// if( rand.nextInt( batchSize ) > 0 ) return;
		// PlagueSky.mutter( "Pct:" + Config.growthPercent );
		long growths = Config.COMMON.growthPercent.get() / 100;
		// PlagueSky.mutter( "Growths:" + growths );
		int fraction = Config.COMMON.growthPercent.get() % 100;
		
		if( rand.nextInt( 100 ) < fraction ) growths++;
		growthQueue += growths;
		PlagueSky.mutter( "Queueing " + growths + " growths" );
		
        // PlagueSky.mutter( "lastSpread: " + lastSpread + "; spreadDelay: " + Config.spreadDelay + "; Now: " + now ); 
		if( lastSpread + Config.COMMON.spreadDelay.get() * 20 > now ) return; 
		
		// growths *= batchSize;
		
		// Don't try to spread into unloaded chunks
        if( !world.isAreaLoaded( pos, 1 )) return;

        growths = growthQueue;
        if( Config.COMMON.spreadCap.get() != 0 && growths > Config.COMMON.spreadCap.get() )
        {
        	growths = Config.COMMON.spreadCap.get();
        }
        
        PlagueSky.mutter( "Growing " + growths + " blocks queued over past " + 
        		(now - lastSpread) + " ticks" );
		for( int i = 0; i < growths; i++ )
		{
			pos = spread( world, pos, rand );
		}

		growthQueue = 0;
        lastSpread = now;
	}
	
	public BlockPos spread( ServerWorld world, BlockPos pos, Random rand )
	{
		PlagueSky.mutter( "Spreading skin at " + pos.getX() + "x" + pos.getY() + "x" + pos.getZ() );
		// Pick a random direction; N/E/S/W
		int dir = rand.nextInt( 4 );
		int dx = 0;
		int dz = 0;
		if( dir == 0 ) dx = 1;
		if( dir == 1 ) dx = -1;
		if( dir == 2 ) dz = 1;
		if( dir == 3 ) dz = -1;
		
		BlockPos newPos = new BlockPos( pos.getX() + dx, pos.getY(), pos.getZ() + dz );
		world.setBlockState( newPos, BlockRegistry.dragonSkin.get().getDefaultState() );
		
		return newPos;
	}

	/**
	 * Seed new growths if no existing skins have spread recently
	 * 
	 * @param event Information about the event
	 */
    @SubscribeEvent	
	public void onWorldTick( TickEvent.WorldTickEvent event )	
	{
	    String status =	doTick( event );
		// if( !status.isEmpty() ) PlagueSky.LOGGER.info( status );
	}
	
    /**
     * Do the actual seeding for onWorldTick
     * 
     * @param event 	Information about the event
     * @return 			Status string for debugging purposes
     */
	private String doTick( TickEvent.WorldTickEvent event )
	{
		String status = "";
		
		// I hope this is correct.
		ServerWorld world = (ServerWorld) event.world;
		
		// Prevent any of this shit from running more than once a second to minimize lag
		if( !(world.getGameTime() % 20 == 0 &&
				event.phase == TickEvent.Phase.START) ) return status;
		if( !world.getDimensionKey().equals( World.OVERWORLD ))
		{
			return "";
		}
		status = status + "Tick.";
		Data data = world.getSavedData().getOrCreate( Data::create, PlagueSky.MODID );
		if( data.isHealing() ) return status + " Healing.";
		status = status + " Not healing.";
		long time = event.world.getGameTime(); 
		if( lastSpreadCheck == 0 )
		{
			lastSpreadCheck = time;
			return status + " First check.";
		}

		// Check if it's time for a new seed
		if( time < lastSpreadCheck + Config.COMMON.seedTime.get() * 20 ) 
			return status + " Not time. (Spread " + 
			((time - lastSpreadCheck) / 20) + "s ago)"; 
		
		status = status + " Time to seed.";

		lastSpreadCheck = time;
		/*
		 * Ok, new strategy for seeding new growths. Pick a player in the overworld
		 * (if any), pick some random blocks within X radius, and seed them.
		 * 
		 * We used to be able to pick loaded chunks in the overworld, but that
		 * functionality is now behind private methods, and I can't be fucked to
		 * try to hack around it with reflection.
		 * 
		 * TLDR: private is evil
		 */

		List<ServerPlayerEntity> players = world.getServer().getPlayerList().getPlayers();
		Vector<ServerPlayerEntity> online = new Vector<ServerPlayerEntity>(); 
		
		for( ServerPlayerEntity player: players )
		{
			if( player.world.getDimensionKey().equals( World.OVERWORLD )) {
				online.add( player );
				PlagueSky.mutter( "Considering player " + player.toString() ); 
			}
		}

		if( online.size() == 0 ) return status + " No players in overworld";
		
		ServerPlayerEntity player = online.get( event.world.rand.nextInt( online.size() ));
		int r = Config.COMMON.seedRadius.get();
		for( int i = 0; i < Config.COMMON.seedChunks.get(); i++ ) 
		{
			BlockPos pos = player.getPosition();
			int x = pos.getX();
			int z = pos.getZ();
			
			int tx = x + event.world.rand.nextInt( 2 * r ) - r;
			int tz = z + event.world.rand.nextInt( 2 * r ) - r;
			
			world.setBlockState( new BlockPos( tx, 255, tz ), 
					BlockRegistry.dragonSkin.get().getDefaultState() );
		}
		
		return status + " Seeded chunks";
	}

	/**
	 * Reset internal data when the world is loaded.
	 * 
	 * This prevents stale data if the player switches worlds
	 * 
	 * @param event Information about the event
	 */
	@SubscribeEvent
	public void OnWorldLoad( WorldEvent.Load event )
	{
		if( ((World) event.getWorld()).getDimensionKey().equals( World.OVERWORLD ))
		{
			lastSpread = 0;
			lastSpreadCheck = 0;
			growthQueue = 0;
			nextDecay = 0;
			
			PlagueSky.mutter( "Resetting internal data " );
		}
	}
	
	
	/*
	 * Fun fact:
	 * Mobs can spawn above the build limit!
	 */

	/**
	 * Prevent mobs from spawning on this block, ever
	 */
	private static Boolean neverAllowSpawn(
			BlockState state, IBlockReader reader, BlockPos pos, EntityType<?> entity ) 
	{
		return (boolean)false;
	}

	
	
}
