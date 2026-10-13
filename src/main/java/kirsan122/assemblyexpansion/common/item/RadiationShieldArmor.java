package kirsan122.assemblyexpansion.common.item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;

public class RadiationShieldArmor extends ArmorItem {

    public RadiationShieldArmor(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }


    public int getRadiationResistancePercent() {
        if (this.getType() == ArmorItem.Type.CHESTPLATE) return 45;
        if (this.getType() == Type.LEGGINGS) return 30;
        if (this.getType() == Type.HELMET) return 15;
        if (this.getType() == Type.BOOTS) return 10;
        return 0;
    }
}

