package farmBot;

import farmBot.PlayerTickHandler;
import ibxm.Player;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.EnumSet;
import java.util.Timer;
import java.util.TimerTask;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = FarmBot.modid, name = "Farm Bot", version = "0.1")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class FarmBot
{
	@Instance(FarmBot.modid)
	public static FarmBot instance;
	static boolean moveState;
	static int dirFor = 0;
	static int dirSide = 0;
	static boolean attackState = false;
	static boolean useState = false;
	static boolean runBot;
	static KeyBinding botStart = new KeyBinding("FarmingStart", Keyboard.KEY_P);
	static KeyBinding botStop = new KeyBinding("FarmingStop", Keyboard.KEY_O);
	final static int yawSouth = 0;
	final static int yawWest = 90;
	final static int yawNorth = 180;
	final static int yawEast = 270;
	static boolean harvestBack;
	static boolean hasChanged = false;
	static int seedID = 295;
	static int useID = 292;
	static int FarmLength;
	static int FarmWidth;
	static String runType = "civcraft";
	
	static final String modid = "cartycrabber_FarmBot";
	
	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		TickRegistry.registerTickHandler(new PlayerTickHandler(EnumSet.of(TickType.PLAYER)), Side.CLIENT);
		KeyBinding[] key = {botStart, botStop};
        boolean[] repeat = {false, false};
        KeyBindingRegistry.registerKeyBinding(new KeyBindHandler(key, repeat));
        NetworkRegistry.instance().registerGuiHandler(this, new GuiHandler());
	}
	
	static void storeAllItems(int itemID)
	{
//		KeyBinding.setKeyBindState(net.minecraft.client.settings.GameSettings.keyBindUseItem.keyCode, true);
//		PlayerTickHandler.waitTicks(1);
//		KeyBinding.setKeyBindState(net.minecraft.client.settings.GameSettings.keyBindUseItem.keyCode, false);
		EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
//		PlayerTickHandler.waitTicks(20);
//		player.openContainer.slotClick(1, 1, 0, player);
//		player.openContainer.transferStackInSlot(player, 1);
//		player.inventoryContainer.transferStackInSlot(player, 1);
		player.inventoryContainer.detectAndSendChanges();
		player.openContainer.detectAndSendChanges();
	}
	
	static int getBlockRelative(int xOff, int yOff, int zOff, boolean getMeta)
	{
		EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
		int blockX = (int)player.posX + xOff - 1;
		int blockY = (int)player.posY + yOff;
		int blockZ = (int)player.posZ + zOff - 1;
/*		System.out.println("[FarmBot] Pos X: " + (int)player.posX);
		System.out.println("[FarmBot] Pos Y: " + (int)player.posY);
		System.out.println("[FarmBot] Pos Z: " + (int)player.posZ);
		System.out.println("[FarmBot] Block X: " + blockX);
		System.out.println("[FarmBot] Block Y: " + blockY);
		System.out.println("[FarmBot] block Z: " + blockZ);
*/		WorldClient world = FMLClientHandler.instance().getClient().theWorld;
		if(getMeta)
			return world.getBlockMetadata(blockX, blockY, blockZ);
		else
			return world.getBlockId(blockX, blockY, blockZ);
	}
	
	// 0 is south, 1 west, 2 is north, 3 is east
	public static void movePlayer(final int direction, final int blocks)
	{
		EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
		double oldXpos = player.posX - 1;
		double oldZpos = player.posZ - 1;
		if (direction == 0)
		{
			player.rotationYaw = 0;
			oldZpos = oldZpos + blocks;
		}
		else if (direction == 1)
		{
			player.rotationYaw = 90;
			oldZpos = oldXpos - blocks;
		}
		else if (direction == 2)
		{
			player.rotationYaw = 180;
			oldZpos = oldZpos - blocks;
		}
		else if (direction == 3)
		{
			player.rotationYaw = 270;
			oldXpos = oldXpos + blocks;
		}
		final double newXpos = oldXpos;
		final double newZpos = oldZpos;
		player.cameraYaw = 0;
		moveState = true;
//		System.out.println("Current: " + ((int)player.posX  - 1) + " , " + (int)player.posZ);
//		System.out.println("New: " + (int)newXpos + " , " + (int)newZpos);
		final Timer moveTimer = new Timer();
		moveTimer.scheduleAtFixedRate(new TimerTask()
		{
			@Override
			public void run()
			{
				EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
				int fixedX = (int)player.posX - 1;
				int fixedZ = (int)player.posZ - 1;
				if (fixedX == (int)newXpos && fixedZ == (int)newZpos)
				{
					moveState = false;
					moveTimer.cancel();
					moveTimer.purge();
				}
			}
		}, 0, 10);
	}
	
	//hit the block the player is facing for time(in milliseconds)
	public static void hitBlock(int time)
	{
		attackState = true;
		final Timer hitTimer = new Timer();
		hitTimer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				attackState = false;
			}
		}, time);
	}
	
	public static void placeBlock(int time)
	{
//		useState = true;
		final Timer placeTimer = new Timer();
		placeTimer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				useState = false;
			}
		}, time);
	}
	
	public static void runBot()
	{
		runBot = true;
		final EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
		player.addChatMessage("§2Starting FarmBot");
		final Timer farmingTimer = new Timer();
		final int length = FarmLength;
		final int width = FarmWidth;
		final int subWidth = 8;
		harvestBack = false;
		farmingTimer.scheduleAtFixedRate(new TimerTask()
		{
			double currentX = player.posX;
			double currentZ = player.posZ;
			double nextX;
			double nextZ = currentZ;
			int rowStart = (int) currentZ;
			int rowEnd = (int) (currentZ - length);
			int rowWideEnd = (int) (currentX + subWidth);
			int farmWideEnd = (int) (currentX + width);
			boolean firstRun = true;
			@Override
			public void run()
			{
				if (firstRun)
				{
					System.out.println("[FarmBot] Row Start: " + rowStart);
					System.out.println("[FarmBot] Row End: " + rowEnd);
					firstRun = false;
				}
				if(runBot)
				{
					if(runType == "civcraft")
					{
						player.rotationPitch = 90;
						moveState = true;
						currentX = player.posX;
						currentZ = player.posZ;
						if(harvestBack)
						{
							player.rotationYaw = yawSouth;
							if((int)currentZ == rowStart)
							{
								moveState = false;
								player.rotationYaw = yawEast;
								if(!hasChanged)
								{
									nextX = currentX + 1;
									System.out.println("[FarmBot] Next X: " + (int)nextX);
									System.out.println("[FarmBot] farmWideEnd: " + farmWideEnd);
									if((int)nextX == farmWideEnd)
									{
										runBot = false;
									}
									hasChanged = true;
								}
								moveState = true;
								if((int)currentX == (int)nextX)
								{
									harvestBack = false;
									hasChanged = false;
									nextZ = currentZ - 1;
								}
							}
							else if ((int)currentZ == (int)nextZ)
							{
								moveState = false;
								player.inventory.setCurrentItem(useID, 0, false, false);
								KeyBinding.setKeyBindState(net.minecraft.client.settings.GameSettings.keyBindUseItem.keyCode, true);
								PlayerTickHandler.waitTicks(1);
								KeyBinding.setKeyBindState(net.minecraft.client.settings.GameSettings.keyBindUseItem.keyCode, false);
								KeyBinding.setKeyBindState(net.minecraft.client.settings.GameSettings.keyBindAttack.keyCode, true);
								PlayerTickHandler.waitTicks(1);
								KeyBinding.setKeyBindState(net.minecraft.client.settings.GameSettings.keyBindAttack.keyCode, false);
								player.inventory.setCurrentItem(seedID, 0, false, false);
								PlayerTickHandler.waitTicks(7);
								KeyBinding.setKeyBindState(net.minecraft.client.settings.GameSettings.keyBindUseItem.keyCode, true);
								PlayerTickHandler.waitTicks(1);
								KeyBinding.setKeyBindState(net.minecraft.client.settings.GameSettings.keyBindUseItem.keyCode, false);
								nextZ++;
								moveState = true;
							}
						}
						else
						{
							player.rotationYaw = yawNorth;
							if((int)currentZ == rowEnd)
							{
								System.out.println("[FarmBot] Current X: " + (int)currentX);
								System.out.println("[FarmBot] Next X: " + (int)nextX);
								moveState = false;
								player.rotationYaw = yawEast;
								if(!hasChanged)
								{
									nextX = currentX + 1;
									System.out.println("[FarmBot] Next X: " + (int)nextX);
									System.out.println("[FarmBot] farmWideEnd: " + farmWideEnd);
									if((int)nextX == farmWideEnd)
									{
										runBot = false;
									}
									hasChanged = true;
								}
								moveState = true;
								if((int)currentX == (int)nextX)
								{
									harvestBack = true;
									hasChanged = false;
									nextZ = currentZ + 1;
								}
							}
							else if ((int)currentZ == (int)nextZ)
							{
								moveState = false;
								player.inventory.setCurrentItem(useID, 0, false, false);
								KeyBinding.setKeyBindState(net.minecraft.client.settings.GameSettings.keyBindUseItem.keyCode, true);
								PlayerTickHandler.waitTicks(1);
								KeyBinding.setKeyBindState(net.minecraft.client.settings.GameSettings.keyBindUseItem.keyCode, false);
								KeyBinding.setKeyBindState(net.minecraft.client.settings.GameSettings.keyBindAttack.keyCode, true);
								PlayerTickHandler.waitTicks(1);
								KeyBinding.setKeyBindState(net.minecraft.client.settings.GameSettings.keyBindAttack.keyCode, false);
								player.inventory.setCurrentItem(seedID, 0, false, false);
								PlayerTickHandler.waitTicks(7);
								KeyBinding.setKeyBindState(net.minecraft.client.settings.GameSettings.keyBindUseItem.keyCode, true);
								PlayerTickHandler.waitTicks(1);
								KeyBinding.setKeyBindState(net.minecraft.client.settings.GameSettings.keyBindUseItem.keyCode, false);
								nextZ--;
								moveState = true;
							}
						}
					}
					else if (runType == "other")
					{
						if(harvestBack)
						{
							player.rotationYaw = yawSouth;
							player.rotationPitch = 90;
							if (getBlockRelative(0, -2, 0, false) == 60)
							{
								if(getBlockRelative(0, -1, 0, false) == 59 && getBlockRelative(0,-1,0,true) == 7)
								{
									KeyBinding.setKeyBindState(net.minecraft.client.settings.GameSettings.keyBindAttack.keyCode, true);
									PlayerTickHandler.waitTicks(1);
									KeyBinding.setKeyBindState(net.minecraft.client.settings.GameSettings.keyBindAttack.keyCode, false);
								}
								else if(getBlockRelative(0, -1, 0, false) == 59 && getBlockRelative(0, -1, 0, true) != 7)
								{
									player.inventory.setCurrentItem(280, 0, false, false);
									KeyBinding.setKeyBindState(net.minecraft.client.settings.GameSettings.keyBindUseItem.keyCode, true);
									PlayerTickHandler.waitTicks(1);
									KeyBinding.setKeyBindState(net.minecraft.client.settings.GameSettings.keyBindUseItem.keyCode, false);
								}
								else if(getBlockRelative(0, -1, 0, false) == 0)
								{
									player.inventory.setCurrentItem(seedID, 0, false, false);
									KeyBinding.setKeyBindState(net.minecraft.client.settings.GameSettings.keyBindUseItem.keyCode, true);
									PlayerTickHandler.waitTicks(1);
									KeyBinding.setKeyBindState(net.minecraft.client.settings.GameSettings.keyBindUseItem.keyCode, false);
								}
							}
							if(getBlockRelative(0, -2, 1, false) == 60)
							{
								movePlayer(0, 1);
							}
							else if(getBlockRelative(0, -2, 1, false) == 5)
								runBot = false;
							else
							{
								if(!hasChanged)
								{
									nextX = player.posX;
									hasChanged = true;
								}
								if(((int)player.posX - 1) != (int)nextX)
								{
									movePlayer(3,1);
								}
								else
								{
									if(getBlockRelative(0, -2, 0, false) == 44)
										nextX = player.posX;
									else
									{
										harvestBack = false;
										hasChanged = false;
									}
								}
							}
						}
						else
						{
							player.rotationYaw = yawNorth;
							player.rotationPitch = 90;
							if (getBlockRelative(0, -2, 0, false) == 60)
							{
								if(getBlockRelative(0, -1, 0, false) == 59 && getBlockRelative(0,-1,0,true) == 7)
								{
									KeyBinding.setKeyBindState(net.minecraft.client.settings.GameSettings.keyBindAttack.keyCode, true);
									PlayerTickHandler.waitTicks(1);
									KeyBinding.setKeyBindState(net.minecraft.client.settings.GameSettings.keyBindAttack.keyCode, false);
								}
								else if(getBlockRelative(0, -1, 0, false) == 59 && getBlockRelative(0, -1, 0, true) != 7)
								{
									player.inventory.setCurrentItem(280, 0, false, false);
									KeyBinding.setKeyBindState(net.minecraft.client.settings.GameSettings.keyBindUseItem.keyCode, true);
									PlayerTickHandler.waitTicks(1);
									KeyBinding.setKeyBindState(net.minecraft.client.settings.GameSettings.keyBindUseItem.keyCode, false);
								}
								else if(getBlockRelative(0, -1, 0, false) == 0)
								{
									player.inventory.setCurrentItem(seedID, 0, false, false);
									KeyBinding.setKeyBindState(net.minecraft.client.settings.GameSettings.keyBindUseItem.keyCode, true);
									PlayerTickHandler.waitTicks(1);
									KeyBinding.setKeyBindState(net.minecraft.client.settings.GameSettings.keyBindUseItem.keyCode, false);
								}
							}
							if(getBlockRelative(0, -2, -1, false) == 60)
							{
									movePlayer(2, 1);
							}
							else if(getBlockRelative(0, -2, -1, false) == 5)
								runBot = false;
							else
							{
								if(!hasChanged)
								{
									nextX = player.posX;
									hasChanged = true;
								}
								if(getBlockRelative(1, -1, 0, false) == 53)
								{
									Robot bot = null;
									try {
										bot = new Robot();
									} catch (AWTException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									bot.keyPress(Keyboard.KEY_P);
									bot.delay(50);
									bot.keyRelease(Keyboard.KEY_P);
									PlayerTickHandler.waitTicks(200);
								}
								if(((int)player.posX - 1) != (int)nextX)
								{
									movePlayer(3,1);
								}
								else
								{
									if(getBlockRelative(0, -2, 0, false) == 44)
										nextX = player.posX;
									else
									{
										harvestBack = true;
										hasChanged = false;
									}
								}
							}
						}
					}
				}
				else
				{
					player.addChatMessage("§4Stopping FarmBot");
					moveState = false;
					attackState = false;
					useState = false;
					farmingTimer.cancel();
					farmingTimer.purge();
				}
			}
		}, 0, 10);
	}
}
