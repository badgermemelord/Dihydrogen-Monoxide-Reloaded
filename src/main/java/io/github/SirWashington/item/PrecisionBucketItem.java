package io.github.SirWashington.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PrecisionBucketItem extends Item {

    public PrecisionBucketItem(Item.Settings properties) {
        super(properties);
    }

    public ActionResult useOnBlock(ItemUsageContext useOnContext) {
        World level = useOnContext.getWorld();
        PlayerEntity player = useOnContext.getPlayer();
        ItemStack itemStack = useOnContext.getStack();
        BlockPos targetPos = useOnContext.getBlockPos();

        if (!itemStack.hasNbt()) {
            NbtCompound tag = new NbtCompound();
            tag.putInt("washwater:bucketFillLevel", 0);
            itemStack.setNbt(tag);
        }
        if (player != null) {
            if (!player.isInSneakingPose()) {
                BucketMechanics.precisionBucketPlace(level, targetPos, itemStack, player);
            }
            else {
                BucketMechanics.precisionBucketPickup(level, targetPos, itemStack, player);
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack itemStack, @Nullable World level, List<Text> list, TooltipContext tooltipFlag) {
        if (itemStack.hasNbt()) {
            int bucketFillLevel = itemStack.getNbt().getInt("washwater:bucketFillLevel");
            String toolTipText = "Bucket contains: " + bucketFillLevel + "l " + "of fluid";
            list.add(new LiteralText(toolTipText));
        }
    }

    @Override
    public boolean isItemBarVisible(ItemStack itemStack) {
        return true;
    }

    @Override
    public int getItemBarColor(ItemStack itemStack) {
        return MathHelper.packRgb(56, 141, 252);
    }

    @Override
    public int getItemBarStep(ItemStack itemStack) {
        if (itemStack.hasNbt()) {
            int fillLevel = itemStack.getNbt().getInt("washwater:bucketFillLevel");
            int maxFillLevel = 8;
            float fraction = (float) fillLevel / (float) maxFillLevel;
            return (int) (13f * fraction);
        }
        else return 0;
    }
}
