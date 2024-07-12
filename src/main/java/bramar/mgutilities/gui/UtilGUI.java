package bramar.mgutilities.gui;

import java.awt.Color;
import java.io.IOException;

import bramar.mgutilities.MGUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;

public class UtilGUI extends GuiScreen {
	MGUtilities main;
	public UtilGUI(MGUtilities main) { this.main = main; }
	@Override
	public void initGui() {
		// Render everything
		super.initGui();
		GuiButton b1 = new GuiButton(831, 20, 50, 100, 20, "Solo Bedwars");
		GuiButton b2 = new GuiButton(832, 20, 80, 100, 20, "Doubles Bedwars");
		GuiButton b3 = new GuiButton(833, 20, 110, 100, 20, "Triples Bedwars");
		GuiButton b4 = new GuiButton(834, 20, 140, 100, 20, "Quads Bedwars");
		GuiButton s1 = new GuiButton(531, 150, 50, 100, 20, "Solo Skywars");
		GuiButton s2 = new GuiButton(532, 150, 80, 100, 20, "Teams Skywars");
		GuiButton ap = new GuiButton(33301, 270, 50, 100, 20, "Auto Party");
		GuiButton gl = new GuiButton(33302, 270, 80, 100, 20, "Get Level");
		GuiButton hub = new GuiButton(5301, 270, 110, 100, 20, "Go to Hub");
		GuiLabel title = newGuiLabel(fontRendererObj, 55501, width/2-33, 15, 40, 20, Color.WHITE, true, "Minigame Shortcut GUI");
		buttonList.add(b1);
		buttonList.add(b2);
		buttonList.add(b3);
		buttonList.add(b4);
		buttonList.add(s1);
		buttonList.add(s2);
		buttonList.add(ap);
		buttonList.add(gl);
		buttonList.add(hub);
		labelList.add(title);
	}
	// Readable function to create a GuiLabel (GuiLabel constructor is hard to understand)
	public GuiLabel newGuiLabel(FontRenderer fontRenderer, int id, int x, int y, int width, int height, Color c, boolean centered, String... text) {
//		int tColor = 0xFF000000 | (255 << 16) & 0x00FF0000 | (255 << 8) & 0x0000FF00 | 255 & 0xFF000000;
		GuiLabel label = new GuiLabel(fontRenderer, id, x, y, width, height, c.getRGB());
		if(centered) label.setCentered();
		if(text != null) for(String s : text) {
			label.func_175202_a(s);
		}
		return label;
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false; // Singleplayer
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		String command = null;
		EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
		if(button.id == 831) command = "/bedwars-1";
		if(button.id == 832) command = "/bedwars-2";
		if(button.id == 833) command = "/bedwars-3";
		if(button.id == 834) command = "/bedwars-4";
		if(button.id == 531) command = "/skywars-1";
		if(button.id == 532) command = "/skywars-2";
		if(button.id == 5301) command = "/hub";
		try {
			if(button.id == 33301) main.commands.get(2).processCommand(p, new String[0]);
			if(button.id == 33302) main.commands.get(0).processCommand(p, new String[0]);
		}catch(Exception ignored) {}
		
		if(command != null) p.sendChatMessage(command);
	}
}
