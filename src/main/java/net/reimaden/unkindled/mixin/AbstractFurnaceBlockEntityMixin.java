package net.reimaden.unkindled.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.reimaden.unkindled.Unkindled;
import net.reimaden.unkindled.util.Igniter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin implements Igniter {
    @Unique
    private boolean unkindled$ignited = false;

    @Override
    public void unkindled$setIgnited(boolean ignited) {
        this.unkindled$ignited = ignited;
    }

    @Override
    public boolean unkindled$isIgnited() {
        return this.unkindled$ignited;
    }

        @ModifyExpressionValue(
            method = "serverTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;canBurn(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/item/crafting/RecipeHolder;Lnet/minecraft/core/NonNullList;I)Z"
            )
    )
    private static boolean unkindled$requiresIgniting(boolean original, Level world, BlockPos pos, BlockState state,
                                                      AbstractFurnaceBlockEntity blockEntity, @Local(ordinal = 0) ItemStack stack) {
        if (!state.is(Unkindled.NEEDS_IGNITING) || stack.is(Unkindled.SELF_IGNITING_FUEL)) {
            return original;
        }
        return original && ((Igniter) blockEntity).unkindled$isIgnited();
    }

    @Inject(
            method = "serverTick",
            at = @At(
                    "TAIL"
            )
    )
    private static void unkindled$setUnignited(Level world, BlockPos pos, BlockState state,
                                               AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci) {
        if (!state.getValue(AbstractFurnaceBlock.LIT)) {
            if (((AbstractFurnaceBlockEntityAccessor) blockEntity).unkindled$cookingTotalTime() > 0) {
                return;
            }
            ((Igniter) blockEntity).unkindled$setIgnited(false);
        } else if (!((AbstractFurnaceBlockEntityAccessor) blockEntity).unkindled$isLit()) {
            state = state.setValue(AbstractFurnaceBlock.LIT, false);
            world.setBlock(pos, state, Block.UPDATE_ALL);
        }
    }

    @Inject(
            method = "loadAdditional",
            at = @At(
                    "TAIL"
            )
    )
    private void unkindled$readNbt(CompoundTag nbt, HolderLookup.Provider registryLookup, CallbackInfo ci) {
        this.unkindled$ignited = nbt.getBoolean("Ignited");
    }

    @Inject(
            method = "saveAdditional",
            at = @At(
                    "TAIL"
            )
    )
    private void unkindled$writeNbt(CompoundTag nbt, HolderLookup.Provider registryLookup, CallbackInfo ci) {
        nbt.putBoolean("Ignited", this.unkindled$ignited);
    }
}
