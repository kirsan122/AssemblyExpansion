package kirsan122.assemblyexpansion.common.init;

import kirsan122.assemblyexpansion.common.inventory.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static kirsan122.assemblyexpansion.AssemblyExpansion.MOD_ID;

public class AEMenues {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, MOD_ID);

    public static final RegistryObject<MenuType<CrusherMenu>> CRUSHER_MENU = MENUS.register("crusher_menu",
            () -> IForgeMenuType.create(CrusherMenu::new));
    public static final RegistryObject<MenuType<GeneratorMenu>> GENERATOR_MENU =
            MENUS.register("generator_menu", () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new GeneratorMenu(windowId, inv, inv.player.level().getBlockEntity(pos), new SimpleContainerData(4));}));

    public static final RegistryObject<MenuType<ElectricFurnaceMenu>> ELECTRIC_FURNACE_MENU = MENUS.register("electric_furnace_menu",
            () -> IForgeMenuType.create(ElectricFurnaceMenu::new));

    public static final RegistryObject<MenuType<MechanicalSintererMenu>> MECHANICAL_SINTERER_MENU = MENUS.register("mechanical_sinterer_menu",
            () -> IForgeMenuType.create(MechanicalSintererMenu::new));

    public static final RegistryObject<MenuType<FluidTankMenu>> FLUID_TANK_MENU = MENUS.register("fluid_tank_menu",
            () -> IForgeMenuType.create(FluidTankMenu::new));

    public static final RegistryObject<MenuType<DistillationColumnMenu>> DISTILLATION_COLUMN_MENU = MENUS.register("distillation_column_menu",
            () -> IForgeMenuType.create(DistillationColumnMenu::new));

    public static final RegistryObject<MenuType<ElectrolyzerMenu>> ELECTROLYZER_MENU = MENUS.register("electrolyzer_menu",
            () -> IForgeMenuType.create(ElectrolyzerMenu::new));

    public static final RegistryObject<MenuType<AirCollectorMenu>> AIR_COLLECTOR_MENU = MENUS.register("air_collector_menu",
            () -> IForgeMenuType.create(AirCollectorMenu::new));

    public static final RegistryObject<MenuType<FluidCoolerMenu>> FLUID_COOLER_MENU = MENUS.register("fluid_cooler_menu",
            () -> IForgeMenuType.create(FluidCoolerMenu::new));

    public static final RegistryObject<MenuType<ChemicalMixerMenu>> CHEMICAL_MIXER_MENU = MENUS.register("chemical_mixer_menu",
            () -> IForgeMenuType.create(ChemicalMixerMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
