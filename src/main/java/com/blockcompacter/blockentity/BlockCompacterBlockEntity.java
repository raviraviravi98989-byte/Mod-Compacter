package com.blockcompacter.blockentity;

import com.blockcompacter.BlockCompacterMod;
import com.blockcompacter.screen.BlockCompacterScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockCompacterBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {

    // Slots: 0-7 = input slots (8 blocks), 8 = output slot
    public static final int INPUT_SLOTS = 8;
    public static final int OUTPUT_SLOT = 8;
    public static final int TOTAL_SLOTS = 9;

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(TOTAL_SLOTS, ItemStack.EMPTY);

    // Progress tracking
    private int progress = 0;
    private int maxProgress = 80; // 4 seconds at 20 ticks/s

    public BlockCompacterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLOCK_COMPACTER_ENTITY, pos, state);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.blockcompacter.block_compacter");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new BlockCompacterScreenHandler(syncId, playerInventory, this);
    }

    public DefaultedList<ItemStack> getInventory() {
        return inventory;
    }

    public ItemStack getStack(int slot) {
        return inventory.get(slot);
    }

    public void setStack(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        if (!stack.isEmpty() && stack.getCount() > stack.getMaxCount()) {
            stack.setCount(stack.getMaxCount());
        }
        markDirty();
    }

    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(inventory, slot, amount);
        if (!result.isEmpty()) markDirty();
        return result;
    }

    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(inventory, slot);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putInt("Progress", progress);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);
        progress = nbt.getInt("Progress");
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;

        if (canCompact()) {
            progress++;
            if (progress >= maxProgress) {
                compact();
                progress = 0;
            }
            markDirty();
        } else {
            if (progress > 0) {
                progress = 0;
                markDirty();
            }
        }
    }

    /**
     * Check if all 8 input slots contain the same item and output has room.
     */
    private boolean canCompact() {
        // All 8 input slots must have at least 1 item
        ItemStack firstStack = inventory.get(0);
        if (firstStack.isEmpty()) return false;

        for (int i = 0; i < INPUT_SLOTS; i++) {
            ItemStack stack = inventory.get(i);
            if (stack.isEmpty()) return false;
            if (!ItemStack.areItemsEqual(stack, firstStack)) return false;
        }

        // Check output slot
        ItemStack output = inventory.get(OUTPUT_SLOT);
        if (output.isEmpty()) return true;

        // Output must be same item and have room
        return ItemStack.areItemsEqual(output, firstStack) &&
               output.getCount() < output.getMaxCount();
    }

    /**
     * Consume 1 from each of the 8 input slots and put 1 compacted result in output.
     */
    private void compact() {
        ItemStack firstStack = inventory.get(0);
        ItemStack result = new ItemStack(firstStack.getItem(), 1);

        // Consume 1 from each input slot
        for (int i = 0; i < INPUT_SLOTS; i++) {
            inventory.get(i).decrement(1);
        }

        // Add to output
        ItemStack output = inventory.get(OUTPUT_SLOT);
        if (output.isEmpty()) {
            inventory.set(OUTPUT_SLOT, result);
        } else {
            output.increment(1);
        }
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public boolean isCompacting() {
        return canCompact();
    }
}
