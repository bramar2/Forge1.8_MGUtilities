package bramar.mgutilities;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class JartexLevelHead {
	private MGUtilities main;
	private Minecraft mc = Minecraft.getMinecraft();
	JartexLevelHead(MGUtilities main) {
		this.main = main;
		MinecraftForge.EVENT_BUS.register(this);
	}
	public String removeChatColor(String str) {
		String output = str;
		for(EnumChatFormatting f : EnumChatFormatting.values())
			output = output.replace(f.toString(), "");
		return output;
	}
	// 70% of code from LevelHead
	@SubscribeEvent
	public void renderEntity(RenderLivingEvent.Specials.Pre<EntityLivingBase> e) {
		if(!(e.entity instanceof EntityPlayer) || !main.jartexLevelHead) return;
		EntityPlayer p = (EntityPlayer) e.entity;
		if(p.getDistanceToEntity(mc.thePlayer) < 4096.0d) {
			double offset = 0.3d;
			Scoreboard scoreboard = p.getWorldScoreboard();
			ScoreObjective score = scoreboard.getObjectiveInDisplaySlot(2);
			if(score != null && p.getDistanceToEntity(mc.thePlayer) < 100.0d) offset *= 2d;
			if(p.getUniqueID().equals(mc.thePlayer.getUniqueID())) offset = 0.0d;
			renderName(p, e.x, e.y + offset, e.z);
		}
	}
	public void renderName(EntityPlayer p, double x, double y, double z) {
		FontRenderer font = mc.fontRendererObj;
		float textScale = 0.016666668f * (float) 1.600000023841858D;
		GlStateManager.pushMatrix();
		int xMultiplier = 1;
		if(mc.gameSettings != null && mc.gameSettings.thirdPersonView == 2) xMultiplier = -1;
		GlStateManager.translate((float) x + 0.0f, (float) y + p.height + 0.5f, (float) z);
		GL11.glNormal3f(0.0f, 1.0f, 0.0f);
		RenderManager render = mc.getRenderManager();
		GlStateManager.rotate(-render.playerViewY, 0.0f, 1.0f, 0.0f);
		GlStateManager.rotate(render.playerViewX * xMultiplier, 1.0f, 0.0f, 0.0f);
		GlStateManager.scale(-textScale, -textScale, textScale);
		GlStateManager.disableLighting();
		GlStateManager.depthMask(false);
		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldRender = tessellator.getWorldRenderer();
		
	}
	
	
	public ChatStyle getColor(int lvl) {
		ChatStyle style = new ChatStyle();
		if(lvl >= 75) {
			style.setBold(true);
			if(lvl >= 100) style.setColor(EnumChatFormatting.GOLD);
			else style.setColor(EnumChatFormatting.YELLOW);
			style.setBold(true);
		}else if(lvl >= 60) style.setColor(EnumChatFormatting.DARK_AQUA);
		else if(lvl >= 50) style.setColor(EnumChatFormatting.DARK_RED);
		else if(lvl >= 45) style.setColor(EnumChatFormatting.DARK_GREEN);
		else if(lvl >= 40) style.setColor(EnumChatFormatting.GOLD);
		else if(lvl >= 35) style.setColor(EnumChatFormatting.RED);
		else if(lvl >= 30) style.setColor(EnumChatFormatting.BLUE);
		else if(lvl >= 25) style.setColor(EnumChatFormatting.LIGHT_PURPLE);
		else if(lvl >= 20) style.setColor(EnumChatFormatting.YELLOW);
		else if(lvl >= 15) style.setColor(EnumChatFormatting.GREEN);
		else if(lvl >= 10) style.setColor(EnumChatFormatting.AQUA);
		else if(lvl >= 5) style.setColor(EnumChatFormatting.WHITE);
		else style.setColor(EnumChatFormatting.GRAY);
		
		return style;
	}
}
