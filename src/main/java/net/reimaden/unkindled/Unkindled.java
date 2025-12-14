package net.reimaden.unkindled;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.reimaden.unkindled.util.FurnaceUtil;
import net.reimaden.unkindled.util.Igniter;

public class Unkindled implements ModInitializer {
    public static final String MOD_ID = "unkindled";
    public static final TagKey<Block> NEEDS_IGNITING = TagKey.create(Registries.BLOCK, id("needs_igniting"));
    public static final TagKey<Item> SELF_IGNITING_FUEL = TagKey.create(Registries.ITEM, id("self_igniting_fuel"));

    @Override
    public void onInitialize() {
        // Event to allow lighting furnaces with any igniter tool
        // "c:tools/igniter"
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            final BlockPos pos = hitResult.getBlockPos();
            final BlockState state = world.getBlockState(pos);
            if (!FurnaceUtil.canBeLit(state) || !player.isShiftKeyDown() || player.isSpectator()) return InteractionResult.PASS;

            final ItemStack stack = player.getItemInHand(hand);
            final boolean isFireCharge = stack.is(Items.FIRE_CHARGE);

            if (stack.is(ConventionalItemTags.IGNITER_TOOLS) || isFireCharge) {
                final RandomSource random = world.getRandom();
                final SoundEvent sound = isFireCharge ? SoundEvents.FIRECHARGE_USE : SoundEvents.FLINTANDSTEEL_USE;
                float pitch = isFireCharge ? (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F : random.nextFloat() * 0.4F + 0.8F;

                world.playSound(player, pos, sound, SoundSource.BLOCKS, 1.0F, pitch);
                world.setBlock(pos, state.setValue(AbstractFurnaceBlock.LIT, true), Block.UPDATE_ALL_IMMEDIATE);
                world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);

                final BlockEntity blockEntity = world.getBlockEntity(pos);
                // Sanity check
                if (blockEntity instanceof AbstractFurnaceBlockEntity furnace) {
                    ((Igniter) furnace).unkindled$setIgnited(true);
                }

                if (isFireCharge) {
                    stack.consume(1, player);
                } else {
                    stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                }

                return InteractionResult.sidedSuccess(world.isClientSide());
            }

            return InteractionResult.PASS;
        });
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
