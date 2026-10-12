package kirsan122.assemblyexpansion.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import kirsan122.assemblyexpansion.common.init.AEMachineBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Matrix4f;

public class FluidTankItemRenderer extends BlockEntityWithoutLevelRenderer {
    public static final FluidTankItemRenderer INSTANCE = new FluidTankItemRenderer();

    public FluidTankItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

        BlockState blockState = AEMachineBlocks.FLUID_TANK.get().defaultBlockState();
        BakedModel blockModel = blockRenderer.getBlockModel(blockState);


        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        Minecraft.getInstance().getItemRenderer().render(
                stack, context, false, poseStack, buffer, combinedLight, combinedOverlay, blockModel
        );
        poseStack.popPose();


        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("BlockEntityTag")) {
            CompoundTag blockEntityTag = tag.getCompound("BlockEntityTag");

            if (blockEntityTag.contains("FluidName") || blockEntityTag.contains("id")) {
                FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(blockEntityTag);

                if (!fluidStack.isEmpty() && fluidStack.getAmount() > 0) {
                    poseStack.pushPose();



                    poseStack.translate(0.5F, 0.5F, 0.5F);


                    boolean leftHand = context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
                    blockModel.applyTransform(context, poseStack, leftHand);



                    renderFluidCube(poseStack, buffer, fluidStack);

                    poseStack.popPose();
                }
            }
        }
    }


    protected void renderFluidCube(PoseStack poseStack, MultiBufferSource buffer, FluidStack fluidStack) {
        Fluid fluid = fluidStack.getFluid();
        IClientFluidTypeExtensions fluidProperties = IClientFluidTypeExtensions.of(fluid);

        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(fluidProperties.getStillTexture(fluidStack));

        int color = fluidProperties.getTintColor(fluidStack);
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = ((color >> 24) & 0xFF) / 255.0f;


        float capacity = 10000.0f;
        float amount = fluidStack.getAmount();


        float maxFluidHeightPixels = 14.0f;
        float currentFluidHeightPixels = (amount / capacity) * maxFluidHeightPixels;







        float minX = -0.30625f;
        float maxX = 0.30625f;
        float minZ = -0.30625f;
        float maxZ = 0.30625f;





        float minY = -0.4375f;


        float maxY = minY + (currentFluidHeightPixels * 0.0625f);

        VertexConsumer builder = buffer.getBuffer(RenderType.translucent());
        Matrix4f matrix = poseStack.last().pose();

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();


        int light = 15728880;


        buildVertex(matrix, builder, minX, maxY, minZ, u0, v0, r, g, b, a, light);
        buildVertex(matrix, builder, minX, maxY, maxZ, u0, v1, r, g, b, a, light);
        buildVertex(matrix, builder, maxX, maxY, maxZ, u1, v1, r, g, b, a, light);
        buildVertex(matrix, builder, maxX, maxY, minZ, u1, v0, r, g, b, a, light);


        buildVertex(matrix, builder, minX, minY, minZ, u0, v1, r, g, b, a, light);
        buildVertex(matrix, builder, minX, maxY, minZ, u0, v0, r, g, b, a, light);
        buildVertex(matrix, builder, maxX, maxY, minZ, u1, v0, r, g, b, a, light);
        buildVertex(matrix, builder, maxX, minY, minZ, u1, v1, r, g, b, a, light);


        buildVertex(matrix, builder, maxX, minY, maxZ, u0, v1, r, g, b, a, light);
        buildVertex(matrix, builder, maxX, maxY, maxZ, u0, v0, r, g, b, a, light);
        buildVertex(matrix, builder, minX, maxY, maxZ, u1, v0, r, g, b, a, light);
        buildVertex(matrix, builder, minX, minY, maxZ, u1, v1, r, g, b, a, light);


        buildVertex(matrix, builder, maxX, minY, minZ, u0, v1, r, g, b, a, light);
        buildVertex(matrix, builder, maxX, maxY, minZ, u0, v0, r, g, b, a, light);
        buildVertex(matrix, builder, maxX, maxY, maxZ, u1, v0, r, g, b, a, light);
        buildVertex(matrix, builder, maxX, minY, maxZ, u1, v1, r, g, b, a, light);


        buildVertex(matrix, builder, minX, minY, maxZ, u0, v1, r, g, b, a, light);
        buildVertex(matrix, builder, minX, maxY, maxZ, u0, v0, r, g, b, a, light);
        buildVertex(matrix, builder, minX, maxY, minZ, u1, v0, r, g, b, a, light);
        buildVertex(matrix, builder, minX, minY, minZ, u1, v1, r, g, b, a, light);
    }

    protected void buildVertex(Matrix4f matrix, VertexConsumer builder, float x, float y, float z, float u, float v, float r, float g, float b, float a, int light) {
        builder.vertex(matrix, x, y, z).color(r, g, b, a).uv(u, v).uv2(light).normal(0, 1, 0).endVertex();
    }
}
