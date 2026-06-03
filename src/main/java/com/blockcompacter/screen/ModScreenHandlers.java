package com.blockcompacter.screen;

import com.blockcompacter.BlockCompacterMod;
import com.blockcompacter.blockentity.ModBlockEntities;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ModScreenHandlers {

    public static ScreenHandlerType<BlockCompacterScreenHandler> BLOCK_COMPACTER_SCREEN_HANDLER;

    public static void register() {
        BLOCK_COMPACTER_SCREEN_HANDLER = Registry.register(
                Registries.SCREEN_HANDLER,
                Identifier.of(BlockCompacterMod.MOD_ID, "block_compacter"),
                new ExtendedScreenHandlerType<>((syncId, inventory, buf) ->
                        new BlockCompacterScreenHandler(syncId, inventory, buf.readBlockPos()),
                        BlockPos.class)
        );
    }
}
