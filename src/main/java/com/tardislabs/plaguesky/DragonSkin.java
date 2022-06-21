package com.tardislabs.plaguesky;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

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
		super( Material.ROCK );
		setBlockUnbreakable();
		setUnlocalizedName( PlagueSky.MODID + "." + "dragonskin" );
		setTickRandomly( true );
		setCreativeTab( CreativeTabs.MISC );
		setRegistryName( "dragonskin" );
	}

	/*
	 * Fun fact:
	 * Mobs can spawn above the build height!
	 */
	public boolean canCreatureSpawn( IBlockState state, IBlockAccess world, BlockPos pos, SpawnPlacementType type ) 
	{
		return false;
	}



	public void updateTick( World world, BlockPos pos, IBlockState state, 
			Random rand ) 
	{
		long now = world.getTotalWorldTime();
		
		if( Data.get( world ).isHealing() )
		{
			if( now < nextDecay ) return;
			if( rand.nextInt( 100 ) >= Config.decayPercent ) return;

			if( rand.nextInt( 100 ) < Config.sloughPercent )
			{
				world.setBlockState( pos, com.tardislabs.plaguesky.Blocks.DRAGONSTONE.getDefaultState() );
			}
			else
			{
				world.setBlockState( pos, Blocks.AIR.getDefaultState() );
			}
			
			if( Config.patchyDecay )
			{
				/* When a block decays, schedule its neighbors to decay. When this
				 * happens recursively, blocks will decay in 'patches', rather than
				 * individual blocks.
				 * 
				 * There is only a 50% chance for a given neighbor to have a decay
				 * scheduled. This gives the patches a more 'organic' shape
				 */
				if( rand.nextBoolean() ) world.scheduleBlockUpdate( new BlockPos( pos.getX() + 1, pos.getY(), pos.getZ() ), this, 1, 1 );
				if( rand.nextBoolean() ) world.scheduleBlockUpdate( new BlockPos( pos.getX() - 1, pos.getY(), pos.getZ() ), this, 1, 1 );
				if( rand.nextBoolean() ) world.scheduleBlockUpdate( new BlockPos( pos.getX(), pos.getY(), pos.getZ() + 1 ), this, 1, 1 );
				if( rand.nextBoolean() ) world.scheduleBlockUpdate( new BlockPos( pos.getX(), pos.getY(), pos.getZ() - 1 ), this, 1, 1 );
				// /fill ~-32 255 ~-32 ~32 255 ~32 plaguesky:dragonskin
			}

			decayCount++;
			if( decayCount > Config.spreadCap && Config.spreadCap != 0 )
			{
				nextDecay = now + Config.spreadDelay * 20;
				decayCount = 0;
				PlagueSky.mutter( "Decay limit exceeded; waiting " + Config.spreadDelay + "s" );
				
			}
			return;
		}
		
		lastSpreadCheck = now;
		
		// int batchSize = Config.spreadBatch;
		// if( rand.nextInt( batchSize ) > 0 ) return;
		// PlagueSky.mutter( "Pct:" + Config.growthPercent );
		long growths = Config.growthPercent / 100;
		// PlagueSky.mutter( "Growths:" + growths );
		int fraction = Config.growthPercent % 100;
		
		if( rand.nextInt( 100 ) < fraction ) growths++;
		growthQueue += growths;
		
        // PlagueSky.mutter( "lastSpread: " + lastSpread + "; spreadDelay: " + Config.spreadDelay + "; Now: " + now ); 
		if( lastSpread + Config.spreadDelay * 20 > now ) return; 
		
		// growths *= batchSize;
		
		// Don't try to spread into unloaded chunks
        if( !world.isAreaLoaded( pos, 1 )) return;

        growths = growthQueue;
        if( Config.spreadCap != 0 && growths > Config.spreadCap )
        {
        	growths = Config.spreadCap;
        }
        
        PlagueSky.mutter( "Growing " + growths + " blocks queued over past " + (now - lastSpread) + " ticks" );
		for( int i = 0; i < growths; i++ )
		{
			pos = spread( world, pos, rand );
		}

		growthQueue = 0;
        lastSpread = now;
	}
	
	public BlockPos spread( World world, BlockPos pos, Random rand )
	{
		// PlagueSky.mutter( "Spreading skin at " + pos.getX() + "x" + pos.getY() + "x" + pos.getZ() );
		// Pick a random direction; N/E/S/W
		int dir = rand.nextInt( 4 );
		int dx = 0;
		int dz = 0;
		if( dir == 0 ) dx = 1;
		if( dir == 1 ) dx = -1;
		if( dir == 2 ) dz = 1;
		if( dir == 3 ) dz = -1;
		
		BlockPos newPos = new BlockPos( pos.getX() + dx, pos.getY(), pos.getZ() + dz );
		world.setBlockState( newPos, com.tardislabs.plaguesky.Blocks.DRAGONSKIN.getDefaultState() );
		
		return newPos;
	}

	/*
	 * Seed new growths if no existing skins have spread recently
	 */
	@SubscribeEvent
	public void onWorldTick( WorldTickEvent event )
	{
		String status =	doTick( event );
		// if( !status.isEmpty() ) PlagueSky.mutter( status );
	}
	
	@SubscribeEvent
	public void OnWorldLoad( WorldEvent.Load event )
	{
		if( event.getWorld().provider.getDimension() == 0 )
		{
			/* Reset all of this when the world is loaded, since we'll have
			 * stale data if the player has switched worlds */

			PlagueSky.mutter( "Resetting internal variables" );
			lastSpread = 0;
			lastSpreadCheck = 0;
			growthQueue = 0;
			nextDecay = 0;
			long decayCount = 0;
		}
	}
	
	private String doTick( WorldTickEvent event )
	{
		String status = "";
		/* Prevent any of this shit from running more than once a second
		 * to minimize lag */
		if( !(event.world.getTotalWorldTime() % 20 == 0 &&
				event.phase == TickEvent.Phase.START) ) return ""; // status;
		if( event.world.provider.getDimension() != 0 ) return ""; // status + " Not overworld.";
		
		status = status + "Tick.";
		if( Data.get( event.world ).isHealing() ) return ""; // status + " Healing.";
		status = status + " Not healing.";
		long time = event.world.getTotalWorldTime(); 
		if( lastSpreadCheck == 0 )
		{
			lastSpreadCheck = time;
			return status + " First check.";
		}

		// Check if it's time for a new seed
		if( time < lastSpreadCheck + Config.seedTime * 20 ) return ""; // status + " Not time. (Spread " + ((time - lastSpreadCheck) / 20) + "s ago)"; 
		
		status = status + " Time to seed.";

		lastSpreadCheck = time;
		
		// Check if there's players in the overworld before seeding, so we
		// don't spam up the sky when nobody's around
		boolean owp = false;
		for( EntityPlayerMP player: event.world.getMinecraftServer().getPlayerList().getPlayers() )
		{
			int dim = player.getEntityWorld().provider.getDimension();
			if( dim == 0 )
			{
				owp = true;
				break;
			}
			// PlagueSky.mutter( "Player in world " + dim );
		}

		if( !owp ) return "";
		
		// Grab some chunks and seed them 
		IChunkProvider iprovider = event.world.getChunkProvider();
		if( !(iprovider instanceof ChunkProviderServer) ) return ""; // status + " Not on server side.";
		ChunkProviderServer provider = (ChunkProviderServer) iprovider;
		
		ArrayList<Chunk> chunks = new ArrayList<>( provider.getLoadedChunks().size() );
		for( Chunk c: provider.getLoadedChunks() )
		{
			if( c.getWorld().provider.getDimension() == 0 )
			{
				chunks.add( c );
			}
		}
		status = status + " " + chunks.size() + " candidate chunks.";
		Collections.shuffle( chunks );
		int seeded = 0;
		for( int i = 0; i < Config.seedChunks; i++ ) 
		{
			if( i >= chunks.size() ) break;
			Chunk c = chunks.get( i );
			seeded++;
			int x = event.world.rand.nextInt( 16 );
			int z = event.world.rand.nextInt( 16 );
			c.setBlockState( new BlockPos( x, 255, z ), com.tardislabs.plaguesky.Blocks.DRAGONSKIN.getDefaultState() );
		}
		PlagueSky.mutter( "Seeded " + seeded + " chunks" );
		
		return status;
	}


}