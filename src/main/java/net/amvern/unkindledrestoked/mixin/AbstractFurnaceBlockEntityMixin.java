package net.amvern.unkindledrestoked.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
                    target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;canBurn(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/item/crafting/RecipeHolder;Lnet/minecraft/world/item/crafting/SingleRecipeInput;Lnet/minecraft/core/NonNullList;I)Z"
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
    private static void unkindledrestoked$setUnignited(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState, AbstractFurnaceBlockEntity abstractFurnaceBlockEntity, CallbackInfo ci) {
        if (!blockState.getValue(AbstractFurnaceBlock.LIT)) {
            if (((AbstractFurnaceBlockEntityAccessor) abstractFurnaceBlockEntity).unkindledrestoked$cookingTotalTime() > 0) {
                return;
            }
            ((Igniter) abstractFurnaceBlockEntity).unkindledrestoked$setIgnited(false);
        } else if (!((AbstractFurnaceBlockEntityAccessor) abstractFurnaceBlockEntity).unkindledrestoked$isLit()) {
            blockState = blockState.setValue(AbstractFurnaceBlock.LIT, false);
            serverLevel.setBlock(blockPos, blockState, Block.UPDATE_ALL);
        }
    }

    @Inject(
            method = "loadAdditional",
            at = @At(
                    "TAIL"
            )
    )
    private void unkindledrestoked$readNbt(ValueInput valueInput, CallbackInfo ci) {
        this.unkindledrestoked$ignited = valueInput.getBooleanOr("Ignited", false);
    }

    @Inject(
            method = "saveAdditional",
            at = @At(
                    "TAIL"
            )
    )
    private void unkindledrestoked$writeNbt(ValueOutput valueOutput, CallbackInfo ci) {
        valueOutput.putBoolean("Ignited", this.unkindledrestoked$ignited);
    }
}
