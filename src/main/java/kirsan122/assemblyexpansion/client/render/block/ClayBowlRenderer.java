package kirsan122.assemblyexpansion.client.render.block;

import kirsan122.assemblyexpansion.common.blockEntity.ClayBowlBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

public class ClayBowlRenderer implements BlockEntityRenderer<ClayBowlBlockEntity> {

    public ClayBowlRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(ClayBowlBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        int amount = blockEntity.getFluidAmount();


        float minHeight = 0.0625f;
        float currentFluidHeight = minHeight;


        if (amount > 0) {

            TextureAtlasSprite sprite = Minecraft.getInstance()
                    .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(new net.minecraft.resources.ResourceLocation("minecraft", "block/water_still"));


            float maxHeight = 4.0f / 16.0f;
            currentFluidHeight = minHeight + ((float) amount / 1000.0f) * (maxHeight - minHeight);


            if (amount >= 1000) {
                currentFluidHeight -= 0.005f;
            }


            float minX = 3.0f / 16.0f;
            float maxX = 13.0f / 16.0f;
            float minZ = 3.0f / 16.0f;
            float maxZ = 13.0f / 16.0f;

            VertexConsumer builder = bufferSource.getBuffer(RenderType.translucent());
            Matrix4f matrix = poseStack.last().pose();

            float u0 = sprite.getU0();
            float u1 = sprite.getU1();
            float v0 = sprite.getV0();
            float v1 = sprite.getV1();


            builder.vertex(matrix, minX, currentFluidHeight, minZ).color(255, 255, 255, 255).uv(u0, v0).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0, 1, 0).endVertex();
            builder.vertex(matrix, minX, currentFluidHeight, maxZ).color(255, 255, 255, 255).uv(u0, v1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0, 1, 0).endVertex();
            builder.vertex(matrix, maxX, currentFluidHeight, maxZ).color(255, 255, 255, 255).uv(u1, v1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0, 1, 0).endVertex();
            builder.vertex(matrix, maxX, currentFluidHeight, minZ).color(255, 255, 255, 255).uv(u1, v0).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0, 1, 0).endVertex();
        }


        ItemStack stack = blockEntity.getItemHandler().getStackInSlot(0);
        if (!stack.isEmpty()) {
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            int count = stack.getCount();




            float ITEM_SCALE = 0.5f;
            float STEP_HEIGHT = 0.03125f;
            float EXTRA_Y_OFFSET = 1.0f / 16.0f;


            float BOWL_BOTTOM = 1.0f / 16.0f;


            for (int i = 0; i < count; i++) {
                poseStack.pushPose();


                float yOffset = BOWL_BOTTOM + EXTRA_Y_OFFSET + (i * STEP_HEIGHT);
                poseStack.translate(0.5f, yOffset, 0.5f);


                poseStack.mulPose(Axis.XP.rotationDegrees(90.0f));


                if (i > 0) {
                    float angle = (i * 25.0f) + (blockEntity.getBlockPos().getX() % 10);
                    poseStack.mulPose(Axis.ZP.rotationDegrees(angle));
                }


                poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);


                itemRenderer.renderStatic(
                        stack,
                        ItemDisplayContext.FIXED,
                        combinedLight,
                        combinedOverlay,
                        poseStack,
                        bufferSource,
                        blockEntity.getLevel(),
                        0
                );

                poseStack.popPose();
            }
        }
    }
}
