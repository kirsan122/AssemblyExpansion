package kirsan122.assemblyexpansion;

import kirsan122.assemblyexpansion.client.gui.inventory.*;
import kirsan122.assemblyexpansion.client.model.AntiRadiationHelmetModel;
import kirsan122.assemblyexpansion.client.render.block.ClayBowlRenderer;
import kirsan122.assemblyexpansion.client.render.block.FluidTankRenderer;
import kirsan122.assemblyexpansion.client.render.entity.HeveaBoatRenderer;
import kirsan122.assemblyexpansion.common.entity.hevea.behavior.HeveaBoatDispenseBehavior;
import kirsan122.assemblyexpansion.common.init.*;
import kirsan122.assemblyexpansion.common.liquid.FluidObject;
import kirsan122.assemblyexpansion.common.particle.LatexParticle;
import kirsan122.assemblyexpansion.network.AEPacketHandler;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;


@Mod(AssemblyExpansion.MOD_ID)
public class AssemblyExpansion {
    public static final String MOD_ID = "assembly_expansion";

    public AssemblyExpansion() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::clientSetup);

        AEItems.register(modEventBus);
        AEBlocks.register(modEventBus);
        AEFluids.register(modEventBus);
        AEBlockItems.register(modEventBus);
        AEMachineBlocks.register(modEventBus);
        AEBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        AEMenues.register(modEventBus);
        AERecipes.RECIPE_TYPES.register(modEventBus);
        AERecipes.SERIALIZERS.register(modEventBus);
        AECreativeTabs.register(modEventBus);
        AEParticles.register(modEventBus);
        AEEntities.register(modEventBus);
        AEPlacers.register(modEventBus);
        AEPacketHandler.register();

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(AEMenues.CRUSHER_MENU.get(), CrusherScreen::new);
            MenuScreens.register(AEMenues.GENERATOR_MENU.get(), GeneratorScreen::new);
            MenuScreens.register(AEMenues.ELECTRIC_FURNACE_MENU.get(), ElectricFurnaceScreen::new);
            MenuScreens.register(AEMenues.MECHANICAL_SINTERER_MENU.get(), MechanicalSintererScreen::new);
            MenuScreens.register(AEMenues.FLUID_TANK_MENU.get(), FluidTankScreen::new);
            MenuScreens.register(AEMenues.DISTILLATION_COLUMN_MENU.get(), DistillationColumnScreen::new);
            MenuScreens.register(AEMenues.ELECTROLYZER_MENU.get(), ElectrolyzerScreen::new);
            MenuScreens.register(AEMenues.AIR_COLLECTOR_MENU.get(), AirCollectorScreen::new);
            MenuScreens.register(AEMenues.FLUID_COOLER_MENU.get(), FluidCoolerScreen::new);
        });
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(AEParticles.LATEX_DROP.get(), LatexParticle.Provider::new);
        }
        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {

            event.registerLayerDefinition(AntiRadiationHelmetModel.LAYER_LOCATION, AntiRadiationHelmetModel::createBodyLayer);
        }

        @SubscribeEvent
        public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {

            ModelResourceLocation tankItemModelLoc = new ModelResourceLocation(MOD_ID, "fluid_tank", "inventory");
            BakedModel originalModel = event.getModels().get(tankItemModelLoc);

            if (originalModel != null) {

                event.getModels().put(tankItemModelLoc, new BakedModel() {
                    @Override public boolean isCustomRenderer() { return true; }


                    @Override public java.util.List<net.minecraft.client.renderer.block.model.BakedQuad> getQuads(net.minecraft.world.level.block.state.BlockState state, net.minecraft.core.Direction direction, net.minecraft.util.RandomSource random) { return originalModel.getQuads(state, direction, random); }
                    @Override public boolean useAmbientOcclusion() { return originalModel.useAmbientOcclusion(); }
                    @Override public boolean isGui3d() { return originalModel.isGui3d(); }
                    @Override public boolean usesBlockLight() { return originalModel.usesBlockLight(); }
                    @Override public net.minecraft.client.renderer.texture.TextureAtlasSprite getParticleIcon() { return originalModel.getParticleIcon(); }
                    @Override public net.minecraft.client.renderer.block.model.ItemTransforms getTransforms() { return originalModel.getTransforms(); }
                    @Override public net.minecraft.client.renderer.block.model.ItemOverrides getOverrides() { return originalModel.getOverrides(); }
                });
            }
        }



        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {

            event.registerBlockEntityRenderer(AEBlockEntities.HEVEA_SIGN_BE.get(), SignRenderer::new);
            event.registerBlockEntityRenderer(AEBlockEntities.HEVEA_HANGING_SIGN_BE.get(), HangingSignRenderer::new);
            event.registerBlockEntityRenderer(AEBlockEntities.CLAY_BOWL_BE.get(), ClayBowlRenderer::new);
            event.registerBlockEntityRenderer(AEBlockEntities.FLUID_TANK_BE.get(), FluidTankRenderer::new);

            Sheets.SIGN_MATERIALS.put(AEBlocks.HEVEA_WOOD_TYPE,
                    new Material(Sheets.SIGN_SHEET, new ResourceLocation(MOD_ID, "entity/signs/hevea")));
            Sheets.HANGING_SIGN_MATERIALS.put(
                    AEBlocks.HEVEA_WOOD_TYPE,
                    new Material(Sheets.SIGN_SHEET, new ResourceLocation(MOD_ID, "entity/signs/hanging/hevea"))
            );
            event.registerEntityRenderer(AEEntities.HEVEA_BOAT.get(), context -> new HeveaBoatRenderer(context, false));
            event.registerEntityRenderer(AEEntities.HEVEA_CHEST_BOAT.get(), context -> new HeveaBoatRenderer(context, true));
        }

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                WoodType.register(AEBlocks.HEVEA_WOOD_TYPE);
                ItemProperties.register(AEItems.BATTERY.get(), new ResourceLocation(MOD_ID, "energy"),
                        (stack, level, entity, seed) -> {
                            int currentEnergy = stack.getOrCreateTag().getInt("energy");


                            return (float) currentEnergy / 2000.0f;
                        });
                for (FluidObject fluidObject : AEFluids.ALL_AE_FLUIDS) {

                    if (fluidObject.getBlock() != null) {

                        net.minecraftforge.fml.DistExecutor.unsafeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT, () -> () -> {
                            RenderType finalRenderType;


                            if ("solid".equalsIgnoreCase(fluidObject.renderType)) {
                                finalRenderType = RenderType.solid();
                            } else if ("cutout".equalsIgnoreCase(fluidObject.renderType)) {
                                finalRenderType = RenderType.cutout();
                            } else {
                                finalRenderType = RenderType.translucent();
                            }


                            ItemBlockRenderTypes.setRenderLayer(fluidObject.getSource(), finalRenderType);
                            ItemBlockRenderTypes.setRenderLayer(fluidObject.getFlowing(), finalRenderType);
                            ItemBlockRenderTypes.setRenderLayer(fluidObject.getBlock(), finalRenderType);
                        });
                    }
                }

            });
        }

        @SubscribeEvent
        public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {

            event.register((state, level, pos, tintIndex) -> {
                if (level != null && pos != null) {
                    return BiomeColors.getAverageFoliageColor(level, pos);
                }
                return FoliageColor.getDefaultColor();
            }, AEBlocks.HEVEA_LEAVES.get());
        }

        @SubscribeEvent
        public static void registerItemColors(RegisterColorHandlersEvent.Item event) {

            event.register((stack, tintIndex) -> {
                return FoliageColor.getDefaultColor();
            }, AEBlocks.HEVEA_LEAVES.get());

            for (FluidObject fluidObject : AEFluids.ALL_AE_FLUIDS) {
                if (fluidObject.getBucket() != null) {


                    IClientFluidTypeExtensions clientExtensions = IClientFluidTypeExtensions.of(fluidObject.getType());

                    event.register((itemStack, tintIndex) -> {
                        if (tintIndex == 1) {

                            return clientExtensions.getTintColor();
                        }
                        return -1;
                    }, fluidObject.getBucket());
                }
            }
        }


    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ServerModEvents {

        @SubscribeEvent
        public static void commonSetup(FMLCommonSetupEvent event) {

            event.enqueueWork(() -> {
                DispenserBlock.registerBehavior(AEItems.HEVEA_BOAT.get(),
                        new HeveaBoatDispenseBehavior(AEEntities.HEVEA_BOAT::get, false));

                DispenserBlock.registerBehavior(AEItems.HEVEA_CHEST_BOAT.get(),
                        new HeveaBoatDispenseBehavior(AEEntities.HEVEA_CHEST_BOAT::get, true));
            });
        }
    }
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    public class RegistryFixer {

        @SubscribeEvent
        public static void onMissingMappings(MissingMappingsEvent event) {


            var mazutFluid = AEFluids.MAZUT;


            if (mazutFluid.getBlock() != null) {
                event.getMappings(ForgeRegistries.Keys.BLOCKS, MOD_ID).forEach(mapping -> {
                    if (mapping.getKey().getPath().equals("heavy_oil")) {
                        mapping.remap(mazutFluid.getBlock());
                    }
                });
            }


            if (mazutFluid.getBucket() != null) {
                event.getMappings(ForgeRegistries.Keys.ITEMS, MOD_ID).forEach(mapping -> {
                    if (mapping.getKey().getPath().equals("heavy_oil_bucket")) {
                        mapping.remap(mazutFluid.getBucket());
                    }
                });
            }


            event.getMappings(ForgeRegistries.Keys.FLUIDS, MOD_ID).forEach(mapping -> {
                String path = mapping.getKey().getPath();


                if (path.equals("heavy_oil")) {
                    mapping.remap(mazutFluid.getSource());
                }

                else if (path.equals("heavy_oil_flowing")) {
                    mapping.remap(mazutFluid.getFlowing());
                }
            });


            event.getMappings(ForgeRegistries.Keys.FLUID_TYPES, MOD_ID).forEach(mapping -> {
                if (mapping.getKey().getPath().equals("heavy_oil_type")) {
                    mapping.remap(mazutFluid.getType());
                }
            });
        }
    }














































































}
