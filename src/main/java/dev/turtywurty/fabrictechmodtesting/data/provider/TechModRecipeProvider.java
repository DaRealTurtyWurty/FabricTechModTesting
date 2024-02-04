package dev.turtywurty.fabrictechmodtesting.data.provider;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.core.init.BlockInit;
import dev.turtywurty.fabrictechmodtesting.core.init.ItemInit;
import dev.turtywurty.fabrictechmodtesting.core.util.CountedIngredient;
import dev.turtywurty.fabrictechmodtesting.data.builder.AlloyFurnaceRecipeBuilder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public class TechModRecipeProvider extends FabricRecipeProvider {
    public TechModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    private static void mineralBlockStorage(RecipeOutput output, ItemLike item, ItemLike block) {
        nineBlockStorageRecipes(output, RecipeCategory.MISC, item, RecipeCategory.BUILDING_BLOCKS, block);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        mineralBlockStorage(output, ItemInit.STEEL_INGOT, BlockInit.STEEL_BLOCK);
        alloyFurnaceRecipe(output, RecipeCategory.MISC,
                CountedIngredient.of(8, Items.IRON_INGOT),
                CountedIngredient.of(1, Items.COAL),
                new ItemStack(ItemInit.STEEL_INGOT, 2),
                200);
    }

    private static void alloyFurnaceRecipe(RecipeOutput recipeOutput, RecipeCategory category, CountedIngredient inputA, CountedIngredient inputB, ItemStack output, int time) {
        alloyFurnaceRecipe(recipeOutput, category, inputA, inputB, output, time, getSimpleRecipeName(output.getItem()));
    }

    private static void alloyFurnaceRecipe(RecipeOutput recipeOutput, RecipeCategory category, CountedIngredient inputA, CountedIngredient inputB, ItemStack output, int time, String name) {
        var builder = new AlloyFurnaceRecipeBuilder(
                category,
                inputA,
                inputB,
                output,
                time);
        builder.save(recipeOutput, FabricTechModTesting.id("alloy_" + name));
    }
}
