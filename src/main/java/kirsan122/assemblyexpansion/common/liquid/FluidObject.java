package kirsan122.assemblyexpansion.common.liquid;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import static kirsan122.assemblyexpansion.common.init.AEBlocks.BLOCKS;
import static kirsan122.assemblyexpansion.common.init.AEFluids.FLUIDS;
import static kirsan122.assemblyexpansion.common.init.AEFluids.FLUID_TYPES;
import static kirsan122.assemblyexpansion.common.init.AEItems.ITEMS;

public class FluidObject {
    private final RegistryObject<FluidType> type;
    private final RegistryObject<ForgeFlowingFluid.Source> source;
    private final RegistryObject<ForgeFlowingFluid.Flowing> flowing;
    private RegistryObject<LiquidBlock> block = null;
    private RegistryObject<Item> bucket = null;
    private final String name;
    private int color;
    public final String renderType;

    public FluidObject(String prefix, FluidProperties properties) {
        String cleanColor = properties.color.replace("0x", "").replace("#", "");
        this.color = Integer.parseUnsignedInt(cleanColor, 16);
        this.name = prefix;
        this.renderType = properties.renderType;
        this.type = FLUID_TYPES.register(prefix + "_type", () -> new FluidType(FluidType.Properties.create().descriptionId(properties.descriptionId != null ? properties.descriptionId : "fluid.assembly_expansion." + prefix).viscosity(properties.viscosity).density(properties.density).motionScale(properties.motionScale).canPushEntity(properties.canPushEntity).canSwim(properties.canSwim).canDrown(properties.canDrown).fallDistanceModifier(properties.fallDistanceModifier).lightLevel(properties.lightLevel).temperature(properties.temperature)

        ) {
            @Override
            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(new IClientFluidTypeExtensions() {

                    @Override
                    public ResourceLocation getStillTexture() {
                        return properties.still;
                    }

                    @Override
                    public ResourceLocation getFlowingTexture() {
                        return properties.flow;
                    }

                    @Override
                    public int getTintColor() {
                        return color;
                    }

                    @Override
                    public int getTintColor(net.minecraft.world.level.material.FluidState state, net.minecraft.world.level.BlockAndTintGetter world, net.minecraft.core.BlockPos pos) {
                        return color;
                    }



                    @Override
                    public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {

                        float r = ((color >> 16) & 0xFF) / 255.0f;
                        float g = ((color >> 8) & 0xFF) / 255.0f;
                        float b = (color & 0xFF) / 255.0f;

                        return new Vector3f(r, g, b);
                    }


                    @Override
                    public void modifyFogRender(Camera camera, net.minecraft.client.renderer.FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, com.mojang.blaze3d.shaders.FogShape shape) {


                        RenderSystem.setShaderFogStart(properties.fogNear);
                        RenderSystem.setShaderFogEnd(properties.fogFar);


                        RenderSystem.setShaderFogShape(shape);
                    }



                });
            }
        });


        final RegistryObject<ForgeFlowingFluid.Source>[] sourceHolder = new RegistryObject[1];
        final RegistryObject<ForgeFlowingFluid.Flowing>[] flowingHolder = new RegistryObject[1];


        ForgeFlowingFluid.Properties fluidProperties = new ForgeFlowingFluid.Properties(this.type, () -> sourceHolder[0].get(), () -> flowingHolder[0].get()).slopeFindDistance(properties.slopeFindDistance).levelDecreasePerBlock(properties.levelDecreasePerBlock).tickRate(properties.tickRate);


        this.source = sourceHolder[0] = FLUIDS.register(prefix, () -> new ForgeFlowingFluid.Source(fluidProperties));
        this.flowing = flowingHolder[0] = FLUIDS.register(prefix + "_flowing", () -> new ForgeFlowingFluid.Flowing(fluidProperties));

        if (properties.hasBlock) {
            this.block = BLOCKS.register(prefix, () -> new LiquidBlock(this.source, BlockBehaviour.Properties.copy(Blocks.WATER).noCollission().liquid()));
            fluidProperties.block(() -> block.get());
        }

        if (properties.hasBucket) {
            this.bucket = ITEMS.register(prefix + "_bucket", () -> new BucketItem(this.source, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
            fluidProperties.bucket(() -> bucket.get());
        }
    }

    public FluidType getType() {
        return this.type.get();
    }

    public ForgeFlowingFluid.Source getSource() {
        return this.source.get();
    }

    public ForgeFlowingFluid.Flowing getFlowing() {
        return this.flowing.get();
    }

    @Nullable
    public LiquidBlock getBlock() {
        return this.block != null ? this.block.get() : null;
    }

    @Nullable
    public Item getBucket() {





        return (this.bucket != null) ? this.bucket.get() : null;
    }


    public void setFluidColor(int newColor) {
        this.color = newColor;
    }
}
