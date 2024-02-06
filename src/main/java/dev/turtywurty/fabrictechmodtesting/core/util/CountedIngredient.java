package dev.turtywurty.fabrictechmodtesting.core.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.stream.Stream;

public record CountedIngredient(Ingredient ingredient, int count) implements CustomIngredient {
    public static final Codec<CountedIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(CountedIngredient::ingredient),
            Codec.INT.fieldOf("count").forGetter(CountedIngredient::count)
    ).apply(instance, CountedIngredient::new));
    public static final Serializer SERIALIZER = new Serializer();

    public static CountedIngredient of(int count, ItemLike... items) {
        return new CountedIngredient(Ingredient.of(items), count);
    }

    public static CountedIngredient of(int count, ItemStack... items) {
        return new CountedIngredient(Ingredient.of(items), count);
    }

    public static CountedIngredient of(int count, Stream<ItemStack> items) {
        return new CountedIngredient(Ingredient.of(items), count);
    }

    public static CountedIngredient of(int count, TagKey<Item> tagKey) {
        return new CountedIngredient(Ingredient.of(tagKey), count);
    }

    @Override
    public boolean test(ItemStack stack) {
        return this.ingredient.test(stack) && stack.getCount() >= this.count;
    }

    @Override
    public List<ItemStack> getMatchingStacks() {
        return List.of(this.ingredient.getItems());
    }

    @Override
    public boolean requiresTesting() {
        return false;
    }

    @Override
    public CustomIngredientSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer implements CustomIngredientSerializer<CountedIngredient> {
        public static final ResourceLocation ID = FabricTechModTesting.id("counted_ingredient");

        @Override
        public ResourceLocation getIdentifier() {
            return ID;
        }

        @Override
        public Codec<CountedIngredient> getCodec(boolean allowEmpty) {
            return CODEC;
        }

        @Override
        public CountedIngredient read(FriendlyByteBuf buf) {
            return new CountedIngredient(Ingredient.fromNetwork(buf), buf.readVarInt());
        }

        @Override
        public void write(FriendlyByteBuf buf, CountedIngredient ingredient) {
            ingredient.ingredient.toNetwork(buf);
            buf.writeVarInt(ingredient.count);
        }
    }
}
