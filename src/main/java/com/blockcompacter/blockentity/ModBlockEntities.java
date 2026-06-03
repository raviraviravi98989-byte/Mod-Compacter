package com.blockcompacter.blockentity;

import com.blockcompacter.BlockCompacterMod;
import com.blockcompacter.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static BlockEntityType<BlockCompacterBlockEntity> BLOCK_COMPACTER_ENTITY;

    public static void register() {
        BLOCK_COMPACTER_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(BlockCompacterMod.MOD_ID, "block_compacter"),
                FabricBlockEntityTypeBuilder.create(BlockCompacterBlockEntity::new,
                        ModBlocks.BLOCK_COMPACTER).build()
        );
    }
}
