package com.tardislabs.plaguesky;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

class ItemRegistry
{
    public static final DeferredRegister<Item> ITEMS = 
    		DeferredRegister.create( ForgeRegistries.ITEMS, PlagueSky.MODID );
    public static final RegistryObject<Item> dragonSkin = 
    		ITEMS.register("dragonskin", () -> new BlockItem(
    				BlockRegistry.dragonSkin.get(), 
    				new Item.Properties().group( ItemGroup.MATERIALS )));
    public static final RegistryObject<Item> dragonStone = 
    		ITEMS.register("dragonstone", () -> new BlockItem(
    				BlockRegistry.dragonStone.get(), 
    				new Item.Properties().group( ItemGroup.MATERIALS )));
}
