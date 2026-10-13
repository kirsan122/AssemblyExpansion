package kirsan122.assemblyexpansion.common.inventory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import java.util.Arrays;
import java.util.function.Predicate;

public class SizedIngredient implements Predicate<ItemStack> {

    private final Ingredient ingredient;
    private final int count;

    public SizedIngredient(Ingredient vanillaIngredient, int count) {
        this.ingredient = vanillaIngredient;
        this.count = count;
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public int getCount() {
        return this.count;
    }


    @Override
    public boolean test(ItemStack stack) {
        return this.ingredient.test(stack) && stack.getCount() >= this.count;
    }


    public ItemStack[] getSizedStacks() {
        return Arrays.stream(this.ingredient.getItems())
                .map(stack -> {
                    ItemStack copy = stack.copy();
                    copy.setCount(this.count);
                    return copy;
                })
                .toArray(ItemStack[]::new);
    }



    public static SizedIngredient fromJson(JsonObject json) {

        Ingredient ingredient = Ingredient.fromJson(json);

        int count = GsonHelper.getAsInt(json, "count", 1);
        return new SizedIngredient(ingredient, count);
    }

    public JsonElement toJson() {

        JsonElement element = this.ingredient.toJson();
        JsonObject jsonObject = element.getAsJsonObject();


        if (this.count > 1) {
            jsonObject.addProperty("count", this.count);
        }
        return jsonObject;
    }



    public void toNetwork(FriendlyByteBuf buffer) {
        this.ingredient.toNetwork(buffer);
        buffer.writeVarInt(this.count);
    }

    public static SizedIngredient fromNetwork(FriendlyByteBuf buffer) {
        Ingredient ingredient = Ingredient.fromNetwork(buffer);
        int count = buffer.readVarInt();
        return new SizedIngredient(ingredient, count);
    }
}