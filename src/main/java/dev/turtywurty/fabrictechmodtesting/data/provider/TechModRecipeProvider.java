package dev.turtywurty.fabrictechmodtesting.data.provider;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.core.init.BlockInit;
import dev.turtywurty.fabrictechmodtesting.core.init.ItemInit;
import dev.turtywurty.fabrictechmodtesting.core.util.CountedIngredient;
import dev.turtywurty.fabrictechmodtesting.data.builder.AlloyFurnaceRecipeBuilder;
import dev.turtywurty.fabrictechmodtesting.data.builder.CrusherRecipeBuilder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

public class TechModRecipeProvider extends FabricRecipeProvider {
    public TechModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    private static void mineralBlockStorage(RecipeOutput output, ItemLike item, ItemLike block) {
        nineBlockStorageRecipes(output, RecipeCategory.MISC, item, RecipeCategory.BUILDING_BLOCKS, block);
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

    private static void crusherRecipe(RecipeOutput recipeOutput, RecipeCategory category, CountedIngredient input, ItemStack outputA, ItemStack outputB, int time) {
        var builder = new CrusherRecipeBuilder(
                category,
                input,
                outputA,
                outputB,
                time);

        if (outputB.isEmpty()) {
            builder.save(recipeOutput, FabricTechModTesting.id("crusher_" + getSimpleRecipeName(outputA.getItem())));
            return;
        }

        builder.save(recipeOutput, FabricTechModTesting.id("crusher_" + getSimpleRecipeName(outputA.getItem()) + "_and_" + getSimpleRecipeName(outputB.getItem())));
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

    @Override
    public void buildRecipes(RecipeOutput output) {
        mineralBlockStorage(output, ItemInit.STEEL_INGOT, BlockInit.STEEL_BLOCK);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockInit.ALLOY_FURNACE)
                .define('I', Blocks.IRON_BLOCK)
                .define('F', Items.FURNACE)
                .pattern("III")
                .pattern("IFI")
                .pattern("III")
                .group("alloy_furnace")
                .unlockedBy(getHasName(BlockInit.STEEL_BLOCK), has(BlockInit.STEEL_BLOCK))
                .save(output, FabricTechModTesting.id("alloy_furnace"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockInit.CRUSHER)
                .define('S', BlockInit.STEEL_BLOCK)
                .define('A', Items.ANVIL)
                .pattern("SSS")
                .pattern("SAS")
                .pattern("SSS")
                .group("crusher")
                .unlockedBy(getHasName(BlockInit.STEEL_BLOCK), has(BlockInit.STEEL_BLOCK))
                .save(output, FabricTechModTesting.id("crusher"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockInit.BASIC_BATTERY)
                .define('I', Items.IRON_INGOT)
                .define('R', Items.REDSTONE)
                .define('G', Items.GOLD_INGOT)
                .pattern("IGI")
                .pattern("GRG")
                .pattern("IGI")
                .group("basic_battery")
                .unlockedBy(getHasName(BlockInit.STEEL_BLOCK), has(BlockInit.STEEL_BLOCK))
                .save(output, FabricTechModTesting.id("basic_battery"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockInit.ADVANCED_BATTERY)
                .define('B', BlockInit.BASIC_BATTERY)
                .define('D', Items.DIAMOND)
                .pattern("DBD")
                .pattern("B B")
                .pattern("DBD")
                .group("advanced_battery")
                .unlockedBy(getHasName(BlockInit.BASIC_BATTERY), has(BlockInit.BASIC_BATTERY))
                .save(output, FabricTechModTesting.id("advanced_battery"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockInit.ELITE_BATTERY)
                .define('B', BlockInit.ADVANCED_BATTERY)
                .define('E', Items.EMERALD)
                .pattern("EBE")
                .pattern("B B")
                .pattern("EBE")
                .group("elite_battery")
                .unlockedBy(getHasName(BlockInit.ADVANCED_BATTERY), has(BlockInit.ADVANCED_BATTERY))
                .save(output, FabricTechModTesting.id("elite_battery"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockInit.ULTIMATE_BATTERY)
                .define('B', BlockInit.ELITE_BATTERY)
                .define('N', Items.NETHERITE_INGOT)
                .define('S', Items.NETHER_STAR)
                .pattern("NBN")
                .pattern("BSB")
                .pattern("NBN")
                .group("ultimate_battery")
                .unlockedBy(getHasName(BlockInit.ELITE_BATTERY), has(BlockInit.ELITE_BATTERY))
                .save(output, FabricTechModTesting.id("ultimate_battery"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockInit.COMBUSTION_GENERATOR)
                .define('B', BlockInit.BASIC_BATTERY)
                .define('S', ItemInit.STEEL_INGOT)
                .define('F', Blocks.FURNACE)
                .define('C', Items.COAL_BLOCK)
                .pattern("SFS")
                .pattern("BCB")
                .pattern("SFS")
                .group("combustion_generator")
                .unlockedBy(getHasName(BlockInit.BASIC_BATTERY), has(BlockInit.BASIC_BATTERY))
                .save(output, FabricTechModTesting.id("combustion_generator"));

        alloyFurnaceRecipe(output, RecipeCategory.MISC,
                CountedIngredient.of(8, Items.IRON_INGOT),
                CountedIngredient.of(1, Items.COAL),
                new ItemStack(ItemInit.STEEL_INGOT, 2),
                200);

        crusherRecipe(output, RecipeCategory.MISC,
                CountedIngredient.of(1, Items.IRON_ORE),
                new ItemStack(Items.RAW_IRON, 2),
                new ItemStack(Items.IRON_NUGGET, 1),
                200);

        crusherRecipe(output, RecipeCategory.MISC,
                CountedIngredient.of(1, Items.GOLD_ORE),
                new ItemStack(Items.RAW_GOLD, 2),
                new ItemStack(Items.GOLD_NUGGET, 1),
                200);
    }
}
