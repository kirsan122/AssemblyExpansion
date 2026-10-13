package kirsan122.assemblyexpansion.common.item.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnergyBatteryItem extends Item {
    private final int maxEnergy;
    private final int maxExtract;

    public EnergyBatteryItem(int maxEnergy, int maxExtract, Properties properties) {
        super(properties.stacksTo(1));
        this.maxEnergy = maxEnergy;
        this.maxExtract = maxExtract;
    }
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> {

            tooltip.add(Component.translatable("tooltip.assembly_expansion.battery_info.energy", String.valueOf(energy.getEnergyStored()), String.valueOf(energy.getMaxEnergyStored())));
            tooltip.add(Component.translatable("tooltip.assembly_expansion.battery_info.maxExtract", String.valueOf(this.getTransferSpeed(stack))));

        });
    }
    public int getMaxEnergy(ItemStack stack) {
        return this.maxEnergy;
    }

    public int getTransferSpeed(ItemStack stack) {
        return this.maxExtract;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        return false;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ENERGY)
                .map(energy -> Math.round((float) energy.getEnergyStored() * 13.0F / (float) energy.getMaxEnergyStored()))
                .orElse(0);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {


            private final LazyOptional<IEnergyStorage> lazyEnergy = LazyOptional.of(() -> {

                int initialEnergy = 0;
                if (stack.hasTag() && stack.getTag().contains("energy")) {
                    initialEnergy = stack.getTag().getInt("energy");
                } else if (nbt != null && nbt.contains("energy")) {

                    initialEnergy = nbt.getInt("energy");
                }


                return new EnergyStorage(getMaxEnergy(stack), getTransferSpeed(stack), getTransferSpeed(stack), initialEnergy) {

                    @Override
                    public int extractEnergy(int maxExtract, boolean simulate) {
                        int extracted = super.extractEnergy(maxExtract, simulate);
                        if (!simulate && extracted > 0) {

                            stack.getOrCreateTag().putInt("energy", this.getEnergyStored());
                        }
                        return extracted;
                    }

                    @Override
                    public int receiveEnergy(int maxReceive, boolean simulate) {
                        int received = super.receiveEnergy(maxReceive, simulate);
                        if (!simulate && received > 0) {

                            stack.getOrCreateTag().putInt("energy", this.getEnergyStored());
                        }
                        return received;
                    }



                };
            });

            @Override
            public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                if (cap == ForgeCapabilities.ENERGY) {
                    return lazyEnergy.cast();
                }
                return LazyOptional.empty();
            }
        };
    }
}
