package dev.turtywurty.fabrictechmodtesting.mixin;

import dev.turtywurty.fabrictechmodtesting.core.util.CountedIngredient;
import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerPlaceRecipe.class)
public abstract class ServerPlaceRecipeMixin {
    @Shadow @Final protected StackedContents stackedContents;

    @Shadow protected abstract void handleRecipeClicked(RecipeHolder<? extends Recipe<C>> recipeHolder, boolean bl);

    @Shadow protected abstract void clearGrid();

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(
            method = "recipeClicked",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/StackedContents;canCraft(Lnet/minecraft/world/item/crafting/Recipe;Lit/unimi/dsi/fastutil/ints/IntList;)Z"
            ),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void fabrictechmodtesting$recipeClicked(ServerPlayer serverPlayer, @Nullable RecipeHolder recipeHolder,
                                                    boolean isShifting, CallbackInfo callback) {
        if(recipeHolder == null)
            return;

        Recipe<?> recipe = recipeHolder.value();
        if (recipeHolder.value() instanceof CountedIngredient.CountedIngredientRecipe countedIngredientRecipe) {

        }
    }
}
