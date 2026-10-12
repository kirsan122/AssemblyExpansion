package kirsan122.assemblyexpansion.common.liquid;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class FluidProperties {
    @Nullable
    String descriptionId = null;
    String color = "0xFFFFFFFF";
    String renderType = "translucent";
    ResourceLocation still = new ResourceLocation("block/water_still");
    ResourceLocation flow = new ResourceLocation("block/water_flow");
    int density = 1000;
    int viscosity = 1000;
    int levelDecreasePerBlock = 1;
    int slopeFindDistance = 4;
    int lightLevel = 0;
    int temperature = 300;
    int tickRate = 5;
    boolean hasBlock = false;
    boolean hasBucket = false;
    boolean canPushEntity = true;
    boolean canSwim = true;
    boolean canDrown = true;
    double motionScale = 0.014;
    float fallDistanceModifier = 0.5f;
    float fogNear = 0.0f;
    float fogFar = 12.0f;

    public FluidProperties fogDistance(float near, float far) {
        this.fogNear = near;
        this.fogFar = far;
        return this;
    }

    public FluidProperties density(int value) {
        this.density = value;
        return this;
    }

    public FluidProperties setRenderType(String value) {
        this.renderType = value;
        return this;
    }

    public FluidProperties viscosity(int value) {
        this.viscosity = value;
        return this;
    }

    public FluidProperties tickRate(int value) {
        this.tickRate = value;
        return this;
    }


    public FluidProperties descriptionId(String value) {
        this.descriptionId = value;
        return this;
    }

    public FluidProperties setColor(String value) {
        this.color = value;
        return this;
    }

    public FluidProperties hasBlock() {
        this.hasBlock = true;
        return this;
    }

    public FluidProperties hasBucket() {
        this.hasBucket = true;
        return this;
    }

    public FluidProperties levelDecreasePerBlock(int value) {
        this.levelDecreasePerBlock = value;
        return this;
    }

    public FluidProperties slopeFindDistance(int value) {
        this.slopeFindDistance = value;
        return this;
    }

    public FluidProperties setNewTexture(ResourceLocation still, ResourceLocation flow) {
        this.still = still;
        this.flow = flow;
        return this;
    }

    public FluidProperties temperature(int value) {
        this.temperature = value;
        return this;
    }

    public FluidProperties motionScale(double value) {
        this.motionScale = value;
        return this;
    }

    public FluidProperties canPushEntity(boolean value) {
        this.canPushEntity = value;
        return this;
    }

    public FluidProperties canSwim(boolean value) {
        this.canSwim = value;
        return this;
    }

    public FluidProperties canDrown(boolean value) {
        this.canDrown = value;
        return this;
    }

    public FluidProperties fallDistanceModifier(float value) {
        this.fallDistanceModifier = value;
        return this;
    }

    public FluidProperties lightLevel(int value) {
        this.lightLevel = value;
        return this;
    }
}
