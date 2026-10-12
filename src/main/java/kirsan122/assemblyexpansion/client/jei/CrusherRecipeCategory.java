package kirsan122.assemblyexpansion.client.jei;

import kirsan122.assemblyexpansion.AssemblyExpansion;
import kirsan122.assemblyexpansion.client.gui.GuiData;
import kirsan122.assemblyexpansion.common.recipe.CrusherRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
@SuppressWarnings("removal")
public class CrusherRecipeCategory implements IRecipeCategory<CrusherRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(AssemblyExpansion.MOD_ID, "crushing");
    public static final RecipeType<CrusherRecipe> TYPE = new RecipeType<>(UID, CrusherRecipe.class);
    private static final ResourceLocation TEXTURE = new ResourceLocation(AssemblyExpansion.MOD_ID, "textures/gui/crusher_gui.png");
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;

    public CrusherRecipeCategory(IGuiHelper helper, ItemStack icon) {
        this.background = helper.createDrawable(TEXTURE, 7, 13, 162, 65);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, icon);

        IDrawableStatic staticArrow = helper.createDrawable(TEXTURE, 176, 14, 24, 17);
        this.arrow = helper.createAnimatedDrawable(staticArrow, 180, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override public RecipeType<CrusherRecipe> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return Component.translatable("jei.assembly_expansion.crushing"); }
    @Override public IDrawable getBackground() { return this.background; }
    @Override public IDrawable getIcon() { return this.icon; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CrusherRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 49, 4)
                .addIngredients(recipe.getIngredients().get(0));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 109, 22)
                .addItemStack(recipe.getResultMain());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 109, 46)
                .addItemStack(recipe.getResultSecond())
                .addTooltipCallback((recipeSlotView, tooltip) -> {
                    if (!recipe.getResultSecond().isEmpty()) {
                        int percentChance = (int) (recipe.getSecondChance() * 100);

                        tooltip.add(Component.translatable("jei.assembly_expansion.chance", percentChance));
                    }
                });
    }

    @Override
    public void draw(CrusherRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.arrow.draw(guiGraphics, 72, 22);


        int totalEnergyNeeded = recipe.getEnergyConsumption() * 200;



        int barHeight = (int) ((long) totalEnergyNeeded * 50 / 10000);


        barHeight = Math.min(barHeight, 50);

        if (barHeight > 0) {

            int yOffset = 50 - barHeight;
            guiGraphics.blit(GuiData.ENERGY_LINE, 155, 5 + yOffset, 0, yOffset, 5, barHeight, 5, 50);
        }
    }

    @Override
    public @NotNull List<Component> getTooltipStrings(CrusherRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        List<Component> tooltips = new ArrayList<>();

        if (mouseX >= 154 && mouseX <= 160 && mouseY >= 4 && mouseY <= 55) {
            int energyPerTick = recipe.getEnergyConsumption();
            int totalEnergy = energyPerTick * 200;

            tooltips.add(Component.literal(energyPerTick + " FE/t"));
            tooltips.add(Component.literal(totalEnergy + " FE"));
        }

        return tooltips;
    }
}
