package dev.turtywurty.fabrictechmodtesting.mixin;

import dev.turtywurty.fabrictechmodtesting.common.recipe.AlloyFurnaceRecipe;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@Mixin(ClientRecipeBook.class)
public class ClientRecipeBookMixin {
    @Inject(method = "getCategory", at = @At("HEAD"), cancellable = true)
    private static void fabrictechmodtesting$getCategory(RecipeHolder<?> recipeHolder, CallbackInfoReturnable<RecipeBookCategories> callback) {
        if (recipeHolder.value() instanceof AlloyFurnaceRecipe) {
            callback.setReturnValue(RecipeBookCategories.valueOf("ALLOY_FURNACE"));
        }
    }

    @Inject(method = "categorizeAndGroupRecipes", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void fabrictechmodtesting$categorizeAndGroupRecipes(
            Iterable<RecipeHolder<?>> recipes,
            CallbackInfoReturnable<Map<RecipeBookCategories, List<List<RecipeHolder<?>>>>> callback,
            Map<RecipeBookCategories, List<List<RecipeHolder<?>>>> map) {
        List<RecipeHolder<?>> alloyFurnaceRecipes = StreamSupport.stream(recipes.spliterator(), false)
                .filter(recipeHolder -> recipeHolder.value() instanceof AlloyFurnaceRecipe)
                .toList();
        map.put(RecipeBookCategories.valueOf("ALLOY_FURNACE"), List.of(alloyFurnaceRecipes));
    }
}
