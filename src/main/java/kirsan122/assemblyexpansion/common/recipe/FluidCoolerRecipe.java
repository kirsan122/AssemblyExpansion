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
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class FluidCoolerRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final FluidStack inputFluid;
    private final FluidStack outputFluid;

    public FluidCoolerRecipe(ResourceLocation id, FluidStack input, FluidStack output) {
        this.id = id;
        this.inputFluid = input;
        this.outputFluid = output;
    }

    public FluidStack getInputFluid() { return inputFluid; }
    public FluidStack getOutputFluid() { return outputFluid; }

    @Override public boolean matches(Container pContainer, Level pLevel) { return false; }
    @Override public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) { return ItemStack.EMPTY; }
    @Override public boolean canCraftInDimensions(int pWidth, int pHeight) { return true; }
    @Override public ItemStack getResultItem(RegistryAccess pRegistryAccess) { return ItemStack.EMPTY; }
    @Override public ResourceLocation getId() { return id; }
    @Override public RecipeSerializer<?> getSerializer() { return AERecipes.FLUID_COOLER_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return AERecipes.FLUID_COOLER_TYPE.get(); }


    public static class Serializer implements RecipeSerializer<FluidCoolerRecipe> {
        @Override
        public FluidCoolerRecipe fromJson(ResourceLocation id, JsonObject json) {

            JsonObject inputJson = GsonHelper.getAsJsonObject(json, "input");
            ResourceLocation inputFluidId = new ResourceLocation(GsonHelper.getAsString(inputJson, "fluid"));
            Fluid inputFluid = ForgeRegistries.FLUIDS.getValue(inputFluidId);
            if (inputFluid == null) {
                throw new IllegalArgumentException("Unknown input fluid '" + inputFluidId + "' in recipe " + id);
            }
            FluidStack input = new FluidStack(inputFluid, GsonHelper.getAsInt(inputJson, "amount"));


            JsonObject outputJson = GsonHelper.getAsJsonObject(json, "output");
            ResourceLocation outputFluidId = new ResourceLocation(GsonHelper.getAsString(outputJson, "fluid"));
            Fluid outputFluid = ForgeRegistries.FLUIDS.getValue(outputFluidId);
            if (outputFluid == null) {
                throw new IllegalArgumentException("Unknown output fluid '" + outputFluidId + "' in recipe " + id);
            }
            FluidStack output = new FluidStack(outputFluid, GsonHelper.getAsInt(outputJson, "amount"));

            return new FluidCoolerRecipe(id, input, output);
        }

        @Override
        public @Nullable FluidCoolerRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            FluidStack input = buf.readFluidStack();
            FluidStack output = buf.readFluidStack();
            return new FluidCoolerRecipe(id, input, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, FluidCoolerRecipe recipe) {
            buf.writeFluidStack(recipe.inputFluid);
            buf.writeFluidStack(recipe.outputFluid);
        }
    }
}