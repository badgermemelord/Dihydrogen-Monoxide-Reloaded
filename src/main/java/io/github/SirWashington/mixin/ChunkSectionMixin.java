package io.github.SirWashington.mixin;

import com.ewoudje.lasagna.chunkstorage.ExtraStorageSectionContainer;
import io.github.SirWashington.WaterSection;
import net.minecraft.block.BlockState;
import net.minecraft.world.chunk.ChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkSection.class)
public class ChunkSectionMixin {

    @Inject(at = @At("HEAD"), method = "setBlockState(IIILnet/minecraft/block/BlockState;Z)Lnet/minecraft/block/BlockState;")
    public void setBlockState(int x, int y, int z, BlockState state, boolean lock, CallbackInfoReturnable<BlockState> cir) {
        WaterSection section = (WaterSection) ((ExtraStorageSectionContainer) this).getSectionStorage(WaterSection.ID);
        if (section != null) section.setWaterVolumeByState(x, y, z, state);
    }
}
