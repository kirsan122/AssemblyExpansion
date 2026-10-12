package kirsan122.assemblyexpansion.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import kirsan122.assemblyexpansion.common.init.AERecipes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GeneratorRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final List<FuelEntry> fuelEntries;

    public GeneratorRecipe(ResourceLocation id, List<FuelEntry> fuelEntries) {
        this.id = id;
        this.fuelEntries = fuelEntries;
    }



    public int getBurnTime(ItemStack stack) {
        if (stack.isEmpty()) return 0;

        for (FuelEntry entry : this.fuelEntries) {

            if (entry.isItemValid(stack)) {

                return entry.getEnergy() / 20;
            }
        }
        return 0;
    }

    public List<FuelEntry> getFuelEntries() {
        return this.fuelEntries;
    }



    @Override public boolean matches(Container container, Level level) { return true; }
    @Override public ItemStack assemble(Container container, RegistryAccess registryAccess) { return ItemStack.EMPTY; }
    @Override public boolean canCraftInDimensions(int width, int height) { return true; }
    @Override public ItemStack getResultItem(RegistryAccess registryAccess) { return ItemStack.EMPTY; }
    @Override public ResourceLocation getId() { return this.id; }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AERecipes.GENERATOR_FUEL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return AERecipes.GENERATOR_FUEL_TYPE.get();
    }




    public static class Serializer implements RecipeSerializer<GeneratorRecipe> {

        @Override
        public GeneratorRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            List<FuelEntry> entries = new ArrayList<>();

            if (json.has("fuels") && json.get("fuels").isJsonArray()) {
                JsonArray jsonArray = json.getAsJsonArray("fuels");

                for (JsonElement element : jsonArray) {
                    if (!element.isJsonObject()) {
                        throw new JsonSyntaxException("Each element in the 'fuels' array must be a JSON object");
                    }
                    entries.add(FuelEntry.fromJson(element.getAsJsonObject()));
                }
            } else {
                throw new JsonSyntaxException("The 'fuels' array is missing from the fuel generator recipe");
            }

            return new GeneratorRecipe(recipeId, entries);
        }

        @Override
        public @Nullable GeneratorRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int size = buffer.readInt();
            List<FuelEntry> entries = new ArrayList<>(size);

            for (int i = 0; i < size; i++) {
                entries.add(FuelEntry.fromNetwork(buffer));
            }

            return new GeneratorRecipe(recipeId, entries);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, GeneratorRecipe recipe) {
            buffer.writeInt(recipe.getFuelEntries().size());
            for (FuelEntry entry : recipe.getFuelEntries()) {
                entry.toNetwork(buffer);
            }
        }
    }




    public static class FuelEntry {
        private final Ingredient ingredient;
        private final int energy;

        public FuelEntry(Ingredient ingredient, int energy) {
            this.ingredient = ingredient;
            this.energy = energy;
        }

        public Ingredient getIngredient() { return this.ingredient; }
        public int getEnergy() { return this.energy; }



        public boolean isItemValid(ItemStack stack) {
            return this.ingredient != null && this.ingredient.test(stack);
        }

        public static FuelEntry fromJson(JsonObject json) {
            Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));
            int energy = GsonHelper.getAsInt(json, "energy");
            return new FuelEntry(ingredient, energy);
        }

        public void toNetwork(FriendlyByteBuf buffer) {
            this.ingredient.toNetwork(buffer);
            buffer.writeInt(this.energy);
        }

        public static FuelEntry fromNetwork(FriendlyByteBuf buffer) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            int energy = buffer.readInt();
            return new FuelEntry(ingredient, energy);
        }
    }
}
