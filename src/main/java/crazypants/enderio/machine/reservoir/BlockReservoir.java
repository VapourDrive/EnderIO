package crazypants.enderio.machine.reservoir;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.gui.IResourceTooltipProvider;
import crazypants.enderio.machine.reservoir.TileReservoir.Pos;
import crazypants.util.BlockCoord;
import crazypants.util.FluidUtil;
import crazypants.util.Util;
import crazypants.vecmath.Vector3d;

public class BlockReservoir extends BlockContainer implements IResourceTooltipProvider {

  public static BlockReservoir create() {
    BlockReservoir result = new BlockReservoir();
    result.init();
    return result;
  }

  private static enum MbFace {
    TL("reservoirMbTl"),
    TR("reservoirMbTr"),
    BL("reservoirMbBl"),
    BR("reservoirMbBr"),
    T("reservoirMbT"),
    B("reservoirMbB"),
    L("reservoirMbL"),
    R("reservoirMbR");

    String iconName;

    private MbFace(String iconName) {
      this.iconName = iconName;
    }

  }

  private IIcon[] mbIcons = new IIcon[8];
  IIcon switchIcon;

  private BlockReservoir() {
    super(Material.rock);
    setHardness(0.5F);
    setStepSound(Block.soundTypeStone);
    setBlockName(ModObject.blockReservoir.unlocalisedName);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  @Override
  public TileEntity createNewTileEntity(World var1, int var2) {
    return new TileReservoir();
  }

  private void init() {
    GameRegistry.registerBlock(this, ModObject.blockReservoir.unlocalisedName);
    GameRegistry.registerTileEntity(TileReservoir.class, ModObject.blockReservoir.unlocalisedName + "TileEntity");
  }

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {

    ItemStack current = entityPlayer.inventory.getCurrentItem();
    if(current != null) {

      TileReservoir tank = (TileReservoir) world.getTileEntity(x, y, z);

      FluidStack liquid = FluidUtil.getFluidFromItem(current);
      if(liquid != null) {
        // Handle filled containers
        int qty = tank.getController().doFill(ForgeDirection.UNKNOWN, liquid, true);

        if(qty != 0 && !entityPlayer.capabilities.isCreativeMode) {
          entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, Util.consumeItem(current));
        }
        return true;

      } else {
        // Handle empty containers

        FluidStack available = tank.getTankInfo(ForgeDirection.UNKNOWN)[0].fluid;
        if(available != null) {
          ItemStack filled = FluidContainerRegistry.fillFluidContainer(available, current);
          if(current.getItem() == Items.bucket) {
            filled = new ItemStack(Items.water_bucket);
          }
          liquid = FluidContainerRegistry.getFluidForFilledItem(filled);
          if(current.getItem() == Items.bucket) {
            liquid = new FluidStack(FluidRegistry.WATER, 1000);
          }

          if(liquid != null) {
            if(!entityPlayer.capabilities.isCreativeMode) {
              if(current.stackSize > 1) {
                if(!entityPlayer.inventory.addItemStackToInventory(filled)) {
                  return false;
                } else {
                  entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, Util.consumeItem(current));
                }
              } else {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, Util.consumeItem(current));
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, filled);
              }
            }
            tank.drain(ForgeDirection.UNKNOWN, liquid.amount, true);
            return true;

          } else if(ConduitUtil.isToolEquipped(entityPlayer) && tank.isMultiblock()) {
            tank.setAutoEject(!tank.isAutoEject());
            for (BlockCoord bc : tank.multiblock) {
              world.markBlockForUpdate(bc.x, bc.y, bc.z);
            }

          }

        }
      }
    }

    return false;

  }

  @Override
  @SideOnly(Side.CLIENT)
  public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof TileReservoir)) {
      return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }
    TileReservoir tr = (TileReservoir) te;
    if(!tr.isMultiblock()) {
      return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    Vector3d min = new Vector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
    Vector3d max = new Vector3d(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);
    for (BlockCoord bc : tr.multiblock) {
      min.x = Math.min(min.x, bc.x);
      max.x = Math.max(max.x, bc.x + 1);
      min.y = Math.min(min.y, bc.y);
      max.y = Math.max(max.y, bc.y + 1);
      min.z = Math.min(min.z, bc.z);
      max.z = Math.max(max.z, bc.z + 1);
    }
    return AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x, max.y, max.z);

  }

  @Override
  public boolean renderAsNormalBlock() {
    return false;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public TileEntity createTileEntity(World world, int metadata) {
    return new TileReservoir();
  }

  @Override
  public void onBlockAdded(World world, int x, int y, int z) {
    if(world.isRemote) {
      return;
    }
    TileReservoir tr = (TileReservoir) world.getTileEntity(x, y, z);
    boolean res = tr.onBlockAdded();

  }

  @Override
  public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
    if(world.isRemote) {
      return;
    }
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileReservoir) {
      ((TileReservoir) te).onNeighborBlockChange(block);
    }
  }

  @Override
  public void registerBlockIcons(IIconRegister IIconRegister) {
    blockIcon = IIconRegister.registerIcon("enderio:reservoir");
    for (MbFace face : MbFace.values()) {
      mbIcons[face.ordinal()] = IIconRegister.registerIcon("enderio:" + face.iconName);
    }
    switchIcon = IIconRegister.registerIcon("enderio:reservoirSwitch");
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int blockSide) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof TileReservoir)) {
      return true;
    }
    TileReservoir tr = (TileReservoir) te;
    if(!tr.isMultiblock()) {
      return true;
    }
    return false;
  }

  @Override
  public IIcon getIcon(IBlockAccess world, int x, int y, int z, int blockSide) {
    // used to render the block in the world
    TileEntity te = world.getTileEntity(x, y, z);

    if(!(te instanceof TileReservoir)) {
      return blockIcon;
    }
    TileReservoir tr = (TileReservoir) te;
    if(!tr.isMultiblock()) {
      return blockIcon;
    }

    ForgeDirection side = ForgeDirection.getOrientation(blockSide);
    Pos pos = tr.pos;

    if(tr.front == side || tr.front == side.getOpposite()) { // 2x2 area

      boolean isRight;
      if(tr.isVertical()) { // to to flip right and left for back faces of
        // vertical multiblocks
        isRight = !pos.isRight(side);
      } else {
        isRight = pos.isRight(side);
      }
      if(pos.isTop) {
        return isRight ? mbIcons[MbFace.TR.ordinal()] : mbIcons[MbFace.TL.ordinal()];
      } else {
        return isRight ? mbIcons[MbFace.BR.ordinal()] : mbIcons[MbFace.BL.ordinal()];
      }

    }
    if(tr.up == side || tr.up == side.getOpposite()) { // up or down face
      if(tr.isVertical()) {
        if(tr.right.offsetX != 0) {
          return pos.isRight ? mbIcons[MbFace.L.ordinal()] : mbIcons[MbFace.R.ordinal()];
        } else {
          return pos.isRight ? mbIcons[MbFace.T.ordinal()] : mbIcons[MbFace.B.ordinal()];
        }
      } else {
        if(tr.up == side) {
          return pos.isRight ? mbIcons[MbFace.L.ordinal()] : mbIcons[MbFace.R.ordinal()];
        } else {
          return pos.isRight ? mbIcons[MbFace.R.ordinal()] : mbIcons[MbFace.L.ordinal()];
        }
      }

    } else {
      if(tr.isVertical()) {
        return pos.isTop ? mbIcons[MbFace.T.ordinal()] : mbIcons[MbFace.B.ordinal()];
      } else {
        return pos.isTop(side) ? mbIcons[MbFace.L.ordinal()] : mbIcons[MbFace.R.ordinal()];
      }
    }

  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    return getUnlocalizedName();
  }

}
