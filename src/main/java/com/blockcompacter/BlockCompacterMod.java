package com.blockcompacter;

import com.blockcompacter.block.ModBlocks;
import com.blockcompacter.blockentity.ModBlockEntities;
import com.blockcompacter.recipe.ModRecipes;
import com.blockcompacter.screen.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockCompacterMod implements ModInitializer {

    public static final String MOD_ID = "blockcompacter";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Block Compacter Mod Initializing...");
        ModBlocks.register();
        ModBlockEntities.register();
        ModRecipes.register();
        ModScreenHandlers.register();
        LOGGER.info("Block Compacter Mod Initialized!");
    }
}
