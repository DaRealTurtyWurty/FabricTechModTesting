package dev.turtywurty.fabrictechmodtesting.data.builder;

import dev.turtywurty.fabrictechmodtesting.common.recipe.AlloyFurnaceRecipe;
import dev.turtywurty.fabrictechmodtesting.core.util.CountedIngredient;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
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

public class AlloyFurnaceRecipeBuilder implements RecipeBuilder {
    private final CountedIngredient inputA, inputB;
    private final ItemStack output;
    private final int time;

    private final RecipeCategory category;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public AlloyFurnaceRecipeBuilder(RecipeCategory category, CountedIngredient inputA, CountedIngredient inputB, ItemStack output, int time) {
        this.category = category;
        this.inputA = inputA;
        this.inputB = inputB;
        this.output = output;
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
    public @NotNull Item getResult() {
        return this.output.getItem();
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation resourceLocation) {
        Advancement.Builder builder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceLocation))
                .rewards(AdvancementRewards.Builder.recipe(resourceLocation))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(builder::addCriterion);
        recipeOutput.accept(resourceLocation,
                new AlloyFurnaceRecipe(this.inputA, this.inputB, this.output, this.time),
                builder.build(resourceLocation.withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }
}
