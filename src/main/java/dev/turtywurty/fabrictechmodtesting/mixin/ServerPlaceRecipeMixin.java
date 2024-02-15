package dev.turtywurty.fabrictechmodtesting.mixin;

import dev.turtywurty.fabrictechmodtesting.core.util.CountedIngredient;
import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ServerPlaceRecipe.class)
public abstract class ServerPlaceRecipeMixin {
    @Shadow
    protected Inventory inventory;

    @Shadow
    protected abstract void clearGrid();

    // Find the stack with the closest count to the ingredient's count
    @Unique
    private static @Nullable ItemStack fabrictechmodtesting$findClosestMatchingStack(Inventory inventory, List<Integer> ignoreList, CountedIngredient ingredient) {
        ItemStack closestStack = null;
        int closestCount = Integer.MAX_VALUE;

        int closestIndex = -1;
        for (int index = 0; index < inventory.getContainerSize(); index++) {
            if (ignoreList.contains(index))
                continue;

            ItemStack stack = inventory.getItem(index);
            if (ingredient.test(stack)) {
                int count = stack.getCount();
                if (count == ingredient.count()) {
                    ignoreList.add(index);
                    return stack;
                } else if (count < closestCount) {
                    closestStack = stack;
                    closestCount = count;
                    closestIndex = index;
                }
            }
        }

        if (closestIndex != -1)
            ignoreList.add(closestIndex);

        return closestStack;
    }

    @Inject(
            method = "recipeClicked",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/StackedContents;canCraft(Lnet/minecraft/world/item/crafting/Recipe;Lit/unimi/dsi/fastutil/ints/IntList;)Z"
            ),
            cancellable = true
    )
    private void fabrictechmodtesting$recipeClicked(ServerPlayer serverPlayer, @Nullable RecipeHolder<? extends Recipe<?>> recipeHolder,
                                                    boolean isShifting, CallbackInfo callback) {
        if (recipeHolder == null || !(recipeHolder.value() instanceof CountedIngredient.CountedIngredientRecipe countedIngredientRecipe))
            return;

        List<CountedIngredient> ingredients = countedIngredientRecipe.getCountedIngredients();
        List<ItemStack> matches = new ArrayList<>();
        List<Integer> ignoreList = new ArrayList<>();

        for (int index = 0; index < ingredients.size(); index++) {
            CountedIngredient ingredient = ingredients.get(index);
            Slot slot = serverPlayer.containerMenu.getSlot(countedIngredientRecipe.getRealSlotIndex(index));

            ItemStack stack = fabrictechmodtesting$findClosestMatchingStack(this.inventory, ignoreList, ingredient);
            if (stack == null) {
                clearGrid();
                serverPlayer.connection.send(new ClientboundPlaceGhostRecipePacket(serverPlayer.containerMenu.containerId, recipeHolder));
                this.inventory.setChanged();
                callback.cancel();
                return;
            }

            if(stack.isEmpty()) {
                matches.add(ItemStack.EMPTY);
                continue;
            }

            // check to see if the container can take that count
            if(!slot.mayPlace(stack.copyWithCount(ingredient.count()))) {
                clearGrid();
                serverPlayer.connection.send(new ClientboundPlaceGhostRecipePacket(serverPlayer.containerMenu.containerId, recipeHolder));
                this.inventory.setChanged();
                callback.cancel();
                return;
            }

            matches.add(stack);
        }

        if(matches.size() != ingredients.size()) {
            clearGrid();
            serverPlayer.connection.send(new ClientboundPlaceGhostRecipePacket(serverPlayer.containerMenu.containerId, recipeHolder));
        } else { // If all ingredients are found
            for (int index = 0; index < matches.size(); index++) {
                CountedIngredient ingredient = ingredients.get(index);

                int removeIndex = ignoreList.get(index);
                ItemStack copy = this.inventory.getItem(removeIndex).copyWithCount(ingredient.count());
                if (copy.isEmpty())
                    continue;

                this.inventory.removeItem(removeIndex, ingredient.count());
                serverPlayer.containerMenu.getSlot(countedIngredientRecipe.getRealSlotIndex(index)).safeInsert(copy);
            }
        }

        this.inventory.setChanged();
        serverPlayer.containerMenu.broadcastChanges();
        callback.cancel();
    }
}
