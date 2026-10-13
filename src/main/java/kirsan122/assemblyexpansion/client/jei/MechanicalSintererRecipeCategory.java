package kirsan122.assemblyexpansion.client.jei;

import kirsan122.assemblyexpansion.AssemblyExpansion;
import kirsan122.assemblyexpansion.common.recipe.MechanicalSintererRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("removal")
public class MechanicalSintererRecipeCategory implements IRecipeCategory<MechanicalSintererRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(AssemblyExpansion.MOD_ID, "sintering");
    public static final RecipeType<MechanicalSintererRecipe> TYPE = new RecipeType<>(UID, MechanicalSintererRecipe.class);
    private static final ResourceLocation TEXTURE = new ResourceLocation(AssemblyExpansion.MOD_ID, "textures/gui/mechanical_sinterer_gui.png");
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;

    public MechanicalSintererRecipeCategory(IGuiHelper helper, ItemStack icon) {
        this.background = helper.createDrawable(TEXTURE, 8, 11, 161, 60);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, icon);

        IDrawableStatic staticArrow = helper.createDrawable(TEXTURE, 176, 0, 34, 11);
        this.arrow = helper.createAnimatedDrawable(staticArrow, 200, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override public RecipeType<MechanicalSintererRecipe> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return Component.translatable("jei.assembly_expansion.sintering"); }
    @Override public IDrawable getBackground() { return this.background; }
    @Override public IDrawable getIcon() { return this.icon; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MechanicalSintererRecipe recipe, IFocusGroup focuses) {

        var itemRendererWithCount = new IIngredientRenderer<ItemStack>() {
            @Override
            public void render(net.minecraft.client.gui.GuiGraphics guiGraphics, ItemStack ingredient) {
                net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
                guiGraphics.renderItem(ingredient, 0, 0);

                guiGraphics.renderItemDecorations(mc.font, ingredient, 0, 0);
            }

            @Override
            public java.util.List<net.minecraft.network.chat.Component> getTooltip(ItemStack ingredient, net.minecraft.world.item.TooltipFlag tooltipFlag) {
                return Screen.getTooltipFromItem(net.minecraft.client.Minecraft.getInstance(), ingredient);
            }
        };


        ItemStack[] sizedInputs1 = recipe.getInfusorInput().getSizedStacks();
        int count1 = recipe.getInfusorInput().getCount();
        var slot1 = builder.addSlot(RecipeIngredientRole.INPUT, 24, 24)
                .addItemStacks(java.util.List.of(sizedInputs1));


        if (count1 > 1) {
            slot1.setCustomRenderer(VanillaTypes.ITEM_STACK, itemRendererWithCount);
        }


        ItemStack[] sizedInputs0 = recipe.getBaseInput().getSizedStacks();
        int count0 = recipe.getBaseInput().getCount();
        var slot0 = builder.addSlot(RecipeIngredientRole.INPUT, 45, 24)
                .addItemStacks(java.util.List.of(sizedInputs0));

        if (count0 > 1) {
            slot0.setCustomRenderer(VanillaTypes.ITEM_STACK, itemRendererWithCount);
        }


        builder.addSlot(RecipeIngredientRole.OUTPUT, 105, 24)
                .addItemStack(recipe.getResult());
    }


    @Override
    public void draw(MechanicalSintererRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.arrow.draw(guiGraphics, 67, 26);
    }
}
