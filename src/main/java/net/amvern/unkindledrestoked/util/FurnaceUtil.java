package net.amvern.unkindledrestoked.util;

import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.amvern.unkindledrestoked.UnkindledRestoked;

public class FurnaceUtil {
    public static boolean canBeLit(BlockState state) {
        return state.is(UnkindledRestoked.NEEDS_IGNITING, s -> s.hasProperty(AbstractFurnaceBlock.LIT) && !state.getValue(AbstractFurnaceBlock.LIT));
    }
}
