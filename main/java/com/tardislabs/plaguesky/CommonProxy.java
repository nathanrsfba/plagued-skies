package com.tardislabs.plaguesky;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author "Nathan Roberts <nroberts@tardislabs.com>"
 *
 */
@Mod.EventBusSubscriber
public class CommonProxy 
{
    /**
     * The configuration file for the mod
     */
    public static Configuration config;
	
    public void preInit( FMLPreInitializationEvent event ) 
    {
        File directory = event.getModConfigurationDirectory();
        config = new Configuration( new File( directory.getPath(), "plaguesky.cfg" ));
        Config.readConfig();
    }

    public void init( FMLInitializationEvent event ) 
    {
    }

    public void postInit( FMLPostInitializationEvent event ) 
    {
    	// PlagueSky.mutter( "------------------------------------------------- Attempting to register proxy" );
    	MinecraftForge.EVENT_BUS.register( Blocks.DRAGONSKIN );
    	MinecraftForge.EVENT_BUS.register( new EggHandler() );
    	MinecraftForge.EVENT_BUS.register( new Commands() );
        Config.lookupBlocks();

    }
    
	public void serverStart( FMLServerStartingEvent event ) 
	{
		// PlagueSky.mutter( "-------------------- Registering commands" );
		Commands.register( event.getServer() );
	}
    
    
    @SubscribeEvent
    public static void registerBlocks( RegistryEvent.Register<Block> event ) 
    {
    	Block[] blocks = {
    			new DragonSkin(),
    			new DragonStone()
    	};
    	
    	event.getRegistry().registerAll( blocks );
    }

    @SubscribeEvent
    public static void registerItems( RegistryEvent.Register<Item> event ) 
    {
    	Item[] itemBlocks = {
    			new ItemBlock( Blocks.DRAGONSKIN ).setRegistryName( Blocks.DRAGONSKIN.getRegistryName() ),
    			new ItemBlock( Blocks.DRAGONSTONE ).setRegistryName( Blocks.DRAGONSTONE.getRegistryName() )
    	};
    	
    	event.getRegistry().registerAll( itemBlocks );
    			
    }
}