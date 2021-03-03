package com.tardislabs.plaguesky;

import java.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;


public class Config 
{
	private static final String CATEGORY_GENERAL = "general";

	public static int seedTime;
	public static int seedChunks;
	public static int growthPercent;
	public static int decayPercent;
	public static int sloughPercent;
	public static int orePercent;
	// public static int spreadBatch;
	public static int spreadDelay;
	public static int spreadCap;
	public static boolean healDefault;
	public static boolean debug;
	public static boolean patchyDecay;
	public static String[] skinOres;
	public static Vector<IBlockState> skinOreBlocks;;

	private static String[] defaultOres = {
			"oreGold",
			"oreIron",
			"oreDiamond" 
	};


	public static void readConfig() 
	{
		Configuration cfg = CommonProxy.config;
		try 
		{
			cfg.load();
			initGeneralConfig(cfg);
			// PlagueSky.mutter( "Pct:" + growthPercent );
		} 
		catch( Exception e1 ) 
		{
			PlagueSky.logger.error( "Error loading configuration: ", e1 );
		} 
		finally 
		{
			if( cfg.hasChanged() ) 
			{
				cfg.save();
			}
		}
	}

	private static void initGeneralConfig( Configuration cfg ) 
	{
		cfg.addCustomCategoryComment( CATEGORY_GENERAL, "General configuration" );
		seedTime = cfg.getInt( 
				"seedTime", CATEGORY_GENERAL, 60, 1, Integer.MAX_VALUE,
				"How long to wait (in seconds) before seeding new growths if no existing spread is detected" );
		seedChunks = cfg.getInt( 
				"seedChunks", CATEGORY_GENERAL, 50, 1, Integer.MAX_VALUE,
				"How many chunks to seed when seeding new growths" );
		growthPercent = cfg.getInt( 
				"growthPercent", CATEGORY_GENERAL, 100, 0, Integer.MAX_VALUE,
				"The speed at which dragonskin grows. 100 is roughly the speed of spreading grass" );
		/*
        spreadBatch = cfg.getInt( 
        		"spreadBatch", CATEGORY_GENERAL, 1, 1, Integer.MAX_VALUE,
        		"Raising this will cause dragonskin to spread less frequently, but spread to more blocks when it does. The overall growth rate should be roughly the same, but it might help with lag." );
		 */
		spreadDelay = cfg.getInt( 
				"spreadDelay", CATEGORY_GENERAL, 60, 0, Integer.MAX_VALUE,
				"This causes plague block spread to be batched and performed at once, with an interval of this many seconds. The overall growth rate should be roughly the same, but it might help with lag." );
		spreadCap = cfg.getInt( 
				"spreadCap", CATEGORY_GENERAL, 500, 0, Integer.MAX_VALUE,
				"The maximum number of block spreads performed in a single update. This limits the total spread to an average of spreadCap per spreadDelay seconds. Lower this if you're getting a lot of lag/dropped ticks on spreading. (0=No limit)" );
		decayPercent = cfg.getInt( 
				"decayPercent", CATEGORY_GENERAL, 100, 0, 100,
				"The speed at which dragonskin decays in heal mode. 100 is roughtly the speed that grass dies when covered. If decaying too much at once lags, try lowering this." );
		sloughPercent = cfg.getInt( 
				"sloughPercent", CATEGORY_GENERAL, 10, 0, 100,
				"How often (percentage) a decaying skin block should drop a piece of dragonsscale. Too high might lag the server." );
		orePercent = cfg.getInt( 
				"orePercent", CATEGORY_GENERAL, 1, 0, 100,
				"How quickly a decaying dragonscale block should turn into an ore." );
		healDefault = cfg.getBoolean( "healDefault", CATEGORY_GENERAL, false, 
				"Start the world in heal mode. Perhaps you want to configure your pack to start the plague when a certain event happens. (The command /healplague off can start it.)" );
		patchyDecay = cfg.getBoolean( "patchyDecay", CATEGORY_GENERAL, true, 
				"When dragonskin blocks decay, they decay in patches of connected blocks, rather than individual blocks" );
		skinOres = cfg.getStringList( "skinOres", CATEGORY_GENERAL, defaultOres,
				"The ores that might be released from Dragonscale. This can be a resource ID (minecraft:stone) or an oredict entry (oreIron)" );
		debug = cfg.getBoolean( "debug", CATEGORY_GENERAL, false, 
				"Display debugging data in the console" );
	}

	/*
	 * Block.getStateFromMeta is marked as deprecated, but there seems to
	 * be no replacement.
	 */
	@SuppressWarnings("deprecation")
	public static void lookupBlocks()
	{
		skinOreBlocks = new Vector<IBlockState>();

		/* Convert ore texts to blocks */
		for( String ore: Config.skinOres )
		{
			// Try to get item by ID first
			IBlockState block = null;
			Block base = Block.getBlockFromName( ore );
			if( base != null ) block = base.getDefaultState();

			if( block == null )
			{
				// See if this is in item:metadata format
				int split = ore.lastIndexOf( ":" );
				if( split >= 0 )
				{
					String item = ore.substring( 0,  split );
					String meta = ore.substring( split + 1 );
					base = Block.getBlockFromName(item);
					if( base != null )
					{
						try
						{
							block = base.getStateFromMeta( Integer.parseInt( meta ));
						}
						finally
						{
						}
					}
				}
			}

			// Else, try by oredict
			if( block == null && OreDictionary.doesOreNameExist( ore ))
			{
				ItemStack item = OreDictionary.getOres( ore ).get( 0 );
				block = Block.getBlockFromItem( item.getItem() ).getDefaultState();
			}
			if( block != null )	
			{
				skinOreBlocks.add( block );
			}
			else
			{
				PlagueSky.mutter( "Don't know what ore " + ore + " is" );
			}


		}
	}
}