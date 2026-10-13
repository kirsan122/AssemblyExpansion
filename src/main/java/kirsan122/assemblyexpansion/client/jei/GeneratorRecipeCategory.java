package kirsan122.assemblyexpansion.client.jei;

import kirsan122.assemblyexpansion.AssemblyExpansion;
import kirsan122.assemblyexpansion.common.recipe.GeneratorRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class GeneratorRecipeCategory implements IRecipeCategory<GeneratorRecipe.FuelEntry> {
    public static final ResourceLocation UID = new ResourceLocation(AssemblyExpansion.MOD_ID, "generator_fuel");
    public static final RecipeType<GeneratorRecipe.FuelEntry> TYPE = new RecipeType<>(UID, GeneratorRecipe.FuelEntry.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated flame;
    private final IDrawable staticFlame;
    private final IDrawable slotDrawable;
    private final Component localizedName;

    public GeneratorRecipeCategory(IGuiHelper helper, ItemStack icon) {
        ResourceLocation furnaceTexture = new ResourceLocation("minecraft", "textures/gui/container/furnace.png");



        this.background = helper.createBlankDrawable(160, 34);


        this.slotDrawable = helper.createDrawable(furnaceTexture, 55, 52, 18, 18);

        this.staticFlame = helper.createDrawable(furnaceTexture, 56, 36, 14, 14);

        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, icon);
        this.localizedName = Component.translatable("jei.assembly_expansion.generator_fuel");


        this.flame = helper.drawableBuilder(furnaceTexture, 176, 0, 14, 14).buildAnimated(200, IDrawableAnimated.StartDirection.BOTTOM, false);
    }

    @Override
    public RecipeType<GeneratorRecipe.FuelEntry> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return this.localizedName;
    }
    @SuppressWarnings("removal")
    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GeneratorRecipe.FuelEntry recipe, IFocusGroup focuses) {

        builder.addSlot(RecipeIngredientRole.INPUT, 1, 16)
                .addIngredients(recipe.getIngredient());
    }

    @Override
    public void draw(GeneratorRecipe.FuelEntry recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {

        slotDrawable.draw(graphics, 0, 15);
        staticFlame.draw(graphics, 2, 0);

        flame.draw(graphics, 2, 0);

        Component energyText = Component.translatable("jei.assembly_expansion.tooltip.generator_fuel", recipe.getEnergy());

        var font = Minecraft.getInstance().font;

        graphics.drawString(font, energyText, 24, 10, 0xFF8B8B8B, false);
    }
}
