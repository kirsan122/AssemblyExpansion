package kirsan122.assemblyexpansion.client.jei;

import kirsan122.assemblyexpansion.AssemblyExpansion;
import kirsan122.assemblyexpansion.client.gui.GuiData;
import kirsan122.assemblyexpansion.client.gui.inventory.FluidCoolerScreen;
import kirsan122.assemblyexpansion.common.blockEntity.machines.FluidCoolerBlockEntity;
import kirsan122.assemblyexpansion.common.recipe.FluidCoolerRecipe;
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
public class FluidCoolerRecipeCategory implements IRecipeCategory<FluidCoolerRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(AssemblyExpansion.MOD_ID, "fluid_cooling");
    public static final RecipeType<FluidCoolerRecipe> TYPE = new RecipeType<>(UID, FluidCoolerRecipe.class);
    private static final ResourceLocation TEXTURE = new ResourceLocation(AssemblyExpansion.MOD_ID, "textures/gui/fluid_cooler_gui.png");

    private final IDrawable background;
    private final IDrawable icon;

    public FluidCoolerRecipeCategory(IGuiHelper helper, ItemStack icon) {


        this.background = helper.createDrawable(TEXTURE, 50, 15, 76, 58);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, icon);
    }

    @Override
    public RecipeType<FluidCoolerRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.assembly_expansion.fluid_cooling");
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
    public void draw(FluidCoolerRecipe recipe, IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {
        graphics.blit(GuiData.ELECTROLYZER_DIVISIONS, 3, 6, 0, 0, 16, 45, 16, 45);

        graphics.blit(GuiData.ELECTROLYZER_DIVISIONS, 57, 6, 0, 0, 16, 45, 16, 45);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FluidCoolerRecipe recipe, IFocusGroup focuses) {

        builder.addSlot(RecipeIngredientRole.INPUT, 3, 3)
                .addIngredient(ForgeTypes.FLUID_STACK, recipe.getInputFluid())
                .setFluidRenderer(recipe.getInputFluid().getAmount(), false, 16, 52);

        FluidStack output = recipe.getOutputFluid();
        if (output != null && !output.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 57, 3)
                    .addIngredient(ForgeTypes.FLUID_STACK, output)
                    .setFluidRenderer(output.getAmount(), false, 16, 52);

        }
    }

    public static class FluidCoolerGuiHandler implements IGuiContainerHandler<FluidCoolerScreen> {

        @Override
        public @NotNull Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse(FluidCoolerScreen screen, double mouseX, double mouseY) {
            FluidCoolerBlockEntity blockEntity = screen.getMenu().getBlockEntity();


            int guiLeft = screen.getGuiLeft();
            int guiTop = screen.getGuiTop();
            int x = (int) (mouseX - guiLeft);
            int y = (int) (mouseY - guiTop);


            if (isMouseOver(x, y, 53, 18, 16, 52)) {
                FluidStack fluid = blockEntity.getTankCapability(0).map(t -> t.getFluidInTank(0)).orElse(FluidStack.EMPTY);
                if (!fluid.isEmpty()) {
                    Rect2i area = new Rect2i(guiLeft + 8, guiTop + 13, 16, 52);
                    return Optional.of(createClickableIngredient(ForgeTypes.FLUID_STACK, fluid, area));
                }
            }


            if (isMouseOver(x, y, 107, 18, 16, 52)) {
                FluidStack fluid = blockEntity.getTankCapability(1).map(t -> t.getFluidInTank(0)).orElse(FluidStack.EMPTY);
                if (!fluid.isEmpty()) {
                    Rect2i area = new Rect2i(guiLeft + 107, guiTop + 18, 16, 52);
                    return Optional.of(createClickableIngredient(ForgeTypes.FLUID_STACK, fluid, area));
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
                        public @NotNull IIngredientType<T> getType() {
                            return type;
                        }

                        @Override
                        public @NotNull T getIngredient() {
                            return value;
                        }
                    };
                }

                @Override
                public @NotNull Rect2i getArea() {
                    return area;
                }
            };
        }
    }
}
