package bramar.mgutilities.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.lwjgl.input.Keyboard;

import bramar.mgutilities.MGUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class GetLevelCommand extends CommandBase {
	MGUtilities main;
	private KeyBinding key;
	private boolean pressedBefore = false;
	public GetLevelCommand(MGUtilities main, boolean autoRegister) {
		this.main = main;
		if(autoRegister) {
			ClientCommandHandler.instance.registerCommand(this);
			this.key = new KeyBinding("Get Level Shortcut", Keyboard.KEY_G, MGUtilities.category);
			ClientRegistry.registerKeyBinding(key);
			MinecraftForge.EVENT_BUS.register(this);
		}
	}
	@SubscribeEvent
	public void keyShortcut(ClientTickEvent e) {
		if(e.phase == Phase.END && key.isKeyDown() && Minecraft.getMinecraft().thePlayer != null && !pressedBefore) {
			pressedBefore = true;
			noArgs(Minecraft.getMinecraft().thePlayer);
		}else if(pressedBefore) pressedBefore = false;
	}
	
	@Override
	public String getCommandName() {
		return "gl";
	}
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "Usage: /gl [player_name]: Gets a player's or everyone in the lobby's Jartex Level.";
	}
	public void noArgs(ICommandSender sender) {
		try {
			List<IChatComponent> comps = new ArrayList<IChatComponent>();
			Collection<NetworkPlayerInfo> map = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();
			for(NetworkPlayerInfo info : map) {
				try {
					String playerName = info.getGameProfile().getName();
					processCommand(sender, new String[] {playerName});
				}catch(Exception e1) {
					try {
						String playerName = info.getDisplayName().getUnformattedText();
						processCommand(sender, new String[] {playerName});
					}catch(Exception ignored) {}
				}
			}
		}catch(Exception e1) {
			sendChat(sender, "ERROR: " + e1.getClass().getSimpleName() + ": " + e1.getMessage(), EnumChatFormatting.RED);
		}
	}
	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if(args == null) noArgs(sender);
		else if(args.length == 0) {
			noArgs(sender);
		}else {
			try {
				String playerName = args[0];
				int lvl = main.getLevel(playerName); // Avoid NullPointerException by using the wrapped class
				if(lvl < 0) {
					IChatComponent c1 = new ChatComponentText(playerName + ": ");
					ChatStyle style = new ChatStyle();
					style.setColor(EnumChatFormatting.GRAY);
					c1 = c1.setChatStyle(style);
					IChatComponent c2 = new ChatComponentText("Unknown");
					style = new ChatStyle();
					style.setColor(EnumChatFormatting.RED);
					IChatComponent c = c1.appendSibling(c2);
					sender.addChatMessage(c);
					
				}
				else {
					IChatComponent c1 = new ChatComponentText(playerName + ": ");
					ChatStyle style = new ChatStyle();
					style.setColor(EnumChatFormatting.AQUA);
					c1 = c1.setChatStyle(style);
					IChatComponent c2 = new ChatComponentText(main.getLevel(playerName) + "");
					style = new ChatStyle();
					style.setColor(EnumChatFormatting.GREEN);
					IChatComponent c = c1.appendSibling(c2);
					sender.addChatMessage(c);
				}
			}catch(Exception e1) {
				sendChat(sender, "ERROR: " + e1.getClass().getSimpleName() + ": " + e1.getMessage(), EnumChatFormatting.RED);
			}
		}
	}
	public void sendChat(ICommandSender sender, String chat, EnumChatFormatting... optionalColor) {
		IChatComponent comp = new ChatComponentText(chat);
		if(optionalColor != null) if(optionalColor.length != 0) comp.setChatStyle(new ChatStyle().setColor(optionalColor[0]));
		sender.addChatMessage(comp);
	}
	@Override
	public List<String> getCommandAliases() {
		return Arrays.asList("getlevel", "getlevels");
	}
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}
}
