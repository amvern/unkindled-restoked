package net.amvern.unkindledrestoked.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.amvern.unkindledrestoked.util.FurnaceUtil;
import net.amvern.unkindledrestoked.util.Igniter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.core.dispenser.DispenseItemBehavior$18")
public abstract class FlintAndSteelDispenserBehaviorMixin {
    @ModifyExpressionValue(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/CandleCakeBlock;canLight(Lnet/minecraft/world/level/block/state/BlockState;)Z"
            )
    )
    private boolean unkindledrestoked$allowUsingOnFurnaces(boolean original, @Local BlockState state) {
        return original || FurnaceUtil.canBeLit(state);
    }

    @Inject(
            method = "execute",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/level/gameevent/GameEvent;BLOCK_CHANGE:Lnet/minecraft/world/level/gameevent/GameEvent;"
            )
    )
    private void unkindledrestoked$setIgnited(BlockSource blockSource, ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir,  @Local BlockPos pos) {
        BlockEntity blockEntity = blockSource.getLevel().getBlockEntity(pos);
        if (blockEntity instanceof AbstractFurnaceBlockEntity furnace) {
            ((Igniter) furnace).unkindledrestoked$setIgnited(true);
        }
    }
}
