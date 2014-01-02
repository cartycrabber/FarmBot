package farmBot;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.EnumSet;



//import sobiohazardous.crazyfoods.TutorialKeyBind;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
public class PlayerTickHandler implements ITickHandler
{
	private final EnumSet<TickType> ticksToGet;
	static int ticksPassed;
	static boolean waiting;
	static boolean attackPressed;
	static boolean usePressed;
	
	public PlayerTickHandler(EnumSet<TickType> ticksToGet)
	{
	         this.ticksToGet = ticksToGet;
	}
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{

	}
	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
		playerTick((EntityPlayer)tickData[0]);
//		System.out.println(ticksPassed);
	}
	@Override
	public EnumSet<TickType> ticks()
	{
		return ticksToGet;
	}
	@Override
	public String getLabel()
	{
		return "FarmBotTick";
	}
	
	public static void playerTick(EntityPlayer player)
	{
		ticksPassed++;
		Robot bot = null;
		try
		{
			bot = new Robot();
		}
		catch (AWTException e)
		{
			e.printStackTrace();
		}
		if(FarmBot.moveState)
			player.moveEntityWithHeading(0, 1);
		if(FarmBot.attackState)
		{
        	 bot.mousePress(InputEvent.BUTTON1_MASK);
		}
		else
		{
			if(attackPressed)
			{
	        	 bot.mouseRelease(InputEvent.BUTTON1_MASK);
	        	 attackPressed = false;
			}
		}
		if(FarmBot.useState)
		{
			bot.mousePress(InputEvent.BUTTON3_MASK);
	    }
		else
		{
			if(usePressed)
			{
		        bot.mouseRelease(InputEvent.BUTTON3_MASK);
		        usePressed = false;
			}
		}
	}
}