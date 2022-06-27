package com.tardislabs.plaguesky;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EggHandler
{
	public EggHandler()
	{
        MinecraftForge.EVENT_BUS.register( this );
	}
	@SubscribeEvent
	public void onPlaceBlock( BlockEvent.EntityPlaceEvent event )
	{
		// Make sure we're running on the server side
		if( event.getWorld().isRemote() ) return;
		
		ServerWorld world = (ServerWorld) event.getWorld();
		if( !world.getDimensionKey().equals( World.OVERWORLD )) return;
		if( !event.getPlacedBlock().getBlock().equals( Blocks.DRAGON_EGG )) return;
		
		Data data = world.getSavedData().getOrCreate( Data::create, PlagueSky.MODID );
		if( data.isHealing() ) return;
		data.setHealing();
		List<ServerPlayerEntity> players = world.getServer().getPlayerList().getPlayers();
		for( ServerPlayerEntity player: players ) 
		{
			player.sendStatusMessage( new StringTextComponent( "The healing has begun." ), false );
		}
	}

	@SubscribeEvent
	public void onBreakBlock( BlockEvent.BreakEvent event )
	{
		// Make sure we're running on the server side
		if( event.getWorld().isRemote() ) return;
		
		ServerWorld world = (ServerWorld) event.getWorld();
		if( !world.getDimensionKey().equals( World.OVERWORLD )) return;
		if( !event.getState().getBlock().equals( Blocks.DRAGON_EGG )) return;
		
		Data data = world.getSavedData().getOrCreate( Data::create, PlagueSky.MODID );
		if( !data.isHealing() ) return;
		data.setHealing( false );
		List<ServerPlayerEntity> players = world.getServer().getPlayerList().getPlayers();
		for( ServerPlayerEntity player: players ) 
		{
			player.sendStatusMessage( new StringTextComponent( "The healing has stopped." ), false );
		}
	}

}
