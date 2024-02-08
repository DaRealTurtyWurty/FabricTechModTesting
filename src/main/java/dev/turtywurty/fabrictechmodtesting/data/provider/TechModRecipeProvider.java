package dev.turtywurty.fabrictechmodtesting.data.provider;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.core.init.BlockInit;
import dev.turtywurty.fabrictechmodtesting.core.init.ItemInit;
import dev.turtywurty.fabrictechmodtesting.core.util.CountedIngredient;
import dev.turtywurty.fabrictechmodtesting.data.builder.AlloyFurnaceRecipeBuilder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockInit.ALLOY_FURNACE)
                .define('S', BlockInit.STEEL_BLOCK)
                .define('F', Items.FURNACE)
                .pattern("SSS")
                .pattern("SFS")
                .pattern("SSS")
                .group("alloy_furnace")
                .unlockedBy(getHasName(BlockInit.STEEL_BLOCK), has(BlockInit.STEEL_BLOCK))
                .save(output, FabricTechModTesting.id("alloy_furnace"));
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

    public static void nineBlockStorageRecipes(
            RecipeOutput recipeOutput, RecipeCategory recipeCategory, ItemLike itemLike, RecipeCategory recipeCategory2, ItemLike itemLike2
    ) {
        nineBlockStorageRecipes(
                recipeOutput, recipeCategory, itemLike, recipeCategory2, itemLike2, getSimpleRecipeName(itemLike2), null, getSimpleRecipeName(itemLike), null
        );
    }

    public static void nineBlockStorageRecipes(
            RecipeOutput recipeOutput,
            RecipeCategory recipeCategory,
            ItemLike itemLike,
            RecipeCategory recipeCategory2,
            ItemLike itemLike2,
            String string,
            @Nullable String string2,
            String string3,
            @Nullable String string4
    ) {
        ShapelessRecipeBuilder.shapeless(recipeCategory, itemLike, 9)
                .requires(itemLike2)
                .group(string4)
                .unlockedBy(getHasName(itemLike2), has(itemLike2))
                .save(recipeOutput, FabricTechModTesting.id(string3));
        ShapedRecipeBuilder.shaped(recipeCategory2, itemLike2)
                .define('#', itemLike)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .group(string2)
                .unlockedBy(getHasName(itemLike), has(itemLike))
                .save(recipeOutput, FabricTechModTesting.id(string));
    }
}
