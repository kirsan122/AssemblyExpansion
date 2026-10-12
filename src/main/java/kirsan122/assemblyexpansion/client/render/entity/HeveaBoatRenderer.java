package kirsan122.assemblyexpansion.client.render.entity;

import kirsan122.assemblyexpansion.AssemblyExpansion;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;

public class HeveaBoatRenderer extends BoatRenderer {
    private final ResourceLocation texture;
    private final ListModel<Boat> model;

    public HeveaBoatRenderer(EntityRendererProvider.Context context, boolean hasChest) {
        super(context, hasChest);

        if (hasChest) {
            this.texture = new ResourceLocation(AssemblyExpansion.MOD_ID, "textures/entity/chest_boat/hevea.png");
            this.model = new ChestBoatModel(context.bakeLayer(net.minecraft.client.model.geom.ModelLayers.createChestBoatModelName(Boat.Type.OAK)));
        } else {
            this.texture = new ResourceLocation(AssemblyExpansion.MOD_ID, "textures/entity/boat/hevea.png");
            this.model = new BoatModel(context.bakeLayer(net.minecraft.client.model.geom.ModelLayers.createBoatModelName(Boat.Type.OAK)));
        }
    }


    @Override
    public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat entity) {
        return Pair.of(this.texture, this.model);
    }

    @Override
    public ResourceLocation getTextureLocation(Boat entity) {
        return this.texture;
    }
}

