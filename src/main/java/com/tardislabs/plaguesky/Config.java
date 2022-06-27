package com.tardislabs.plaguesky;

import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.util.ResourceLocation;

public class Config 
{
	public static class Common
	{
		public final IntValue seedTime;
		public final IntValue seedChunks;
		public final IntValue seedRadius;
		public final IntValue growthPercent;
		public final IntValue decayPercent;
		public final IntValue sloughPercent;
		public final IntValue orePercent;
		// public static int spreadBatch;
		public final IntValue spreadDelay;
		public final IntValue spreadCap;
		public final BooleanValue healDefault;
		public final BooleanValue debug;
		public final BooleanValue patchyDecay;
		// public static String[] skinOres;
		// public static Vector<IBlockState> skinOreBlocks;
        public final ConfigValue<List<? extends String>> dropBlocks;

		public Common( ForgeConfigSpec.Builder builder ) 
		{
			builder.push( "General" );

			seedTime = builder
					.comment( "How long to wait (in seconds) before seeding new growths " +
							"if no existing spread is detected" )
					.defineInRange( "seedTime", 60, 1, Integer.MAX_VALUE );
			seedChunks = builder
					.comment( "How many chunks to seed when seeding new growths" )
					.defineInRange( "seedChunks", 50, 1, Integer.MAX_VALUE );
			seedRadius = builder
					.comment( "Maximum distance from a player to seed growths" )
					.defineInRange( "seedRadius", 128, 1, Integer.MAX_VALUE );
			growthPercent = builder 
					.comment( "The speed at which dragonskin grows. 100 is roughly the speed " +
							"of spreading grass" )
					.defineInRange( "growthPercent", 100, 0, Integer.MAX_VALUE );
			spreadDelay = builder 
					.comment( "This causes plague block spread to be batched and performed " +
							"at once, with an interval of this many seconds. The overall growth " +
							"rate should be roughly the same, but it might help with lag." )
					.defineInRange( "spreadDelay", 60, 0, Integer.MAX_VALUE );
			spreadCap = builder 
					.comment( "The maximum number of block spreads performed in a " +
							"single update. This limits the total spread to an average " +
							"of spreadCap per spreadDelay seconds. Lower this if you're " +
							"getting a lot of lag/dropped ticks on spreading. (0=No limit)" )
					.defineInRange( "spreadCap", 500, 0, Integer.MAX_VALUE );
			decayPercent = builder 
					.comment( "The speed at which dragonskin decays in heal mode. 100 is " +
							"roughtly the speed that grass dies when covered. If decaying " +
							"too much at once lags, try lowering this." )
					.defineInRange( "decayPercent", 100, 0, 100 );
			sloughPercent = builder
					.comment( "How often (percentage) a decaying skin block should drop a " +
							"piece of dragonsscale (as opposed to just disappearing). Too high " +
							"might lag the server." )
					.defineInRange( "sloughPercent", 10, 0, 100 );
			orePercent = builder 
					.comment( "How quickly a decaying dragonscale block should turn into an ore. " +
							"100 means 'the moment it lands'. Technically this is the chance it " +
							"has to change each time the block ticks." )
					.defineInRange( "orePercent", 10, 0, 100 );
			healDefault = builder
					.comment( "Start the world in heal mode. Perhaps you want to configure " +
							"your pack to start the plague when a certain event happens. " +
							"(The command /healplague off can start it.)" )
					.define( "healDefault", false ); 
			patchyDecay = builder
					.comment( "When dragonskin blocks decay, they decay in patches of " +
							"connected blocks, rather than individual blocks" )
					.define( "patchyDecay",  true );
			
			// This is also somewhat black magic to me
			dropBlocks = builder
					.comment( "The blocks that might be released from Dragonscale. " +
							"This can be a resource ID (minecraft:stone) or a tag" +
							"entry (forge:ores/iron)" )
             .defineList( "dropBlocks", ImmutableList.of(
            		 "forge:ores/iron",
            		 "forge:ores/gold",
            		 "forge:ores/diamond"
            		 ), (val) -> true);

			debug = builder
					.comment( "Display debugging data in the console" )
					.define( "debug", false );
			builder.pop();
		}

	}
	
	public static final Common COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;
	
	static
	{
		// This is all still black magic to me - NR
		Pair<Common, ForgeConfigSpec> commonSpecPair =
				new ForgeConfigSpec.Builder().configure( Common::new );
		COMMON = commonSpecPair.getLeft();
		COMMON_SPEC = commonSpecPair.getRight();
	}
}

/*
 * Block.getStateFromMeta is marked as deprecated, but there seems to
 * be no replacement.
 */
/*
@SuppressWarnings("deprecation")
public static void lookupBlocks()
{
skinOreBlocks = new Vector<IBlockState>();

// Convert ore texts to blocks
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
 */
/*
private static final String CATEGORY_GENERAL = "general";


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
 */
