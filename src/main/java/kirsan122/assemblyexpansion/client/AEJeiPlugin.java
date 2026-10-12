package kirsan122.assemblyexpansion.client;

import kirsan122.assemblyexpansion.AssemblyExpansion;
import kirsan122.assemblyexpansion.client.gui.GuiData;
import kirsan122.assemblyexpansion.client.gui.inventory.*;
import kirsan122.assemblyexpansion.client.jei.*;
import kirsan122.assemblyexpansion.common.recipe.*;
import kirsan122.assemblyexpansion.common.init.AEBlockItems;
import kirsan122.assemblyexpansion.common.init.AEMachineBlocks;
import kirsan122.assemblyexpansion.common.init.AERecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class AEJeiPlugin implements IModPlugin {

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return new ResourceLocation(AssemblyExpansion.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new CrusherRecipeCategory(
                registration.getJeiHelpers().getGuiHelper(),
                new ItemStack(AEMachineBlocks.CRUSHER.get())
        ));

        registration.addRecipeCategories(new GeneratorRecipeCategory(
                registration.getJeiHelpers().getGuiHelper(),
                new ItemStack(AEMachineBlocks.GENERATOR.get())
        ));

        registration.addRecipeCategories(new MechanicalSintererRecipeCategory(
                registration.getJeiHelpers().getGuiHelper(),
                new ItemStack(AEMachineBlocks.MECHANICAL_SINTERER.get())
        ));

        registration.addRecipeCategories(new DistillationColumnRecipeCategory(
                registration.getJeiHelpers().getGuiHelper(),
                new ItemStack(AEMachineBlocks.DISTILLATION_COLUMN_BASE.get())
        ));

        registration.addRecipeCategories(new ElectrolyzerRecipeCategory(
                registration.getJeiHelpers().getGuiHelper(),
                new ItemStack(AEMachineBlocks.ELECTROLYZER.get())
        ));

        registration.addRecipeCategories(new FluidCoolerRecipeCategory(
                registration.getJeiHelpers().getGuiHelper(),
                new ItemStack(AEMachineBlocks.FLUID_COOLER.get())
        ));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            RecipeManager rm = level.getRecipeManager();

            registration.addIngredientInfo(
                    new ItemStack(AEBlockItems.AIR_COLLECTOR_ITEM.get()),
                    VanillaTypes.ITEM_STACK,
                    Component.translatable("jei.assembly_expansion.collecting_air")
            );

            List<CrusherRecipe> crusherRecipes = rm.getAllRecipesFor(AERecipes.CRUSHER_RECIPE_TYPE.get());
            if (!crusherRecipes.isEmpty()) {
                registration.addRecipes(CrusherRecipeCategory.TYPE, crusherRecipes);
            }
            List<DistillationColumnRecipe> distillationRecipes = rm.getAllRecipesFor(AERecipes.DISTILLATION_COLUMN_TYPE.get());
            if (!distillationRecipes.isEmpty()) {
                registration.addRecipes(DistillationColumnRecipeCategory.TYPE, distillationRecipes);
            }
            List<MechanicalSintererRecipe> mechanicalSintererRecipes = rm.getAllRecipesFor(AERecipes.MECHANICAL_SINTERER_TYPE.get());
            if (!mechanicalSintererRecipes.isEmpty()) {
                registration.addRecipes(MechanicalSintererRecipeCategory.TYPE, mechanicalSintererRecipes);
            }
            List<ElectrolyzerRecipe> electrolyzerRecipes = rm.getAllRecipesFor(AERecipes.ELECTROLYZER_TYPE.get());
            if (!electrolyzerRecipes.isEmpty()) {
                registration.addRecipes(ElectrolyzerRecipeCategory.TYPE, electrolyzerRecipes);
            }

            List<FluidCoolerRecipe> fluidCoolerRecipes = rm.getAllRecipesFor(AERecipes.FLUID_COOLER_TYPE.get());
            if (!electrolyzerRecipes.isEmpty()) {
                registration.addRecipes(FluidCoolerRecipeCategory.TYPE, fluidCoolerRecipes);
            }
            List<GeneratorRecipe> generatorRecipes = rm.getAllRecipesFor(AERecipes.GENERATOR_FUEL_TYPE.get());
            if (!generatorRecipes.isEmpty()) {
                List<GeneratorRecipe.FuelEntry> singleEntries = new ArrayList<>();

                for (GeneratorRecipe recipe : generatorRecipes) {
                    for (GeneratorRecipe.FuelEntry entry : recipe.getFuelEntries()) {
                        net.minecraft.world.item.crafting.Ingredient ingredient = entry.getIngredient();

                        if (ingredient != null && !ingredient.isEmpty()) {
                            boolean hasValidContent = false;

                            try {

                                com.google.gson.JsonElement json = ingredient.toJson();

                                if (json.isJsonObject()) {
                                    com.google.gson.JsonObject obj = json.getAsJsonObject();


                                    if (obj.has("tag")) {
                                        String tagIdStr = obj.get("tag").getAsString();
                                        net.minecraft.resources.ResourceLocation tagId = new net.minecraft.resources.ResourceLocation(tagIdStr);
                                        var tagKey = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, tagId);
                                        var tag = net.minecraftforge.registries.ForgeRegistries.ITEMS.tags().getTag(tagKey);


                                        if (tag != null && !tag.isEmpty()) {
                                            hasValidContent = true;
                                        }
                                    }

                                    else if (obj.has("item")) {
                                        hasValidContent = true;
                                    }
                                }

                                else if (json.isJsonArray()) {
                                    for (com.google.gson.JsonElement element : json.getAsJsonArray()) {
                                        if (element.isJsonObject()) {
                                            com.google.gson.JsonObject obj = element.getAsJsonObject();
                                            if (obj.has("tag")) {
                                                String tagIdStr = obj.get("tag").getAsString();
                                                net.minecraft.resources.ResourceLocation tagId = new net.minecraft.resources.ResourceLocation(tagIdStr);
                                                var tagKey = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, tagId);
                                                var tag = net.minecraftforge.registries.ForgeRegistries.ITEMS.tags().getTag(tagKey);
                                                if (tag != null && !tag.isEmpty()) {
                                                    hasValidContent = true;
                                                    break;
                                                }
                                            } else if (obj.has("item")) {
                                                hasValidContent = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {

                                hasValidContent = ingredient.getItems().length > 0;
                            }


                            if (hasValidContent) {
                                singleEntries.add(entry);
                            }
                        }
                    }
                }

                if (!singleEntries.isEmpty()) {
                    registration.addRecipes(GeneratorRecipeCategory.TYPE, singleEntries);
                }
            }

        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {

        registration.addRecipeCatalyst(new ItemStack(AEMachineBlocks.CRUSHER.get()), CrusherRecipeCategory.TYPE);


        registration.addRecipeCatalyst(new ItemStack(AEMachineBlocks.GENERATOR.get()), GeneratorRecipeCategory.TYPE);

        registration.addRecipeCatalyst(new ItemStack(AEMachineBlocks.ELECTRIC_FURNACE.get()), RecipeTypes.SMELTING);

        registration.addRecipeCatalyst(new ItemStack(AEMachineBlocks.ELECTRIC_FURNACE.get()), RecipeTypes.BLASTING);

        registration.addRecipeCatalyst(new ItemStack(AEMachineBlocks.ELECTRIC_FURNACE.get()), RecipeTypes.SMOKING);

        registration.addRecipeCatalyst(new ItemStack(AEMachineBlocks.MECHANICAL_SINTERER.get()), MechanicalSintererRecipeCategory.TYPE);

        registration.addRecipeCatalyst(new ItemStack(AEMachineBlocks.DISTILLATION_COLUMN_BASE.get()), DistillationColumnRecipeCategory.TYPE);

        registration.addRecipeCatalyst(new ItemStack(AEMachineBlocks.DISTILLATION_COLUMN_SECTION.get()), DistillationColumnRecipeCategory.TYPE);

        registration.addRecipeCatalyst(new ItemStack(AEMachineBlocks.DISTILLATION_COLUMN_TOP.get()), DistillationColumnRecipeCategory.TYPE);

        registration.addRecipeCatalyst(new ItemStack(AEMachineBlocks.FLUID_COOLER.get()), FluidCoolerRecipeCategory.TYPE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {

        registration.addRecipeClickArea(CrusherScreen.class, 79, 35, 24, 24, CrusherRecipeCategory.TYPE);

        registration.addRecipeClickArea(ElectricFurnaceScreen.class, 79, 34, 24, 17, RecipeTypes.SMELTING);

        registration.addRecipeClickArea(ElectricFurnaceScreen.class, 79, 34, 24, 17, RecipeTypes.BLASTING);

        registration.addRecipeClickArea(ElectricFurnaceScreen.class, 79, 34, 24, 17, RecipeTypes.SMOKING);

        registration.addRecipeClickArea(MechanicalSintererScreen.class, 75, 38, 22, 10, MechanicalSintererRecipeCategory.TYPE);


        registration.addGuiContainerHandler(DistillationColumnScreen.class, new DistillationColumnRecipeCategory.DistillationColumnGuiHandler());

        registration.addGuiContainerHandler(ElectrolyzerScreen.class, new ElectrolyzerRecipeCategory.ElectrolyzerGuiHandler());

        registration.addGuiContainerHandler(FluidCoolerScreen.class, new FluidCoolerRecipeCategory.FluidCoolerGuiHandler());


        registration.addGuiContainerHandler(CrusherScreen.class, new IGuiContainerHandler<>() {
            @Override
            public @NotNull List<Rect2i> getGuiExtraAreas(@NotNull CrusherScreen containerScreen) {
                return guiExtraAreas(containerScreen);
            }
        });
        registration.addGuiContainerHandler(ElectricFurnaceScreen.class, new IGuiContainerHandler<>() {
            @Override
            public @NotNull List<Rect2i> getGuiExtraAreas(@NotNull ElectricFurnaceScreen containerScreen) {
                return guiExtraAreas(containerScreen);
            }
        });
        registration.addGuiContainerHandler(MechanicalSintererScreen.class, new IGuiContainerHandler<>() {
            @Override
            public @NotNull List<Rect2i> getGuiExtraAreas(@NotNull MechanicalSintererScreen containerScreen) {
                return guiExtraAreas(containerScreen);
            }
        });
        registration.addGuiContainerHandler(ElectrolyzerScreen.class, new IGuiContainerHandler<>() {
            @Override
            public @NotNull List<Rect2i> getGuiExtraAreas(@NotNull ElectrolyzerScreen containerScreen) {
                List<Rect2i> areas = new ArrayList<>();
                if (containerScreen.getUpgradeAnimationProgress() > 0.0f) {
                    areas.add(new Rect2i(
                            containerScreen.getGuiLeft() - 82,
                            containerScreen.getGuiTop() + 4,
                            82,
                            82
                    ));
                }
                return areas;
            }
        });
        registration.addGuiContainerHandler(AirCollectorScreen.class, new IGuiContainerHandler<>() {
            @Override
            public @NotNull List<Rect2i> getGuiExtraAreas(@NotNull AirCollectorScreen containerScreen) {
                return guiExtraAreas(containerScreen);
            }
        });

        registration.addGuiContainerHandler(FluidCoolerScreen.class,new IGuiContainerHandler<>() {
            @Override
            public @NotNull List<Rect2i> getGuiExtraAreas(@NotNull FluidCoolerScreen containerScreen) {
                return guiExtraAreas(containerScreen);
            }
        });
        registration.addRecipeClickArea(GeneratorScreen.class, 56, 36, 14, 14, GeneratorRecipeCategory.TYPE);
        registration.addRecipeClickArea(DistillationColumnScreen.class, 47, 39, 25, 8, DistillationColumnRecipeCategory.TYPE);
        registration.addRecipeClickArea(ElectrolyzerScreen.class, 29, 32, 42, 8, ElectrolyzerRecipeCategory.TYPE);
        registration.addRecipeClickArea(FluidCoolerScreen.class, 76, 40, 24, 8, FluidCoolerRecipeCategory.TYPE);

    }

    private static List<Rect2i> guiExtraAreas(IProgressGetter containerScreen) {
        List<Rect2i> areas = new ArrayList<>();
        if (containerScreen.getUpgradeAnimationProgress() > 0.0f) {
            areas.add(new Rect2i(
                    containerScreen.getGuiLeft() - 82,
                    containerScreen.getGuiTop() + 4,
                    82,
                    82
            ));
        }
        if (containerScreen.getConfigAnimationProgress() > 0.0f) {
            int configPanelWidth = 75;
            areas.add(new Rect2i(
                    containerScreen.getGuiLeft() + 176,
                    containerScreen.getGuiTop() + 4,
                    configPanelWidth,
                    82
            ));
        }
        return areas;
    }
}
