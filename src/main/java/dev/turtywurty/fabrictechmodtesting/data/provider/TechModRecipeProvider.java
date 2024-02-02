package dev.turtywurty.fabrictechmodtesting.data.provider;

import dev.turtywurty.fabrictechmodtesting.core.init.BlockInit;
import dev.turtywurty.fabrictechmodtesting.core.init.ItemInit;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
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
    }
}
