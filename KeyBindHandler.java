package farmBot;

import ibxm.Player;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.EnumSet;
import java.util.Iterator;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
class KeyBindHandler extends KeyHandler
{
     private EnumSet tickTypes = EnumSet.of(TickType.CLIENT);
     public static boolean keyPressed = false;
    
     public KeyBindHandler(KeyBinding[] keyBindings, boolean[] repeatings)
     {
             super(keyBindings, repeatings);
     }
     @Override
     public String getLabel()
     {
             return "FarmBotGUI";
     }
     @Override
     public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat)
     {
    	 keyPressed = true;
    	 if(tickEnd)
    	 {
    		 if (kb == FarmBot.botStart)
    		 {
	        	 System.out.println("[FarmBot] Key Pressed!");
	        	 EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
	        	 FarmBot.storeAllItems(296);
//	        	 player.inventory.setCurrentItem(295, 0, false, false);
//	        	 FarmBot.runBot();
//	        	 System.out.println("[FarmBot] Block Beneath: " + FarmBot.getBlockRelative(0, -2, 0));
//	        	 player.openGui(FarmBot.instance, 0, player.getEntityWorld(), 0, 0, 0);
    		 }
    		 else if (kb == FarmBot.botStop)
    		 {
    			 FarmBot.runBot = false;
    		 }
    	 }
     }
     @Override
     public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd)
     {
    	 keyPressed = false;
     }
     @Override
     public EnumSet<TickType> ticks()
     {
             return EnumSet.of(TickType.CLIENT);
     }
}