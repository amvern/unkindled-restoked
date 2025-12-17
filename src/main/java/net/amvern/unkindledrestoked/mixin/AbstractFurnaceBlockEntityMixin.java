package net.amvern.unkindledrestoked.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
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
                    target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;canBurn(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/item/crafting/RecipeHolder;Lnet/minecraft/core/NonNullList;I)Z"
        )
    )
    private static boolean unkindledrestoked$requiresIgniting(boolean original, @Local AbstractFurnaceBlockEntity blockEntity, @Local(ordinal = 0) ItemStack stack) {
        if (!blockEntity.getBlockState().is(UnkindledRestoked.NEEDS_IGNITING) || stack.is(UnkindledRestoked.SELF_IGNITING_FUEL)) {
            return original;
        }
        return original && ((Igniter) blockEntity).unkindledrestoked$isIgnited();
    }

    @Inject(
            method = "serverTick",
            at = @At(
                    "TAIL"
            )
    )
    private static void unkindledrestoked$setUnignited(Level level, BlockPos blockPos, BlockState blockState, AbstractFurnaceBlockEntity abstractFurnaceBlockEntity, CallbackInfo ci) {
        if (!blockState.getValue(AbstractFurnaceBlock.LIT)) {
            ((Igniter) abstractFurnaceBlockEntity).unkindledrestoked$setIgnited(false);
        }
    }

    @Inject(
            method = "loadAdditional",
            at = @At(
                    "TAIL"
            )
    )
    private void unkindledrestoked$readNbt(CompoundTag compoundTag, HolderLookup.Provider provider, CallbackInfo ci) {
        this.unkindledrestoked$ignited = compoundTag.getBoolean("Ignited");
    }

    @Inject(
            method = "saveAdditional",
            at = @At(
                    "TAIL"
            )
    )
    private void unkindledrestoked$writeNbt(CompoundTag compoundTag, HolderLookup.Provider provider, CallbackInfo ci) {
        compoundTag.putBoolean("Ignited", this.unkindledrestoked$ignited);
    }
}
