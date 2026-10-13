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
import org.jetbrains.annotations.Nullable;

public class CrusherRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final ItemStack result;
    private final ItemStack resultSecond;
    private final float secondChance;
    private final int energyConsumption;

    public CrusherRecipe(ResourceLocation id, Ingredient ingredient, ItemStack result, ItemStack resultSecond, float secondChance, int energyConsumption) {
        this.id = id;
        this.ingredient = ingredient;
        this.result = result;
        this.resultSecond = resultSecond;
        this.secondChance = secondChance;
        this.energyConsumption = energyConsumption;
    }

    @Override public boolean matches(Container pContainer, Level pLevel) { return ingredient.test(pContainer.getItem(0)); }
    @Override public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) { return result.copy(); }
    @Override public boolean canCraftInDimensions(int pWidth, int pHeight) { return true; }
    @Override public ItemStack getResultItem(RegistryAccess pRegistryAccess) { return result; }

    public ItemStack getResultMain() { return result; }
    public ItemStack getResultSecond() { return resultSecond; }
    public float getSecondChance() { return secondChance; }
    public int getEnergyConsumption() { return energyConsumption; }

    @Override public ResourceLocation getId() { return id; }
    @Override public RecipeSerializer<?> getSerializer() { return AERecipes.CRUSHER_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return AERecipes.CRUSHER_RECIPE_TYPE.get(); }

    @Override
    public net.minecraft.core.NonNullList<Ingredient> getIngredients() {
        net.minecraft.core.NonNullList<Ingredient> list = net.minecraft.core.NonNullList.create();
        list.add(this.ingredient);
        return list;
    }

    public static class Serializer implements RecipeSerializer<CrusherRecipe> {
        @Override
        public CrusherRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "ingredient"));


            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "result"));


            ItemStack resultSecond = ItemStack.EMPTY;
            float chance = 0.0f;
            if (pSerializedRecipe.has("resultSecond")) {
                JsonObject secondObj = GsonHelper.getAsJsonObject(pSerializedRecipe, "resultSecond");
                resultSecond = ShapedRecipe.itemStackFromJson(secondObj);
                chance = GsonHelper.getAsFloat(secondObj, "chance", 1.0f);
            }

            int energyConsumption = GsonHelper.getAsInt(pSerializedRecipe, "energyConsumption");
            return new CrusherRecipe(pRecipeId, ingredient, result, resultSecond, chance, energyConsumption);
        }

        @Override
        public @Nullable CrusherRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
            ItemStack result = pBuffer.readItem();
            ItemStack resultSecond = pBuffer.readItem();
            float chance = pBuffer.readFloat();
            int energyConsumption = pBuffer.readInt();
            return new CrusherRecipe(pRecipeId, ingredient, result, resultSecond, chance, energyConsumption);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, CrusherRecipe pRecipe) {
            pRecipe.ingredient.toNetwork(pBuffer);
            pBuffer.writeItem(pRecipe.result);
            pBuffer.writeItem(pRecipe.resultSecond);
            pBuffer.writeFloat(pRecipe.secondChance);
            pBuffer.writeInt(pRecipe.energyConsumption);
        }
    }
}
