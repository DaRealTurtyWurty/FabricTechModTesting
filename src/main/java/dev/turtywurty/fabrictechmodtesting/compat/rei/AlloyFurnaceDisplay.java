package dev.turtywurty.fabrictechmodtesting.compat.rei;

import dev.turtywurty.fabrictechmodtesting.common.recipe.AlloyFurnaceRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

public class AlloyFurnaceDisplay extends BasicDisplay {
    public AlloyFurnaceDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        super(inputs, outputs);
    }

    public AlloyFurnaceDisplay(RecipeHolder<AlloyFurnaceRecipe> recipeHolder) {
        super(getInputs(recipeHolder), getOutputs(recipeHolder));
    }

    private static List<EntryIngredient> getInputs(RecipeHolder<AlloyFurnaceRecipe> recipeHolder) {
        if (recipeHolder == null) return List.of();

        AlloyFurnaceRecipe recipe = recipeHolder.value();
        List<EntryIngredient> inputs = new ArrayList<>();
        for (Ingredient ingredient : recipe.getIngredients()) {
            inputs.add(EntryIngredients.ofIngredient(ingredient));
        }

        return inputs;
    }

    private static List<EntryIngredient> getOutputs(RecipeHolder<AlloyFurnaceRecipe> recipeHolder) {
        if (recipeHolder == null) return List.of();

        AlloyFurnaceRecipe recipe = recipeHolder.value();
        List<EntryIngredient> outputs = new ArrayList<>();
        outputs.add(EntryIngredients.of(recipe.getResultItem(null)));
        return outputs;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return AlloyFurnaceCategory.CATEGORY_ID;
    }
}
