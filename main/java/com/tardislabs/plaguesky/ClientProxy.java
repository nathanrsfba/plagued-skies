package com.tardislabs.plaguesky;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author "Nathan Roberts <nroberts@tardislabs.com>"
 *
 */

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy 
{
	@SubscribeEvent
	public static void registerModels( ModelRegistryEvent event )
	{
		// PlagueSky.mutter( "------------------- Registering models...");
		registerModel( Item.getItemFromBlock( Blocks.DRAGONSKIN ));
		registerModel( Item.getItemFromBlock( Blocks.DRAGONSTONE ));
	}
	
	public static void registerModel( Item item )
	{
		// PlagueSky.mutter( "Registering model " + item.getUnlocalizedName() );
		ModelLoader.setCustomModelResourceLocation( item, 0,
				new ModelResourceLocation( item.getRegistryName(), "inventory" ));
		
	}
}