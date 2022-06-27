package com.tardislabs.plaguesky;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.WorldSavedData;

public class Data extends WorldSavedData 
{
    private boolean heal = false;

	public Data() 
	{
		super( PlagueSky.MODID );
	}
	
	public static Data create()
	{
		Data data = new Data();
		data.heal = Config.COMMON.healDefault.get();
		return data;
	}
	
	@Override
	public void read( CompoundNBT nbt ) 
	{
		if( nbt.contains( "heal" )) heal = nbt.getBoolean( "heal" );
	}


	@Override
	public CompoundNBT write( CompoundNBT nbt ) 
	{
		nbt.putBoolean( "heal", heal );
		
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