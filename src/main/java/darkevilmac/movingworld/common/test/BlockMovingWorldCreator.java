package darkevilmac.movingworld.common.test;

import darkevilmac.movingworld.MovingWorldMod;
import darkevilmac.movingworld.common.core.IMovingWorld;
import darkevilmac.movingworld.common.core.assembly.Assembler;
import darkevilmac.movingworld.common.core.assembly.BlockMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Just a basic indev block, when activated the block assembles nearby blocks and creates a movingworld.
 */

public class BlockMovingWorldCreator extends Block {
    public static PropertyBool ASSEMBLING = PropertyBool.create("assembling");

    public BlockMovingWorldCreator(Material blockMaterialIn) {
        super(blockMaterialIn);
        this.setUnlocalizedName("movingWorldCreator");
        this.setDefaultState(this.blockState.getBaseState().withProperty(ASSEMBLING, Boolean.valueOf(false)));
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (worldIn != null && !worldIn.isRemote) {
            if (state.getValue(ASSEMBLING))
                return false;

            worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(ASSEMBLING, true));

            System.out.println("Initializing an assembler.");
            final Assembler assembler = new Assembler(new CustomAssemblyInteractor(), worldIn, pos, !player.isSneaking());
            assembler.setAssemblyListener(new Assembler.IAssemblyListener() {
                @Override
                public void onComplete(World world, BlockPos origin, BlockMap map) {
                    world.setBlockState(origin, world.getBlockState(origin).withProperty(ASSEMBLING, false));

                    if (MovingWorldMod.movingWorldFactory != null) {
                        IMovingWorld movingWorld = MovingWorldMod.movingWorldFactory.createMovingWorld(map, world);
                        if (movingWorld != null) {
                            movingWorld.move(new Vec3d(origin.getX() - assembler.initialOffset.getX(), map.getMin().getY(), origin.getZ() - assembler.initialOffset.getZ()), true);
                        }
                    }

                }
            });

            return true;
        }

        return false;
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(ASSEMBLING, Boolean.valueOf(meta == 1));
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(ASSEMBLING) ? 1 : 0;
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{ASSEMBLING});
    }
}
