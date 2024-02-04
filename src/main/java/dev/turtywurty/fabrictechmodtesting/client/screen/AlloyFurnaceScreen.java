package dev.turtywurty.fabrictechmodtesting.client.screen;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.common.menu.AlloyFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AlloyFurnaceScreen extends AbstractContainerScreen<AlloyFurnaceMenu> {
    private static final ResourceLocation TEXTURE = FabricTechModTesting.id("textures/gui/alloy_furnace.png");

    public AlloyFurnaceScreen(AlloyFurnaceMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);

        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        float progress = this.menu.getProgressPercent();
        guiGraphics.blit(TEXTURE, this.leftPos + 79, this.topPos + 34, 176, 14, (int) (progress * 24), 17);

        float fuelProgress = this.menu.getFuelProgressPercent();
        guiGraphics.blit(TEXTURE, this.leftPos + 56, this.topPos + 36 + (int) ((1 - fuelProgress) * 14), 176, 31 + (int) ((1 - fuelProgress) * 14), 14, (int) (fuelProgress * 14));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
