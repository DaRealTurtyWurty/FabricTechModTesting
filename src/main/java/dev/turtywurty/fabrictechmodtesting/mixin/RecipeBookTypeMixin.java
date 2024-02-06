package dev.turtywurty.fabrictechmodtesting.mixin;

import net.minecraft.world.inventory.RecipeBookType;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(RecipeBookType.class)
@Unique
public abstract class RecipeBookTypeMixin {
    @Shadow
    @Final
    @Mutable
    private static RecipeBookType[] $VALUES;

    private static final RecipeBookType ALLOY_FURNACE = fabrictechmodtesting$addVariant("ALLOY_FURNACE");

    @Invoker("<init>")
    public static RecipeBookType fabrictechmodtesting$invokeInit(String internalName, int internalId) {
        throw new AssertionError();
    }

    @Unique
    private static RecipeBookType fabrictechmodtesting$addVariant(String internalName) {
        if(RecipeBookTypeMixin.$VALUES == null)
            throw new IllegalStateException("RecipeBookTypeMixin.$VALUES is null!");

        List<RecipeBookType> variants = new ArrayList<>(Arrays.asList(RecipeBookTypeMixin.$VALUES));
        RecipeBookType recipeBookType = fabrictechmodtesting$invokeInit(internalName, variants.get(variants.size() - 1).ordinal() + 1);
        variants.add(recipeBookType);
        RecipeBookTypeMixin.$VALUES = variants.toArray(new RecipeBookType[0]);
        return recipeBookType;
    }
}
