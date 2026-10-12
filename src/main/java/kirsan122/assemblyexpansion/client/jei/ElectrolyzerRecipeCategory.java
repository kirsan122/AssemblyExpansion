package kirsan122.assemblyexpansion.client.jei;

import kirsan122.assemblyexpansion.AssemblyExpansion;
import kirsan122.assemblyexpansion.client.gui.GuiData;
import kirsan122.assemblyexpansion.client.gui.inventory.ElectrolyzerScreen;
import kirsan122.assemblyexpansion.common.blockEntity.machines.ElectrolyzerBlockEntity;
import kirsan122.assemblyexpansion.common.recipe.ElectrolyzerRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
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
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@SuppressWarnings("removal")
public class ElectrolyzerRecipeCategory implements IRecipeCategory<ElectrolyzerRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(AssemblyExpansion.MOD_ID, "electrolysis");
    public static final RecipeType<ElectrolyzerRecipe> TYPE = new RecipeType<>(UID, ElectrolyzerRecipe.class);
    private static final ResourceLocation TEXTURE = new ResourceLocation(AssemblyExpansion.MOD_ID, "textures/gui/electrolyzer_gui.png");

    private final IDrawable background;
    private final IDrawable icon;

    public ElectrolyzerRecipeCategory(IGuiHelper helper, ItemStack icon) {


        this.background = helper.createDrawable(TEXTURE, 4, 4, 168, 76);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, icon);
    }

    @Override
    public RecipeType<ElectrolyzerRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.assembly_expansion.electrolysis");
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
    public void draw(ElectrolyzerRecipe recipe, IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {




        graphics.blit(GuiData.ELECTROLYZER_DIVISIONS, 4, 9, 0, 0, 16, 45, 16, 45);


        FluidStack[] outputs = recipe.getOutputs();
        for (int i = 0; i < 2; i++) {
            if (outputs[i] != null && !outputs[i].isEmpty()) {
                int tankX = 76 + (i * 36);
                graphics.blit(GuiData.ELECTROLYZER_DIVISIONS, tankX, 9, 0, 0, 16, 45, 16, 45);
            }
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ElectrolyzerRecipe recipe, IFocusGroup focuses) {





        builder.addSlot(RecipeIngredientRole.INPUT, 4, 9)
                .addIngredient(ForgeTypes.FLUID_STACK, recipe.getInputFluid())

                .setFluidRenderer(8000, false, 16, 45);


        FluidStack[] outputs = recipe.getOutputs();
        for (int i = 0; i < 2; i++) {
            if (outputs[i] != null && !outputs[i].isEmpty()) {

                int tankX = 76 + (i * 36);
                builder.addSlot(RecipeIngredientRole.OUTPUT, tankX, 9)
                        .addIngredient(ForgeTypes.FLUID_STACK, outputs[i])
                        .setFluidRenderer(8000, false, 16, 45);
            }
        }
    }

    public static class ElectrolyzerGuiHandler implements IGuiContainerHandler<ElectrolyzerScreen> {

        @Override
        public @NotNull Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse(ElectrolyzerScreen screen, double mouseX, double mouseY) {
            ElectrolyzerBlockEntity blockEntity = screen.getMenu().getBlockEntity();


            int guiLeft = screen.getGuiLeft();
            int guiTop = screen.getGuiTop();
            int relX = (int) (mouseX - guiLeft);
            int relY = (int) (mouseY - guiTop);


            if (isMouseOver(relX, relY, 8, 13, 16, 45)) {
                FluidStack fluid = blockEntity.getTankCapability(0).map(t -> t.getFluidInTank(0)).orElse(FluidStack.EMPTY);
                if (!fluid.isEmpty()) {
                    Rect2i area = new Rect2i(guiLeft + 8, guiTop + 13, 16, 45);
                    return Optional.of(createClickableIngredient(ForgeTypes.FLUID_STACK, fluid, area));
                }
            }


            for (int i = 0; i < 2; i++) {
                int tankX = (i == 0) ? 80 : 116;
                int tankY = 13;
                if (isMouseOver(relX, relY, tankX, tankY, 16, 45)) {
                    FluidStack fluid = blockEntity.getTankCapability(i + 1).map(t -> t.getFluidInTank(0)).orElse(FluidStack.EMPTY);
                    if (!fluid.isEmpty()) {
                        Rect2i area = new Rect2i(guiLeft + tankX, guiTop + tankY, 16, 45);
                        return Optional.of(createClickableIngredient(ForgeTypes.FLUID_STACK, fluid, area));
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
                public @NotNull ITypedIngredient<T> getTypedIngredient() {
                    return new ITypedIngredient<T>() {
                        @Override
                        public @NotNull IIngredientType<T> getType() { return type; }
                        @Override
                        public @NotNull T getIngredient() { return value; }
                    };
                }

                @Override
                public @NotNull Rect2i getArea() { return area; }
            };
        }
    }
}
