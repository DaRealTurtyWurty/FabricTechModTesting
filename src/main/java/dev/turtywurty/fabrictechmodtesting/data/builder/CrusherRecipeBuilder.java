package dev.turtywurty.fabrictechmodtesting.data.builder;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.common.recipe.CrusherRecipe;
import dev.turtywurty.fabrictechmodtesting.core.util.CountedIngredient;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class CrusherRecipeBuilder implements RecipeBuilder {
    private final CountedIngredient input;
    private final ItemStack outputA, outputB;
    private final int time;

    private final RecipeCategory category;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public CrusherRecipeBuilder(RecipeCategory category, CountedIngredient input, ItemStack outputA, ItemStack outputB, int time) {
        this.category = category;
        this.input = input;
        this.outputA = outputA;
        this.outputB = outputB;
        this.time = time;
    }

    @Override
    public @NotNull RecipeBuilder unlockedBy(String string, Criterion<?> criterion) {
        this.criteria.put(string, criterion);
        return this;
    }

    @Override
    public @NotNull RecipeBuilder group(@Nullable String value) {
        return this;
    }

    @Override
    public @Nullable Item getResult() {
        return this.outputA.getItem();
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation location) {
        Advancement.Builder builder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(location))
                .rewards(AdvancementRewards.Builder.recipe(location))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(builder::addCriterion);
        recipeOutput.accept(location,
                new CrusherRecipe(this.input, this.outputA, this.outputB, this.time),
                builder.build(location.withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    @Override
    public void save(RecipeOutput recipeOutput) {
        var location = new ResourceLocation(FabricTechModTesting.MOD_ID, BuiltInRegistries.ITEM.getKey(this.outputA.getItem()).getPath() + "_" + BuiltInRegistries.ITEM.getKey(this.outputA.getItem()).getPath());
        save(recipeOutput, location);
    }
}
