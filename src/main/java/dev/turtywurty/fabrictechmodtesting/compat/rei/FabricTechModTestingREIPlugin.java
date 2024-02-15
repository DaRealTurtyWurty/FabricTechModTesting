package dev.turtywurty.fabrictechmodtesting.compat.rei;

import dev.turtywurty.fabrictechmodtesting.client.screen.AlloyFurnaceScreen;
import dev.turtywurty.fabrictechmodtesting.common.menu.AlloyFurnaceMenu;
import dev.turtywurty.fabrictechmodtesting.common.recipe.AlloyFurnaceRecipe;
import dev.turtywurty.fabrictechmodtesting.core.init.BlockInit;
import dev.turtywurty.fabrictechmodtesting.core.init.RecipeInit;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.client.registry.transfer.simple.SimpleTransferHandler;
import me.shedaniel.rei.api.common.util.EntryStacks;

public class FabricTechModTestingREIPlugin implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new AlloyFurnaceCategory());
        registry.addWorkstations(AlloyFurnaceCategory.CATEGORY_ID, EntryStacks.of(BlockInit.ALLOY_FURNACE));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(AlloyFurnaceRecipe.class, RecipeInit.ALLOY_FURNACE_RECIPE, AlloyFurnaceDisplay::new);
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerClickArea(screen -> new Rectangle(79, 34, 24, 17),
                AlloyFurnaceScreen.class, AlloyFurnaceCategory.CATEGORY_ID);
    }

    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry) {
        registry.register(SimpleTransferHandler.create(AlloyFurnaceMenu.class, AlloyFurnaceCategory.CATEGORY_ID,
                new SimpleTransferHandler.IntRange(0, 1)));
    }
}
