package farmBot;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class FarmBotGui extends GuiScreen
{
	public static final int GUI_ID = 20;
	private GuiButton StartButton;
	private GuiTextField FarmLength;
	private GuiTextField FarmWidth;
	
	public FarmBotGui () {}
	
	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);
		super.initGui();
		this.buttonList.add(new GuiButton(1, (this.width / 2) - 50, this.height - 30, 100, 20, "Start"));
		this.FarmLength = new GuiTextField(this.fontRenderer, (this.width / 2) - 150, this.height - 150, 100, 20);
		this.FarmWidth = new GuiTextField(this.fontRenderer, (this.width / 2) + 50, this.height - 150, 100, 20);
		this.FarmWidth.setTextColor(-1);
		this.FarmWidth.setDisabledTextColour(-1);
		this.FarmWidth.setEnableBackgroundDrawing(true);
		this.FarmWidth.setMaxStringLength(5);
		this.FarmLength.setTextColor(-1);
		this.FarmLength.setDisabledTextColour(-1);
		this.FarmLength.setEnableBackgroundDrawing(true);
		this.FarmLength.setMaxStringLength(5);
	}
	
	public void keyTyped(char c, int i)
	{
		super.keyTyped(c, i);
		if(FarmLength.isFocused())
			FarmLength.textboxKeyTyped(c, i);
		else if(FarmWidth.isFocused())
			FarmWidth.textboxKeyTyped(c, i);
	}
	
	public void mouseClicked(int i, int j, int k)
	{
		super.mouseClicked(i, j, k);
		FarmLength.mouseClicked(i, j, k);
		FarmWidth.mouseClicked(i, j, k);
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();
		this.FarmLength.drawTextBox();
		this.FarmWidth.drawTextBox();
		this.fontRenderer.drawString("Farm Length", (this.width / 2) - 135, this.height - 170, -1);
		this.fontRenderer.drawString("Farm Width", (this.width / 2) + 70, this.height - 170, -1);
		super.drawScreen(par1, par2, par3);
	}
	
	protected void actionPerformed (GuiButton guibutton)
	{
		switch(guibutton.id)
		{
			case 1:
			{
				if (FarmLength.getText().isEmpty() || FarmLength.getText().matches("[0-9]+") == false || FarmWidth.getText().isEmpty() || FarmWidth.getText().matches("[0-9]+") == false)
					break;
				else
				{
					System.out.println("[FarmBot] " + FarmLength.getText());
					FarmBot.FarmLength = Integer.parseInt(FarmLength.getText());
					FarmBot.FarmWidth = Integer.parseInt(FarmWidth.getText());
					FarmBot.runBot();
				}
			}
		}
	}
	
	public void onGuiClosed()
	{
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}
}
