package com.blockcompacter.client;

import com.blockcompacter.client.screen.BlockCompacterScreen;
import com.blockcompacter.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class BlockCompacterClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.BLOCK_COMPACTER_SCREEN_HANDLER,
                BlockCompacterScreen::new);
    }
}
