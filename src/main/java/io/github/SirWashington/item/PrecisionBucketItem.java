package io.github.SirWashington.item;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class PrecisionBucketItem extends Item {

    public PrecisionBucketItem(Item.Properties properties) {
        super(properties);
    }

    public InteractionResult useOn(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        Player player = useOnContext.getPlayer();
        ItemStack itemStack = useOnContext.getItemInHand();
        BlockPos targetPos = useOnContext.getClickedPos();

        if (!itemStack.hasTag()) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("washwater:bucketFillLevel", 0);
            itemStack.setTag(tag);
        }
        if (player != null) {
            if (!player.isCrouching()) {
                BucketMechanics.precisionBucketPlace(level, targetPos, itemStack, player);
            }
            else {
                BucketMechanics.precisionBucketPickup(level, targetPos, itemStack, player);
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        if (itemStack.hasTag()) {
            int bucketFillLevel = itemStack.getTag().getInt("washwater:bucketFillLevel");
            String toolTipText = "Bucket contains: " + bucketFillLevel + "levels " + "of fluid";
            list.add(new TextComponent(toolTipText));
        }
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {
        return true;
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        return Mth.color(56, 141, 252);
    }

    @Override
    public int getBarWidth(ItemStack itemStack) {
        if (itemStack.hasTag()) {
            int fillLevel = itemStack.getTag().getInt("washwater:bucketFillLevel");
            int maxFillLevel = 8;
            float fraction = (float) fillLevel / (float) maxFillLevel;
            return (int) (13f * fraction);
        }
        else return 0;
    }
}
