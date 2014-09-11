package crazypants.enderio.machine.painter;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.util.Lang;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class BlockPaintedGlowstone extends Block implements ITileEntityProvider {
   
  public static int renderId = -1;

  public static BlockPaintedGlowstone create() {
    BlockPaintedGlowstone result = new BlockPaintedGlowstone();
    result.init();
    return result;
  }

  private IIcon lastRemovedComponetIcon = null;

  private Random rand = new Random();

  protected BlockPaintedGlowstone() {
    super(Material.glass);
    setCreativeTab(null);
    setBlockName(ModObject.blockPaintedGlowstone.unlocalisedName);    
  }

  private void init() {
    GameRegistry.registerBlock(this, BlockItemPaintedGlowstone.class, ModObject.blockPaintedGlowstone.unlocalisedName);
    GameRegistry.registerTileEntity(TileEntityPaintedBlock.class, ModObject.blockPaintedGlowstone.unlocalisedName + "TileEntity");
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.unlocalisedName, new PainterTemplate(this));
  }

  public static ItemStack createItemStackForSourceBlock(Block block, int damage) {
    ItemStack result = new ItemStack(EnderIO.blockPaintedGlowstone, 1, damage);
    PainterUtil.setSourceBlock(result, block, damage);
    return result;
  }
  
  @Override
  public int getLightValue() {  
    return 15;
  }  

  @Override
  @SideOnly(Side.CLIENT)
  public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileEntityPaintedBlock) {
      TileEntityPaintedBlock tef = (TileEntityPaintedBlock) te;
      if(tef.getSourceBlock() != null) {
        return tef.getSourceBlock().colorMultiplier(world, x, y, z);
      }
    }    
    return super.colorMultiplier(world, x, y, z);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addHitEffects(World world, MovingObjectPosition target,
      EffectRenderer effectRenderer) {
    IIcon tex = null;

    TileEntityPaintedBlock cb = (TileEntityPaintedBlock)
        world.getTileEntity(target.blockX, target.blockY, target.blockZ);
    Block b = cb.getSourceBlock();
    if(b != null) {
      tex = b.getIcon(ForgeDirection.NORTH.ordinal(), cb.getSourceBlockMetadata());
    }
    if(tex == null) {
      tex = blockIcon;
    }
    lastRemovedComponetIcon = tex;
    addBlockHitEffects(world, effectRenderer, target.blockX, target.blockY,
        target.blockZ, target.sideHit, tex);
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean addDestroyEffects(World world, int x, int y, int z, int
      meta, EffectRenderer effectRenderer) {
    IIcon tex = lastRemovedComponetIcon;
    byte b0 = 4;
    for (int j1 = 0; j1 < b0; ++j1) {
      for (int k1 = 0; k1 < b0; ++k1) {
        for (int l1 = 0; l1 < b0; ++l1) {
          double d0 = x + (j1 + 0.5D) / b0;
          double d1 = y + (k1 + 0.5D) / b0;
          double d2 = z + (l1 + 0.5D) / b0;
          int i2 = this.rand.nextInt(6);
          EntityDiggingFX fx = new EntityDiggingFX(world, d0, d1, d2, d0 - x - 0.5D,
              d1 - y - 0.5D, d2 - z - 0.5D, this, i2, 0).applyColourMultiplier(x, y, z);
          fx.setParticleIcon(tex);
          effectRenderer.addEffect(fx);
        }
      }
    }
    return true;

  }

  @SideOnly(Side.CLIENT)
  private void addBlockHitEffects(World world, EffectRenderer effectRenderer,
      int x, int y, int z, int side, IIcon tex) {
    float f = 0.1F;
    double d0 = x + rand.nextDouble() * (getBlockBoundsMaxX() -
        getBlockBoundsMinX() - f * 2.0F) + f + getBlockBoundsMinX();
    double d1 = y + rand.nextDouble() * (getBlockBoundsMaxY() -
        getBlockBoundsMinY() - f * 2.0F) + f + getBlockBoundsMinY();
    double d2 = z + rand.nextDouble() * (getBlockBoundsMaxZ() -
        getBlockBoundsMinZ() - f * 2.0F) + f + getBlockBoundsMinZ();
    if(side == 0) {
      d1 = y + getBlockBoundsMinY() - f;
    } else if(side == 1) {
      d1 = y + getBlockBoundsMaxY() + f;
    } else if(side == 2) {
      d2 = z + getBlockBoundsMinZ() - f;
    } else if(side == 3) {
      d2 = z + getBlockBoundsMaxZ() + f;
    } else if(side == 4) {
      d0 = x + getBlockBoundsMinX() - f;
    } else if(side == 5) {
      d0 = x + getBlockBoundsMaxX() + f;
    }
    EntityDiggingFX digFX = new EntityDiggingFX(world, d0, d1, d2, 0.0D, 0.0D,
        0.0D, this, side, 0);
    digFX.applyColourMultiplier(x, y,
        z).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F);
    digFX.setParticleIcon(tex);
    effectRenderer.addEffect(digFX);

  }

  @Override
  public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {    
    return true;
  }

  @Override
  public IIcon getIcon(IBlockAccess world, int x, int y, int z, int blockSide) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileEntityPaintedBlock) {
      TileEntityPaintedBlock tef = (TileEntityPaintedBlock) te;
      if(tef.getSourceBlock() != null) {
        return tef.getSourceBlock().getIcon(blockSide, tef.getSourceBlockMetadata());
      }
    }
    return Blocks.anvil.getIcon(world, x, y, z, blockSide);
  }
   
  @SideOnly(Side.CLIENT)
  @Override
  public void registerBlockIcons(IIconRegister IIconRegister) {
    blockIcon = IIconRegister.registerIcon("enderio:conduitConnector");
  }

  @Override
  public TileEntity createNewTileEntity(World world, int metadata) {
    return new TileEntityPaintedBlock();
  }

  @Override
  public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
    Block b = PainterUtil.getSourceBlock(stack);
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileEntityPaintedBlock) {
      TileEntityPaintedBlock tef = (TileEntityPaintedBlock) te;
      tef.setSourceBlock(b);
      tef.setSourceBlockMetadata(PainterUtil.getSourceBlockMetadata(stack));
    }
    world.markBlockForUpdate(x, y, z);
    super.onBlockPlacedBy(world, x, y, z, player, stack);
  }

  /**
   * Remove the tile entity too.
   */
  @Override
  public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
    if(!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
      TileEntity te = world.getTileEntity(x, y, z);

      if(te instanceof TileEntityPaintedBlock) {
        TileEntityPaintedBlock tef = (TileEntityPaintedBlock) te;

        ItemStack itemStack = createItemStackForSourceBlock(tef.getSourceBlock(), tef.getSourceBlockMetadata());

        float f = 0.7F;
        double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
        double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
        double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
        EntityItem entityitem = new EntityItem(world, x + d0, y + d1, z + d2, itemStack);
        entityitem.delayBeforeCanPickup = 10;
        world.spawnEntityInWorld(entityitem);
      } 
    }
    world.removeTileEntity(x, y, z);
  }

  @Override
  public int quantityDropped(Random par1Random) {
    return 0; // need to do custom dropping to maintain source metadata
  }

  @Override
  public int getRenderType() {    
    return renderId;
  }

  public static final class PainterTemplate extends BasicPainterTemplate {

    public PainterTemplate(Block paintedGlowstone) {
      super(Blocks.glowstone, paintedGlowstone);
      MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public ResultStack[] getCompletedResult(float chance, MachineRecipeInput... inputs) {
      ItemStack paintSource = MachineRecipeInput.getInputForSlot(1, inputs);
      if(paintSource == null) {
        return new ResultStack[0];
      }
      
      if (paintSource.getItem() == Item.getItemFromBlock(Blocks.glowstone)) {
        ItemStack stack = new ItemStack(Blocks.glowstone);
        stack.stackTagCompound = new NBTTagCompound();
        String tagName = "wasPainted";
        stack.stackTagCompound.setBoolean(tagName, true);
        return new ResultStack[] { new ResultStack(stack) };
      }
      
      return new ResultStack[] { new ResultStack(createItemStackForSourceBlock(Block.getBlockFromItem(paintSource.getItem()), paintSource.getItemDamage())) };
    }
    
    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
      if (event.itemStack != null && Block.getBlockFromItem(event.itemStack.getItem()) == Blocks.glowstone && event.itemStack.stackTagCompound != null) {
        if (event.itemStack.stackTagCompound.getBoolean("wasPainted")) {
          event.toolTip.add(Lang.localize("painter.tooltip.wasPainted"));
        }
      }
    }
  }
}
