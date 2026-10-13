package kirsan122.assemblyexpansion.common.init;

import kirsan122.assemblyexpansion.client.model.AntiRadiationHelmetModel;
import kirsan122.assemblyexpansion.common.item.HeveaBoatItem;
import kirsan122.assemblyexpansion.common.item.HeveaChestBoatItem;
import kirsan122.assemblyexpansion.common.item.RadiationShieldArmor;
import kirsan122.assemblyexpansion.common.item.RadioactiveItem;
import kirsan122.assemblyexpansion.common.item.tools.EnergyBatteryItem;
import kirsan122.assemblyexpansion.common.item.tools.HammerItem;
import kirsan122.assemblyexpansion.common.item.tools.KnifeItem;
import kirsan122.assemblyexpansion.common.item.tools.WireCuttersItem;
import kirsan122.assemblyexpansion.common.item.upgrades.EnergyUpgradeItem;
import kirsan122.assemblyexpansion.common.item.upgrades.SpeedUpgradeItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static kirsan122.assemblyexpansion.AssemblyExpansion.MOD_ID;

public class AEItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Item> CIRCUIT = register("circuit");
    public static final RegistryObject<Item> ENGINEERING_HAMMER = register("engineering_hammer", () -> new HammerItem(new Item.Properties()));
    public static final RegistryObject<Item> BATTERY = register("battery", () -> new EnergyBatteryItem(2000, 20, new Item.Properties()));
    public static final RegistryObject<Item> IRON_KNIFE = register("iron_knife", () -> new KnifeItem(Tiers.IRON, new Item.Properties()));
    public static final RegistryObject<Item> DIAMOND_KNIFE = register("diamond_knife", () -> new KnifeItem(Tiers.DIAMOND, new Item.Properties()));
    public static final RegistryObject<Item> NETHERITE_KNIFE = register("netherite_knife", () -> new KnifeItem(Tiers.NETHERITE, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> ANTI_RADIATION_HELMET = register("anti_radiation_helmet", () -> new RadiationShieldArmor(AEArmorMaterials.ANTI_RADIATION, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1)) {
        @Override
        public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.extensions.common.IClientItemExtensions> consumer) {
            consumer.accept(new net.minecraftforge.client.extensions.common.IClientItemExtensions() {
                @Override
                public net.minecraft.client.model.HumanoidModel<?> getHumanoidArmorModel(net.minecraft.world.entity.LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, net.minecraft.client.model.HumanoidModel<?> defaultModel) {

                    net.minecraft.client.model.geom.ModelPart modelPart = net.minecraft.client.Minecraft.getInstance()
                            .getEntityModels().bakeLayer(AntiRadiationHelmetModel.LAYER_LOCATION);


                    AntiRadiationHelmetModel model = new AntiRadiationHelmetModel(modelPart);


                    model.young = defaultModel.young;
                    model.crouching = defaultModel.crouching;
                    model.riding = defaultModel.riding;

                    return model;
                }
            });
        }
    });
    public static final RegistryObject<Item> ANTI_RADIATION_CHESTPLATE = register("anti_radiation_chestplate", () -> new RadiationShieldArmor(AEArmorMaterials.ANTI_RADIATION, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ANTI_RADIATION_LEGGINGS = register("anti_radiation_leggings", () -> new RadiationShieldArmor(AEArmorMaterials.ANTI_RADIATION, ArmorItem.Type.LEGGINGS, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ANTI_RADIATION_BOOTS = register("anti_radiation_boots", () -> new RadiationShieldArmor(AEArmorMaterials.ANTI_RADIATION, ArmorItem.Type.BOOTS, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> UPGRADE_SPEED = register("upgrade_speed", () -> new SpeedUpgradeItem(new Item.Properties().stacksTo(8)));
    public static final RegistryObject<Item> UPGRADE_ENERGY = register("upgrade_energy", () -> new EnergyUpgradeItem(5000, new Item.Properties().stacksTo(8)));
    public static final RegistryObject<Item> HEVEA_BOAT = ITEMS.register("hevea_boat", () -> new HeveaBoatItem(false, AEEntities.HEVEA_BOAT::get, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> HEVEA_CHEST_BOAT = register("hevea_chest_boat", () -> new HeveaChestBoatItem(true, AEEntities.HEVEA_CHEST_BOAT::get, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> RAW_TIN = register("raw_tin");
    public static final RegistryObject<Item> TIN_INGOT = register("tin_ingot");
    public static final RegistryObject<Item> TIN_DUST = register("tin_dust");
    public static final RegistryObject<Item> TIN_PLATE = register("tin_plate");
    public static final RegistryObject<Item> TIN_GEAR = register("tin_gear");
    public static final RegistryObject<Item> COPPER_DUST = register("copper_dust");
    public static final RegistryObject<Item> COPPER_PLATE = register("copper_plate");
    public static final RegistryObject<Item> COPPER_GEAR = register("copper_gear");
    public static final RegistryObject<Item> IRON_DUST = register("iron_dust");
    public static final RegistryObject<Item> IRON_PLATE = register("iron_plate");
    public static final RegistryObject<Item> IRON_GEAR = register("iron_gear");
    public static final RegistryObject<Item> LEAD_DUST = register("lead_dust");
    public static final RegistryObject<Item> LEAD_PLATE = register("lead_plate");
    public static final RegistryObject<Item> LEAD_INGOT = register("lead_ingot");
    public static final RegistryObject<Item> RAW_LEAD = register("raw_lead");
    public static final RegistryObject<Item> LATEX_DISC = register("latex_disc");
    public static final RegistryObject<Item> RAW_RUBBER = register("raw_rubber");
    public static final RegistryObject<Item> PURIFIED_RUBBER = register("purified_rubber");
    public static final RegistryObject<Item> RUBBER = register("rubber");
    public static final RegistryObject<Item> STEEL_INGOT = register("steel_ingot");
    public static final RegistryObject<Item> STEEL_DUST = register("steel_dust");
    public static final RegistryObject<Item> STEEL_PLATE = register("steel_plate");
    public static final RegistryObject<Item> COAL_DUST = register("coal_dust");
    public static final RegistryObject<Item> BRONZE_INGOT = register("bronze_ingot");
    public static final RegistryObject<Item> BRONZE_PLATE = register("bronze_plate");
    public static final RegistryObject<Item> BRONZE_DUST = register("bronze_dust");
    public static final RegistryObject<Item> ADVANCED_CIRCUIT = register("advanced_circuit");
    public static final RegistryObject<Item> GOLD_DUST = register("gold_dust");
    public static final RegistryObject<Item> URANIUM_DUST = register("uranium_dust", () -> new RadioactiveItem(10, new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> URANIUM_INGOT = register("uranium_ingot", () -> new RadioactiveItem(1, new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> URANYL_NITRATE = register("uranyl_nitrate", () -> new RadioactiveItem(5, new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> ENRICHED_URANYL_NITRATE = register("enriched_uranyl_nitrate", () -> new RadioactiveItem(40, new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> DEPLETED_URANYL_NITRATE = register("depleted_uranyl_nitrate", () -> new RadioactiveItem(2, new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> RAW_URANIUM = register("raw_uranium", () -> new RadioactiveItem(1, new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> WIRE_CUTTERS = register("wire_cutters", () -> new WireCuttersItem(new Item.Properties()));
    public static final RegistryObject<Item> COPPER_WIRE = register("copper_wire");
    public static final RegistryObject<Item> FAN = register("fan");
    public static final RegistryObject<Item> HEAT_EXCHANGER = register("heat_exchanger");


    private static <T extends Item> @NotNull RegistryObject<T> register(@NotNull String id, @NotNull Supplier<T> itemSupplier) {return ITEMS.register(id, itemSupplier);}
    private static RegistryObject<Item> register(@NotNull String id) {return ITEMS.register(id, () -> new Item(new Item.Properties()));}
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
