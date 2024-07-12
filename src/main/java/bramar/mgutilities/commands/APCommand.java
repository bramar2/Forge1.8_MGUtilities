package bramar.mgutilities.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.naming.NameNotFoundException;

import org.lwjgl.input.Keyboard;

import bramar.mgutilities.MGUtilities;
import net.minecraft.client.Minecraft;
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

import static bramar.mgutilities.MGUtilities.category;

public class APCommand extends CommandBase {
	MGUtilities main;
	KeyBinding key;
	boolean isPressed = false;
	public APCommand(MGUtilities main, boolean autoRegister) {
		this.main = main;
		if(autoRegister) {
			ClientCommandHandler.instance.registerCommand(this);
			MinecraftForge.EVENT_BUS.register(this);
			key = new KeyBinding("Auto-parties the first entry", Keyboard.KEY_P, category);
			ClientRegistry.registerKeyBinding(key);
		}
	}
	public <T> String toString(T[] array) {
		if(array == null) return "";
		else if(array.length == 0) return "";
		String s = "";
		for(int i = 0; i < array.length; i++) {
			s += array[i] + " ";
		}
		return s.substring(0, s.length() - 1);
	}
	@SubscribeEvent
	public void onTick(ClientTickEvent e) {
		if(e.phase == Phase.END && Minecraft.getMinecraft() != null) {
    		if(key.isKeyDown() && !isPressed && Minecraft.getMinecraft().thePlayer != null) {
    			// Do stuff
    			try {
					processCommand(Minecraft.getMinecraft().thePlayer, new String[] {});
				}catch (CommandException e1) {
					sendChat(Minecraft.getMinecraft().thePlayer, "CommandException error occured: " + e1.getMessage() + ", ErrorObjects: " + (e1.getErrorObjects() == null ? "None" : toString(e1.getErrorObjects())), EnumChatFormatting.RED);
				}
    			//
    			isPressed = true;
    		}
    		if(!key.isKeyDown() && isPressed) isPressed = false;
    	}
	}
	@Override
	public String getCommandName() {
		return "ap";
	}
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "Usage: /ap [int] [player]: If no argument is put, it automatically party invites to the first stored Player name. If int argument is put, it automatically parties the stored player name with that int. If the player argument is also put, it sets the Int value to that player.";
	}
	public void noArgs(ICommandSender sender) {
		try {
			if(main.autoParty.size() == 0) throw new NameNotFoundException("There is no player name in the database!");
			String p = "";
			for(Map.Entry<Integer, String> entry : main.autoParty.entrySet()) {
				p = entry.getValue();
				break;
			}
			Minecraft.getMinecraft().thePlayer.sendChatMessage("/party invite " + p);
		}catch(Exception e1) {
			sendChat(sender, "ERROR: " + e1.getClass().getName() + ": " + e1.getMessage(), EnumChatFormatting.RED);
		}
	}
	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if(args == null) noArgs(sender);
		else if(args.length == 0) noArgs(sender);
		else if(args.length == 1) {
			try {
				String p = main.autoParty.getOrDefault(Integer.parseInt(args[0]), null);
				if(p == null) throw new NameNotFoundException("No player name is set with this int!");
				Minecraft.getMinecraft().thePlayer.sendChatMessage("/party invite " + p);
			}catch(Exception e1) {
				sendChat(sender, "ERROR: " + e1.getClass().getName() + ": " + e1.getMessage(), EnumChatFormatting.RED);
			}
		}else {
			try {
				int n = Integer.parseInt(args[0]);
				String p = args[1];
				
				main.autoParty.remove(n);
				main.autoParty.put(n, p);
				main.saveAP();
				sendChat(sender, "Successfully set int " + n + " to value " + p, EnumChatFormatting.GREEN);
			}catch(Exception e1) {
				sendChat(sender, "ERROR: " + e1.getClass().getName() + ": " + e1.getMessage(), EnumChatFormatting.RED);
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
		return Arrays.asList("autoparty");
	}
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}
}
