package kirsan122.assemblyexpansion.common.init;

import kirsan122.assemblyexpansion.AssemblyExpansion;
import kirsan122.assemblyexpansion.common.blockEntity.ClayBowlBlockEntity;
import kirsan122.assemblyexpansion.common.blockEntity.FluidTankBlockEntity;
import kirsan122.assemblyexpansion.common.blockEntity.hevea.HeveaHangingSignBlockEntity;
import kirsan122.assemblyexpansion.common.blockEntity.hevea.HeveaSignBlockEntity;
import kirsan122.assemblyexpansion.common.blockEntity.machines.*;
import kirsan122.assemblyexpansion.common.blockEntity.machines.distillation_column.DistillationColumnBlockEntity;
import kirsan122.assemblyexpansion.common.blockEntity.machines.distillation_column.DistillationComponentBlockEntity;
import kirsan122.assemblyexpansion.common.blockEntity.transportation.CableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AEBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AssemblyExpansion.MOD_ID);

    public static final RegistryObject<BlockEntityType<CrusherBlockEntity>> CRUSHER_BE =
            BLOCK_ENTITIES.register("crusher_be",
                    () -> BlockEntityType.Builder.of(CrusherBlockEntity::new, AEMachineBlocks.CRUSHER.get()).build(null));
    public static final RegistryObject<BlockEntityType<GeneratorBlockEntity>> GENERATOR_BE =
            BLOCK_ENTITIES.register("generator_be", () -> BlockEntityType.Builder.of(GeneratorBlockEntity::new,
                    AEMachineBlocks.GENERATOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<ClayBowlBlockEntity>> CLAY_BOWL_BE =
            BLOCK_ENTITIES.register("clay_bowl_be", () ->
                    BlockEntityType.Builder.of(ClayBowlBlockEntity::new, AEBlocks.CLAY_BOWL.get()).build(null));

    public static final RegistryObject<BlockEntityType<CableBlockEntity>> CABLE_TYPE =
            BLOCK_ENTITIES.register("cable_be", () ->
                    BlockEntityType.Builder.of(CableBlockEntity::new, AEBlocks.COPPER_CABLE.get()).build(null));

    public static final RegistryObject<BlockEntityType<HeveaSignBlockEntity>> HEVEA_SIGN_BE = BLOCK_ENTITIES.register("hevea_sign_be",
            () -> BlockEntityType.Builder.of(HeveaSignBlockEntity::new,
                    AEBlocks.HEVEA_SIGN.get(), AEBlocks.HEVEA_WALL_SIGN.get()).build(null));

    public static final RegistryObject<BlockEntityType<HeveaHangingSignBlockEntity>> HEVEA_HANGING_SIGN_BE = BLOCK_ENTITIES.register("hevea_hanging_sign_be",
            () -> BlockEntityType.Builder.of(HeveaHangingSignBlockEntity::new,
                    AEBlocks.HEVEA_HANGING_SIGN.get(), AEBlocks.HEVEA_WALL_HANGING_SIGN.get()).build(null));

    public static final RegistryObject<BlockEntityType<ElectricFurnaceBlockEntity>> ELECTRIC_FURNACE_BE =
            BLOCK_ENTITIES.register("electric_furnace_be", () -> BlockEntityType.Builder.of(ElectricFurnaceBlockEntity::new,
                    AEMachineBlocks.ELECTRIC_FURNACE.get()).build(null));

    public static final RegistryObject<BlockEntityType<MechanicalSintererBlockEntity>> MECHANICAL_SINTERER_BE =
            BLOCK_ENTITIES.register("mechanical_sinterer_be", () -> BlockEntityType.Builder.of(MechanicalSintererBlockEntity::new,
                    AEMachineBlocks.MECHANICAL_SINTERER.get()).build(null));

    public static final RegistryObject<BlockEntityType<FluidTankBlockEntity>> FLUID_TANK_BE =
            BLOCK_ENTITIES.register("fluid_tank_be", () ->
                    BlockEntityType.Builder.of((pos, state) -> {
                        return new FluidTankBlockEntity(AEMachineBlocks.FLUID_TANK.get().maxCapacity, pos, state);
                    }, AEMachineBlocks.FLUID_TANK.get()).build(null));

    public static final RegistryObject<BlockEntityType<DistillationColumnBlockEntity>> DISTILLATION_COLUMN_BE =
            BLOCK_ENTITIES.register("distillation_column_base", () ->
                    BlockEntityType.Builder.of(DistillationColumnBlockEntity::new,
                            AEMachineBlocks.DISTILLATION_COLUMN_BASE.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<DistillationComponentBlockEntity>> DISTILLATION_COMPONENT_BE =
            BLOCK_ENTITIES.register("distillation_component", () ->
                    BlockEntityType.Builder.of(DistillationComponentBlockEntity::new,
                            AEMachineBlocks.DISTILLATION_COLUMN_SECTION.get(),
                            AEMachineBlocks.DISTILLATION_COLUMN_TOP.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<ElectrolyzerBlockEntity>> ELECTROLYZER_BE =
            BLOCK_ENTITIES.register("electrolyzer_be", () ->
                    BlockEntityType.Builder.of(ElectrolyzerBlockEntity::new,
                            AEMachineBlocks.ELECTROLYZER.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<AirCollectorBlockEntity>> AIR_COLLECTOR_BE =
            BLOCK_ENTITIES.register("air_collector_be", () ->
                    BlockEntityType.Builder.of(AirCollectorBlockEntity::new,
                            AEMachineBlocks.AIR_COLLECTOR.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<FluidCoolerBlockEntity>> FLUID_COOLER_BE =
            BLOCK_ENTITIES.register("fluid_cooler_be", () ->
                    BlockEntityType.Builder.of(FluidCoolerBlockEntity::new,
                            AEMachineBlocks.FLUID_COOLER.get()
                    ).build(null));






}
