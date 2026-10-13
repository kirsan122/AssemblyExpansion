package kirsan122.assemblyexpansion.common.init;

import kirsan122.assemblyexpansion.common.item.FluidTankBlockItem;
import kirsan122.assemblyexpansion.common.item.upgrades.EnergyUpgradeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static kirsan122.assemblyexpansion.AssemblyExpansion.MOD_ID;

public class AEBlockItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Item> TIN_ORE_ITEM = register(AEBlocks.TIN_ORE);

    public static final RegistryObject<Item> DEEPSLATE_TIN_ORE_ITEM = register(AEBlocks.DEEPSLATE_TIN_ORE);

    public static final RegistryObject<Item> RAW_TIN_BLOCK_ITEM = register(AEBlocks.RAW_TIN_BLOCK);

    public static final RegistryObject<Item> TIN_BLOCK_ITEM = register(AEBlocks.TIN_BLOCK);

    public static final RegistryObject<Item> CRUSHER_ITEM = ITEMS.register("crusher",
            () -> new BlockItem(AEMachineBlocks.CRUSHER.get(), new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                    if (Screen.hasControlDown()) {
                        tooltip.add(Component.translatable("tooltip.assembly_expansion.machines.crusher"));

                    } else if (Screen.hasShiftDown()) {
                        int currentEnergy = 0;
                        int baseMaxEnergy = 10000;
                        int maxEnergy = baseMaxEnergy;

                        String inventorySummary = "tooltip.assembly_expansion.machines.inv.empty";
                        CompoundTag mainTag = stack.getTag();

                        if (mainTag != null && mainTag.contains("BlockEntityTag")) {
                            CompoundTag beTag = mainTag.getCompound("BlockEntityTag");


                            if (beTag.contains("energy")) {
                                currentEnergy = beTag.getInt("energy");
                            }


                            CompoundTag upgradeSlotsTag = beTag.contains("upgrade_slots") ? beTag.getCompound("upgrade_slots") : null;
                            maxEnergy = EnergyUpgradeItem.getMaxEnergy(baseMaxEnergy, upgradeSlotsTag);


                            if (beTag.contains("inventory")) {
                                CompoundTag invTag = beTag.getCompound("inventory");
                                if (invTag.contains("Items", Tag.TAG_LIST)) {
                                    ListTag itemsList = invTag.getList("Items", Tag.TAG_COMPOUND);
                                    List<String> foundItems = new ArrayList<>();

                                    for (int i = 0; i < itemsList.size(); i++) {
                                        CompoundTag itemTag = itemsList.getCompound(i);
                                        ItemStack itemInSlot = ItemStack.of(itemTag);

                                        if (!itemInSlot.isEmpty()) {
                                            String itemName = itemInSlot.getHoverName().getString();
                                            int count = itemInSlot.getCount();
                                            foundItems.add(itemName + " x" + count);
                                        }
                                    }

                                    if (!foundItems.isEmpty()) {
                                        inventorySummary = String.join(", ", foundItems);
                                    }
                                }
                            }
                        }


                        tooltip.add(Component.translatable("tooltip.assembly_expansion.battery_info.energy", currentEnergy, maxEnergy));

                        if (inventorySummary.equals("tooltip.assembly_expansion.machines.inv.empty")) {
                            tooltip.add(Component.translatable("tooltip.assembly_expansion.machines.info.inv")
                                    .append(Component.translatable(inventorySummary)));
                        } else {
                            tooltip.add(Component.translatable("tooltip.assembly_expansion.machines.info.inv")
                                    .append(Component.literal(inventorySummary)));
                        }

                    } else {
                        tooltip.add(Component.translatable("tooltip.assembly_expansion.universal.shift"));
                        tooltip.add(Component.translatable("tooltip.assembly_expansion.universal.ctrl"));
                    }
                    super.appendHoverText(stack, level, tooltip, flag);
                }

            });

    public static final RegistryObject<Item> MACHINE_FRAME_ITEM = register(AEBlocks.MACHINE_FRAME);

    public static final RegistryObject<Item> GENERATOR_ITEM = ITEMS.register("generator",
            () -> new BlockItem(AEMachineBlocks.GENERATOR.get(), new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                    if (Screen.hasControlDown()) {
                        tooltip.add(Component.translatable("tooltip.assembly_expansion.machines.generator"));

                    } else if (Screen.hasShiftDown()) {
                        int currentEnergy = 0;
                        String inventorySummary = "tooltip.assembly_expansion.machines.inv.empty";


                        CompoundTag mainTag = stack.getTag();


                        if (mainTag != null && mainTag.contains("BlockEntityTag")) {

                            CompoundTag beTag = mainTag.getCompound("BlockEntityTag");



                            if (beTag.contains("energy")) {


                                currentEnergy = beTag.getInt("energy");
                            }


                            if (beTag.contains("inventory")) {
                                CompoundTag invTag = beTag.getCompound("inventory");



                                if (invTag.contains("Items", Tag.TAG_LIST)) {
                                    ListTag itemsList = invTag.getList("Items", Tag.TAG_COMPOUND);


                                    List<String> foundItems = new ArrayList<>();


                                    for (int i = 0; i < itemsList.size(); i++) {
                                        CompoundTag itemTag = itemsList.getCompound(i);


                                        ItemStack itemInSlot = ItemStack.of(itemTag);


                                        if (!itemInSlot.isEmpty()) {

                                            String itemName = itemInSlot.getHoverName().getString();
                                            int count = itemInSlot.getCount();


                                            foundItems.add(itemName + " x" + count);
                                        }
                                    }


                                    if (!foundItems.isEmpty()) {

                                        inventorySummary = String.join(", ", foundItems);
                                    }
                                }
                            }
                        }




                        tooltip.add(Component.translatable("tooltip.assembly_expansion.machines.info.energy", currentEnergy));
                        tooltip.add(Component.translatable("tooltip.assembly_expansion.machines.info.inv")
                                .append(Component.translatable(inventorySummary)));

                    } else {
                        tooltip.add(Component.translatable("tooltip.assembly_expansion.universal.shift"));
                        tooltip.add(Component.translatable("tooltip.assembly_expansion.universal.ctrl"));
                    }
                    super.appendHoverText(stack, level, tooltip, flag);
                }
            });

    public static final RegistryObject<Item> ELECTRIC_FURNACE_ITEM = ITEMS.register("electric_furnace",
            () -> new BlockItem(AEMachineBlocks.ELECTRIC_FURNACE.get(), new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                    if (Screen.hasControlDown()) {
                        tooltip.add(Component.translatable("tooltip.assembly_expansion.machines.electric_furnace"));

                    } else if (Screen.hasShiftDown()) {
                        int currentEnergy = 0;
                        int baseMaxEnergy = 10000;
                        int maxEnergy = baseMaxEnergy;

                        String inventorySummary = "tooltip.assembly_expansion.machines.inv.empty";
                        CompoundTag mainTag = stack.getTag();

                        if (mainTag != null && mainTag.contains("BlockEntityTag")) {
                            CompoundTag beTag = mainTag.getCompound("BlockEntityTag");


                            if (beTag.contains("energy")) {
                                currentEnergy = beTag.getInt("energy");
                            }


                            CompoundTag upgradeSlotsTag = beTag.contains("upgrade_slots") ? beTag.getCompound("upgrade_slots") : null;
                            maxEnergy = EnergyUpgradeItem.getMaxEnergy(baseMaxEnergy, upgradeSlotsTag);


                            if (beTag.contains("inventory")) {
                                CompoundTag invTag = beTag.getCompound("inventory");
                                if (invTag.contains("Items", Tag.TAG_LIST)) {
                                    ListTag itemsList = invTag.getList("Items", Tag.TAG_COMPOUND);
                                    List<String> foundItems = new ArrayList<>();

                                    for (int i = 0; i < itemsList.size(); i++) {
                                        CompoundTag itemTag = itemsList.getCompound(i);
                                        ItemStack itemInSlot = ItemStack.of(itemTag);

                                        if (!itemInSlot.isEmpty()) {
                                            String itemName = itemInSlot.getHoverName().getString();
                                            int count = itemInSlot.getCount();
                                            foundItems.add(itemName + " x" + count);
                                        }
                                    }

                                    if (!foundItems.isEmpty()) {
                                        inventorySummary = String.join(", ", foundItems);
                                    }
                                }
                            }
                        }


                        tooltip.add(Component.translatable("tooltip.assembly_expansion.battery_info.energy", currentEnergy, maxEnergy));

                        if (inventorySummary.equals("tooltip.assembly_expansion.machines.inv.empty")) {
                            tooltip.add(Component.translatable("tooltip.assembly_expansion.machines.info.inv")
                                    .append(Component.translatable(inventorySummary)));
                        } else {
                            tooltip.add(Component.translatable("tooltip.assembly_expansion.machines.info.inv")
                                    .append(Component.literal(inventorySummary)));
                        }

                    } else {
                        tooltip.add(Component.translatable("tooltip.assembly_expansion.universal.shift"));
                        tooltip.add(Component.translatable("tooltip.assembly_expansion.universal.ctrl"));
                    }
                    super.appendHoverText(stack, level, tooltip, flag);
                }
            });

    public static final RegistryObject<Item> CLAY_BOWL_ITEM = ITEMS.register(
            "clay_bowl", () -> new BlockItem(AEBlocks.CLAY_BOWL.get(), new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {

                    if (stack.hasTag() && stack.getTag() != null) {
                        CompoundTag rootTag = stack.getTag();


                        if (rootTag.contains("BlockEntityTag", 10)) {
                            CompoundTag blockEntityTag = rootTag.getCompound("BlockEntityTag");


                            if (blockEntityTag.contains("Amount")) {
                                int amount = blockEntityTag.getInt("Amount");

                                if (amount > 0) {
                                    tooltip.add(Component.translatable("tooltip.assembly_expansion.latex_amount")
                                            .append(Integer.toString(amount))
                                            .append(Component.literal(" / 1000 mB"))
                                            .withStyle(ChatFormatting.GRAY));
                                    return;
                                }
                            }
                        }
                    }

                    tooltip.add(Component.translatable("tooltip.assembly_expansion.empty").withStyle(ChatFormatting.GRAY));
                }

            }
    );
    public static final RegistryObject<Item> MECHANICAL_SINTERER_ITEM = ITEMS.register("mechanical_sinterer",
            () -> new BlockItem(AEMachineBlocks.MECHANICAL_SINTERER.get(), new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                    if (Screen.hasControlDown()) {
                        tooltip.add(Component.translatable("tooltip.assembly_expansion.machines.mechanical_sinterer"));

                    } else if (Screen.hasShiftDown()) {
                        int currentEnergy = 0;
                        int baseMaxEnergy = 10000;
                        int maxEnergy = baseMaxEnergy;

                        String inventorySummary = "tooltip.assembly_expansion.machines.inv.empty";
                        CompoundTag mainTag = stack.getTag();

                        if (mainTag != null && mainTag.contains("BlockEntityTag")) {
                            CompoundTag beTag = mainTag.getCompound("BlockEntityTag");


                            if (beTag.contains("energy")) {
                                currentEnergy = beTag.getInt("energy");
                            }


                            CompoundTag upgradeSlotsTag = beTag.contains("upgrade_slots") ? beTag.getCompound("upgrade_slots") : null;
                            maxEnergy = EnergyUpgradeItem.getMaxEnergy(baseMaxEnergy, upgradeSlotsTag);


                            if (beTag.contains("inventory")) {
                                CompoundTag invTag = beTag.getCompound("inventory");
                                if (invTag.contains("Items", Tag.TAG_LIST)) {
                                    ListTag itemsList = invTag.getList("Items", Tag.TAG_COMPOUND);
                                    List<String> foundItems = new ArrayList<>();

                                    for (int i = 0; i < itemsList.size(); i++) {
                                        CompoundTag itemTag = itemsList.getCompound(i);
                                        ItemStack itemInSlot = ItemStack.of(itemTag);

                                        if (!itemInSlot.isEmpty()) {
                                            String itemName = itemInSlot.getHoverName().getString();
                                            int count = itemInSlot.getCount();
                                            foundItems.add(itemName + " x" + count);
                                        }
                                    }

                                    if (!foundItems.isEmpty()) {
                                        inventorySummary = String.join(", ", foundItems);
                                    }
                                }
                            }
                        }


                        tooltip.add(Component.translatable("tooltip.assembly_expansion.battery_info.energy", currentEnergy, maxEnergy));

                        if (inventorySummary.equals("tooltip.assembly_expansion.machines.inv.empty")) {
                            tooltip.add(Component.translatable("tooltip.assembly_expansion.machines.info.inv")
                                    .append(Component.translatable(inventorySummary)));
                        } else {
                            tooltip.add(Component.translatable("tooltip.assembly_expansion.machines.info.inv")
                                    .append(Component.literal(inventorySummary)));
                        }

                    } else {
                        tooltip.add(Component.translatable("tooltip.assembly_expansion.universal.shift"));
                        tooltip.add(Component.translatable("tooltip.assembly_expansion.universal.ctrl"));
                    }
                    super.appendHoverText(stack, level, tooltip, flag);
                }
            });
    public static final RegistryObject<Item> ELECTROLYZER_ITEM = ITEMS.register("electrolyzer",
            () -> new BlockItem(AEMachineBlocks.ELECTROLYZER.get(), new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                    if (Screen.hasControlDown()) {
                        tooltip.add(Component.translatable("tooltip.assembly_expansion.machines.electrolyzer"));

                    } else if (Screen.hasShiftDown()) {
                        int currentEnergy = 0;
                        int baseMaxEnergy = 12000;
                        int maxEnergy = baseMaxEnergy;

                        String inventorySummary = "tooltip.assembly_expansion.machines.inv.empty";
                        CompoundTag mainTag = stack.getTag();


                        List<Component> fluidTooltips = new ArrayList<>();

                        if (mainTag != null && mainTag.contains("BlockEntityTag")) {
                            CompoundTag beTag = mainTag.getCompound("BlockEntityTag");


                            if (beTag.contains("energy")) {
                                CompoundTag energyTag = beTag.getCompound("energy");
                                if (energyTag.contains("energy")) {
                                    currentEnergy = energyTag.getInt("energy");
                                }
                            }


                            CompoundTag upgradeSlotsTag = beTag.contains("upgrade_slots") ? beTag.getCompound("upgrade_slots") : null;
                            maxEnergy = EnergyUpgradeItem.getMaxEnergy(baseMaxEnergy, upgradeSlotsTag);


                            if (beTag.contains("inventory")) {
                                CompoundTag invTag = beTag.getCompound("inventory");
                                if (invTag.contains("Items", Tag.TAG_LIST)) {
                                    ListTag itemsList = invTag.getList("Items", Tag.TAG_COMPOUND);
                                    List<String> foundItems = new ArrayList<>();

                                    for (int i = 0; i < itemsList.size(); i++) {
                                        CompoundTag itemTag = itemsList.getCompound(i);
                                        ItemStack itemInSlot = ItemStack.of(itemTag);

                                        if (!itemInSlot.isEmpty()) {
                                            String itemName = itemInSlot.getHoverName().getString();
                                            int count = itemInSlot.getCount();
                                            foundItems.add(itemName + " x" + count);
                                        }
                                    }

                                    if (!foundItems.isEmpty()) {
                                        inventorySummary = String.join(", ", foundItems);
                                    }
                                }
                            }



                            if (beTag.contains("inputTank")) {
                                FluidStack fluid = FluidStack.loadFluidStackFromNBT(beTag.getCompound("inputTank"));
                                if (!fluid.isEmpty()) {
                                    fluidTooltips.add(Component.literal("  • ").withStyle(ChatFormatting.GRAY)
                                            .append(fluid.getDisplayName().copy().withStyle(ChatFormatting.AQUA))
                                            .append(Component.literal(": " + fluid.getAmount() + " mB").withStyle(ChatFormatting.WHITE)));
                                }
                            }

                            for (int i = 1; i <= 2; i++) {
                                if (beTag.contains("Tank" + i)) {
                                    FluidStack fluid = FluidStack.loadFluidStackFromNBT(beTag.getCompound("Tank" + i));
                                    if (!fluid.isEmpty()) {
                                        fluidTooltips.add(Component.literal("  • ").withStyle(ChatFormatting.GRAY)
                                                .append(fluid.getDisplayName().copy().withStyle(ChatFormatting.DARK_AQUA))
                                                .append(Component.literal(": " + fluid.getAmount() + " mB").withStyle(ChatFormatting.WHITE)));
                                    }
                                }
                            }
                        }


                        tooltip.add(Component.translatable("tooltip.assembly_expansion.battery_info.energy", currentEnergy, maxEnergy));


                        if (!fluidTooltips.isEmpty()) {
                            tooltip.addAll(fluidTooltips);
                        }


                        if (inventorySummary.equals("tooltip.assembly_expansion.machines.inv.empty")) {
                            tooltip.add(Component.translatable("tooltip.assembly_expansion.machines.info.inv")
                                    .append(Component.translatable(inventorySummary)));
                        } else {
                            tooltip.add(Component.translatable("tooltip.assembly_expansion.machines.info.inv")
                                    .append(Component.literal(inventorySummary)));
                        }

                    } else {
                        tooltip.add(Component.translatable("tooltip.assembly_expansion.universal.shift"));
                        tooltip.add(Component.translatable("tooltip.assembly_expansion.universal.ctrl"));
                    }
                    super.appendHoverText(stack, level, tooltip, flag);
                }
            });
    public static final RegistryObject<Item> AIR_COLLECTOR_ITEM = register(AEMachineBlocks.AIR_COLLECTOR);

    public static final RegistryObject<Item> FLUID_COOLER_ITEM = register(AEMachineBlocks.FLUID_COOLER);

    public static final RegistryObject<Item> FLUID_TANK_ITEM = ITEMS.register("fluid_tank",
            () -> new FluidTankBlockItem(AEMachineBlocks.FLUID_TANK.get(), new Item.Properties()));

    public static final RegistryObject<Item> COPPER_CABLE_ITEM = register(AEBlocks.COPPER_CABLE);
    public static final RegistryObject<Item> INSULATED_COPPER_CABLE = register(AEBlocks.INSULATED_COPPER_CABLE);

    public static final RegistryObject<Item> HEVEA_LOG_ITEM = register(AEBlocks.HEVEA_LOG);
    public static final RegistryObject<Item> HEVEA_PLANKS_ITEM = register(AEBlocks.HEVEA_PLANKS);
    public static final RegistryObject<Item> HEVEA_FENCE_ITEM = register(AEBlocks.HEVEA_FENCE);
    public static final RegistryObject<Item> HEVEA_STAIRS_ITEM = register(AEBlocks.HEVEA_STAIRS);
    public static final RegistryObject<Item> HEVEA_SLAB_ITEM = register(AEBlocks.HEVEA_SLAB);
    public static final RegistryObject<Item> HEVEA_DOOR_ITEM = ITEMS.register("hevea_door", () -> new BlockItem(AEBlocks.HEVEA_DOOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> HEVEA_TRAPDOOR_ITEM = ITEMS.register("hevea_trapdoor", () -> new BlockItem(AEBlocks.HEVEA_TRAPDOOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> HEVEA_FENCE_GATE_ITEM = ITEMS.register("hevea_fence_gate", () -> new BlockItem(AEBlocks.HEVEA_FENCE_GATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> HEVEA_BUTTON_ITEM = ITEMS.register("hevea_button", () -> new BlockItem(AEBlocks.HEVEA_BUTTON.get(), new Item.Properties()));
    public static final RegistryObject<Item> HEVEA_PRESSURE_PLATE_ITEM = ITEMS.register("hevea_pressure_plate", () -> new BlockItem(AEBlocks.HEVEA_PRESSURE_PLATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> HEVEA_SIGN_ITEM = ITEMS.register("hevea_sign", () -> new SignItem(new Item.Properties().stacksTo(16), AEBlocks.HEVEA_SIGN.get(), AEBlocks.HEVEA_WALL_SIGN.get()));
    public static final RegistryObject<Item> HEVEA_HANGING_SIGN_ITEM = ITEMS.register("hevea_hanging_sign", () -> new HangingSignItem(AEBlocks.HEVEA_HANGING_SIGN.get(), AEBlocks.HEVEA_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> HEVEA_WOOD_ITEM= register(AEBlocks.HEVEA_WOOD);
    public static final RegistryObject<Item> STRIPPED_HEVEA_LOG_ITEM= register(AEBlocks.STRIPPED_HEVEA_LOG);
    public static final RegistryObject<Item> STRIPPED_HEVEA_WOOD_ITEM= register(AEBlocks.STRIPPED_HEVEA_WOOD);
    public static final RegistryObject<Item> HEVEA_LEAVES_ITEM = ITEMS.register("hevea_leaves", () -> new BlockItem(AEBlocks.HEVEA_LEAVES.get(), new Item.Properties()));
    public static final RegistryObject<Item> HEVEA_SAPLING_ITEM = ITEMS.register("hevea_sapling", () -> new BlockItem(AEBlocks.HEVEA_SAPLING.get(), new Item.Properties()));
    public static final RegistryObject<Item> LEAD_ORE_ITEM = register(AEBlocks.LEAD_ORE);
    public static final RegistryObject<Item> DEEPSLATE_LEAD_ORE_ITEM = register(AEBlocks.DEEPSLATE_LEAD_ORE);
    public static final RegistryObject<Item> RAW_LEAD_BLOCK_ITEM = register(AEBlocks.RAW_LEAD_BLOCK);
    public static final RegistryObject<Item> LEAD_BLOCK_ITEM = register(AEBlocks.LEAD_BLOCK);
    public static final RegistryObject<Item> DISTILLATION_COLUMN_BASE_ITEM = register(AEMachineBlocks.DISTILLATION_COLUMN_BASE);
    public static final RegistryObject<Item> DISTILLATION_COLUMN_SECTION_ITEM = register(AEMachineBlocks.DISTILLATION_COLUMN_SECTION);
    public static final RegistryObject<Item> DISTILLATION_COLUMN_TOP_ITEM = register(AEMachineBlocks.DISTILLATION_COLUMN_TOP);
    public static final RegistryObject<Item> DEEPSLATE_URANIUM_ORE_ITEM = ITEMS.register("deepslate_uranium_ore", () -> new BlockItem(AEBlocks.DEEPSLATE_URANIUM_ORE.get(), new Item.Properties().rarity(Rarity.COMMON)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
    public static RegistryObject<Item> register(RegistryObject<Block> block) {return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));}
}
