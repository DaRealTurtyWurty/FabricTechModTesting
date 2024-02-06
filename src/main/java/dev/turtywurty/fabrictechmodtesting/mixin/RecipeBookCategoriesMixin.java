package dev.turtywurty.fabrictechmodtesting.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import dev.turtywurty.fabrictechmodtesting.core.init.ItemInit;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(RecipeBookCategories.class)
@Unique
public class RecipeBookCategoriesMixin {
    @Shadow
    @Final
    @Mutable
    private static RecipeBookCategories[] $VALUES;

    @Unique
    private static final RecipeBookCategories ALLOY_FURNACE =
            fabrictechmodtesting$addVariant("ALLOY_FURNACE", new ItemStack(ItemInit.STEEL_INGOT));

    @Unique
    private static final RecipeBookCategories ALLOY_FURNACE_SEARCH =
            fabrictechmodtesting$addVariant("ALLOY_FURNACE_SEARCH", new ItemStack(Items.COMPASS));

    @Unique
    private static final List<RecipeBookCategories> ALLOY_FURNACE_CATEGORIES = ImmutableList.of(ALLOY_FURNACE);

    @Shadow
    @Mutable
    @Final
    public static Map<RecipeBookCategories, List<RecipeBookCategories>> AGGREGATE_CATEGORIES;

    static {
        Map<RecipeBookCategories, List<RecipeBookCategories>> mutableCopy = new HashMap<>(AGGREGATE_CATEGORIES);
        mutableCopy.put(ALLOY_FURNACE_SEARCH, ImmutableList.of(ALLOY_FURNACE));
        AGGREGATE_CATEGORIES = ImmutableMap.copyOf(mutableCopy);
    }

    @Invoker("<init>")
    public static RecipeBookCategories fabrictechmodtesting$invokeInit(String internalName, int internalId, ItemStack... stacks) {
        throw new AssertionError();
    }

    @Unique
    private static RecipeBookCategories fabrictechmodtesting$addVariant(String internalName, ItemStack... stacks) {
        if (RecipeBookCategoriesMixin.$VALUES == null)
            throw new IllegalStateException("RecipeBookTypeMixin.$VALUES is null!");

        List<RecipeBookCategories> variants = new ArrayList<>(Arrays.asList(RecipeBookCategoriesMixin.$VALUES));
        RecipeBookCategories recipeBookType = fabrictechmodtesting$invokeInit(internalName, variants.get(variants.size() - 1).ordinal() + 1, stacks);
        variants.add(recipeBookType);
        RecipeBookCategoriesMixin.$VALUES = variants.toArray(new RecipeBookCategories[0]);
        return recipeBookType;
    }

    @Inject(method = "getCategories", at = @At("HEAD"), cancellable = true)
    private static void fabrictechmodtesting$addAlloyFurnaceCategory(RecipeBookType recipeBookType, CallbackInfoReturnable<List<RecipeBookCategories>> callback) {
        if (recipeBookType == RecipeBookType.valueOf("ALLOY_FURNACE")) {
            callback.setReturnValue(ALLOY_FURNACE_CATEGORIES);
        }
    }
}
