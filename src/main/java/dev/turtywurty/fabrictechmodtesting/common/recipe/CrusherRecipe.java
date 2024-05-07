package dev.turtywurty.fabrictechmodtesting.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.CrusherBlockEntity;
import dev.turtywurty.fabrictechmodtesting.core.util.CountedIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// TODO: Re-define this to have the outputs use random or predetermined chances
public record CrusherRecipe(CountedIngredient input, ItemStack outputA, ItemStack outputB,
                            int processTime) implements Recipe<SimpleContainer>, CountedIngredient.CountedIngredientRecipe {
    @Override
    public boolean matches(SimpleContainer container, Level level) {
        return this.input.test(container.getItem(CrusherBlockEntity.INPUT_SLOT));
    }

    @Override
    public @NotNull ItemStack assemble(SimpleContainer container, RegistryAccess registryAccess) {
        return this.outputA.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(RegistryAccess registryAccess) {
        return this.outputA.copy();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return CrusherRecipe.Serializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return CrusherRecipe.Type.INSTANCE;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.of(this.input.getMatchingStacks().toArray(new ItemStack[0])));
    }

    @Override
    public List<CountedIngredient> getCountedIngredients() {
        return List.of(this.input);
    }

    public static class Type implements RecipeType<CrusherRecipe> {
        public static final CrusherRecipe.Type INSTANCE = new CrusherRecipe.Type();

        private Type() {
        }
    }

    public static class Serializer implements RecipeSerializer<CrusherRecipe> {
        public static final CrusherRecipe.Serializer INSTANCE = new CrusherRecipe.Serializer();
        private static final Codec<CrusherRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CountedIngredient.CODEC.fieldOf("input").forGetter(CrusherRecipe::input),
                ItemStack.CODEC.fieldOf("output_a").forGetter(CrusherRecipe::outputA),
                ItemStack.CODEC.fieldOf("output_b").forGetter(CrusherRecipe::outputB),
                Codec.INT.fieldOf("process_time").forGetter(CrusherRecipe::processTime)
        ).apply(instance, CrusherRecipe::new));

        @Override
        public @NotNull Codec<CrusherRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull CrusherRecipe fromNetwork(FriendlyByteBuf friendlyByteBuf) {
            var input = CountedIngredient.SERIALIZER.read(friendlyByteBuf);
            var outputA = friendlyByteBuf.readItem();
            var outputB = friendlyByteBuf.readItem();
            var processTime = friendlyByteBuf.readVarInt();
            return new CrusherRecipe(input, outputA, outputB, processTime);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, CrusherRecipe recipe) {
            CountedIngredient.SERIALIZER.write(friendlyByteBuf, recipe.input());
            friendlyByteBuf.writeItem(recipe.outputA());
            friendlyByteBuf.writeItem(recipe.outputB());
            friendlyByteBuf.writeVarInt(recipe.processTime());
        }
    }
}