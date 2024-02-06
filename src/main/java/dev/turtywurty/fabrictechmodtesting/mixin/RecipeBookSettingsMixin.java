package dev.turtywurty.fabrictechmodtesting.mixin;

import net.minecraft.stats.RecipeBookSettings;
import net.minecraft.world.inventory.RecipeBookType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(RecipeBookSettings.class)
public class RecipeBookSettingsMixin {
    @Final
    @Shadow
    private Map<RecipeBookType, RecipeBookSettings.TypeSettings> states;

    @Inject(method="<init>(Ljava/util/Map;)V", at=@At("TAIL"))
    private void fabrictechmodtesting$init(CallbackInfo callback) {
        this.states.put(RecipeBookType.valueOf("ALLOY_FURNACE"), new RecipeBookSettings.TypeSettings(false, false));
    }
}
