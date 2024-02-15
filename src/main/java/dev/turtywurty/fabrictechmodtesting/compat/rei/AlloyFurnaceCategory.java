package dev.turtywurty.fabrictechmodtesting.compat.rei;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.AlloyFurnaceBlockEntity;
import dev.turtywurty.fabrictechmodtesting.core.init.BlockInit;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class AlloyFurnaceCategory implements DisplayCategory<BasicDisplay> {
    public static final CategoryIdentifier<AlloyFurnaceDisplay> CATEGORY_ID =
            CategoryIdentifier.of(FabricTechModTesting.MOD_ID, "alloy_furnace");

    @Override
    public CategoryIdentifier<? extends BasicDisplay> getCategoryIdentifier() {
        return CATEGORY_ID;
    }

    @Override
    public Component getTitle() {
        return AlloyFurnaceBlockEntity.TITLE;
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(BlockInit.ALLOY_FURNACE);
    }

    @Override
    public List<Widget> setupDisplay(BasicDisplay display, Rectangle bounds) {
        var startPoint = new Point(bounds.getCenterX() - 80, bounds.getCenterY() - 35);
        List<Widget> widgets = new ArrayList<>();

        // Background
        widgets.add(Widgets.createRecipeBase(bounds));

        // Progress Arrow
        widgets.add(Widgets.createArrow(new Point(startPoint.x + 79, startPoint.y + 34))
                .animationDurationTicks(100));

        // Burn Time
        widgets.add(Widgets.createBurningFire(new Point(startPoint.x + 56, startPoint.y + 36))
                .animationDurationTicks(600));

        // Output Slot
        widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 116, startPoint.y + 35)));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 116, startPoint.y + 35))
                .entries(display.getOutputEntries().get(0))
                .disableBackground()
                .markOutput());

        // Input Slots
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 42, startPoint.y + 17))
                .entries(display.getInputEntries().get(0))
                .markInput());
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 70, startPoint.y + 17))
                .entries(display.getInputEntries().get(1))
                .markInput());

        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 90;
    }
}
