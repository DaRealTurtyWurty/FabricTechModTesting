package dev.turtywurty.fabrictechmodtesting.client.screen;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.AlloyFurnaceBlockEntity;
import dev.turtywurty.fabrictechmodtesting.common.menu.AlloyFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AlloyFurnaceScreen extends AbstractContainerScreen<AlloyFurnaceMenu> implements RecipeUpdateListener {
    private static final ResourceLocation TEXTURE = FabricTechModTesting.id("textures/gui/alloy_furnace.png");

    private final AlloyFurnaceRecipeBookComponent recipeBookComponent = new AlloyFurnaceRecipeBookComponent();
    private boolean widthTooNarrow;

    public AlloyFurnaceScreen(AlloyFurnaceMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);

        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.widthTooNarrow = this.width < 379;
        this.recipeBookComponent.init(this.leftPos, this.topPos, this.minecraft, false, this.menu);
        this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
        this.addRenderableWidget(new ImageButton(this.leftPos + 20, this.height / 2 - 49, 20, 18, RecipeBookComponent.RECIPE_BUTTON_SPRITES, button -> {
            this.recipeBookComponent.toggleVisibility();
            this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
            button.setPosition(this.leftPos + 20, this.height / 2 - 49);
        }));
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.recipeBookComponent.tick();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        int progress = Mth.ceil(this.menu.getProgress() * 24);
        guiGraphics.blit(TEXTURE, this.leftPos + 79, this.topPos + 34, 176, 14, progress, 17);

        int burnTime = Mth.ceil(this.menu.getBurnTimePercent() * 14);
        guiGraphics.blit(TEXTURE, this.leftPos + 56, this.topPos + 36 + 14 - burnTime, 176, 14 - burnTime, 14, burnTime);
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
    }

    @Override
    public void recipesUpdated() {

    }

    @Override
    public RecipeBookComponent getRecipeBookComponent() {
        return this.recipeBookComponent;
    }

    public static class AlloyFurnaceRecipeBookComponent extends RecipeBookComponent {
        @Nullable
        private Ingredient fuels;

        @Override
        public void setupGhostRecipe(RecipeHolder<?> recipeHolder, List<Slot> list) {
            ItemStack result = recipeHolder.value().getResultItem(this.minecraft.level.registryAccess());
            this.ghostRecipe.setRecipe(recipeHolder);
            this.ghostRecipe.addIngredient(Ingredient.of(result), list.get(AlloyFurnaceBlockEntity.OUTPUT_SLOT).x, list.get(AlloyFurnaceBlockEntity.OUTPUT_SLOT).y);
            NonNullList<Ingredient> ingredients = recipeHolder.value().getIngredients();
            Slot fuelSlot = list.get(AlloyFurnaceBlockEntity.FUEL_SLOT);
            if(fuelSlot.getItem().isEmpty()) {
                if(this.fuels == null) {
                    this.fuels = Ingredient.of(this.getFuelItems().stream().filter(item -> item.isEnabled(this.minecraft.level.enabledFeatures())).map(ItemStack::new));
                }

                this.ghostRecipe.addIngredient(this.fuels, fuelSlot.x, fuelSlot.y);
            }

            Iterator<Ingredient> iterator = ingredients.iterator();
            for(int i = 0; i < 3; i++) {
                if(!iterator.hasNext())
                    return;

                Ingredient ingredient = iterator.next();
                if(!ingredient.isEmpty()) {
                    Slot slot = list.get(i);
                    this.ghostRecipe.addIngredient(ingredient, slot.x, slot.y);
                }
            }
        }

        private Set<Item> getFuelItems() {
            return FurnaceBlockEntity.getFuel().keySet();
        }
    }
}
