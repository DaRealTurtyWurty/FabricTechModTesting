package dev.turtywurty.fabrictechmodtesting.core.init;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.common.recipe.AlloyFurnaceRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class RecipeInit {
    public static final RecipeType<AlloyFurnaceRecipe> ALLOY_FURNACE_RECIPE =
            register("alloy_furnace", AlloyFurnaceRecipe.Type.INSTANCE);

    public static final RecipeSerializer<AlloyFurnaceRecipe> ALLOY_FURNACE_SERIALIZER =
            register("alloy_furnace", AlloyFurnaceRecipe.Serializer.INSTANCE);

    public static <C extends Recipe<?>, T extends RecipeSerializer<C>> T register(String name, T item) {
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, FabricTechModTesting.id(name), item);
    }

    public static <C extends Recipe<?>, T extends RecipeType<C>> T register(String name, T item) {
        return Registry.register(BuiltInRegistries.RECIPE_TYPE, FabricTechModTesting.id(name), item);
    }

    public static void init() {

    }
}
