package kirsan122.assemblyexpansion.client.jei;

import kirsan122.assemblyexpansion.AssemblyExpansion;
import kirsan122.assemblyexpansion.client.gui.GuiData;
import kirsan122.assemblyexpansion.client.gui.inventory.DistillationColumnScreen;
import kirsan122.assemblyexpansion.common.blockEntity.machines.distillation_column.DistillationColumnBlockEntity;
import kirsan122.assemblyexpansion.common.recipe.DistillationColumnRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IClickableIngredient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Optional;

@SuppressWarnings("removal")
public class DistillationColumnRecipeCategory implements IRecipeCategory<DistillationColumnRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(AssemblyExpansion.MOD_ID, "distillation");
    public static final RecipeType<DistillationColumnRecipe> TYPE = new RecipeType<>(UID, DistillationColumnRecipe.class);
    private static final ResourceLocation TEXTURE = new ResourceLocation(AssemblyExpansion.MOD_ID, "textures/gui/distillation_column_gui.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;

    public DistillationColumnRecipeCategory(IGuiHelper helper, ItemStack icon) {

        this.background = helper.createDrawable(TEXTURE, 4, 4, 168, 76);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, icon);

        IDrawableStatic staticArrow = helper.createDrawable(TEXTURE, 176, 0, 27, 9);
        this.arrow = helper.createAnimatedDrawable(staticArrow, 100, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public RecipeType<DistillationColumnRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.assembly_expansion.distillation");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(DistillationColumnRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        this.arrow.draw(graphics, 42, 34);
        graphics.blit(GuiData.DISTILLATION_DIVISIONS, 22, 21, 0, 0, 16, 37, 16, 37);

        FluidStack[] outputs = recipe.getOutputs();
        for (int i = 0; i < 4; i++) {
            if (outputs[i] != null) {
                int tankX = 72 + (i * 22);
                graphics.blit(GuiData.DISTILLATION_DIVISIONS, tankX, 21, 0, 0, 16, 37, 16, 37);
            }
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DistillationColumnRecipe recipe, IFocusGroup focuses) {

        builder.addSlot(RecipeIngredientRole.INPUT, 22, 21)
                .addIngredient(ForgeTypes.FLUID_STACK, recipe.getInputFluid())
                .setFluidRenderer(recipe.getInputFluid().getAmount(), false, 16, 37);


        FluidStack[] outputs = recipe.getOutputs();
        for (int i = 0; i < Math.min(outputs.length, 4); i++) {
            if (outputs[i] != null && !outputs[i].isEmpty()) {
                builder.addSlot(RecipeIngredientRole.OUTPUT, 72 + (i * 22), 21)
                        .addIngredient(ForgeTypes.FLUID_STACK, outputs[i])
                        .setFluidRenderer(outputs[i].getAmount(), false, 16, 37);

            }
        }
    }
    public static class DistillationColumnGuiHandler implements IGuiContainerHandler<DistillationColumnScreen> {

        @Override
        public Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse(DistillationColumnScreen screen, double mouseX, double mouseY) {
            DistillationColumnBlockEntity blockEntity = screen.getMenu().getBlockEntity();


            int guiLeft = screen.getGuiLeft();
            int guiTop = screen.getGuiTop();
            int relX = (int) (mouseX - guiLeft);
            int relY = (int) (mouseY - guiTop);


            if (isMouseOver(relX, relY, 26, 25, 16, 37)) {
                FluidStack fluid = blockEntity.getInputTankFluid();
                if (!fluid.isEmpty()) {
                    Rect2i area = new Rect2i(guiLeft + 22, guiTop + 21, 16, 37);
                    return Optional.of(createClickableIngredient(ForgeTypes.FLUID_STACK, fluid, area));
                }
            }


            FluidStack[] outputs = blockEntity.getOutputTankFluids();
            for (int i = 0; i < 4; i++) {
                int tankX = 76 + (i * 22);
                int tankY = 25;
                if (isMouseOver(relX, relY, tankX, tankY, 16, 37)) {
                    if (outputs[i] != null && !outputs[i].isEmpty()) {
                        Rect2i area = new Rect2i(guiLeft + tankX, guiTop + tankY, 16, 37);
                        return Optional.of(createClickableIngredient(ForgeTypes.FLUID_STACK, outputs[i], area));
                    }
                }
            }

            return Optional.empty();
        }

        private boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height) {
            return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        }


        private <T> IClickableIngredient<T> createClickableIngredient(IIngredientType<T> type, T value, Rect2i area) {
            return new IClickableIngredient<T>() {
                @Override
                public ITypedIngredient<T> getTypedIngredient() {

                    return new ITypedIngredient<T>() {
                        @Override
                        public IIngredientType<T> getType() { return type; }
                        @Override
                        public T getIngredient() { return value; }
                    };
                }

                @Override
                public Rect2i getArea() {
                    return area;
                }
            };
        }
    }

}
