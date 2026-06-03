package com.blockcompacter.screen;

import com.blockcompacter.blockentity.BlockCompacterBlockEntity;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockCompacterScreenHandler extends ScreenHandler {

    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;

    // Server-side constructor (with block entity)
    public BlockCompacterScreenHandler(int syncId, PlayerInventory playerInventory,
                                        BlockCompacterBlockEntity blockEntity) {
        this(syncId, playerInventory, blockEntity,
                new PropertyDelegate() {
                    @Override public int get(int index) {
                        return switch (index) {
                            case 0 -> blockEntity.getProgress();
                            case 1 -> blockEntity.getMaxProgress();
                            default -> 0;
                        };
                    }
                    @Override public void set(int index, int value) {}
                    @Override public int size() { return 2; }
                });
    }

    // Client-side constructor (with BlockPos)
    public BlockCompacterScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, new SimpleInventory(BlockCompacterBlockEntity.TOTAL_SLOTS),
                new ArrayPropertyDelegate(2));
    }

    private BlockCompacterScreenHandler(int syncId, PlayerInventory playerInventory,
                                         Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ModScreenHandlers.BLOCK_COMPACTER_SCREEN_HANDLER, syncId);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;

        checkSize(inventory, BlockCompacterBlockEntity.TOTAL_SLOTS);
        inventory.onOpen(playerInventory.player);
        addProperties(propertyDelegate);

        // === 8 Input slots (2 rows of 4) ===
        // Row 1: slots 0-3, starting at x=8, y=17
        for (int col = 0; col < 4; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, 17));
        }
        // Row 2: slots 4-7, starting at x=8, y=35
        for (int col = 0; col < 4; col++) {
            addSlot(new Slot(inventory, 4 + col, 8 + col * 18, 35));
        }

        // === Output slot (slot 8) ===
        addSlot(new OutputSlot(inventory, BlockCompacterBlockEntity.OUTPUT_SLOT, 134, 35));

        // === Player Inventory (27 slots) ===
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // === Player Hotbar (9 slots) ===
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);

        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            // Output slot -> player inventory
            if (invSlot == BlockCompacterBlockEntity.OUTPUT_SLOT) {
                if (!insertItem(originalStack, 9, 45, true)) return ItemStack.EMPTY;
            }
            // Player inventory -> input slots
            else if (invSlot >= 9) {
                if (!insertItem(originalStack, 0, 8, false)) return ItemStack.EMPTY;
            }
            // Input slots -> player inventory
            else {
                if (!insertItem(originalStack, 9, 45, false)) return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) slot.setStack(ItemStack.EMPTY);
            else slot.markDirty();

            if (originalStack.getCount() == newStack.getCount()) return ItemStack.EMPTY;
            slot.onTakeItem(player, originalStack);
        }

        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public int getProgress() {
        return propertyDelegate.get(0);
    }

    public int getMaxProgress() {
        return propertyDelegate.get(1);
    }

    public float getProgressPercent() {
        int max = getMaxProgress();
        if (max == 0) return 0f;
        return (float) getProgress() / max;
    }

    // Simple array property delegate for client side
    static class ArrayPropertyDelegate implements PropertyDelegate {
        private final int[] data;
        ArrayPropertyDelegate(int size) { this.data = new int[size]; }
        @Override public int get(int index) { return data[index]; }
        @Override public void set(int index, int value) { data[index] = value; }
        @Override public int size() { return data.length; }
    }

    // Output slot that prevents manual insertion
    static class OutputSlot extends Slot {
        public OutputSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }
        @Override
        public boolean canInsert(ItemStack stack) { return false; }
    }
}
