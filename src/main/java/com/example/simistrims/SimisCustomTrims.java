
package com.example.simistrims;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public class SimisCustomTrims implements ModInitializer {
    @Override
    public void onInitialize() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient()) return ActionResult.PASS;
            if (!(entity instanceof ArmorStandEntity armorStand)) return ActionResult.PASS;

            ItemStack held = player.getStackInHand(hand);
            if (!held.isOf(Items.GLOW_INK_SAC)) return ActionResult.PASS;

            boolean changed = false;
            for (ItemStack stack : armorStand.getArmorItems()) {
                if (hasTrim(stack)) {
                    changed |= setGlow(stack);
                }
            }

            if (changed) {
                if (!player.isCreative()) held.decrement(1);
                player.sendMessage(Text.literal("Trims are now glowing!"), true);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
    }

    private boolean hasTrim(ItemStack stack) {
        NbtCompound trim = stack.getSubNbt("Trim");
        return trim != null && trim.contains("material");
    }

    private boolean setGlow(ItemStack stack) {
        NbtCompound trim = stack.getSubNbt("Trim");
        if (trim == null) return false;

        String materialId = trim.getString("material");
        int color = switch (materialId) {
            case "minecraft:redstone" -> 0xFF0000;
            case "minecraft:diamond" -> 0x00FFFF;
            case "minecraft:emerald" -> 0x00FF00;
            case "minecraft:gold" -> 0xFFD700;
            case "minecraft:netherite" -> 0x555555;
            default -> 0xFFFFFF;
        };

        NbtCompound display = stack.getOrCreateSubNbt("display");
        display.putBoolean("simis_glow", true);
        display.putInt("simis_glow_color", color);
        return true;
    }
}
