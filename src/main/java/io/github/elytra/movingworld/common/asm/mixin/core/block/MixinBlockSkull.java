package io.github.elytra.movingworld.common.asm.mixin.core.block;

import net.minecraft.block.BlockSkull;
import net.minecraft.tileentity.TileEntitySkull;

import org.spongepowered.asm.mixin.Mixin;

import io.github.elytra.movingworld.api.rotation.IRotationBlock;
import io.github.elytra.movingworld.common.chunk.LocatedBlock;
import io.github.elytra.movingworld.common.util.RotationHelper;

@Mixin(BlockSkull.class)
public class MixinBlockSkull implements IRotationBlock {

    @Override
    public LocatedBlock rotate(LocatedBlock locatedBlock, boolean ccw) {
        if (locatedBlock.tileEntity != null) {
            int skullRot = ((TileEntitySkull) locatedBlock.tileEntity).getSkullRotation();
            for (int i = 0; i < 4; i++) {
                skullRot = RotationHelper.rotateInteger(skullRot, 0, 16, ccw);
            }
            ((TileEntitySkull) locatedBlock.tileEntity).setSkullRotation(skullRot);
        }

        return locatedBlock;
    }

    @Override
    public boolean fullRotation() {
        return false;
    }
}
