package kirsan122.assemblyexpansion.common.item.tools;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class KnifeItem extends TieredItem {

    public static final ToolAction KNIFE_SCRATCH = ToolAction.get("knife_scratch");

    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public KnifeItem(Tier tier, Properties properties) {
        super(tier, properties.stacksTo(1));

        float attackDamage = 1.0F + tier.getAttackDamageBonus();
        float attackSpeed = -2.0F;

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double) attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double) attackSpeed, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return toolAction == KNIFE_SCRATCH || super.canPerformAction(stack, toolAction);
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        if (state.is(BlockTags.LEAVES)) {
            return this.getTier().getSpeed() * 1.5F;
        }
        return super.getDestroySpeed(stack, state);
    }


    @Override
    public boolean isCorrectToolForDrops(@NotNull BlockState state) {
        return state.is(BlockTags.LEAVES) || super.isCorrectToolForDrops(state);
    }
}
