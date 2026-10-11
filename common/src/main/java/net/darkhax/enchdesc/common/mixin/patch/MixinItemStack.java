package net.darkhax.enchdesc.common.mixin.patch;

import net.darkhax.enchdesc.common.impl.EnchantmentDescriptionsMod;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ItemStack.class)
public class MixinItemStack {

    @Inject(method = "getTooltipLines(Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;)Ljava/util/List;", at = @At("RETURN"), cancellable = true)
    public void afterEnchantmentTooltips(Item.TooltipContext tooltipContext, Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir) {
        List<Component> tooltips = cir.getReturnValue();
        // 空列表可能表示物品提示已隐藏，保留原有行为。
        if (tooltips.isEmpty()) {
            return;
        }
        if (EnchantmentDescriptionsMod.canDisplayDescription() && !EnchantmentDescriptionsMod.isKeybindConditionMet() && EnchantmentDescriptionsMod.config.display_activate_text) {
            List<Component> updatedTooltips = new ArrayList<>(tooltips);
            updatedTooltips.add(EnchantmentDescriptionsMod.getKeybindText());
            cir.setReturnValue(updatedTooltips);
        }
    }

    @Inject(method = "addDetailsToTooltip(Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/item/component/TooltipDisplay;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;Ljava/util/function/Consumer;)V", at = @At("HEAD"))
    public void beforeTooltipComponent(CallbackInfo ci) {
        EnchantmentDescriptionsMod.CURRENT_STACK = (ItemStack) (Object) this;
    }
}
