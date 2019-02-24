package gregtech.common.items;

import gregtech.api.GTValues;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.type.Material;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.api.util.GTLog;
import gregtech.api.util.SlotDelegate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = GTValues.MODID)
public class EnchantmentTableTweaks {

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        onContainerOpen(event.getEntityPlayer(), event.getContainer());
    }

    @SubscribeEvent
    public static void onGuiOpen(GuiOpenEvent event) {
        if(event.getGui() instanceof GuiContainer) {
            GuiContainer guiContainer = (GuiContainer) event.getGui();
            onContainerOpen(Minecraft.getMinecraft().player, guiContainer.inventorySlots);
        }
    }

    private static void onContainerOpen(EntityPlayer player, Container container) {
        if(container instanceof ContainerEnchantment) {
            //wrap in try-catch because such kind of tweaks is subject to breaking
            //don't let it crash game if some mod borked it
            try {
                int index = EnchantmentLapisSlot.ENCHANTMENT_LAPIS_SLOT_INDEX;
                Slot previousLapisSlot = container.inventorySlots.get(index);
                EnchantmentLapisSlot resultSlot = new EnchantmentLapisSlot(previousLapisSlot);
                container.inventorySlots.set(index, resultSlot);
            } catch (Throwable exception) {
                GTLog.logger.warn("Failed to replace enchantment container slot", exception);
            }
        }
    }

    private static boolean isValidForEnchantment(ItemStack itemStack) {
        UnificationEntry entry = OreDictUnifier.getUnificationEntry(itemStack);
        if(entry == null || entry.orePrefix != OrePrefix.gem) {
            return false;
        }
        Material material = entry.material;
        return material == Materials.Lapis ||
            material == Materials.Lazurite ||
            material == Materials.Sodalite;
    }

    private static class EnchantmentLapisSlot extends SlotDelegate {

        private static final int ENCHANTMENT_LAPIS_SLOT_INDEX = 1;

        public EnchantmentLapisSlot(Slot delegate) {
            super(delegate);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return super.isItemValid(stack) || isValidForEnchantment(stack);
        }
    }

}