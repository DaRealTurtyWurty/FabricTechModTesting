package dev.turtywurty.fabrictechmodtesting.client.screen;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.common.menu.SolarPanelMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class SolarPanelScreen extends AbstractContainerScreen<SolarPanelMenu> {
    private static final ResourceLocation TEXTURE = FabricTechModTesting.id("textures/gui/solar_panel.png");

    public SolarPanelScreen(SolarPanelMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        int energy = Mth.ceil(this.menu.getEnergyPercent() * 66);
        guiGraphics.fill(this.leftPos + 144, this.topPos + 10 + 66 - energy, this.leftPos + 144 + 20, this.topPos + 10 + 66, 0xFFD4AF37);

        if (this.minecraft == null || this.minecraft.level == null)
            return;

        int output = Mth.ceil(this.menu.getEnergyOutputPercent() * 21);
        guiGraphics.blit(TEXTURE, this.leftPos + 36, this.topPos + 33 + 21 - output, 176, 21 - output, 21, output);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        renderTooltip(guiGraphics, mouseX, mouseY);

        if (isHovering(144, 10, 20, 66, mouseX, mouseY)) {
            guiGraphics.renderTooltip(this.font, Component.literal("Energy: " + this.menu.getEnergy() + " / " + this.menu.getMaxEnergy()), mouseX, mouseY);
        }

        if (isHovering(36, 33, 21, 21, mouseX, mouseY)) {
            guiGraphics.renderTooltip(this.font, Component.literal("Sunlight: %d%%".formatted((int) Mth.clamp(this.menu.getEnergyOutputPercent() * 100, 0, 100))), mouseX, mouseY);
        }
    }
}
