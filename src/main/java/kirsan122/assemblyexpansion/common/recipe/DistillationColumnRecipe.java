package kirsan122.assemblyexpansion.common.recipe;

import com.google.gson.JsonObject;
import kirsan122.assemblyexpansion.common.init.AERecipes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class DistillationColumnRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final FluidStack inputFluid;

    private final FluidStack[] outputs;

    public DistillationColumnRecipe(ResourceLocation id, FluidStack inputFluid, FluidStack[] outputs) {
        this.id = id;
        this.inputFluid = inputFluid;
        this.outputs = outputs;
    }

    public FluidStack getInputFluid() { return inputFluid; }
    public FluidStack[] getOutputs() { return outputs; }

    @Override public boolean matches(Container pContainer, Level pLevel) { return false; }
    @Override public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) { return ItemStack.EMPTY; }
    @Override public boolean canCraftInDimensions(int pWidth, int pHeight) { return true; }
    @Override public ItemStack getResultItem(RegistryAccess pRegistryAccess) { return ItemStack.EMPTY; }
    @Override public ResourceLocation getId() { return id; }
    @Override public RecipeSerializer<?> getSerializer() { return AERecipes.DISTILLATION_COLUMN_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() {
        return AERecipes.DISTILLATION_COLUMN_TYPE.get();
    }


    public static class Serializer implements RecipeSerializer<DistillationColumnRecipe> {
        @Override
        public DistillationColumnRecipe fromJson(ResourceLocation id, JsonObject json) {
            JsonObject inputJson = GsonHelper.getAsJsonObject(json, "input");
            FluidStack input = new FluidStack(
                    ForgeRegistries.FLUIDS.getValue(new ResourceLocation(GsonHelper.getAsString(inputJson, "fluid"))),
                    GsonHelper.getAsInt(inputJson, "amount")
            );

            FluidStack[] outputs = new FluidStack[4];
            JsonObject outputsJson = GsonHelper.getAsJsonObject(json, "outputs");
            for (int i = 0; i < 4; i++) {
                if (outputsJson.has("output_" + i)) {
                    JsonObject out = GsonHelper.getAsJsonObject(outputsJson, "output_" + i);
                    outputs[i] = new FluidStack(
                            ForgeRegistries.FLUIDS.getValue(new ResourceLocation(GsonHelper.getAsString(out, "fluid"))),
                            GsonHelper.getAsInt(out, "amount")
                    );
                } else {
                    outputs[i] = FluidStack.EMPTY;
                }
            }
            return new DistillationColumnRecipe(id, input, outputs);
        }

        @Override
        public @Nullable DistillationColumnRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            FluidStack input = buf.readFluidStack();
            FluidStack[] outputs = new FluidStack[4];
            for (int i = 0; i < 4; i++) {
                outputs[i] = buf.readFluidStack();
            }
            return new DistillationColumnRecipe(id, input, outputs);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, DistillationColumnRecipe recipe) {
            buf.writeFluidStack(recipe.inputFluid);
            for (FluidStack out : recipe.outputs) {
                buf.writeFluidStack(out);
            }
        }
    }
}
