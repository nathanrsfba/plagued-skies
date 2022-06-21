package com.tardislabs.plaguesky;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class Data extends WorldSavedData 
{
    private static final String tagKey = "PlagueSky";

    private boolean heal = false;

	public Data( String name ) 
	{
		super( name );
	}
	
	public static Data get( World world )
	{
		MapStorage storage = world.getPerWorldStorage();
		Data result = (Data) storage.getOrLoadData( Data.class, tagKey );
		// PlagueSky.mutter( "-------------------- Loaded data. Was " + (result == null ? "null" : "not null") );
	
		if( result == null ) 
		{
			result = new Data( tagKey );
			if( Config.healDefault ) result.heal = true;
			storage.setData( tagKey, result );
		}
		
		return result;
	}

	public void readFromNBT( NBTTagCompound nbt ) 
	{
		// PlagueSky.mutter( "In readFromNBT. " + nbt.getSize() );
		if( nbt.hasKey( "heal" )) heal = nbt.getBoolean( "heal" );
	}

	public NBTTagCompound writeToNBT( NBTTagCompound nbt ) 
	{
		nbt.setBoolean( "heal",  heal );
		// PlagueSky.mutter( "In writeToNBT. " + nbt.getSize() );
		return nbt;
	}
	
    public boolean isHealing()
    {
    	return heal;
    }

	public void setHealing( boolean value )
    {
		heal = value;
		markDirty();
    }

	public void setHealing()
    {
    	setHealing( true );
    }

}
