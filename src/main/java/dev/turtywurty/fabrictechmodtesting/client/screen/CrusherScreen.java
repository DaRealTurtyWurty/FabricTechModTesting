package dev.turtywurty.fabrictechmodtesting.client.screen;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.CrusherBlockEntity;
import dev.turtywurty.fabrictechmodtesting.common.menu.CrusherMenu;
import dev.turtywurty.fabrictechmodtesting.common.recipe.CrusherRecipe;
import dev.turtywurty.fabrictechmodtesting.core.util.StringUtility;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CrusherScreen extends AbstractContainerScreen<CrusherMenu> implements RecipeUpdateListener {
    private static final ResourceLocation TEXTURE = FabricTechModTesting.id("textures/gui/crusher.png");

    private final CrusherScreen.CrusherRecipeBookComponent recipeBookComponent = new CrusherScreen.CrusherRecipeBookComponent();
    private boolean widthTooNarrow;

    public CrusherScreen(CrusherMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.widthTooNarrow = this.width < 379;
        this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
        this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
        addRenderableWidget(new ImageButton(this.leftPos + 15, this.height / 2 - 49, 20, 18, RecipeBookComponent.RECIPE_BUTTON_SPRITES, button -> {
            this.recipeBookComponent.toggleVisibility();
            this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
            button.setPosition(this.leftPos + 20, this.height / 2 - 49);
        }));
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.recipeBookComponent.tick();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        int progress = Mth.ceil(this.menu.getProgressPercent() * 24);
        guiGraphics.blit(TEXTURE, this.leftPos + 67, this.topPos + 35, 176, 0, progress, 17);

        int energy = Mth.ceil(this.menu.getEnergyPercent() * 66);
        guiGraphics.fill(this.leftPos + 144, this.topPos + 10 + 66 - energy, this.leftPos + 144 + 20, this.topPos + 10 + 66, 0xFFD4AF37);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
            this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
            this.recipeBookComponent.render(guiGraphics, mouseX, mouseY, partialTicks);
        } else {
            super.render(guiGraphics, mouseX, mouseY, partialTicks);
            this.recipeBookComponent.render(guiGraphics, mouseX, mouseY, partialTicks);
            this.recipeBookComponent.renderGhostRecipe(guiGraphics, this.leftPos, this.topPos, true, partialTicks);
        }

        renderTooltip(guiGraphics, mouseX, mouseY);
        this.recipeBookComponent.renderTooltip(guiGraphics, this.leftPos, this.topPos, mouseX, mouseY);

        if (isHovering(144, 10, 20, 66, mouseX, mouseY)) {
            guiGraphics.renderTooltip(this.font, Component.literal("Energy: " +
                    StringUtility.formatNumberWithQuantifier(this.menu.getEnergy()) + " / " +
                    StringUtility.formatNumberWithQuantifier(this.menu.getMaxEnergy())), mouseX, mouseY);
        }
    }

    @Override
    public @NotNull RecipeBookComponent getRecipeBookComponent() {
        return this.recipeBookComponent;
    }

    @Override
    public void recipesUpdated() {
        this.recipeBookComponent.recipesUpdated();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.recipeBookComponent.mouseClicked(mouseX, mouseY, button)) {
            return true;
        } else {
            return this.widthTooNarrow && this.recipeBookComponent.isVisible() || super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    protected void slotClicked(Slot slot, int mouseX, int mouseY, ClickType clickType) {
        super.slotClicked(slot, mouseX, mouseY, clickType);
        this.recipeBookComponent.slotClicked(slot);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return !this.recipeBookComponent.keyPressed(keyCode, scanCode, modifiers) && super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int x, int y, int modifiers) {
        boolean isOutside = mouseX < (double) x || mouseY < (double) y || mouseX >= (double) (x + this.imageWidth) || mouseY >= (double) (y + this.imageHeight);
        return this.recipeBookComponent.hasClickedOutside(mouseX, mouseY, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, modifiers) && isOutside;
    }

    @Override
    public boolean charTyped(char c, int modifiers) {
        return this.recipeBookComponent.charTyped(c, modifiers) || super.charTyped(c, modifiers);
    }

    public static class CrusherRecipeBookComponent extends RecipeBookComponent {
        @Override
        public void slotClicked(@Nullable Slot slot) {
            super.slotClicked(slot);
            if (slot != null && slot.index < this.menu.getSize()) {
                this.ghostRecipe.clear();
            }
        }

        @Override
        public void setupGhostRecipe(RecipeHolder<?> recipeHolder, List<Slot> list) {
            Recipe<?> recipe = recipeHolder.value();
            if (!(recipe instanceof CrusherRecipe crusherRecipe))
                return;

            this.ghostRecipe.setRecipe(recipeHolder);

            ItemStack resultA = crusherRecipe.outputA();
            ItemStack resultB = crusherRecipe.outputB();
            Slot resultSlot0 = list.get(CrusherBlockEntity.OUTPUT_SLOT);
            Slot resultSlot1 = list.get(CrusherBlockEntity.OUTPUT_SLOT + 1);

            this.ghostRecipe.addIngredient(Ingredient.of(resultA), resultSlot0.x, resultSlot0.y);
            this.ghostRecipe.addIngredient(Ingredient.of(resultB), resultSlot1.x, resultSlot1.y);

            Slot slot = list.get(0);
            this.ghostRecipe.addIngredient(Ingredient.of(crusherRecipe.input().getMatchingStacks().stream()), slot.x, slot.y);
        }
    }
}
