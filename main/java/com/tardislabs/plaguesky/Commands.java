package com.tardislabs.plaguesky;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;

public class Commands 
{
	public static void register( MinecraftServer server )
	{
		ServerCommandManager cmd = (ServerCommandManager) server.getCommandManager();
	
		cmd.registerCommand( new CommandHeal() );
		cmd.registerCommand( new CommandLoadChunk() );
	}

}

class CommandHeal extends CommandBase
{
	@Override
	public String getUsage( ICommandSender sender ) 
	{
		return "/healplague [on|off]: Turn plague healing on/off";
	}

	@Override
	public void execute( MinecraftServer server, ICommandSender sender, String[] args ) throws CommandException 
	{
		Data data = Data.get( server.getWorld( 0 ));
		if( args.length != 1 )
		{
			sender.sendMessage( new TextComponentString( "Healing is " + 
					(data.isHealing() ? "on" : "off") ));
			return;
		}
		
		if( !args[0].equalsIgnoreCase( "on" ) && 
				!args[0].equalsIgnoreCase( "off" ))
		{
			sender.sendMessage( new TextComponentString( "Invalid argument" ));
			return;
		
		}
		boolean heal = args[0].equalsIgnoreCase( "on" );
		sender.sendMessage( new TextComponentString( "Turned healing " +
				(heal ? "on" : "off") ));
		data.setHealing( heal );
	}

	@Override
	public int getRequiredPermissionLevel() 
	{
		return 2;
	}

	@Override
	public String getName() {
		return "healplague";
	}
}

class CommandLoadChunk extends CommandBase
{
	@Override
	public String getUsage( ICommandSender sender ) 
	{
		return "/loadchunk <dimension> <chunkX> <chunkZ>";
	}

	@Override
	public void execute( MinecraftServer server, ICommandSender sender, String[] args ) throws CommandException 
	{
		if( args.length != 3 )
		{
			sender.sendMessage( new TextComponentString( "Invalid number of arguments" ));
			return;
		}

		int iargs[] = new int[3];
		try
		{
			for( int i = 0; i < iargs.length; i++ )
			{
				iargs[i] = Integer.parseInt( args[i] );
			}
		}
		catch( NumberFormatException e )
		{
			sender.sendMessage( new TextComponentString( "Invalid argument" ));
			return;
		}
	
		WorldServer worldsvr = server.getWorld( iargs[0] );
		if( worldsvr == null )
		{
			sender.sendMessage( new TextComponentString( "Invalid dimension" ));
			return;
		}
		
		IChunkProvider iprovider = worldsvr.getChunkProvider();
		if( !(iprovider instanceof ChunkProviderServer) ) return;
		ChunkProviderServer provider = (ChunkProviderServer) iprovider;

		provider.loadChunk( iargs[1], iargs[2] );
		sender.sendMessage( new TextComponentString( "Loaded chunk " + iargs[1] + "x" + iargs[2] + " (DIM" + iargs[0] + ")" ));
	}
	
	@Override
	public int getRequiredPermissionLevel() 
	{
		return 2;
	}

	@Override
	public String getName() 
	{
		return "loadchunk";
	}
}