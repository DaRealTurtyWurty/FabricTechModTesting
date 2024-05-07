package dev.turtywurty.fabrictechmodtesting.compat.jei;

import dev.turtywurty.fabrictechmodtesting.common.menu.AlloyFurnaceMenu;
import dev.turtywurty.fabrictechmodtesting.common.recipe.AlloyFurnaceRecipe;
import dev.turtywurty.fabrictechmodtesting.core.init.MenuTypeInit;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AlloyFurnaceTransferHandler implements IRecipeTransferHandler<AlloyFurnaceMenu, AlloyFurnaceRecipe> {
    public static NonNullList<ItemStack> getFirstItemStacks(IRecipeSlotsView recipeSlots) {
        List<IRecipeSlotView> slotViews = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT);
        return slotViews.stream()
                .map(AlloyFurnaceTransferHandler::getFirstItemStack)
                .collect(Collectors.toCollection(NonNullList::create));
    }

    private static ItemStack getFirstItemStack(IRecipeSlotView slotView) {
        return slotView.getDisplayedIngredient(VanillaTypes.ITEM_STACK)
                .or(() -> slotView.getIngredients(VanillaTypes.ITEM_STACK).findFirst())
                .map(ItemStack::copy)
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public @NotNull Class<? extends AlloyFurnaceMenu> getContainerClass() {
        return AlloyFurnaceMenu.class;
    }

    @Override
    public @NotNull Optional<MenuType<AlloyFurnaceMenu>> getMenuType() {
        return Optional.of(MenuTypeInit.ALLOY_FURNACE);
    }

    @Override
    public @NotNull RecipeType<AlloyFurnaceRecipe> getRecipeType() {
        return AlloyFurnaceCategory.RECIPE_TYPE;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(AlloyFurnaceMenu container, AlloyFurnaceRecipe recipe, IRecipeSlotsView recipeSlots,
                                                         Player player, boolean maxTransfer, boolean doTransfer) {
        if (doTransfer) {
            NonNullList<ItemStack> firstItemStacks = getFirstItemStacks(recipeSlots);
            for (int i = 0; i < firstItemStacks.size(); i++) {
                container.setItem(i, container.incrementStateId(), firstItemStacks.get(i));
            }

            List<RecipeHolder<AlloyFurnaceRecipe>> recipes = player.level().getRecipeManager().getAllRecipesFor(AlloyFurnaceRecipe.Type.INSTANCE).stream()
                    .toList();

            RecipeHolder<AlloyFurnaceRecipe> matchingRecipe = recipes.stream()
                    .filter(recipeHolder -> recipeHolder.value() == recipe)
                    .findFirst()
                    .orElse(null);
            if (matchingRecipe == null) {
                return () -> IRecipeTransferError.Type.INTERNAL;
            }

            container.setRecipeUsed(matchingRecipe);
        }

        return null;
    }
}
