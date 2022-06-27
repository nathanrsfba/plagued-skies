package com.tardislabs.plaguesky;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockRegistry
{
	public static final DeferredRegister<Block> BLOCKS = 
			DeferredRegister.create( ForgeRegistries.BLOCKS, PlagueSky.MODID );
	public static final RegistryObject<Block> dragonSkin = 
			BLOCKS.register( "dragonskin", DragonSkin::new );
	public static final RegistryObject<Block> dragonStone = 
			BLOCKS.register( "dragonstone", DragonStone::new );
}
