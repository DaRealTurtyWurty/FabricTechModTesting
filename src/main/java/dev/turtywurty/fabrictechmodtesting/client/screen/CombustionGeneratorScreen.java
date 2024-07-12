package dev.turtywurty.fabrictechmodtesting.client.screen;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.common.menu.CombustionGeneratorMenu;
import dev.turtywurty.fabrictechmodtesting.core.util.StringUtility;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class CombustionGeneratorScreen extends AbstractContainerScreen<CombustionGeneratorMenu> {
    private static final ResourceLocation TEXTURE = FabricTechModTesting.id("textures/gui/combustion_generator.png");

    public CombustionGeneratorScreen(CombustionGeneratorMenu menu, Inventory inventory, Component title) {
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

        int burnTime = Mth.ceil(this.menu.getBurnTimePercent() * 14);
        guiGraphics.blit(TEXTURE, this.leftPos + 82, this.topPos + 26 + 14 - burnTime, 176, 14 - burnTime, 14, burnTime);

        int energy = Mth.ceil(this.menu.getEnergyPercent() * 66);
        guiGraphics.fill(this.leftPos + 144, this.topPos + 10 + 66 - energy, this.leftPos + 144 + 20, this.topPos + 10 + 66, 0xFFD4AF37);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        renderTooltip(guiGraphics, mouseX, mouseY);

        if (isHovering(144, 10, 20, 66, mouseX, mouseY)) {
            guiGraphics.renderTooltip(this.font, Component.literal("Energy: " +
                    StringUtility.formatNumberWithQuantifier(this.menu.getEnergy()) + " / " +
                    StringUtility.formatNumberWithQuantifier(this.menu.getMaxEnergy())), mouseX, mouseY);
        }
    }
}
