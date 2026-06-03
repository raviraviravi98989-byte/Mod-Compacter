package com.blockcompacter.block;

import com.blockcompacter.BlockCompacterMod;
import com.blockcompacter.blockentity.BlockCompacterBlockEntity;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block BLOCK_COMPACTER = new BlockCompacterBlock(
            AbstractBlock.Settings.create().strength(3.5f).requiresTool()
    );

    public static void register() {
        Registry.register(Registries.BLOCK,
                Identifier.of(BlockCompacterMod.MOD_ID, "block_compacter"),
                BLOCK_COMPACTER);

        Registry.register(Registries.ITEM,
                Identifier.of(BlockCompacterMod.MOD_ID, "block_compacter"),
                new BlockItem(BLOCK_COMPACTER, new Item.Settings()));

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries ->
                entries.add(BLOCK_COMPACTER));
    }
}
