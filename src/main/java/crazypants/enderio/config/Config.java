package crazypants.enderio.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.vecmath.VecmathUtil;

public final class Config {

  public static class Section {
    public final String name;
    public final String lang;

    public Section(String name, String lang) {
      this.name = name;
      this.lang = lang;
      register();
    }

    private void register() {
      sections.add(this);
    }

    public String lc() {
      return name.toLowerCase();
    }
  }

  public static final List<Section> sections;

  static {
    sections = new ArrayList<Section>();
  }

  public static Configuration config;

  public static final Section sectionPower = new Section("Power Settings", "power");
  public static final Section sectionRecipe = new Section("Recipe Settings", "recipe");
  public static final Section sectionItems = new Section("Item Enabling", "item");
  public static final Section sectionEfficiency = new Section("Efficiency Settings", "efficiency");
  public static final Section sectionPersonal = new Section("Personal Settings", "personal");
  public static final Section sectionAnchor = new Section("Anchor Settings", "anchor");
  public static final Section sectionStaff = new Section("Staff Settings", "staff");
  public static final Section sectionDarkSteel = new Section("Dark Steel", "darksteel");
  public static final Section sectionFarm = new Section("Farm Settings", "farm");
  public static final Section sectionAesthetic = new Section("Aesthetic Settings", "aesthetic");
  public static final Section sectionAdvanced = new Section("Advanced Settings", "advanced");
  public static final Section sectionMagnet = new Section("Magnet Settings", "magnet");
  public static final Section sectionFluid = new Section("Fluid Settings", "fluid");
  public static final Section sectionSpawner = new Section("PoweredSpawner Settings", "spawner");
  public static final Section sectionKiller = new Section("Killer Joe Settings", "killerjoe");
  public static final Section sectionSoulBinder = new Section("Soul Binder Settings", "soulBinder");

  public static final double DEFAULT_CONDUIT_SCALE = 0.5;

  public static boolean reinforcedObsidianEnabled = true;

  public static boolean useAlternateBinderRecipe;

  public static boolean useAlternateTesseractModel;

  public static boolean photovoltaicCellEnabled = true;

  public static double conduitScale = DEFAULT_CONDUIT_SCALE;

  public static int numConduitsPerRecipe = 8;

  public static double transceiverEnergyLoss = 0.1;

  public static double transceiverUpkeepCost = 0.25;

  public static double transceiverBucketTransmissionCost = 1;

  public static int transceiverMaxIO = 2048;

  public static File configDirectory;

  public static boolean useHardRecipes = false;

  public static boolean useSteelInChassi = false;

  public static boolean detailedPowerTrackingEnabled = false;

  public static boolean useSneakMouseWheelYetaWrench = true;
  public static boolean useSneakRightClickYetaWrench = false;

  public static boolean useRfAsDefault = true;

  public static boolean itemConduitUsePhyscialDistance = false;

  public static int enderFluidConduitExtractRate = 200;
  public static int enderFluidConduitMaxIoRate = 800;
  public static int advancedFluidConduitExtractRate = 100;
  public static int advancedFluidConduitMaxIoRate = 400;
  public static int fluidConduitExtractRate = 50;
  public static int fluidConduitMaxIoRate = 200;

  public static int gasConduitExtractRate = 200;
  public static int gasConduitMaxIoRate = 800;

  public static boolean updateLightingWhenHidingFacades = false;

  public static boolean travelAnchorEnabled = true;
  public static int travelAnchorMaxDistance = 48;

  public static int travelStaffMaxDistance = 96;
  public static float travelStaffPowerPerBlockRF = 250;

  public static int travelStaffMaxBlinkDistance = 16;
  public static int travelStaffBlinkPauseTicks = 10;

  public static boolean travelStaffEnabled = true;
  public static boolean travelStaffBlinkEnabled = true;
  public static boolean travelStaffBlinkThroughSolidBlocksEnabled = true;
  public static boolean travelStaffBlinkThroughClearBlocksEnabled = true;

  public static int enderIoRange = 8;
  public static boolean enderIoMeAccessEnabled = true;

  public static int darkSteelPowerStorageBase = 100000;
  public static int darkSteelPowerStorageLevelOne = 150000;
  public static int darkSteelPowerStorageLevelTwo = 250000;
  public static int darkSteelPowerStorageLevelThree = 1000000;

  public static float darkSteelSpeedOneWalkModifier = 0.1f;
  public static float darkSteelSpeedTwoWalkMultiplier = 0.2f;
  public static float darkSteelSpeedThreeWalkMultiplier = 0.3f;

  public static float darkSteelSpeedOneSprintModifier = 0.1f;
  public static float darkSteelSpeedTwoSprintMultiplier = 0.3f;
  public static float darkSteelSpeedThreeSprintMultiplier = 0.5f;

  public static double darkSteelBootsJumpModifier = 1.5;

  public static int darkSteelWalkPowerCost = darkSteelPowerStorageLevelTwo / 3000;
  public static int darkSteelSprintPowerCost = darkSteelWalkPowerCost * 4;
  public static boolean darkSteelDrainPowerFromInventory = false;
  public static int darkSteelBootsJumpPowerCost = 150;
  public static int darkSteelFallDistanceCost = 75;

  public static float darkSteelSwordWitherSkullChance = 0.05f;
  public static float darkSteelSwordWitherSkullLootingModifier = 0.05f; 
  public static float darkSteelSwordSkullChance = 0.1f;
  public static float darkSteelSwordSkullLootingModifier = 0.075f;
  public static float vanillaSwordSkullLootingModifier = 0.05f;
  public static float vanillaSwordSkullChance = 0.05f;
  public static int darkSteelSwordPowerUsePerHit = 750;
  public static double darkSteelSwordEnderPearlDropChance = 1;
  public static double darkSteelSwordEnderPearlDropChancePerLooting = 0.5;

  public static int darkSteelPickPowerUseObsidian = 10000;
  public static int darkSteelPickEffeciencyObsidian = 50;
  public static int darkSteelPickPowerUsePerDamagePoint = 750;
  public static float darkSteelPickEffeciencyBoostWhenPowered = 2;

  public static int darkSteelAxePowerUsePerDamagePoint = 750;
  public static int darkSteelAxePowerUsePerDamagePointMultiHarvest = 1500;
  public static float darkSteelAxeEffeciencyBoostWhenPowered = 2;
  public static float darkSteelAxeSpeedPenaltyMultiHarvest = 8;

  public static int darkSteelUpgradeVibrantCost = 20;
  public static int darkSteelUpgradePowerOneCost = 10;
  public static int darkSteelUpgradePowerTwoCost = 20;
  public static int darkSteelUpgradePowerThreeCost = 30;

  public static int darkSteelGliderCost = 15;
  public static double darkSteelGliderHorizontalSpeed = 0.03;
  public static double darkSteelGliderVerticalSpeed = -0.05;
  public static double darkSteelGliderVerticalSpeedSprinting = -0.15;

  public static int darkSteelSwimCost = 15;

  public static int darkSteelNightVisionCost = 15;

  public static int darkSteelSoundLocatorCost = 10;
  public static int darkSteelSoundLocatorRange = 40;
  public static int darkSteelSoundLocatorLifespan = 40;

  public static int hootchPowerPerCycle = 6;
  public static int hootchPowerTotalBurnTime = 6000;
  public static int rocketFuelPowerPerCycle = 16;
  public static int rocketFuelPowerTotalBurnTime = 7000;
  public static int fireWaterPowerPerCycle = 8;
  public static int fireWaterPowerTotalBurnTime = 15000;
  public static float vatPowerUserPerTick = 2;

  public static double maxPhotovoltaicOutput = 1.0;
  public static double maxPhotovoltaicAdvancedOutput = 4.0;

  public static double zombieGeneratorMjPerTick = 8.0;
  public static int zombieGeneratorTicksPerBucketFuel = 10000;

  public static boolean combustionGeneratorUseOpaqueModel = true;

  public static boolean addFuelTooltipsToAllFluidContainers = true;
  public static boolean addFurnaceFuelTootip = true;
  public static boolean addDurabilityTootip = true;

  public static float farmContinuousEnergyUse = 4;
  public static float farmActionEnergyUse = 50;
  public static float farmAxeActionEnergyUse = 100;
  
  public static int farmDefaultSize = 3;
  public static boolean farmAxeDamageOnLeafBreak = false;
  public static float farmToolTakeDamageChance = 1;
  public static boolean disableFarmNotification = true;

  public static int magnetPowerUsePerSecondRF = 1;
  public static int magnetPowerCapacityRF = 100000;
  public static int magnetRange = 5;

  public static boolean useCombustionGenModel = false;

  public static int crafterMjPerCraft = 250;

  public static int capacitorBankMaxIoMJ = 500;
  public static int capacitorBankMaxStorageMJ = 500000;

  public static int poweredSpawnerMinDelayTicks = 200;
  public static int poweredSpawnerMaxDelayTicks = 800;
  public static float poweredSpawnerLevelOnePowerPerTick = 25;
  public static float poweredSpawnerLevelTwoPowerPerTick = 75;
  public static float poweredSpawnerLevelThreePowerPerTick = 150;
  public static int poweredSpawnerMaxPlayerDistance = 0;
  public static boolean poweredSpawnerUseVanillaSpawChecks = false;
  public static double brokenSpawnerDropChance = 1;
  public static int powerSpawnerAddSpawnerCost = 30;

  public static double vacuumChestRange = 6;

  public static boolean useModMetals = true;

  public static int wirelessChargerRange = 24;

  public static long nutrientFoodBoostDelay = 400;

  public static int enchanterBaseLevelCost = 4;

  public static boolean machineSoundsEnabled = true;

  public static float machineSoundVolume = 1.0f;

  public static int killerJoeNutrientUsePerAttackMb = 5;
  public static double killerJoeAttackHeight = 2;
  public static double killerJoeAttackWidth = 2;
  public static double killerJoeAttackLength = 4;
  public static boolean killerJoeGivePlayerLevelXP = true;
  
  public static boolean allowTileEntitiesAsPaintSource = true;

  public static String isGasConduitEnabled = "auto";

  public static String[] soulVesselBlackList = new String[0];
  public static boolean soulVesselCapturesBosses = false;

  public static double soulBinderLevelOnePowerPerTick = 50;
  public static double soulBinderLevelTwoPowerPerTick = 100;
  public static double soulBinderLevelThreePowerPerTick = 200;
  public static int soulBinderMjForBrokenSpawner = 250000;

  public static void load(FMLPreInitializationEvent event) {

    FMLCommonHandler.instance().bus().register(new Config());
    configDirectory = new File(event.getModConfigurationDirectory(), EnderIO.MODID.toLowerCase());
    if(!configDirectory.exists()) {
      configDirectory.mkdir();
    }

    File configFile = new File(configDirectory, "EnderIO.cfg");
    config = new Configuration(configFile);
    syncConfig();
  }

  public static void syncConfig() {
    try {
      Config.processConfig(config);
    } catch (Exception e) {
      Log.error("EnderIO has a problem loading it's configuration");
      e.printStackTrace();
    } finally {
      if(config.hasChanged()) {
        config.save();
      }
    }
  }

  @SubscribeEvent
  public void onConfigChanged(OnConfigChangedEvent event) {
    if(event.modID.equals(EnderIO.MODID)) {
      Log.info("Updating config...");
      syncConfig();
    }
  }

  public static void processConfig(Configuration config) {
    useRfAsDefault = config
        .get(sectionPower.name, "displayPowerAsRedstoneFlux", useRfAsDefault, "If true, all power is displayed in RF, otherwise MJ is used.")
        .getBoolean(useRfAsDefault);

    capacitorBankMaxIoMJ = config.get(sectionPower.name, "capacitorBankMaxIoMJ", capacitorBankMaxIoMJ, "The maximum IO for a single capacitor in MJ/t")
        .getInt(capacitorBankMaxIoMJ);
    capacitorBankMaxStorageMJ = config.get(sectionPower.name, "capacitorBankMaxStorageMJ", capacitorBankMaxStorageMJ,
        "The maximum storage for a single capacitor in MJ")
        .getInt(capacitorBankMaxStorageMJ);

    useHardRecipes = config.get(sectionRecipe.name, "useHardRecipes", useHardRecipes, "When enabled machines cost significantly more.")
        .getBoolean(useHardRecipes);
    
    allowTileEntitiesAsPaintSource= config.get(sectionRecipe.name, "allowTileEntitiesAsPaintSource", allowTileEntitiesAsPaintSource, "When enabled blocks with tile entities (e.g. machines) can be used as paint targets.")
        .getBoolean(allowTileEntitiesAsPaintSource);

    useSteelInChassi = config.get(sectionRecipe.name, "useSteelInChassi", useSteelInChassi, "When enabled machine chassis will require steel instead of iron.")
        .getBoolean(useSteelInChassi);

    numConduitsPerRecipe = config.get(sectionRecipe.name, "numConduitsPerRecipe", numConduitsPerRecipe,
        "The number of conduits crafted per recipe.").getInt(numConduitsPerRecipe);

    enchanterBaseLevelCost = config.get(sectionRecipe.name, "enchanterBaseLevelCost", enchanterBaseLevelCost,
        "Base level cost added to all recipes in the enchanter.").getInt(enchanterBaseLevelCost);

    photovoltaicCellEnabled = config.get(sectionItems.name, "photovoltaicCellEnabled", photovoltaicCellEnabled,
        "If set to false: Photovoltaic Cells will not be craftable.")
        .getBoolean(photovoltaicCellEnabled);

    maxPhotovoltaicOutput = config.get(sectionPower.name, "maxPhotovoltaicOutput", maxPhotovoltaicOutput,
        "Maximum output in MJ/t of the Photovoltaic Panels.").getDouble(maxPhotovoltaicOutput);
    maxPhotovoltaicAdvancedOutput = config.get(sectionPower.name, "maxPhotovoltaicAdvancedOutput", maxPhotovoltaicAdvancedOutput,
        "Maximum output in MJ/t of the Advanced Photovoltaic Panels.").getDouble(maxPhotovoltaicAdvancedOutput);

    useAlternateBinderRecipe = config.get(sectionRecipe.name, "useAlternateBinderRecipe", false, "Create conduit binder in crafting table instead of furnace")
        .getBoolean(useAlternateBinderRecipe);

    conduitScale = config.get(sectionAesthetic.name, "conduitScale", DEFAULT_CONDUIT_SCALE,
        "Valid values are between 0-1, smallest conduits at 0, largest at 1.\n" +
            "In SMP, all clients must be using the same value as the server.").getDouble(DEFAULT_CONDUIT_SCALE);
    conduitScale = VecmathUtil.clamp(conduitScale, 0, 1);

    wirelessChargerRange = config.get(sectionEfficiency.name, "wirelessChargerRange", wirelessChargerRange,
        "The range of the wireless charger").getInt(wirelessChargerRange);

    fluidConduitExtractRate = config.get(sectionEfficiency.name, "fluidConduitExtractRate", fluidConduitExtractRate,
        "Number of millibuckects per tick extracted by a fluid conduits auto extracting").getInt(fluidConduitExtractRate);

    fluidConduitMaxIoRate = config.get(sectionEfficiency.name, "fluidConduitMaxIoRate", fluidConduitMaxIoRate,
        "Number of millibuckects per tick that can pass through a single connection to a fluid conduit.").getInt(fluidConduitMaxIoRate);

    advancedFluidConduitExtractRate = config.get(sectionEfficiency.name, "advancedFluidConduitExtractRate", advancedFluidConduitExtractRate,
        "Number of millibuckects per tick extracted by pressurised fluid conduits auto extracting").getInt(advancedFluidConduitExtractRate);

    advancedFluidConduitMaxIoRate = config.get(sectionEfficiency.name, "advancedFluidConduitMaxIoRate", advancedFluidConduitMaxIoRate,
        "Number of millibuckects per tick that can pass through a single connection to an pressurised fluid conduit.").getInt(advancedFluidConduitMaxIoRate);

    enderFluidConduitExtractRate = config.get(sectionEfficiency.name, "enderFluidConduitExtractRate", enderFluidConduitExtractRate,
        "Number of millibuckects per tick extracted by ender fluid conduits auto extracting").getInt(enderFluidConduitExtractRate);

    enderFluidConduitMaxIoRate = config.get(sectionEfficiency.name, "enderFluidConduitMaxIoRate", enderFluidConduitMaxIoRate,
        "Number of millibuckects per tick that can pass through a single connection to an ender fluid conduit.").getInt(enderFluidConduitMaxIoRate);

    gasConduitExtractRate = config.get(sectionEfficiency.name, "gasConduitExtractRate", gasConduitExtractRate,
        "Amount of gas per tick extracted by gas conduits auto extracting").getInt(gasConduitExtractRate);

    gasConduitMaxIoRate = config.get(sectionEfficiency.name, "gasConduitMaxIoRate", gasConduitMaxIoRate,
        "Amount of gas per tick that can pass through a single connection to a gas conduit.").getInt(gasConduitMaxIoRate);

    useAlternateTesseractModel = config.get(sectionAesthetic.name, "useAlternateTransceiverModel", useAlternateTesseractModel,
        "Use TheKazador's alternative model for the Dimensional Transceiver")
        .getBoolean(false);
    transceiverEnergyLoss = config.get(sectionPower.name, "transceiverEnergyLoss", transceiverEnergyLoss,
        "Amount of energy lost when transfered by Dimensional Transceiver; 0 is no loss, 1 is 100% loss").getDouble(transceiverEnergyLoss);
    transceiverUpkeepCost = config.get(sectionPower.name, "transceiverUpkeepCost", transceiverUpkeepCost,
        "Number of MJ/t required to keep a Dimensional Transceiver connection open").getDouble(transceiverUpkeepCost);
    transceiverMaxIO = config.get(sectionPower.name, "transceiverMaxIO", transceiverMaxIO,
        "Maximum MJ/t sent and recieved by a Dimensional Transceiver per tick. Input and output limits are not cumulative").getInt(transceiverMaxIO);
    transceiverBucketTransmissionCost = config.get(sectionEfficiency.name, "transceiverBucketTransmissionCost", transceiverBucketTransmissionCost,
        "The cost in MJ of transporting a bucket of fluid via a Dimensional Transceiver.").getDouble(transceiverBucketTransmissionCost);

    vatPowerUserPerTick = (float) config.get(sectionPower.name, "vatPowerUserPerTick", vatPowerUserPerTick,
        "Power use (MJ/t) used by the vat.").getDouble(vatPowerUserPerTick);

    detailedPowerTrackingEnabled = config
        .get(
            sectionAdvanced.name,
            "perInterfacePowerTrackingEnabled",
            detailedPowerTrackingEnabled,
            "Enable per tick sampling on individual power inputs and outputs. This allows slightly more detailed messages from the MJ Reader but has a negative impact on server performance.")
        .getBoolean(detailedPowerTrackingEnabled);

    useSneakMouseWheelYetaWrench = config.get(sectionPersonal.name, "useSneakMouseWheelYetaWrench", useSneakMouseWheelYetaWrench,
        "If true, shift-mouse wheel will change the conduit display mode when the YetaWrench is eqipped.")
        .getBoolean(useSneakMouseWheelYetaWrench);

    useSneakRightClickYetaWrench = config.get(sectionPersonal.name, "useSneakRightClickYetaWrench", useSneakRightClickYetaWrench,
        "If true, shift-clicking the YetaWrench on a null or non wrenchable object will change the conduit display mode.")
        .getBoolean(useSneakRightClickYetaWrench);

    machineSoundsEnabled = config.get(sectionPersonal.name, "useMachineSounds", machineSoundsEnabled,
        "If true, machines will make sounds.")
        .getBoolean(machineSoundsEnabled);

    machineSoundVolume = (float) config.get(sectionPersonal.name, "machineSoundVolume", machineSoundVolume, "Volume of machine sounds.").getDouble(
        machineSoundVolume);

    itemConduitUsePhyscialDistance = config.get(sectionEfficiency.name, "itemConduitUsePhyscialDistance", itemConduitUsePhyscialDistance, "If true, " +
        "'line of sight' distance rather than conduit path distance is used to calculate priorities.")
        .getBoolean(itemConduitUsePhyscialDistance);

    vacuumChestRange = config.get(sectionEfficiency.name, "vacumChestRange", vacuumChestRange, "The range of the vacuum chest").getDouble(vacuumChestRange);

    if(!useSneakMouseWheelYetaWrench && !useSneakRightClickYetaWrench) {
      Log.warn("Both useSneakMouseWheelYetaWrench and useSneakRightClickYetaWrench are set to false. Enabling mouse wheel.");
      useSneakMouseWheelYetaWrench = true;
    }

    reinforcedObsidianEnabled = config.get(sectionItems.name, "reinforcedObsidianEnabled", reinforcedObsidianEnabled,
        "When set to false reinforced obsidian is not craftable.").getBoolean(reinforcedObsidianEnabled);

    travelAnchorEnabled = config.get(sectionItems.name, "travelAnchorEnabled", travelAnchorEnabled,
        "When set to false: the travel anchor will not be craftable.").getBoolean(travelAnchorEnabled);

    travelAnchorMaxDistance = config.get(sectionAnchor.name, "travelAnchorMaxDistance", travelAnchorMaxDistance,
        "Maximum number of blocks that can be traveled from one travel anchor to another.").getInt(travelAnchorMaxDistance);

    travelStaffMaxDistance = config.get(sectionStaff.name, "travelStaffMaxDistance", travelStaffMaxDistance,
        "Maximum number of blocks that can be traveled using the Staff of the Traveling.").getInt(travelStaffMaxDistance);
    travelStaffPowerPerBlockRF = (float) config.get(sectionStaff.name, "travelStaffPowerPerBlockRF", travelStaffPowerPerBlockRF,
        "Number of MJ required per block travelled using the Staff of the Traveling.").getDouble(travelStaffPowerPerBlockRF);

    travelStaffMaxBlinkDistance = config.get(sectionStaff.name, "travelStaffMaxBlinkDistance", travelStaffMaxBlinkDistance,
        "Max number of blocks teleported when shift clicking the staff.").getInt(travelStaffMaxBlinkDistance);

    travelStaffBlinkPauseTicks = config.get(sectionStaff.name, "travelStaffBlinkPauseTicks", travelStaffBlinkPauseTicks,
        "Minimum number of ticks between 'blinks'. Values of 10 or less allow a limited sort of flight.").getInt(travelStaffBlinkPauseTicks);

    travelStaffEnabled = config.get(sectionItems.name, "travelStaffEnabled", travelAnchorEnabled,
        "If set to false: the travel staff will not be craftable.").getBoolean(travelStaffEnabled);
    travelStaffBlinkEnabled = config.get(sectionItems.name, "travelStaffBlinkEnabled", travelStaffBlinkEnabled,
        "If set to false: the travel staff can not be used to shift-right click teleport, or blink.").getBoolean(travelStaffBlinkEnabled);
    travelStaffBlinkThroughSolidBlocksEnabled = config.get(sectionItems.name, "travelStaffBlinkThroughSolidBlocksEnabled",
        travelStaffBlinkThroughSolidBlocksEnabled,
        "If set to false: the travel staff can be used to blink through any block.").getBoolean(travelStaffBlinkThroughSolidBlocksEnabled);
    travelStaffBlinkThroughClearBlocksEnabled = config
        .get(sectionItems.name, "travelStaffBlinkThroughClearBlocksEnabled", travelStaffBlinkThroughClearBlocksEnabled,
            "If travelStaffBlinkThroughSolidBlocksEnabled is set to false and this is true: the travel " +
                "staff can only be used to blink through transparent or partial blocks (e.g. torches). " +
                "If both are false: only air blocks may be teleported through.")
        .getBoolean(travelStaffBlinkThroughClearBlocksEnabled);

    enderIoRange = config.get(sectionEfficiency.name, "enderIoRange", enderIoRange,
        "Range accessable (in blocks) when using the Ender IO.").getInt(enderIoRange);

    enderIoMeAccessEnabled = config.get(sectionPersonal.name, "enderIoMeAccessEnabled", enderIoMeAccessEnabled,
        "If false: you will not be able to access a ME access or crafting terminal using the Ender IO.").getBoolean(enderIoMeAccessEnabled);

    updateLightingWhenHidingFacades = config.get(sectionEfficiency.name, "updateLightingWhenHidingFacades", updateLightingWhenHidingFacades,
        "When true: correct lighting is recalculated (client side) for conduit bundles when transitioning to"
            + " from being hidden behind a facade. This produces "
            + "better quality rendering but can result in frame stutters when switching to/from a wrench.")
        .getBoolean(updateLightingWhenHidingFacades);

    darkSteelPowerStorageBase = config.get(sectionDarkSteel.name, "darkSteelPowerStorageBase", darkSteelPowerStorageBase,
        "Base amount of power stored by dark steel items.").getInt(darkSteelPowerStorageBase);
    darkSteelPowerStorageLevelOne = config.get(sectionDarkSteel.name, "darkSteelPowerStorageLevelOne", darkSteelPowerStorageLevelOne,
        "Amount of power stored by dark steel items with a level 1 upgrade.").getInt(darkSteelPowerStorageLevelOne);
    darkSteelPowerStorageLevelTwo = config.get(sectionDarkSteel.name, "darkSteelPowerStorageLevelTwo", darkSteelPowerStorageLevelTwo,
        "Amount of power stored by dark steel items with a level 2 upgrade.").getInt(darkSteelPowerStorageLevelTwo);
    darkSteelPowerStorageLevelThree = config.get(sectionDarkSteel.name, "darkSteelPowerStorageLevelThree", darkSteelPowerStorageLevelThree,
        "Amount of power stored by dark steel items with a level 3 upgrade.").getInt(darkSteelPowerStorageLevelThree);

    darkSteelUpgradeVibrantCost = config.get(sectionDarkSteel.name, "darkSteelUpgradeVibrantCost", darkSteelUpgradeVibrantCost,
        "Number of levels required for the 'Vibrant' upgrade.").getInt(darkSteelUpgradeVibrantCost);
    darkSteelUpgradePowerOneCost = config.get(sectionDarkSteel.name, "darkSteelUpgradePowerOneCost", darkSteelUpgradePowerOneCost,
        "Number of levels required for the 'Vibrant' upgrade.").getInt(darkSteelUpgradePowerOneCost);
    darkSteelUpgradePowerTwoCost = config.get(sectionDarkSteel.name, "darkSteelUpgradePowerTwoCost", darkSteelUpgradePowerTwoCost,
        "Number of levels required for the 'Vibrant' upgrade.").getInt(darkSteelUpgradePowerTwoCost);
    darkSteelUpgradePowerThreeCost = config.get(sectionDarkSteel.name, "darkSteelUpgradePowerThreeCost", darkSteelUpgradePowerThreeCost,
        "Number of levels required for the 'Vibrant' upgrade.").getInt(darkSteelUpgradePowerThreeCost);

    darkSteelSpeedOneWalkModifier = (float) config.get(sectionDarkSteel.name, "darkSteelSpeedOneWalkModifier", darkSteelSpeedOneWalkModifier,
        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.").getDouble(darkSteelSpeedOneWalkModifier);
    darkSteelSpeedTwoWalkMultiplier = (float) config.get(sectionDarkSteel.name, "darkSteelSpeedTwoWalkMultiplier", darkSteelSpeedTwoWalkMultiplier,
        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.").getDouble(darkSteelSpeedTwoWalkMultiplier);
    darkSteelSpeedThreeWalkMultiplier = (float) config.get(sectionDarkSteel.name, "darkSteelSpeedThreeWalkMultiplier", darkSteelSpeedThreeWalkMultiplier,
        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.").getDouble(darkSteelSpeedThreeWalkMultiplier);

    darkSteelSpeedOneSprintModifier = (float) config.get(sectionDarkSteel.name, "darkSteelSpeedOneSprintModifier", darkSteelSpeedOneSprintModifier,
        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.").getDouble(darkSteelSpeedOneSprintModifier);
    darkSteelSpeedTwoSprintMultiplier = (float) config.get(sectionDarkSteel.name, "darkSteelSpeedTwoSprintMultiplier", darkSteelSpeedTwoSprintMultiplier,
        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.").getDouble(darkSteelSpeedTwoSprintMultiplier);
    darkSteelSpeedThreeSprintMultiplier = (float) config.get(sectionDarkSteel.name, "darkSteelSpeedThreeSprintMultiplier", darkSteelSpeedThreeSprintMultiplier,
        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.").getDouble(darkSteelSpeedThreeSprintMultiplier);

    darkSteelBootsJumpModifier = config.get(sectionDarkSteel.name, "darkSteelBootsJumpModifier", darkSteelBootsJumpModifier,
        "Jump height modifier applied when jumping with Dark Steel Boots equipped").getDouble(darkSteelBootsJumpModifier);

    darkSteelPowerStorageBase = config.get(sectionDarkSteel.name, "darkSteelPowerStorage", darkSteelPowerStorageBase,
        "Amount of power stored (RF) per crystal in the armor items recipe.").getInt(darkSteelPowerStorageBase);
    darkSteelWalkPowerCost = config.get(sectionDarkSteel.name, "darkSteelWalkPowerCost", darkSteelWalkPowerCost,
        "Amount of power stored (RF) per block walked when wearing the dark steel boots.").getInt(darkSteelWalkPowerCost);
    darkSteelSprintPowerCost = config.get(sectionDarkSteel.name, "darkSteelSprintPowerCost", darkSteelWalkPowerCost,
        "Amount of power stored (RF) per block walked when wearing the dark stell boots.").getInt(darkSteelSprintPowerCost);
    darkSteelDrainPowerFromInventory = config.get(sectionDarkSteel.name, "darkSteelDrainPowerFromInventory", darkSteelDrainPowerFromInventory,
        "If true, dark steel armor will drain power stored (RF) in power containers in the players invenotry.").getBoolean(darkSteelDrainPowerFromInventory);

    darkSteelBootsJumpPowerCost = config.get(sectionDarkSteel.name, "darkSteelBootsJumpPowerCost", darkSteelBootsJumpPowerCost,
        "Base amount of power used per jump (RF) dark steel boots. The second jump in a 'double jump' uses 2x this etc").getInt(darkSteelBootsJumpPowerCost);

    darkSteelFallDistanceCost = config.get(sectionDarkSteel.name, "darkSteelFallDistanceCost", darkSteelFallDistanceCost,
        "Amount of power used (RF) per block height of fall distance damage negated.").getInt(darkSteelFallDistanceCost);

    darkSteelSwimCost = config.get(sectionDarkSteel.name, "darkSteelSwimCost", darkSteelSwimCost,
        "Number of levels required for the 'Swim' upgrade.").getInt(darkSteelSwimCost);

    darkSteelNightVisionCost = config.get(sectionDarkSteel.name, "darkSteelNightVisionCost", darkSteelNightVisionCost,
        "Number of levels required for the 'Night Vision' upgrade.").getInt(darkSteelNightVisionCost);

    darkSteelGliderCost = config.get(sectionDarkSteel.name, "darkSteelGliderCost", darkSteelGliderCost,
        "Number of levels required for the 'Glider' upgrade.").getInt(darkSteelGliderCost);
    darkSteelGliderHorizontalSpeed = config.get(sectionDarkSteel.name, "darkSteelGliderHorizontalSpeed", darkSteelGliderHorizontalSpeed,
        "Horizontal movement speed modifier when gliding.").getDouble(darkSteelGliderHorizontalSpeed);
    darkSteelGliderVerticalSpeed = config.get(sectionDarkSteel.name, "darkSteelGliderVerticalSpeed", darkSteelGliderVerticalSpeed,
        "Rate of altitude loss when gliding.").getDouble(darkSteelGliderVerticalSpeed);
    darkSteelGliderVerticalSpeedSprinting = config.get(sectionDarkSteel.name, "darkSteelGliderVerticalSpeedSprinting", darkSteelGliderVerticalSpeedSprinting,
        "Rate of altitude loss when sprinting and gliding.").getDouble(darkSteelGliderVerticalSpeedSprinting);

    darkSteelSoundLocatorCost = config.get(sectionDarkSteel.name, "darkSteelSoundLocatorCost", darkSteelSoundLocatorCost,
        "Number of levels required for the 'Sound Locator' upgrade.").getInt(darkSteelSoundLocatorCost);
    darkSteelSoundLocatorRange = config.get(sectionDarkSteel.name, "darkSteelSoundLocatorRange", darkSteelSoundLocatorRange,
        "Range of the 'Sound Locator' upgrade.").getInt(darkSteelSoundLocatorRange);
    darkSteelSoundLocatorLifespan = config.get(sectionDarkSteel.name, "darkSteelSoundLocatorLifespan", darkSteelSoundLocatorLifespan,
        "Number of ticks the 'Sound Locator' icons are displayed for.").getInt(darkSteelSoundLocatorLifespan);

    darkSteelSwordSkullChance = (float) config.get(sectionDarkSteel.name, "darkSteelSwordSkullChance", darkSteelSwordSkullChance,
        "The base chance that a skull will be dropped when using a powered dark steel sword (0 = no chance, 1 = 100% chance)").getDouble(
        darkSteelSwordSkullChance);
    darkSteelSwordSkullLootingModifier = (float) config.get(sectionDarkSteel.name, "darkSteelSwordSkullLootingModifier", darkSteelSwordSkullLootingModifier,
        "The chance per looting level that a skull will be dropped when using a powered dark steel sword (0 = no chance, 1 = 100% chance)").getDouble(
        darkSteelSwordSkullLootingModifier);

    darkSteelSwordWitherSkullChance = (float) config.get(sectionDarkSteel.name, "darkSteelSwordWitherSkullChance", darkSteelSwordWitherSkullChance,
        "The base chance that a wither skull will be dropped when using a powered dark steel sword (0 = no chance, 1 = 100% chance)").getDouble(
        darkSteelSwordWitherSkullChance);
    darkSteelSwordWitherSkullLootingModifier = (float) config.get(sectionDarkSteel.name, "darkSteelSwordWitherSkullLootingModifie",
        darkSteelSwordWitherSkullLootingModifier,
        "The chance per looting level that a wither skull will be dropped when using a powered dark steel sword (0 = no chance, 1 = 100% chance)").getDouble(
        darkSteelSwordWitherSkullLootingModifier);

    vanillaSwordSkullChance = (float) config.get(sectionDarkSteel.name, "vanillaSwordSkullChance", vanillaSwordSkullChance,
        "The base chance that a skull will be dropped when using a non dark steel sword (0 = no chance, 1 = 100% chance)").getDouble(
        vanillaSwordSkullChance);
    vanillaSwordSkullLootingModifier = (float) config.get(sectionPersonal.name, "vanillaSwordSkullLootingModifier", vanillaSwordSkullLootingModifier,
        "The chance per looting level that a skull will be dropped when using a non-dark steel sword (0 = no chance, 1 = 100% chance)").getDouble(
        vanillaSwordSkullLootingModifier);

    darkSteelSwordPowerUsePerHit = config.get(sectionDarkSteel.name, "darkSteelSwordPowerUsePerHit", darkSteelSwordPowerUsePerHit,
        "The amount of power (RF) used per hit.").getInt(darkSteelSwordPowerUsePerHit);
    darkSteelSwordEnderPearlDropChance = config.get(sectionDarkSteel.name, "darkSteelSwordEnderPearlDropChance", darkSteelSwordEnderPearlDropChance,
        "The chance that an ender pearl will be dropped when using a dark steel sword (0 = no chance, 1 = 100% chance)").getDouble(
        darkSteelSwordEnderPearlDropChance);
    darkSteelSwordEnderPearlDropChancePerLooting = config.get(sectionDarkSteel.name, "darkSteelSwordEnderPearlDropChancePerLooting",
        darkSteelSwordEnderPearlDropChancePerLooting,
        "The chance for each looting level that an additional ender pearl will be dropped when using a dark steel sword (0 = no chance, 1 = 100% chance)")
        .getDouble(
            darkSteelSwordEnderPearlDropChancePerLooting);

    darkSteelPickPowerUseObsidian = config.get(sectionDarkSteel.name, "darkSteelPickPowerUseObsidian", darkSteelPickPowerUseObsidian,
        "The amount of power (RF) used to break an obsidian block.").getInt(darkSteelPickPowerUseObsidian);
    darkSteelPickEffeciencyObsidian = config.get(sectionDarkSteel.name, "darkSteelPickEffeciencyObsidian", darkSteelPickEffeciencyObsidian,
        "The effeciency when breaking obsidian with a powered  Dark Pickaxe.").getInt(darkSteelPickEffeciencyObsidian);
    darkSteelPickPowerUsePerDamagePoint = config.get(sectionDarkSteel.name, "darkSteelPickPowerUsePerDamagePoint", darkSteelPickPowerUsePerDamagePoint,
        "Power use (RF) per damage/durability point avoided.").getInt(darkSteelPickPowerUsePerDamagePoint);
    darkSteelPickEffeciencyBoostWhenPowered = (float) config.get(sectionDarkSteel.name, "darkSteelPickEffeciencyBoostWhenPowered",
        darkSteelPickEffeciencyBoostWhenPowered, "The increase in effciency when powered.").getDouble(darkSteelPickEffeciencyBoostWhenPowered);

    darkSteelAxePowerUsePerDamagePoint = config.get(sectionDarkSteel.name, "darkSteelAxePowerUsePerDamagePoint", darkSteelAxePowerUsePerDamagePoint,
        "Power use (RF) per damage/durability point avoided.").getInt(darkSteelAxePowerUsePerDamagePoint);
    darkSteelAxePowerUsePerDamagePointMultiHarvest = config.get(sectionDarkSteel.name, "darkSteelPickAxeUsePerDamagePointMultiHarvest",
        darkSteelAxePowerUsePerDamagePointMultiHarvest,
        "Power use (RF) per damage/durability point avoided when shift-harvesting multiple logs").getInt(darkSteelAxePowerUsePerDamagePointMultiHarvest);
    darkSteelAxeSpeedPenaltyMultiHarvest = (float) config.get(sectionDarkSteel.name, "darkSteelAxeSpeedPenaltyMultiHarvest",
        darkSteelAxeSpeedPenaltyMultiHarvest,
        "How much slower shift-harvesting logs is.").getDouble(darkSteelAxeSpeedPenaltyMultiHarvest);
    darkSteelAxeEffeciencyBoostWhenPowered = (float) config.get(sectionDarkSteel.name, "darkSteelAxeEffeciencyBoostWhenPowered",
        darkSteelAxeEffeciencyBoostWhenPowered, "The increase in effciency when powered.").getDouble(darkSteelAxeEffeciencyBoostWhenPowered);

    hootchPowerPerCycle = config.get(sectionPower.name, "hootchPowerPerCycle", hootchPowerPerCycle,
        "The amount of power generated per BC engine cycle. Examples: BC Oil = 3, BC Fuel = 6").getInt(hootchPowerPerCycle);
    hootchPowerTotalBurnTime = config.get(sectionPower.name, "hootchPowerTotalBurnTime", hootchPowerTotalBurnTime,
        "The total burn time. Examples: BC Oil = 5000, BC Fuel = 25000").getInt(hootchPowerTotalBurnTime);

    rocketFuelPowerPerCycle = config.get(sectionPower.name, "rocketFuelPowerPerCycle", rocketFuelPowerPerCycle,
        "The amount of power generated per BC engine cycle. Examples: BC Oil = 3, BC Fuel = 6").getInt(rocketFuelPowerPerCycle);
    rocketFuelPowerTotalBurnTime = config.get(sectionPower.name, "rocketFuelPowerTotalBurnTime", rocketFuelPowerTotalBurnTime,
        "The total burn time. Examples: BC Oil = 5000, BC Fuel = 25000").getInt(rocketFuelPowerTotalBurnTime);

    fireWaterPowerPerCycle = config.get(sectionPower.name, "fireWaterPowerPerCycle", fireWaterPowerPerCycle,
        "The amount of power generated per BC engine cycle. Examples: BC Oil = 3, BC Fuel = 6").getInt(fireWaterPowerPerCycle);
    fireWaterPowerTotalBurnTime = config.get(sectionPower.name, "fireWaterPowerTotalBurnTime", fireWaterPowerTotalBurnTime,
        "The total burn time. Examples: BC Oil = 5000, BC Fuel = 25000").getInt(fireWaterPowerTotalBurnTime);

    zombieGeneratorMjPerTick = config.get(sectionPower.name, "zombieGeneratorMjPerTick", zombieGeneratorMjPerTick,
        "The amount of power generated per tick.").getDouble(zombieGeneratorMjPerTick);
    zombieGeneratorTicksPerBucketFuel = config.get(sectionPower.name, "zombieGeneratorTicksPerMbFuel", zombieGeneratorTicksPerBucketFuel,
        "The number of ticks one bucket of fuel lasts.").getInt(zombieGeneratorTicksPerBucketFuel);

    addFuelTooltipsToAllFluidContainers = config.get(sectionPersonal.name, "addFuelTooltipsToAllFluidContainers", addFuelTooltipsToAllFluidContainers,
        "If true, the MJ/t and burn time of the fuel will be displayed in all tooltips for fluid containers with fuel.").getBoolean(
        addFuelTooltipsToAllFluidContainers);
    addDurabilityTootip = config.get(sectionPersonal.name, "addDurabilityTootip", addFuelTooltipsToAllFluidContainers,
        "If true, adds durability tooltips to tools and armor").getBoolean(
        addDurabilityTootip);
    addFurnaceFuelTootip = config.get(sectionPersonal.name, "addFurnaceFuelTootip", addFuelTooltipsToAllFluidContainers,
        "If true, adds burn duration tooltips to furnace fuels").getBoolean(addFurnaceFuelTootip);

    farmContinuousEnergyUse = (float) config.get(sectionFarm.name, "farmContinuousEnergyUse", farmContinuousEnergyUse,
        "The amount of power used by a farm per tick ").getDouble(farmContinuousEnergyUse);
    farmActionEnergyUse = (float) config.get(sectionFarm.name, "farmActionEnergyUse", farmActionEnergyUse,
        "The amount of power used by a farm per action (eg plant, till, harvest) ").getDouble(farmActionEnergyUse);
    
    farmAxeActionEnergyUse= (float) config.get(sectionFarm.name, "farmAxeActionEnergyUse", farmAxeActionEnergyUse,
        "The amount of power used by a farm per wood block 'chopped'").getDouble(farmAxeActionEnergyUse);
    
    farmDefaultSize = config.get(sectionFarm.name, "farmDefaultSize", farmDefaultSize,
        "The number of blocks a farm will extend from its center").getInt(farmDefaultSize);

    farmAxeDamageOnLeafBreak = config.get(sectionFarm.name, "farmAxeDamageOnLeafBreak", farmAxeDamageOnLeafBreak, 
	       "Should axes in a farm take damage when breaking leaves?").getBoolean(farmAxeDamageOnLeafBreak);
    farmToolTakeDamageChance = (float) config.get(sectionFarm.name, "farmToolTakeDamageChance", farmToolTakeDamageChance, 
	       "The chance that a tool in the farm will take damage.").getDouble(farmToolTakeDamageChance);
    
    disableFarmNotification = config.get(sectionFarm.name, "disableFarmNotifications", disableFarmNotification, 
        "Disable the notification text above the farm block.").getBoolean();
    

    combustionGeneratorUseOpaqueModel = config.get(sectionAesthetic.name, "combustionGeneratorUseOpaqueModel", combustionGeneratorUseOpaqueModel,
        "If set to true: fluid will not be shown in combustion generator tanks. Improves FPS. ").getBoolean(combustionGeneratorUseOpaqueModel);

    magnetPowerUsePerSecondRF = config.get(sectionMagnet.name, "magnetPowerUsePerTickRF", magnetPowerUsePerSecondRF,
        "The amount of RF power used per tick when the magnet is active").getInt(magnetPowerUsePerSecondRF);
    magnetPowerCapacityRF = config.get(sectionMagnet.name, "magnetPowerCapacityRF", magnetPowerCapacityRF,
        "Amount of RF power stored in a fully charged magnet").getInt(magnetPowerCapacityRF);
    magnetRange = config.get(sectionMagnet.name, "magnetRange", magnetRange,
        "Range of the magnet in blocks.").getInt(magnetRange);

    useCombustionGenModel = config.get(sectionAesthetic.name, "useCombustionGenModel", useCombustionGenModel,
        "If set to true: WIP Combustion Generator model will be used").getBoolean(useCombustionGenModel);

    crafterMjPerCraft = config.get("AutoCrafter Settings", "crafterMjPerCraft", crafterMjPerCraft,
        "MJ used per autocrafted recipe").getInt(crafterMjPerCraft);

    poweredSpawnerMinDelayTicks = config.get(sectionSpawner.name, "poweredSpawnerMinDelayTicks", poweredSpawnerMinDelayTicks,
        "Min tick delay between spawns for a non-upgraded spawner").getInt(poweredSpawnerMinDelayTicks);
    poweredSpawnerMaxDelayTicks = config.get(sectionSpawner.name, "poweredSpawnerMaxDelayTicks", poweredSpawnerMaxDelayTicks,
        "Min tick delay between spawns for a non-upgraded spawner").getInt(poweredSpawnerMaxDelayTicks);
    poweredSpawnerLevelOnePowerPerTick = (float) config.get(sectionSpawner.name, "poweredSpawnerLevelOnePowerPerTick", poweredSpawnerLevelOnePowerPerTick,
        "MJ per tick for a level 1 (non-upgraded) spawner").getDouble(poweredSpawnerLevelOnePowerPerTick);
    poweredSpawnerLevelTwoPowerPerTick = (float) config.get(sectionSpawner.name, "poweredSpawnerLevelTwoPowerPerTick", poweredSpawnerLevelTwoPowerPerTick,
        "MJ per tick for a level 2 spawner").getDouble(poweredSpawnerLevelTwoPowerPerTick);
    poweredSpawnerLevelThreePowerPerTick = (float) config.get(sectionSpawner.name, "poweredSpawnerLevelThreePowerPerTick",
        poweredSpawnerLevelThreePowerPerTick,
        "MJ per tick for a level 3 spawner").getDouble(poweredSpawnerLevelThreePowerPerTick);
    poweredSpawnerMaxPlayerDistance = config.get(sectionSpawner.name, "poweredSpawnerMaxPlayerDistance", poweredSpawnerMaxPlayerDistance,
        "Max distance of the closest player for the spawner to be active. A zero value will remove the player check").getInt(poweredSpawnerMaxPlayerDistance);
    poweredSpawnerUseVanillaSpawChecks = config.get(sectionSpawner.name, "poweredSpawnerUseVanillaSpawChecks", poweredSpawnerUseVanillaSpawChecks,
        "If true, regular spawn checks such as lighting level and dimension will be made before spawning mobs").getBoolean(poweredSpawnerUseVanillaSpawChecks);
    brokenSpawnerDropChance = (float) config.get(sectionSpawner.name, "brokenSpawnerDropChance", brokenSpawnerDropChance,
        "The chance a brokne spawner will be dropped when a spawner is broken. 1 = 100% chance, 0 = 0% chance").getDouble(brokenSpawnerDropChance);
    powerSpawnerAddSpawnerCost = config.get(sectionSpawner.name, "powerSpawnerAddSpawnerCost", powerSpawnerAddSpawnerCost,
        "The number of levels it costs to add a broken spawner").getInt(powerSpawnerAddSpawnerCost);

    useModMetals = config.get(sectionRecipe.name, "useModMetals", useModMetals,
        "If true copper and tin will be used in recipes when registered in the ore dictionary").getBoolean(useModMetals);

    nutrientFoodBoostDelay = config.get(sectionFluid.name, "nutrientFluidFoodBoostDelay", nutrientFoodBoostDelay,
        "The delay in ticks between when nutrient distillation boosts your food value.").getInt((int) nutrientFoodBoostDelay);

    killerJoeNutrientUsePerAttackMb = config.get(sectionKiller.name, "killerJoeNutrientUsePerAttackMb", killerJoeNutrientUsePerAttackMb,
        "The number of millibuckets of nutrient fluid used per attack.").getInt(killerJoeNutrientUsePerAttackMb);

    killerJoeAttackHeight = config.get(sectionKiller.name, "killerJoeAttackHeight", killerJoeAttackHeight,
        "The reach of attacks above and bellow Joe.").getDouble(killerJoeAttackHeight);
    killerJoeAttackWidth = config.get(sectionKiller.name, "killerJoeAttackWidth", killerJoeAttackWidth,
        "The reach of attacks to each side of Joe.").getDouble(killerJoeAttackWidth);
    killerJoeAttackLength = config.get(sectionKiller.name, "killerJoeAttackLength", killerJoeAttackLength,
        "The reach of attacks in front of Joe.").getDouble(killerJoeAttackLength);
    killerJoeGivePlayerLevelXP = config.get(sectionKiller.name, "killerJoeGivePlayerLevelXP", killerJoeGivePlayerLevelXP, "If true, will give the player one xp of HIS current level. If false, will give the player one level of the killer joe's xp.").getBoolean();
    
    isGasConduitEnabled = config.getString("isGasConduitEnabled", sectionItems.name, isGasConduitEnabled, 
        "Can be set to 'auto', 'true' or 'false'. When set to auto the gas conduit will only be enabled when Mekanism is installed.");
    
    soulVesselBlackList = config.getStringList("soulVesselBlackList", sectionSoulBinder.name, soulVesselBlackList, 
        "Entities listed here will can not be captured in a Soul Vial");
    
    soulVesselCapturesBosses = config.getBoolean("soulVesselCapturesBosses", sectionSoulBinder.name, soulVesselCapturesBosses, 
        "When set to false, any mob with a 'boss bar' won't be able to be captured in the Soul Vial");
    
    soulBinderLevelOnePowerPerTick = config.get(sectionSoulBinder.name, "soulBinderLevelOnePowerPerTick", soulBinderLevelOnePowerPerTick,
        "The number of MJ/t consumed by an unupgraded soul binder.").getDouble(soulBinderLevelOnePowerPerTick);
    
    soulBinderLevelTwoPowerPerTick = config.get(sectionSoulBinder.name, "soulBinderLevelTwoPowerPerTick", soulBinderLevelTwoPowerPerTick,
        "The number of MJ/t consumed by a soul binder with a double layer capacitor upgrade.").getDouble(soulBinderLevelTwoPowerPerTick);
    soulBinderLevelThreePowerPerTick = config.get(sectionSoulBinder.name, "soulBinderLevelThreePowerPerTick", soulBinderLevelThreePowerPerTick,
        "The number of MJ/t consumed by a soul binder with an octadic capacitor upgrade.").getDouble(soulBinderLevelThreePowerPerTick);    
    soulBinderMjForBrokenSpawner = config.get(sectionSoulBinder.name, "soulBinderMjForBrokenSpawner", soulBinderMjForBrokenSpawner,
        "The number of MJ required to change the type of a broken spawner.").getInt(soulBinderMjForBrokenSpawner);
  }

  private Config() {
  }

}
