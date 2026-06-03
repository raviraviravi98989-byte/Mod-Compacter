package com.blockcompacter.client.screen;

import com.blockcompacter.BlockCompacterMod;
import com.blockcompacter.screen.BlockCompacterScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BlockCompacterScreen extends HandledScreen<BlockCompacterScreenHandler> {

    private static final Identifier TEXTURE =
            Identifier.of(BlockCompacterMod.MOD_ID, "textures/gui/block_compacter.png");

    public BlockCompacterScreen(BlockCompacterScreenHandler handler,
                                 PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        // Draw progress arrow (pixels 176-196 in texture, at x=88, y=30 in GUI)
        int progressWidth = (int) (handler.getProgressPercent() * 22);
        if (progressWidth > 0) {
            context.drawTexture(TEXTURE, x + 89, y + 30, 176, 0, progressWidth, 16);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
