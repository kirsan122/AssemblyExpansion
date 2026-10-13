package kirsan122.assemblyexpansion.common.init;

import kirsan122.assemblyexpansion.AssemblyExpansion;
import kirsan122.assemblyexpansion.common.block.FluidTankBlock;
import kirsan122.assemblyexpansion.common.block.machines.*;
import kirsan122.assemblyexpansion.common.block.machines.distillation_column.DistillationColumnBaseBlock;
import kirsan122.assemblyexpansion.common.block.machines.distillation_column.DistillationComponentBlock;
import kirsan122.assemblyexpansion.common.block.machines.distillation_column.DistillationComponentBlockType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AEMachineBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AssemblyExpansion.MOD_ID);

    public static final RegistryObject<Block> CRUSHER = BLOCKS.register("crusher",
            () -> new CrusherBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.5F, 6.0F)
                    .sound(SoundType.METAL)));

    public static final RegistryObject<Block> GENERATOR = BLOCKS.register("generator",
            () -> new GeneratorBlock(BlockBehaviour.Properties.copy(CRUSHER.get())));

    public static final RegistryObject<Block> ELECTRIC_FURNACE = BLOCKS.register("electric_furnace",
            () -> new ElectricFurnaceBlock(BlockBehaviour.Properties.copy(CRUSHER.get())));

    public static final RegistryObject<Block> MECHANICAL_SINTERER = BLOCKS.register("mechanical_sinterer",
            () -> new MechanicalSintererBlock(BlockBehaviour.Properties.copy(CRUSHER.get())));

    public static final RegistryObject<Block> DISTILLATION_COLUMN_BASE = BLOCKS.register("distillation_column_base",
            () -> new DistillationColumnBaseBlock(BlockBehaviour.Properties.copy(CRUSHER.get()).strength(4.5F, 6.0F).noOcclusion()));

    public static final RegistryObject<Block> DISTILLATION_COLUMN_SECTION = BLOCKS.register("distillation_column_section",
            () -> new DistillationComponentBlock(DistillationComponentBlockType.SECTION, BlockBehaviour.Properties.copy(DISTILLATION_COLUMN_BASE.get())));

    public static final RegistryObject<Block> DISTILLATION_COLUMN_TOP = BLOCKS.register("distillation_column_top",
            () -> new DistillationComponentBlock(DistillationComponentBlockType.TOP, BlockBehaviour.Properties.copy(DISTILLATION_COLUMN_BASE.get())));

    public static final RegistryObject<FluidTankBlock> FLUID_TANK = BLOCKS.register("fluid_tank",
            () -> new FluidTankBlock(10000, BlockBehaviour.Properties.copy(Blocks.WHITE_STAINED_GLASS).strength(3.0F, 5.0F)));

    public static final RegistryObject<Block> ELECTROLYZER = BLOCKS.register("electrolyzer",
            () -> new ElectrolyzerBlock(BlockBehaviour.Properties.copy(CRUSHER.get()).noOcclusion()));

    public static final RegistryObject<Block> AIR_COLLECTOR = BLOCKS.register("air_collector",
            () -> new AirCollectorBlock(BlockBehaviour.Properties.copy(CRUSHER.get())));

    public static final RegistryObject<Block> FLUID_COOLER = BLOCKS.register("fluid_cooler",
            () -> new FluidCoolerBlock(BlockBehaviour.Properties.copy(CRUSHER.get())));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
