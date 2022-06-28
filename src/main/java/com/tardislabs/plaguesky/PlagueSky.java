package com.tardislabs.plaguesky;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


// The value here should match an entry in the META-INF/mods.toml file
@Mod( PlagueSky.MODID )
public class PlagueSky
{
	/**
	 * The unique identifier for this mod 
	 */
    public static final String MODID = "plaguesky";
    /**
     * The name of this mod
     */
    public static final String NAME = "Plagued Skies";
    /**
     * The version of this mod
     */
    public static final String VERSION = "1.3.1";
    /**
     * Event handler for dragon egg being placed
     */
    public static final EggHandler egghandler = new EggHandler();
    public static final CommandHandler commandHandler = new CommandHandler();
    
    
	// Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public PlagueSky() 
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener( this::setup );
        ModLoadingContext.get().registerConfig( ModConfig.Type.COMMON, Config.COMMON_SPEC );

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register( this );

        ItemRegistry.ITEMS.register( FMLJavaModLoadingContext.get().getModEventBus() );
        BlockRegistry.BLOCKS.register( FMLJavaModLoadingContext.get().getModEventBus() );
        
    }
    
    /**
     * Log an information message, if debug is enabled
     */
    public static void mutter( String message )
    {
    	if( Config.COMMON.debug.get() ) LOGGER.info( message );
    }

    private void setup( final FMLCommonSetupEvent event )
    {
        // some preinit code
        MinecraftForge.EVENT_BUS.register( commandHandler );
    }
    
}
