package net.amvern.unkindledrestoked.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.amvern.unkindledrestoked.UnkindledRestoked;
import net.amvern.unkindledrestoked.util.Igniter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin implements Igniter {
    @Unique
    private boolean unkindledrestoked$ignited = false;

    @Override
    public void unkindledrestoked$setIgnited(boolean ignited) {
        this.unkindledrestoked$ignited = ignited;
    }

    @Override
    public boolean unkindledrestoked$isIgnited() {
        return this.unkindledrestoked$ignited;
    }

    @ModifyExpressionValue(
            method = "serverTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;canBurn(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/item/crafting/Recipe;Lnet/minecraft/core/NonNullList;I)Z"
            )
    )
    private static boolean unkindled$requiresIgniting(boolean original, Level level, BlockPos pos, BlockState state,
                                                      AbstractFurnaceBlockEntity blockEntity, @Local(ordinal = 0) ItemStack stack) {
        if(!state.is(UnkindledRestoked.NEEDS_IGNITING) || stack.is(UnkindledRestoked.SELF_IGNITING_FUEL)) {
            return original;
        } else {
            return original && ((Igniter) blockEntity).unkindledrestoked$isIgnited();
        }
    }

    @Inject(
            method = "serverTick",
            at = @At(
                    "TAIL"
            )
    )
    private static void unkindled$setUnignited(Level level, BlockPos pos, BlockState state,
                                               AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci) {
        if (!state.getValue(AbstractFurnaceBlock.LIT)) {
            if (((AbstractFurnaceBlockEntityAccessor) blockEntity).unkindledrestoked$cookingTotalTime() > 0) {
                return;
            }
            ((Igniter) blockEntity).unkindledrestoked$setIgnited(false);
        } else if (!((AbstractFurnaceBlockEntityAccessor) blockEntity).unkindledrestoked$isLit()) {
            state = state.setValue(AbstractFurnaceBlock.LIT, false);
            level.setBlockAndUpdate(pos, state);
        }
    }

    @Inject(
            method = "load",
            at = @At(
                    "TAIL"
            )
    )
    private void unkindled$readNbt(CompoundTag compoundTag, CallbackInfo ci) {
        this.unkindledrestoked$ignited = compoundTag.getBoolean("Ignited");
    }

    @Inject(
            method = "saveAdditional",
            at = @At(
                    "TAIL"
            )
    )
    private void unkindled$writeNbt(CompoundTag compoundTag, CallbackInfo ci) {
        compoundTag.putBoolean("Ignited", this.unkindledrestoked$ignited);
    }
}
