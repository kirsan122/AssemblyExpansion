package kirsan122.assemblyexpansion.common.item;

import kirsan122.assemblyexpansion.common.block.FluidTankBlock;
import kirsan122.assemblyexpansion.client.render.item.FluidTankItemRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class FluidTankBlockItem extends BlockItem {
    private final int maxCapacity;
    public FluidTankBlockItem(FluidTankBlock block, Properties properties) {

        super(block, properties);
        this.maxCapacity = block.maxCapacity;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable net.minecraft.world.level.Level level, @NotNull java.util.List<net.minecraft.network.chat.Component> tooltip, @NotNull net.minecraft.world.item.TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);


        CompoundTag tag = stack.getTag();
        CompoundTag beTag = (tag != null && tag.contains("BlockEntityTag")) ? tag.getCompound("BlockEntityTag") : null;

        String fluidName = "";
        int amount = 0;

        if (beTag != null && beTag.contains("FluidName")) {
            net.minecraftforge.fluids.FluidStack fluidStack = net.minecraftforge.fluids.FluidStack.loadFluidStackFromNBT(beTag);
            if (!fluidStack.isEmpty()) {
                fluidName = fluidStack.getDisplayName().getString();
                amount = fluidStack.getAmount();
            }
        }

        if (amount > 0) {
            tooltip.add(Component.literal(fluidName + ": ").withStyle(ChatFormatting.DARK_PURPLE)
                    .append(Component.literal(amount + " / " + this.maxCapacity).withStyle(ChatFormatting.WHITE))
                    .append(Component.literal(" mB").withStyle(ChatFormatting.DARK_PURPLE)));
        } else {

            tooltip.add(Component.translatable("tooltip.assembly_expansion.empty").withStyle(ChatFormatting.GRAY));
        }

        if (Screen.hasShiftDown()) {

            Component inventorySummary = Component.translatable("tooltip.assembly_expansion.machines.inv.empty").withStyle(ChatFormatting.GRAY);


            if (beTag != null && beTag.contains("inventory")) {
                CompoundTag invTag = beTag.getCompound("inventory");


                if (invTag.contains("Items", Tag.TAG_LIST)) {
                    ListTag itemsList = invTag.getList("Items", Tag.TAG_COMPOUND);


                    net.minecraft.network.chat.MutableComponent itemsComponent = Component.empty();

                    for (int i = 0; i < itemsList.size(); i++) {
                        CompoundTag itemTag = itemsList.getCompound(i);
                        ItemStack itemInSlot = ItemStack.of(itemTag);

                        if (!itemInSlot.isEmpty()) {
                            if (!itemsComponent.getSiblings().isEmpty()) {
                                itemsComponent.append(Component.literal(", ").withStyle(ChatFormatting.GRAY));
                            }

                            itemsComponent.append(itemInSlot.getHoverName())
                                    .append(Component.literal(" x" + itemInSlot.getCount()).withStyle(ChatFormatting.WHITE));
                        }
                    }

                    if (!itemsComponent.getSiblings().isEmpty()) {
                        inventorySummary = itemsComponent;
                    }
                }
            }


            tooltip.add(Component.translatable("tooltip.assembly_expansion.machines.info.inv")
                    .withStyle(ChatFormatting.AQUA)
                    .append(inventorySummary));
        } else if (Screen.hasControlDown()) {
            tooltip.add(Component.translatable("tooltip.assembly_expansion.machines.fluid_tank"));

        } else {

            tooltip.add(Component.translatable("tooltip.assembly_expansion.universal.ctrl"));
            tooltip.add(Component.translatable("tooltip.assembly_expansion.universal.shift"));
        }
    }



    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return FluidTankItemRenderer.INSTANCE;
            }
        });
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new FluidHandlerItemStackSimple(stack, 10000);
    }
}
