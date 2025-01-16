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
	/* Location and information for delayed chunk load */
	static int queueX, queueZ, queueDim;
	static MinecraftServer queueServer;
	static ICommandSender queueSender;
	/* World time for delayed chunk load, <0 == None */
	static long queueTime = -1;
	
	@Override
	public String getUsage( ICommandSender sender ) 
	{
		return "/loadchunk <dimension> <chunkX> <chunkZ>";
	}

	@Override
	public void execute( MinecraftServer server, ICommandSender sender, String[] args ) throws CommandException 
	{
		if( args.length < 3 || args.length > 4 )
		{
			sender.sendMessage( new TextComponentString( "Invalid number of arguments" ));
			return;
		}

		int iargs[] = new int[4];
		try
		{
			for( int i = 0; i < args.length; i++ )
			{
				iargs[i] = Integer.parseInt( args[i] );
			}
		}
		catch( NumberFormatException e )
		{
			sender.sendMessage( new TextComponentString( "Invalid argument" ));
			return;
		}
		
		if( args.length < 4 )
		{
			loadChunk( server, sender, iargs[0], iargs[1], iargs[2] );
			PlagueSky.mutter( "Loading immediately" );
		}
		else
		{
			PlagueSky.mutter( "Loading delayed" );
			queueX = iargs[1];
			queueZ = iargs[2];
			queueDim = iargs[0];
			queueServer = server;
			queueSender = sender;
			
			queueTime = server.getWorld( 0 ).getTotalWorldTime() + iargs[3];

		}
	}

	static public void loadQueuedChunk()
	{
		loadChunk( queueServer, queueSender, queueDim, queueX, queueZ );
		queueTime = -1;
	}
	
	static public void loadChunk( MinecraftServer server, ICommandSender sender, int dim, int x, int z )
	{
		WorldServer worldsvr = server.getWorld( dim );
		if( worldsvr == null )
		{
			sender.sendMessage( new TextComponentString( "Invalid dimension" ));
			return;
		}
		
		
		IChunkProvider iprovider = worldsvr.getChunkProvider();
		if( !(iprovider instanceof ChunkProviderServer) ) return;
		ChunkProviderServer provider = (ChunkProviderServer) iprovider;

		provider.loadChunk( x, z );
		sender.sendMessage( new TextComponentString( "Loaded chunk " + x + "x" + z + " (DIM" + dim + ")" ));
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