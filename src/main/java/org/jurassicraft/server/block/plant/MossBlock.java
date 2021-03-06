package org.jurassicraft.server.block.plant;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jurassicraft.server.tab.TabHandler;

public class MossBlock extends Block
{
    private static final AxisAlignedBB BOUNDS = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);

    public MossBlock()
    {
        super(Material.LEAVES);

        this.setHardness(0.2F);
        this.setResistance(0.0F);
        this.setTickRandomly(true);
        this.setCreativeTab(TabHandler.INSTANCE.plants);
        this.setSoundType(SoundType.PLANT);
        this.setLightOpacity(0);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess blockAccess, BlockPos pos)
    {
        return BOUNDS;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos)
    {
        IBlockState below = world.getBlockState(pos.down());
        return super.canPlaceBlockAt(world, pos) && this.canBlockStay(world, pos) && (below.isFullCube());
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block)
    {
        super.neighborChanged(state, world, pos, block);
        this.checkForDrop(world, pos, state);
    }

    private boolean checkForDrop(World world, BlockPos pos, IBlockState state)
    {
        if (!this.canBlockStay(world, pos))
        {
            this.dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
            return false;
        }
        else
        {
            return true;
        }
    }

    private boolean canBlockStay(World world, BlockPos pos)
    {
        return !world.isAirBlock(pos.down());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return side == EnumFacing.UP || super.shouldSideBeRendered(state, world, pos, side);
    }
}
