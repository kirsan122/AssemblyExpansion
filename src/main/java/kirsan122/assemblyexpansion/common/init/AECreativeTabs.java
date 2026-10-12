package kirsan122.assemblyexpansion.common.init;

import kirsan122.assemblyexpansion.AssemblyExpansion;
import kirsan122.assemblyexpansion.common.liquid.FluidObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class AECreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AssemblyExpansion.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TAB_BLOCKS = CREATIVE_MODE_TABS.register("tab_blocks",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(AEMachineBlocks.CRUSHER.get()))
                    .title(Component.translatable("creative_tab.assembly_expansion.blocks"))
                    .displayItems((parameters, output) -> {
                        output.accept(AEBlockItems.GENERATOR_ITEM.get());
                        output.accept(AEBlockItems.CRUSHER_ITEM.get());
                        output.accept(AEBlockItems.ELECTRIC_FURNACE_ITEM.get());
                        output.accept(AEBlockItems.MECHANICAL_SINTERER_ITEM.get());
                        output.accept(AEBlockItems.DISTILLATION_COLUMN_BASE_ITEM.get());
                        output.accept(AEBlockItems.DISTILLATION_COLUMN_SECTION_ITEM.get());
                        output.accept(AEBlockItems.DISTILLATION_COLUMN_TOP_ITEM.get());
                        output.accept(AEBlockItems.ELECTROLYZER_ITEM.get());
                        output.accept(AEBlockItems.AIR_COLLECTOR_ITEM.get());
                        output.accept(AEBlockItems.FLUID_COOLER_ITEM.get());
                        output.accept(AEBlockItems.FLUID_TANK_ITEM.get());
                        output.accept(AEBlockItems.MACHINE_FRAME_ITEM.get());
                        output.accept(AEBlockItems.COPPER_CABLE_ITEM.get());
                        output.accept(AEBlockItems.INSULATED_COPPER_CABLE.get());
                        output.accept(AEBlockItems.HEVEA_LOG_ITEM.get());
                        output.accept(AEBlockItems.HEVEA_WOOD_ITEM.get());
                        output.accept(AEBlockItems.STRIPPED_HEVEA_LOG_ITEM.get());
                        output.accept(AEBlockItems.STRIPPED_HEVEA_WOOD_ITEM.get());
                        output.accept(AEBlockItems.HEVEA_PLANKS_ITEM.get());
                        output.accept(AEBlockItems.HEVEA_STAIRS_ITEM.get());
                        output.accept(AEBlockItems.HEVEA_SLAB_ITEM.get());
                        output.accept(AEBlockItems.HEVEA_FENCE_ITEM.get());
                        output.accept(AEBlockItems.HEVEA_FENCE_GATE_ITEM.get());
                        output.accept(AEBlockItems.HEVEA_DOOR_ITEM.get());
                        output.accept(AEBlockItems.HEVEA_TRAPDOOR_ITEM.get());
                        output.accept(AEBlockItems.HEVEA_PRESSURE_PLATE_ITEM.get());
                        output.accept(AEBlockItems.HEVEA_BUTTON_ITEM.get());
                        output.accept(AEBlockItems.HEVEA_SIGN_ITEM.get());
                        output.accept(AEBlockItems.HEVEA_HANGING_SIGN_ITEM.get());
                        output.accept(AEBlockItems.HEVEA_LEAVES_ITEM.get());
                        output.accept(AEBlockItems.HEVEA_SAPLING_ITEM.get());
                        output.accept(AEBlockItems.TIN_ORE_ITEM.get());
                        output.accept(AEBlockItems.LEAD_ORE_ITEM.get());
                        output.accept(AEBlockItems.DEEPSLATE_TIN_ORE_ITEM.get());
                        output.accept(AEBlockItems.DEEPSLATE_LEAD_ORE_ITEM.get());
                        output.accept(AEBlockItems.DEEPSLATE_URANIUM_ORE_ITEM.get());
                        output.accept(AEBlockItems.RAW_TIN_BLOCK_ITEM.get());
                        output.accept(AEBlockItems.RAW_LEAD_BLOCK_ITEM.get());
                        output.accept(AEBlockItems.TIN_BLOCK_ITEM.get());
                        output.accept(AEBlockItems.LEAD_BLOCK_ITEM.get());

                    }).build());

    public static final RegistryObject<CreativeModeTab> TAB_ITEMS = CREATIVE_MODE_TABS.register("tab_items",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(AEItems.TIN_INGOT.get()))
                    .title(Component.translatable("creative_tab.assembly_expansion.items"))
                    .displayItems((parameters, output) -> {
                        output.accept(AEItems.RAW_TIN.get());
                        output.accept(AEItems.RAW_LEAD.get());
                        output.accept(AEItems.RAW_URANIUM.get());

                        output.accept(AEItems.TIN_INGOT.get());
                        output.accept(AEItems.LEAD_INGOT.get());
                        output.accept(AEItems.URANIUM_INGOT.get());
                        output.accept(AEItems.BRONZE_INGOT.get());
                        output.accept(AEItems.STEEL_INGOT.get());

                        output.accept(AEItems.COAL_DUST.get());
                        output.accept(AEItems.TIN_DUST.get());
                        output.accept(AEItems.COPPER_DUST.get());
                        output.accept(AEItems.IRON_DUST.get());
                        output.accept(AEItems.GOLD_DUST.get());
                        output.accept(AEItems.LEAD_DUST.get());
                        output.accept(AEItems.URANIUM_DUST.get());
                        output.accept(AEItems.BRONZE_DUST.get());
                        output.accept(AEItems.STEEL_DUST.get());

                        output.accept(AEItems.TIN_PLATE.get());
                        output.accept(AEItems.COPPER_PLATE.get());
                        output.accept(AEItems.IRON_PLATE.get());
                        output.accept(AEItems.LEAD_PLATE.get());
                        output.accept(AEItems.BRONZE_PLATE.get());
                        output.accept(AEItems.STEEL_PLATE.get());

                        output.accept(AEItems.LATEX_DISC.get());
                        output.accept(AEItems.RAW_RUBBER.get());
                        output.accept(AEItems.PURIFIED_RUBBER.get());
                        output.accept(AEItems.RUBBER.get());
                        output.accept(AEItems.COPPER_WIRE.get());
                        output.accept(AEItems.CIRCUIT.get());
                        output.accept(AEItems.ADVANCED_CIRCUIT.get());
                        output.accept(AEItems.FAN.get());
                        output.accept(AEItems.HEAT_EXCHANGER.get());
                        output.accept(AEItems.URANYL_NITRATE.get());
                        output.accept(AEItems.ENRICHED_URANYL_NITRATE.get());
                        output.accept(AEItems.DEPLETED_URANYL_NITRATE.get());
                    }).build());
    public static final RegistryObject<CreativeModeTab> TAB_TOOLS = CREATIVE_MODE_TABS.register("tab_tools",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(AEItems.ENGINEERING_HAMMER.get()))
                    .title(Component.translatable("creative_tab.assembly_expansion.tools"))
                    .displayItems((parameters, output) -> {
                        output.accept(AEItems.ENGINEERING_HAMMER.get());
                        output.accept(AEItems.WIRE_CUTTERS.get());
                        output.accept(AEItems.IRON_KNIFE.get());
                        output.accept(AEItems.DIAMOND_KNIFE.get());
                        output.accept(AEItems.NETHERITE_KNIFE.get());
                        output.accept(AEItems.BATTERY.get());
                        ItemStack chargedBattery = new ItemStack(AEItems.BATTERY.get());
                        chargedBattery.getOrCreateTag().putInt("energy", 2000);
                        output.accept(chargedBattery);
                        output.accept(AEItems.ANTI_RADIATION_HELMET.get());
                        output.accept(AEItems.ANTI_RADIATION_CHESTPLATE.get());
                        output.accept(AEItems.ANTI_RADIATION_LEGGINGS.get());
                        output.accept(AEItems.ANTI_RADIATION_BOOTS.get());
                        output.accept(AEBlockItems.CLAY_BOWL_ITEM.get());
                        output.accept(AEItems.HEVEA_BOAT.get());
                        output.accept(AEItems.HEVEA_CHEST_BOAT.get());
                        output.accept(AEItems.UPGRADE_SPEED.get());
                        output.accept(AEItems.UPGRADE_ENERGY.get());
                        for (FluidObject fluidObject : AEFluids.ALL_AE_FLUIDS) {
                            if (fluidObject.getBucket() != null) {
                                output.accept(fluidObject.getBucket());
                            }
                        }
                    }).build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
