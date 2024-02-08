package dev.turtywurty.fabrictechmodtesting.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.recipebook.GhostRecipe;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GhostRecipe.class)
public class GhostRecipeMixin {
    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;fill(Lnet/minecraft/client/renderer/RenderType;IIIII)V",
                    shift = At.Shift.AFTER
            )
    )
    private void fabrictechmodtesting$render(GuiGraphics guiGraphics, Minecraft minecraft, int mouseX, int mouseY,
                                             boolean widthTooNarrow, float partialTicks, CallbackInfo callback,
                                             @Local(ordinal = 2) int index, @Local(ordinal = 3) int xPos,
                                             @Local(ordinal = 4) int yPos, @Local ItemStack itemStack) {
        if (index != 0 && itemStack.getCount() > 1) {
            guiGraphics.renderItemDecorations(minecraft.font, itemStack, xPos, yPos);
        }
    }
}
