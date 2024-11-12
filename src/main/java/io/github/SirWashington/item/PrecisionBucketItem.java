package io.github.SirWashington.item;

import io.github.SirWashington.component.ModDataComponentTypes;
import io.github.SirWashington.nbtUtil.DataComponentUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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

        DataComponentUtils.getOrCreateComponent(ModDataComponentTypes.BUCKET_FILL_LEVEL, itemStack);

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
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        if (false) {
            int bucketFillLevel = DataComponentUtils.getOrCreateComponent(ModDataComponentTypes.BUCKET_FILL_LEVEL, itemStack);
            String toolTipText = "Bucket contains: " + bucketFillLevel + "levels " + "of fluid";
            list.add(Component.literal(toolTipText));
        }
        else {
            String toolTipText = "Bucket contains: " + 0 + "levels " + "of fluid";
            list.add(Component.literal(toolTipText));
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
        int fillLevel = DataComponentUtils.getOrCreateComponent(ModDataComponentTypes.BUCKET_FILL_LEVEL, itemStack);
        int maxFillLevel = 8;
        float fraction = (float) fillLevel / (float) maxFillLevel;
        return (int) (13f * fraction);
    }
}
