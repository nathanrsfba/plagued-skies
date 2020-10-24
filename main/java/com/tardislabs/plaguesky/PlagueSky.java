package com.tardislabs.plaguesky;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = PlagueSky.MODID, name = PlagueSky.NAME, version = PlagueSky.VERSION)
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
    public static final String VERSION = "1.3";

    /**
     * A logger for printing debugging info to the console
     */
    public static Logger logger;
    
    public static void mutter( String info )
    {
    	logger.info( info );
    }

	public static Data store = null;
    public static int heal = -1;
    
    /**
     * The Forge proxy for performing client- or server-specific functions
     */
    @SidedProxy( clientSide = "com.tardislabs.plaguesky.ClientProxy", serverSide = "com.tardislabs.plaguesky.CommonProxy")
    public static CommonProxy proxy;
    
    @EventHandler
    public void preInit( FMLPreInitializationEvent event )
    {
        logger = event.getModLog();
        proxy.preInit( event );
    }

    @EventHandler
    public void init( FMLInitializationEvent event )
    {
    	proxy.init( event );
    }

    @EventHandler
    public void postInit( FMLPostInitializationEvent event )
    {
    	// PlagueSky.mutter( "------------------------------------------------- Attempting to call postinit" );
    	proxy.postInit( event );
    }

    @EventHandler
	public void serverStart( FMLServerStartingEvent event ) 
	{
    	proxy.serverStart( event );
	}

}

