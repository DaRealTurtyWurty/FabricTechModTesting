package dev.turtywurty.fabrictechmodtesting.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.AlloyFurnaceBlockEntity;
import dev.turtywurty.fabrictechmodtesting.core.util.CountedIngredient;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record AlloyFurnaceRecipe(CountedIngredient inputA, CountedIngredient inputB, ItemStack output, int cookTime)
        implements Recipe<SimpleContainer>, CountedIngredient.CountedIngredientRecipe {
    @Override
    public boolean matches(SimpleContainer container, Level level) {
        return this.inputA.test(container.getItem(AlloyFurnaceBlockEntity.INPUT_SLOT_0)) &&
                this.inputB.test(container.getItem(AlloyFurnaceBlockEntity.INPUT_SLOT_1));
    }

    @Override
    public @NotNull ItemStack assemble(SimpleContainer container, RegistryAccess registryAccess) {
        return output().copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(RegistryAccess registryAccess) {
        return output().copy();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return AlloyFurnaceRecipe.Serializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public @NotNull String getGroup() {
        return FabricTechModTesting.id("alloy_furnace").toString();
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY,
                Ingredient.of(inputA.getMatchingStacks().toArray(new ItemStack[0])),
                Ingredient.of(inputB.getMatchingStacks().toArray(new ItemStack[0])));
    }

    @Override
    public List<CountedIngredient> getCountedIngredients() {
        return List.of(CountedIngredient.EMPTY, inputA, inputB);
    }

    @Override
    public int getRealSlotIndex(int index) {
        if(index == 0) return AlloyFurnaceBlockEntity.FUEL_SLOT;
        return index - 1;
    }

    public static class Type implements RecipeType<AlloyFurnaceRecipe> {
        public static final Type INSTANCE = new Type();

        private Type() {
        }
    }

    public static class Serializer implements RecipeSerializer<AlloyFurnaceRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        private static final Codec<AlloyFurnaceRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CountedIngredient.CODEC.fieldOf("inputA").forGetter(AlloyFurnaceRecipe::inputA),
                CountedIngredient.CODEC.fieldOf("inputB").forGetter(AlloyFurnaceRecipe::inputB),
                ItemStack.CODEC.fieldOf("output").forGetter(AlloyFurnaceRecipe::output),
                Codec.INT.fieldOf("cook_time").forGetter(AlloyFurnaceRecipe::cookTime)
        ).apply(instance, AlloyFurnaceRecipe::new));

        @Override
        public @NotNull Codec<AlloyFurnaceRecipe> codec() {
            return Serializer.CODEC;
        }

        @Override
        public @NotNull AlloyFurnaceRecipe fromNetwork(FriendlyByteBuf friendlyByteBuf) {
            var inputA = CountedIngredient.SERIALIZER.read(friendlyByteBuf);
            var inputB = CountedIngredient.SERIALIZER.read(friendlyByteBuf);
            ItemStack output = friendlyByteBuf.readItem();
            int cookTime = friendlyByteBuf.readInt();
            return new AlloyFurnaceRecipe(inputA, inputB, output, cookTime);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, AlloyFurnaceRecipe recipe) {
            CountedIngredient.SERIALIZER.write(friendlyByteBuf, recipe.inputA());
            CountedIngredient.SERIALIZER.write(friendlyByteBuf, recipe.inputB());
            friendlyByteBuf.writeItem(recipe.output());
            friendlyByteBuf.writeInt(recipe.cookTime());
        }
    }
}
