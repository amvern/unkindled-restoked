package net.amvern.unkindledrestoked.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.amvern.unkindledrestoked.UnkindledRestoked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin extends BaseEntityBlock {
    protected CampfireBlockMixin(Properties settings) {
        super(settings);
    }

    @SuppressWarnings("UnresolvedMixinReference")
    @ModifyExpressionValue(
            method = "getStateForPlacement",
            at = @At(
                    value = "INVOKE:LAST",
                    target = "Ljava/lang/Boolean;valueOf(Z)Ljava/lang/Boolean;"
            )
    )
    private Boolean unkindledrestoked$isUnlitByDefault(Boolean original) {
        return original && !this.defaultBlockState().is(UnkindledRestoked.NEEDS_IGNITING);
    }
}
