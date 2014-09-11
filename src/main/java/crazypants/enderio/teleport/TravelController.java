package crazypants.enderio.teleport;

import java.util.HashMap;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.config.Config;
import crazypants.enderio.enderface.TileEnderIO;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.teleport.packet.PacketDrainStaff;
import crazypants.enderio.teleport.packet.PacketOpenAuthGui;
import crazypants.enderio.teleport.packet.PacketTravelEvent;
import crazypants.util.BlockCoord;
import crazypants.util.Util;
import crazypants.vecmath.Camera;
import crazypants.vecmath.Matrix4d;
import crazypants.vecmath.VecmathUtil;
import crazypants.vecmath.Vector2d;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector4d;

public class TravelController {

  public static final TravelController instance = new TravelController();

  private Random rand = new Random();

  private boolean wasJumping = false;

  private boolean showTargets = false;

  BlockCoord onBlockCoord;

  BlockCoord selectedCoord;

  Camera currentView = new Camera();

  private final HashMap<BlockCoord, Float> candidates = new HashMap<BlockCoord, Float>();

  private boolean selectionEnabled = true;

  private double fovRad;

  private double tanFovRad;

  private Minecraft mc = Minecraft.getMinecraft();

  private TravelController() {
  }

  public boolean showTargets() {
    return showTargets && selectionEnabled;
  }

  public void setSelectionEnabled(boolean b) {
    selectionEnabled = b;
    if(!selectionEnabled) {
      candidates.clear();
    }
  }

  public boolean isBlockSelected(BlockCoord coord) {
    if(coord == null) {
      return false;
    }
    return coord.equals(selectedCoord);
  }

  public void addCandidate(BlockCoord coord) {
    if(!candidates.containsKey(coord)) {
      candidates.put(coord, -1f);
    }
  }

  public int getMaxTravelDistanceSq() {
    return TravelSource.getMaxDistanceSq();
  }

  public boolean isTargetEnderIO() {
    if(selectedCoord == null) {
      return false;
    }
    return EnderIO.instance.proxy.getClientPlayer().worldObj.getBlock(selectedCoord.x, selectedCoord.y, selectedCoord.z) == EnderIO.blockEnderIo;
  }

  @SubscribeEvent
  public void onRender(RenderWorldLastEvent event) {

    Vector3d eye = Util.getEyePositionEio(mc.thePlayer);
    Vector3d lookAt = Util.getLookVecEio(mc.thePlayer);
    lookAt.add(eye);
    Matrix4d mv = VecmathUtil.createMatrixAsLookAt(eye, lookAt, new Vector3d(0, 1, 0));

    float fov = 70 + Minecraft.getMinecraft().gameSettings.fovSetting * 40.0F;
    Matrix4d pr = VecmathUtil.createProjectionMatrixAsPerspective(fov, 0.05f, mc.gameSettings.renderDistanceChunks * 16, mc.displayWidth,
        mc.displayHeight);
    currentView.setProjectionMatrix(pr);
    currentView.setViewMatrix(mv);
    currentView.setViewport(0, 0, mc.displayWidth, mc.displayHeight);

    fovRad = Math.toRadians(fov) / 2;
    tanFovRad = Math.tanh(fovRad);
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if(event.phase == TickEvent.Phase.END) {
      EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
      if(player == null) {
        return;
      }
      onBlockCoord = getActiveTravelBlock(player);
      boolean onBlock = onBlockCoord != null;
      showTargets = onBlock || ItemTravelStaff.isEquipped(player);
      if(showTargets) {
        updateSelectedTarget(player);
      } else {
        selectedCoord = null;
      }
      MovementInput input = player.movementInput;
      if(input.jump && !wasJumping && onBlock && selectedCoord != null) {

        BlockCoord target = TravelController.instance.selectedCoord;
        TileEntity te = player.worldObj.getTileEntity(target.x, target.y, target.z);
        if(te instanceof ITravelAccessable) {
          ITravelAccessable ta = (ITravelAccessable) te;
          if(ta.getRequiresPassword(player)) {
            PacketOpenAuthGui p = new PacketOpenAuthGui(target.x, target.y, target.z);
            PacketHandler.INSTANCE.sendToServer(p);
            return;
          }
        }

        if(isTargetEnderIO()) {
          openEnderIO(null, player.worldObj, player);
        } else if(Config.travelAnchorEnabled && travelToSelectedTarget(player, TravelSource.BLOCK)) {
          input.jump = false;
        }

      }
      wasJumping = input.jump;
      candidates.clear();
    }
  }

  public boolean hasTarget() {
    return selectedCoord != null;
  }

  public void openEnderIO(ItemStack equipped, World world, EntityPlayer player) {
    BlockCoord target = TravelController.instance.selectedCoord;
    TileEntity te = world.getTileEntity(target.x, target.y, target.z);
    if(!(te instanceof TileEnderIO)) {
      return;
    }
    TileEnderIO eio = (TileEnderIO) te;
    if(eio.canBlockBeAccessed(player)) {

      int requiredPower = ItemTravelStaff.isEquipped(player) ? TravelController.instance.getRequiredPower(player, TravelSource.STAFF, target) : 0; 
      if(requiredPower <= 0 || requiredPower <= EnderIO.itemTravelStaff.getEnergyStored(equipped)) {
        if(requiredPower > 0) {
          PacketDrainStaff p = new PacketDrainStaff(requiredPower);
          PacketHandler.INSTANCE.sendToServer(p);
        }
        player.openGui(EnderIO.instance, GuiHandler.GUI_ID_ENDERFACE, world, target.x,
            TravelController.instance.selectedCoord.y, TravelController.instance.selectedCoord.z);
      }
    } else {
      player.addChatComponentMessage(new ChatComponentTranslation("enderio.gui.travelAccessable.unauthorised"));
    }
  }

  public boolean travelToSelectedTarget(EntityPlayer player, TravelSource source) {
    return travelToLocation(player, source, selectedCoord);
  }

  public boolean travelToLocation(EntityPlayer player, TravelSource source, BlockCoord coord) {

    if(source != TravelSource.STAFF_BLINK) {
      TileEntity te = player.worldObj.getTileEntity(coord.x, coord.y, coord.z);
      if(te instanceof ITravelAccessable) {
        ITravelAccessable ta = (ITravelAccessable) te;
        if(!ta.canBlockBeAccessed(player)) {
          player.addChatComponentMessage(new ChatComponentTranslation("enderio.gui.travelAccessable.unauthorised"));
          return false;
        }
      }
    }

    int requiredPower = 0;
    requiredPower = getRequiredPower(player, source, coord);
    if(requiredPower < 0) {
      return false;
    }

    if(!isInRangeTarget(player, coord, source.maxDistanceTravelledSq)) {
      if(source != TravelSource.STAFF_BLINK) {
        player.addChatComponentMessage(new ChatComponentTranslation("enderio.blockTravelPlatform.outOfRange"));
      }
      return false;
    }
    if(!isValidTarget(player, coord, source)) {
      if(source != TravelSource.STAFF_BLINK) {
        player.addChatComponentMessage(new ChatComponentTranslation("enderio.blockTravelPlatform.invalidTarget"));
      }
      return false;
    }
    sendTravelEvent(coord, source, requiredPower);
    for (int i = 0; i < 6; ++i) {
      player.worldObj.spawnParticle("portal", player.posX + (rand.nextDouble() - 0.5D), player.posY + rand.nextDouble() * player.height - 0.25D,
          player.posZ + (rand.nextDouble() - 0.5D), (this.rand.nextDouble() - 0.5D) * 2.0D, -rand.nextDouble(),
          (rand.nextDouble() - 0.5D) * 2.0D);
    }
    return true;

  }

  public int getRequiredPower(EntityPlayer player, TravelSource source, BlockCoord coord) {
    if(!ItemTravelStaff.isEquipped(player)) {
      return 0;
    }
    int requiredPower;
    ItemStack staff = player.getCurrentEquippedItem();
    requiredPower = (int) (getDistance(player, coord) * source.powerCostPerBlockTraveledRF);
    int canUsePower = EnderIO.itemTravelStaff.getEnergyStored(staff);
    if(requiredPower > canUsePower) {
      player.addChatComponentMessage(new ChatComponentTranslation("enderio.itemTravelStaff.notEnoughPower"));
      return -1;
    }
    return requiredPower;
  }

  private boolean isInRangeTarget(EntityPlayer player, BlockCoord bc, float maxSq) {
    return getDistanceSquared(player, bc) <= maxSq;
  }

  private double getDistanceSquared(EntityPlayer player, BlockCoord bc) {
    if(player == null || bc == null) {
      return 0;
    }
    Vector3d eye = Util.getEyePositionEio(player);
    Vector3d target = new Vector3d(bc.x + 0.5, bc.y + 0.5, bc.z + 0.5);
    return eye.distanceSquared(target);
  }

  private double getDistance(EntityPlayer player, BlockCoord coord) {
    return Math.sqrt(getDistanceSquared(player, coord));
  }

  private boolean isValidTarget(EntityPlayer player, BlockCoord bc, TravelSource source) {
    if(bc == null) {
      return false;
    }
    World w = player.worldObj;
    BlockCoord baseLoc = bc;
    if(source != TravelSource.STAFF_BLINK) {
      //targeting a block so go one up
      baseLoc = bc.getLocation(ForgeDirection.UP);
    }

    return canTeleportTo(player, source, baseLoc, w)
        && canTeleportTo(player, source, baseLoc.getLocation(ForgeDirection.UP), w);
  }

  private boolean canTeleportTo(EntityPlayer player, TravelSource source, BlockCoord bc, World w) {
    if(source == TravelSource.STAFF_BLINK && !Config.travelStaffBlinkThroughSolidBlocksEnabled) {
      Vec3 start = Util.getEyePosition(player);
      Vec3 target = Vec3.createVectorHelper(bc.x + 0.5f, bc.y + 0.5f, bc.z + 0.5f);
      if(!canBlinkTo(bc, w, start, target)) {
        return false;
      }
    }

    Block block = w.getBlock(bc.x, bc.y, bc.z);
    if(block == null || block.isAir(w, bc.x, bc.y, bc.z)) {
      return true;
    }
    final AxisAlignedBB aabb = block.getCollisionBoundingBoxFromPool(w, bc.x, bc.y, bc.z);
    return aabb == null || aabb.getAverageEdgeLength() < 0.7;
  }

  private boolean canBlinkTo(BlockCoord bc, World w, Vec3 start, Vec3 target) {
    MovingObjectPosition p = w.rayTraceBlocks(start, target, !Config.travelStaffBlinkThroughClearBlocksEnabled);
    if(p != null) {
      if(!Config.travelStaffBlinkThroughClearBlocksEnabled) {
        return false;
      }
      Block block = w.getBlock(p.blockX, p.blockY, p.blockZ);
      if(isClear(w, block, p.blockX, p.blockY, p.blockZ)) {
        if(new BlockCoord(p.blockX, p.blockY, p.blockZ).equals(bc)) {
          return true;
        }
        //need to step
        Vector3d sv = new Vector3d(start.xCoord, start.yCoord, start.zCoord);
        Vector3d rayDir = new Vector3d(target.xCoord, target.yCoord, target.zCoord);
        rayDir.sub(sv);
        rayDir.normalize();
        rayDir.add(sv);
        return canBlinkTo(bc, w, Vec3.createVectorHelper(rayDir.x, rayDir.y, rayDir.z), target);

      } else {
        return false;
      }
    }
    return true;
  }

  private boolean isClear(World w, Block block, int x, int y, int z) {
    if(block == null || block.isAir(w, x, y, z)) {
      return true;
    }
    final AxisAlignedBB aabb = block.getCollisionBoundingBoxFromPool(w, x, y, z);
    if(aabb == null || aabb.getAverageEdgeLength() < 0.7) {
      return true;
    }

    return block.getLightOpacity(w, x, y, z) < 2;
  }

  private void updateSelectedTarget(EntityClientPlayerMP player) {
    selectedCoord = null;
    if(candidates.isEmpty()) {
      return;
    }

    Vector3d eye = Util.getEyePositionEio(player);
    Vec3 look = player.getLookVec();

    Vector3d b = new Vector3d(eye);
    b.add(look.xCoord, look.yCoord, look.zCoord);

    Vector3d c = new Vector3d(eye);
    c.add(0, 1, 0);

    Vector4d leftPlane = new Vector4d();
    VecmathUtil.computePlaneEquation(eye, b, c, leftPlane);

    c.set(eye);
    c.add(leftPlane.x, leftPlane.y, leftPlane.z);

    Vector4d upPlane = new Vector4d();
    VecmathUtil.computePlaneEquation(eye, b, c, upPlane);

    double closestDistance = Double.MAX_VALUE;
    Vector3d point = new Vector3d();
    for (BlockCoord bc : candidates.keySet()) {
      if(!bc.equals(onBlockCoord)) {
        point.set(bc.x + 0.5, bc.y + 0.5, bc.z + 0.5);

        Vector2d sp = currentView.getScreenPoint(new Vector3d(point.x, point.y, point.z));
        Vector2d mid = new Vector2d(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        mid.scale(0.5);

        double d = sp.distance(mid);
        float ratio = (float) d / Minecraft.getMinecraft().displayWidth;
        candidates.put(bc, ratio);
        if(d < closestDistance) {
          selectedCoord = bc;
          closestDistance = d;
        }
      }
    }

    if(selectedCoord != null) {
      Vector2d sp = currentView.getScreenPoint(new Vector3d(selectedCoord.x + 0.5, selectedCoord.y + 0.5, selectedCoord.z + 0.5));
      Vector2d mid = new Vector2d(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
      mid.scale(0.5);
      double ratio = sp.distance(mid) / Minecraft.getMinecraft().displayWidth;
      if(ratio > 0.2) {
        selectedCoord = null;
      }

    }
  }

  public double getScaleForCandidate(Vector3d loc) {

    BlockCoord bc = new BlockCoord((int) loc.x, (int) loc.y, (int) loc.z);
    float ratio = -1;
    Float r = candidates.get(bc);
    if(r != null) {
      ratio = r;
    }
    if(ratio < 0) {
      //no cached value
      Vector2d sp = currentView.getScreenPoint(new Vector3d(bc.x, bc.y, bc.z));
      Vector2d mid = new Vector2d(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
      mid.scale(0.5);
      double d = sp.distance(mid);
      ratio = (float) d / Minecraft.getMinecraft().displayWidth;
      candidates.put(bc, ratio);
    }

    //smoothly zoom to a larger size, starting when the point is the middle 20% of the screen
    float start = 0.2f;
    float end = 0.01f;
    double mix = MathHelper.clamp_float((start - ratio) / (start - end), 0, 1);
    double scale = 1;
    if(mix > 0) {
      double d = tanFovRad * currentView.getEyePoint().distance(loc);
      scale = d / tanFovRad;

      scale = scale * 0.1;// why I need this is completely beyond me.

      //only apply 70% of the scaling so more distance targets are still smaller than closer targets
      float nf = 1 - MathHelper.clamp_float((float) currentView.getEyePoint().distanceSquared(loc) / TravelSource.STAFF.maxDistanceTravelledSq, 0, 1);
      scale = scale * (0.3 + 0.7 * nf);

      scale = (scale * mix) + (1 - mix);
      scale = Math.max(1, scale);
    }
    return scale;
  }

  private int getMaxTravelDistanceSqForPlayer(EntityClientPlayerMP player) {
    if(ItemTravelStaff.isEquipped(player)) {
      return TravelSource.STAFF.maxDistanceTravelledSq;
    }
    return TravelSource.BLOCK.maxDistanceTravelledSq;
  }

  private void sendTravelEvent(BlockCoord bc, TravelSource source, int powerUse) {
    PacketTravelEvent p = new PacketTravelEvent(bc.x, bc.y, bc.z, powerUse, source.getConserveMomentum());
    PacketHandler.INSTANCE.sendToServer(p);
  }

  private BlockCoord getActiveTravelBlock(EntityClientPlayerMP player) {
    World world = Minecraft.getMinecraft().theWorld;
    if(world != null && player != null) {
      int x = MathHelper.floor_double(player.posX);
      int y = MathHelper.floor_double(player.boundingBox.minY) - 1;
      int z = MathHelper.floor_double(player.posZ);
      if(world.getBlock(x, y, z) == EnderIO.blockTravelPlatform) {
        return new BlockCoord(x, y, z);
      }
    }
    return null;
  }

  public boolean isStaffEquipped(EntityPlayer player) {
    return player != null && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == EnderIO.itemTravelStaff;
  }

}