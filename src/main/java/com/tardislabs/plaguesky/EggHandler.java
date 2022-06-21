package com.tardislabs.plaguesky;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EggHandler 
{
	@SubscribeEvent
	public void onPlayerInteract( PlaceEvent event )
	{
		if( event.getWorld().isRemote ) return;
		if( event.getWorld().provider.getDimension() != 0 ) return;
		if( event.getPlacedBlock().getBlock() != Blocks.DRAGON_EGG ) return;
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
}
