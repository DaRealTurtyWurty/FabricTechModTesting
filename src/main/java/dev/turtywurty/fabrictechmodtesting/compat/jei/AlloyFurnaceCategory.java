package dev.turtywurty.fabrictechmodtesting.compat.jei;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.AlloyFurnaceBlockEntity;
import dev.turtywurty.fabrictechmodtesting.common.recipe.AlloyFurnaceRecipe;
import dev.turtywurty.fabrictechmodtesting.core.init.BlockInit;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.jetbrains.annotations.NotNull;

public class AlloyFurnaceCategory implements IRecipeCategory<AlloyFurnaceRecipe> {
    public static final ResourceLocation UID = FabricTechModTesting.id("alloy_furnace");
    public static final RecipeType<AlloyFurnaceRecipe> RECIPE_TYPE = new RecipeType<>(UID, AlloyFurnaceRecipe.class);

    private static final ResourceLocation TEXTURE = FabricTechModTesting.id("textures/gui/alloy_furnace.png");

    private final IDrawable background;
    private final IDrawable icon;

    public AlloyFurnaceCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, BlockInit.ALLOY_FURNACE.asItem().getDefaultInstance());
    }

    @Override
    public @NotNull RecipeType<AlloyFurnaceRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return AlloyFurnaceBlockEntity.TITLE;
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AlloyFurnaceRecipe recipe, IFocusGroup focuses) {
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        int startIndex = 1;
        if (ingredients.size() < 3) {
            startIndex = 0;
        }

        builder.addSlot(RecipeIngredientRole.INPUT, 42, 17).addIngredients(ingredients.get(startIndex++));
        builder.addSlot(RecipeIngredientRole.INPUT, 70, 17).addIngredients(ingredients.get(startIndex++));
        builder.addSlot(RecipeIngredientRole.CATALYST, 56, 53)
                .addItemStacks(AbstractFurnaceBlockEntity.getFuel()
                        .keySet()
                        .stream()
                        .map(Item::getDefaultInstance)
                        .toList());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 116, 35).addItemStack(recipe.getResultItem(null));
    }
}
