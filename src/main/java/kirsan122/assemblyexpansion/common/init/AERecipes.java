package kirsan122.assemblyexpansion.common.init;

import kirsan122.assemblyexpansion.AssemblyExpansion;
import kirsan122.assemblyexpansion.common.recipe.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AERecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, AssemblyExpansion.MOD_ID);

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, AssemblyExpansion.MOD_ID);

    public static final RegistryObject<RecipeType<CrusherRecipe>> CRUSHER_RECIPE_TYPE = RECIPE_TYPES.register(
            "crushing", () -> new RecipeType<>() {});
    public static final RegistryObject<RecipeSerializer<CrusherRecipe>> CRUSHER_SERIALIZER =
            SERIALIZERS.register("crushing", CrusherRecipe.Serializer::new);

    public static final RegistryObject<RecipeSerializer<GeneratorRecipe>> GENERATOR_FUEL_SERIALIZER =
            SERIALIZERS.register("generator_fuel", GeneratorRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<GeneratorRecipe>> GENERATOR_FUEL_TYPE =
            RECIPE_TYPES.register("generator_fuel", () -> new RecipeType<>() {});
    public static final RegistryObject<RecipeType<MechanicalSintererRecipe>> MECHANICAL_SINTERER_TYPE =
            RECIPE_TYPES.register("sintering", () -> new RecipeType<>() {});
    public static final RegistryObject<RecipeSerializer<MechanicalSintererRecipe>> MECHANICAL_SINTERER_SERIALIZER =
            SERIALIZERS.register("sintering", MechanicalSintererRecipe.Serializer::new);


    public static final RegistryObject<RecipeType<DistillationColumnRecipe>> DISTILLATION_COLUMN_TYPE =
            RECIPE_TYPES.register("distillation", () -> new RecipeType<>() {});
    public static final RegistryObject<RecipeSerializer<DistillationColumnRecipe>> DISTILLATION_COLUMN_SERIALIZER =
            SERIALIZERS.register("distillation", DistillationColumnRecipe.Serializer::new);

    public static final RegistryObject<RecipeType<ElectrolyzerRecipe>> ELECTROLYZER_TYPE =
            RECIPE_TYPES.register("electrolysis", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return "electrolysis";
                }
            });
    public static final RegistryObject<RecipeSerializer<ElectrolyzerRecipe>> ELECTROLYZER_SERIALIZER =
            SERIALIZERS.register("electrolysis", ElectrolyzerRecipe.Serializer::new);

    public static final RegistryObject<RecipeType<FluidCoolerRecipe>> FLUID_COOLER_TYPE =
            RECIPE_TYPES.register("fluid_cooling", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return "fluid_cooling";
                }
            });
    public static final RegistryObject<RecipeSerializer<FluidCoolerRecipe>> FLUID_COOLER_SERIALIZER =
            SERIALIZERS.register("fluid_cooling", FluidCoolerRecipe.Serializer::new);










}
