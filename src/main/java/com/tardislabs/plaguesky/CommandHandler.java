package com.tardislabs.plaguesky;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommandHandler
{
	@SubscribeEvent
	public void onRegisterCommandEvent( RegisterCommandsEvent event ) 
	{
		CommandDispatcher<CommandSource> commandDispatcher = event.getDispatcher();
		HealCommand.register( commandDispatcher );
	}
}	  

/**
 * A command to turn plague healing on or off
 */
class HealCommand
{
	enum Mode {
		OFF, ON, GET
	}
	
	public static void register( CommandDispatcher<CommandSource> dispatcher ) 
	{
		dispatcher.register(
				Commands.literal( "healplague" )
				.requires( (commandSource) -> {return commandSource.hasPermissionLevel(2);} )
				
				.then( Commands.literal( "on" )
						.executes( HealCommand::healOn ))

				.then( Commands.literal( "off" )
						.executes( HealCommand::healOff ))
				
				.executes( HealCommand::getHeal )
				

				);
	}

	static int healOn( CommandContext<CommandSource> context ) throws CommandSyntaxException
	{
		return setHeal( context, Mode.ON );
	}

	static int healOff( CommandContext<CommandSource> context ) throws CommandSyntaxException
	{
		return setHeal( context, Mode.OFF );
	}

	static int getHeal( CommandContext<CommandSource> context ) throws CommandSyntaxException
	{
		return setHeal( context, Mode.GET );
	}
/**
 * Get or set the healing mode
 * @param context	Information about the source of the command
 * @param mode		Whether to turn heal on or off, or just display status
 */
	static int setHeal( CommandContext<CommandSource> context, Mode mode ) 
			throws CommandSyntaxException 
	{
		Data data = context.getSource().getWorld().getSavedData()
				.getOrCreate( Data::create, PlagueSky.MODID );

		if( mode == Mode.ON )
		{
			data.setHealing( true );
		}
		if( mode == Mode.OFF )
		{
			data.setHealing( false );
		}
		context.getSource().asPlayer()
			.sendStatusMessage( new StringTextComponent( 
					"Healing is " + (data.isHealing() ? "on" : "off") ), false );
		return 1;
	}
}
