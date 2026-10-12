package kirsan122.assemblyexpansion.common.recipe;

import com.google.gson.JsonObject;
import kirsan122.assemblyexpansion.common.init.AERecipes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class ElectrolyzerRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final FluidStack inputFluid;
    private final FluidStack[] outputFluids;

    public ElectrolyzerRecipe(ResourceLocation id, FluidStack stack, FluidStack[] outputFluids) {
        this.id = id;
        this.inputFluid = stack;
        this.outputFluids = outputFluids;
    }

    public FluidStack getInputFluid() {
        return inputFluid;
    }

    public FluidStack[] getOutputs() {
        return outputFluids;
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        return false;
    }

    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AERecipes.ELECTROLYZER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return AERecipes.ELECTROLYZER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<ElectrolyzerRecipe> {
        @Override
        public ElectrolyzerRecipe fromJson(ResourceLocation id, JsonObject json) {
            JsonObject inputJson = GsonHelper.getAsJsonObject(json, "input");
            FluidStack input = new FluidStack(
                    ForgeRegistries.FLUIDS.getValue(new ResourceLocation(GsonHelper.getAsString(inputJson, "fluid"))),
                    GsonHelper.getAsInt(inputJson, "amount")
            );

            FluidStack[] outputs = new FluidStack[2];
            JsonObject outputsJson = GsonHelper.getAsJsonObject(json, "outputs");
            for (int i = 0; i < 2; i++) {
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
            return new ElectrolyzerRecipe(id, input, outputs);
        }

        @Override
        public @Nullable ElectrolyzerRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            FluidStack input = buf.readFluidStack();
            FluidStack[] outputs = new FluidStack[2];
            for (int i = 0; i < 2; i++) {
                outputs[i] = buf.readFluidStack();
            }
            return new ElectrolyzerRecipe(id, input, outputs);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ElectrolyzerRecipe recipe) {
            buf.writeFluidStack(recipe.inputFluid);
            for (FluidStack out : recipe.outputFluids) {
                buf.writeFluidStack(out);
            }
        }
    }
}