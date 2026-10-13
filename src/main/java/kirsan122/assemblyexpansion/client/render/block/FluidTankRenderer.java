package kirsan122.assemblyexpansion.client.render.block;

import kirsan122.assemblyexpansion.common.blockEntity.FluidTankBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.joml.Matrix4f;

public class FluidTankRenderer implements BlockEntityRenderer<FluidTankBlockEntity> {

    public FluidTankRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(FluidTankBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        FluidTank tank = blockEntity.getFluidTank();
        FluidStack fluidStack = tank.getFluid();


        if (fluidStack.isEmpty() || tank.getFluidAmount() <= 0) {
            return;
        }

        Fluid fluid = fluidStack.getFluid();
        IClientFluidTypeExtensions fluidExtensions = IClientFluidTypeExtensions.of(fluid);

        ResourceLocation textureLocation = fluidExtensions.getStillTexture(fluidStack);
        if (textureLocation == null) {
            textureLocation = new ResourceLocation("block/water_still");
        }

        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(textureLocation);


        int color = fluidExtensions.getTintColor(fluidStack);
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;


        float pct = (float) tank.getFluidAmount() / tank.getCapacity();
        pct = Mth.clamp(pct, 0.0f, 1.0f);


        float minX = 3.01f / 16f;
        float maxX = 12.99f / 16f;
        float minZ = 3.01f / 16f;
        float maxZ = 12.99f / 16f;

        float minY = 1.01f / 16f;
        float maxBlockY = 14.99f / 16f;
        float maxY = minY + pct * (maxBlockY - minY);

        VertexConsumer builder = bufferSource.getBuffer(RenderType.translucent());
        Matrix4f matrix = poseStack.last().pose();

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();




        float vHeight = v1 - v0;
        float vSide0 = v1 - (vHeight * pct);

        int light = LightTexture.FULL_BRIGHT;


        builder.vertex(matrix, minX, maxY, minZ).color(r, g, b, a).uv(u0, v0).uv2(light).normal(0, 1, 0).endVertex();
        builder.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a).uv(u0, v1).uv2(light).normal(0, 1, 0).endVertex();
        builder.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a).uv(u1, v1).uv2(light).normal(0, 1, 0).endVertex();
        builder.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a).uv(u1, v0).uv2(light).normal(0, 1, 0).endVertex();


        builder.vertex(matrix, minX, minY, minZ).color(r, g, b, a).uv(u0, v1).uv2(light).normal(0, 0, -1).endVertex();
        builder.vertex(matrix, minX, maxY, minZ).color(r, g, b, a).uv(u0, vSide0).uv2(light).normal(0, 0, -1).endVertex();
        builder.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a).uv(u1, vSide0).uv2(light).normal(0, 0, -1).endVertex();
        builder.vertex(matrix, maxX, minY, minZ).color(r, g, b, a).uv(u1, v1).uv2(light).normal(0, 0, -1).endVertex();


        builder.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a).uv(u0, v1).uv2(light).normal(0, 0, 1).endVertex();
        builder.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a).uv(u0, vSide0).uv2(light).normal(0, 0, 1).endVertex();
        builder.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a).uv(u1, vSide0).uv2(light).normal(0, 0, 1).endVertex();
        builder.vertex(matrix, minX, minY, maxZ).color(r, g, b, a).uv(u1, v1).uv2(light).normal(0, 0, 1).endVertex();


        builder.vertex(matrix, minX, minY, maxZ).color(r, g, b, a).uv(u0, v1).uv2(light).normal(-1, 0, 0).endVertex();
        builder.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a).uv(u0, vSide0).uv2(light).normal(-1, 0, 0).endVertex();
        builder.vertex(matrix, minX, maxY, minZ).color(r, g, b, a).uv(u1, vSide0).uv2(light).normal(-1, 0, 0).endVertex();
        builder.vertex(matrix, minX, minY, minZ).color(r, g, b, a).uv(u1, v1).uv2(light).normal(-1, 0, 0).endVertex();


        builder.vertex(matrix, maxX, minY, minZ).color(r, g, b, a).uv(u0, v1).uv2(light).normal(1, 0, 0).endVertex();
        builder.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a).uv(u0, vSide0).uv2(light).normal(1, 0, 0).endVertex();
        builder.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a).uv(u1, vSide0).uv2(light).normal(1, 0, 0).endVertex();
        builder.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a).uv(u1, v1).uv2(light).normal(1, 0, 0).endVertex();


        builder.vertex(matrix, minX, minY, maxZ).color(r, g, b, a).uv(u0, v1).uv2(light).normal(0, -1, 0).endVertex();
        builder.vertex(matrix, minX, minY, minZ).color(r, g, b, a).uv(u0, v0).uv2(light).normal(0, -1, 0).endVertex();
        builder.vertex(matrix, maxX, minY, minZ).color(r, g, b, a).uv(u1, v0).uv2(light).normal(0, -1, 0).endVertex();
        builder.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a).uv(u1, v1).uv2(light).normal(0, -1, 0).endVertex();
    }
}
