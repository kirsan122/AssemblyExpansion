package kirsan122.assemblyexpansion.common.init;

import com.google.common.base.Supplier;
import kirsan122.assemblyexpansion.AssemblyExpansion;
import kirsan122.assemblyexpansion.common.block.ClayBowlBlock;
import kirsan122.assemblyexpansion.common.block.hevea.*;
import kirsan122.assemblyexpansion.common.block.transportation.CableBlock;
import kirsan122.assemblyexpansion.common.block.hevea.HeveaTreeGrower;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AEBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AssemblyExpansion.MOD_ID);
    public static final BlockSetType HEVEA_BLOCK_SET_TYPE = BlockSetType.register(new BlockSetType("hevea"));
    public static final WoodType HEVEA_WOOD_TYPE = WoodType.register(new WoodType("hevea", HEVEA_BLOCK_SET_TYPE));


    public static final RegistryObject<Block> TIN_ORE = register(
            "tin_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.IRON_ORE).strength(3.0f, 3.0f).requiresCorrectToolForDrops(), UniformInt.of(2, 5)));
    public static final RegistryObject<Block> DEEPSLATE_TIN_ORE = register(
            "deepslate_tin_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.IRON_ORE).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE)));
    public static final RegistryObject<Block> RAW_TIN_BLOCK = register(
            "raw_tin_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.RAW_IRON_BLOCK)));
    public static final RegistryObject<Block> TIN_BLOCK = register(
            "tin_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> COPPER_CABLE = register(
            "copper_cable", () -> new CableBlock(128, true, BlockBehaviour.Properties.of()
                    .strength(2, 2)
                    .sound(SoundType.METAL)
                    .mapColor(MapColor.METAL)
                    .pushReaction(PushReaction.DESTROY)
                    .isValidSpawn((state, world, pos, type) -> false)
                    .isRedstoneConductor((state, world, pos) -> false)
                    .isSuffocating((state, world, pos) -> false)));

    public static final RegistryObject<Block> INSULATED_COPPER_CABLE = register(
            "insulated_copper_cable", () -> new CableBlock(128, false, BlockBehaviour.Properties.copy(COPPER_CABLE.get())));

    public static final RegistryObject<Block> CLAY_BOWL = register(
            "clay_bowl", () -> new ClayBowlBlock(BlockBehaviour.Properties.copy(Blocks.FLOWER_POT)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)
                    .isValidSpawn((state, world, pos, type) -> false)
                    .isRedstoneConductor((state, world, pos) -> false)
                    .isSuffocating((state, world, pos) -> false)));

    public static final RegistryObject<Block> HEVEA_LOG = register(
            "hevea_log", () -> new HeveaBlock(BlockBehaviour.Properties.copy(Blocks.JUNGLE_LOG).randomTicks().ignitedByLava()));

    public static final RegistryObject<Block> HEVEA_PLANKS = register("hevea_planks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.JUNGLE_PLANKS).ignitedByLava()) {
                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 20;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }

                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

            });

    public static final RegistryObject<Block> HEVEA_FENCE = register("hevea_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.JUNGLE_FENCE).ignitedByLava()) {
                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 20;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }

                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

            });

    public static final RegistryObject<Block> HEVEA_STAIRS = register("hevea_stairs",
            () -> new StairBlock(() -> HEVEA_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.JUNGLE_STAIRS).ignitedByLava()) {
                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 20;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }

                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

            });

    public static final RegistryObject<Block> HEVEA_SLAB = register("hevea_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.JUNGLE_SLAB).ignitedByLava()) {
                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 20;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }

                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

            });


    public static final RegistryObject<DoorBlock> HEVEA_DOOR = BLOCKS.register("hevea_door",
            () -> new DoorBlock(Block.Properties.copy(Blocks.OAK_DOOR).ignitedByLava(), AEBlocks.HEVEA_BLOCK_SET_TYPE) {
                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 20;
                }

                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

            });


    public static final RegistryObject<TrapDoorBlock> HEVEA_TRAPDOOR = BLOCKS.register("hevea_trapdoor",
            () -> new TrapDoorBlock(Block.Properties.copy(Blocks.OAK_TRAPDOOR).ignitedByLava(), AEBlocks.HEVEA_BLOCK_SET_TYPE) {
                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 20;
                }

                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

            });


    public static final RegistryObject<FenceGateBlock> HEVEA_FENCE_GATE = BLOCKS.register("hevea_fence_gate",
            () -> new FenceGateBlock(Block.Properties.copy(Blocks.OAK_FENCE_GATE).ignitedByLava(), AEBlocks.HEVEA_WOOD_TYPE) {
                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 20;
                }

                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

            });


    public static final RegistryObject<ButtonBlock> HEVEA_BUTTON = BLOCKS.register("hevea_button",
            () -> new ButtonBlock(Block.Properties.copy(Blocks.OAK_BUTTON).ignitedByLava(), AEBlocks.HEVEA_BLOCK_SET_TYPE, 30, true) {
                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 20;
                }

                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

            });


    public static final RegistryObject<PressurePlateBlock> HEVEA_PRESSURE_PLATE = BLOCKS.register("hevea_pressure_plate",
            () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING,
                    Block.Properties.copy(Blocks.OAK_PRESSURE_PLATE).ignitedByLava(), AEBlocks.HEVEA_BLOCK_SET_TYPE) {
                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 20;
                }

                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

            });


    public static final RegistryObject<StandingSignBlock> HEVEA_SIGN = BLOCKS.register("hevea_sign",
            () -> new HeveaStandingSign(Block.Properties.copy(Blocks.OAK_SIGN).ignitedByLava(), AEBlocks.HEVEA_WOOD_TYPE));


    public static final RegistryObject<WallSignBlock> HEVEA_WALL_SIGN = BLOCKS.register("hevea_wall_sign",
            () -> new HeveaWallSign(Block.Properties.copy(Blocks.OAK_WALL_SIGN).ignitedByLava(), AEBlocks.HEVEA_WOOD_TYPE));


    public static final RegistryObject<CeilingHangingSignBlock> HEVEA_HANGING_SIGN = BLOCKS.register("hevea_hanging_sign",
            () -> new HeveaCeilingHangingSign(Block.Properties.copy(Blocks.OAK_HANGING_SIGN).ignitedByLava(), AEBlocks.HEVEA_WOOD_TYPE));


    public static final RegistryObject<WallHangingSignBlock> HEVEA_WALL_HANGING_SIGN = BLOCKS.register("hevea_wall_hanging_sign",
            () -> new HeveaWallHangingSign(Block.Properties.copy(Blocks.OAK_WALL_HANGING_SIGN).ignitedByLava(), AEBlocks.HEVEA_WOOD_TYPE));

    public static final RegistryObject<Block> HEVEA_WOOD = register(
            "hevea_wood", () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.JUNGLE_WOOD).ignitedByLava()) {
                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }

                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public BlockState getToolModifiedState(BlockState state, UseOnContext context, net.minecraftforge.common.ToolAction toolAction, boolean simulate) {
                    if (toolAction == ToolActions.AXE_STRIP) {

                        return AEBlocks.STRIPPED_HEVEA_WOOD.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
                    }
                    return super.getToolModifiedState(state, context, toolAction, simulate);
                }

            }
    );
    public static final RegistryObject<Block> STRIPPED_HEVEA_LOG = register(
            "stripped_hevea_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_JUNGLE_LOG).ignitedByLava()) {
                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }


                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }

                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }
            });

    public static final RegistryObject<Block> STRIPPED_HEVEA_WOOD = register(
            "stripped_hevea_wood", () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_JUNGLE_WOOD).ignitedByLava()) {
                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }


                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }

                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

            });

    public static final RegistryObject<Block> HEVEA_LEAVES = register("hevea_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion()));

    public static final RegistryObject<Block> HEVEA_SAPLING = register(
            "hevea_sapling", () -> new SaplingBlock(new HeveaTreeGrower(), BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING)));

    public static final RegistryObject<Block> MACHINE_FRAME = register("machine_frame",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()
            ));

    public static final RegistryObject<Block> LEAD_ORE = register(
            "lead_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.GOLD_ORE).strength(3.5f, 4.0f), UniformInt.of(2, 4)));
    public static final RegistryObject<Block> DEEPSLATE_LEAD_ORE = register(
            "deepslate_lead_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_GOLD_ORE).strength(4.5f, 5.0f), UniformInt.of(2, 4)));
    public static final RegistryObject<Block> RAW_LEAD_BLOCK = register(
            "raw_lead_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.RAW_GOLD_BLOCK)));
    public static final RegistryObject<Block> LEAD_BLOCK = register(
            "lead_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.GOLD_BLOCK)));
    public static final RegistryObject<Block> DEEPSLATE_URANIUM_ORE = register(
            "deepslate_uranium_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_DIAMOND_ORE), UniformInt.of(3, 5)));

    private static RegistryObject<Block> register(String id, Supplier<Block> blockSupplier) {
        return BLOCKS.register(id, blockSupplier);
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
