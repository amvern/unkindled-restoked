package net.amvern.unkindledrestoked.mixin;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface AbstractFurnaceBlockEntityAccessor {
    @Accessor("cookingTotalTime")
    int unkindledrestoked$cookingTotalTime();

    @Invoker("isLit")
    boolean unkindledrestoked$isLit();
}
