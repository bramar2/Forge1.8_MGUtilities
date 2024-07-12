package bramar.mgutilities.commands;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;

import bramar.mgutilities.MGUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class RenameCommand extends CommandBase {
	private MGUtilities main;
	public boolean enabled = false;
	public HashMap<String, String> nickNames = Maps. <String, String> newHashMap();
	public static final String COLOR_CHAR = "&";
	public RenameCommand(MGUtilities main, boolean autoRegister) {
		this.main = main;
		if(autoRegister) {
			ClientCommandHandler.instance.registerCommand(this);
			MinecraftForge.EVENT_BUS.register(this);
		}
	}
	/**
     * Gets the IChatComponent object from the String with COLOR_CHAR as Colors
     * (Using ChatStyle)
     * @param s The string that you want to convert to IChatComponent (COLOR_CHAR for color)
     * @return The chat as IChatComponent with the correct colors
     */
    public IChatComponent getChatWithColorCode(String s) {
    	if(main.debugMode) System.out.println("DEBUG: getChatWithColorCode('" + s + "')");
    	List<IChatComponent> siblings = new ArrayList<IChatComponent>();
    	ChatColor last = null;
    	String[] betweenChar = s.split(COLOR_CHAR);
    	boolean first = true;
    	for(int a = 0; a < betweenChar.length; a++) {
    		String str = betweenChar[a];
    		if(first) {
    			IChatComponent comp = new ChatComponentText(str);
    			if(main.debugMode) System.out.println("DEBUG: Sibling.add(Text:'" + comp.getUnformattedText() + "',Color:'" + comp.getChatStyle().getColor() + "',BOLD:" + comp.getChatStyle().getBold() + ",ITALIC:" + comp.getChatStyle().getItalic() + ",STRIKETHROUGH:" + comp.getChatStyle().getStrikethrough() + ",MAGIC:" + comp.getChatStyle().getObfuscated());
    			siblings.add(comp);
    			first = false;
    			continue;
    		}
    		if(main.debugMode) System.out.println("DEBUG: betweenChar{'" + str + "'}");
    		if(main.debugMode) System.out.println("DEBUG: SiblingSize=" + siblings.size());
    		if(main.debugMode) System.out.println("DEBUG: LastChatColor=" + (last == null ? "NULL" : last.getName()));
    		if(str.length() == 0) {
    			if(main.debugMode) System.out.println("DEBUG: str.length() == 0");
    			if(last != null) System.out.println("DEBUG: LastChatColor != null is TRUE");
    			else System.out.println("DEBUG: LastChatColor is NULL");
    			IChatComponent comp = new ChatComponentText("&");
    			if(last != null) comp.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.valueOf(last.getName())));
    			if(main.debugMode) System.out.println("DEBUG: Sibling.add(Text:'" + comp.getUnformattedText() + "',Color:'" + comp.getChatStyle().getColor() + "',BOLD:" + comp.getChatStyle().getBold() + ",ITALIC:" + comp.getChatStyle().getItalic() + ",STRIKETHROUGH:" + comp.getChatStyle().getStrikethrough() + ",MAGIC:" + comp.getChatStyle().getObfuscated());
    			siblings.add(comp);
    		}else if(str.length() == 1) {
    			if(main.debugMode) System.out.println("DEBUG: str.length() == 1");
    			IChatComponent comp = null;
    			if(ChatColor.isColorChar(str.charAt(0))) {
    				if(a == betweenChar.length - 1 && str.equalsIgnoreCase(Character.toString(Minecraft.getMinecraft().thePlayer.getName().charAt(0)))) {
        				if(main.debugMode) System.out.println("DEBUG: This is the last string && String is == to '&" + Minecraft.getMinecraft().thePlayer.getName().charAt(0) + "'. Aborting! This is to prevent a bug.");
        				continue;
        			}
    				last = new ChatColor(str.charAt(0));
    				if(main.debugMode) System.out.println("DEBUG: Color Char='" + str.charAt(0) + "'");
    			}
    			else {
    				if(a == betweenChar.length - 1 && str.equalsIgnoreCase(Character.toString(Minecraft.getMinecraft().thePlayer.getName().charAt(0)))) {
        				if(main.debugMode) System.out.println("DEBUG: This is the last string && String is == to '&" + Minecraft.getMinecraft().thePlayer.getName().charAt(0) + "'. Aborting! This is to prevent a bug.");
        				continue;
        			}
    				if(main.debugMode) System.out.println("DEBUG: Not a color char.");
    				comp = new ChatComponentText("&" + str);
    				if(last != null) comp.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.valueOf(last.getName())));
    				if(main.debugMode) System.out.println("DEBUG: Sibling.add(Text:'" + comp.getUnformattedText() + "',Color:'" + comp.getChatStyle().getColor() + "',BOLD:" + comp.getChatStyle().getBold() + ",ITALIC:" + comp.getChatStyle().getItalic() + ",STRIKETHROUGH:" + comp.getChatStyle().getStrikethrough() + ",MAGIC:" + comp.getChatStyle().getObfuscated());
    				siblings.add(comp);
    			}
    		}else {
    			if(main.debugMode) System.out.println("DEBUG: str.length() == " + str.length());
    			if(!ChatColor.isColorChar(str.charAt(0))) {
    				if(main.debugMode) System.out.println("DEBUG: ChatColor.isColorChar(str.charAt(0) is FALSE");
    				IChatComponent comp = new ChatComponentText("&" + str);
    				if(last != null) comp.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.valueOf(last.getName())));
    				if(main.debugMode) System.out.println("DEBUG: Sibling.add(Text:'" + comp.getUnformattedText() + "',Color:'" + comp.getChatStyle().getColor() + "',BOLD:" + comp.getChatStyle().getBold() + ",ITALIC:" + comp.getChatStyle().getItalic() + ",STRIKETHROUGH:" + comp.getChatStyle().getStrikethrough() + ",MAGIC:" + comp.getChatStyle().getObfuscated());
    				siblings.add(comp);
    				continue;
    			}
    			EnumChatFormatting lastInEnum = EnumChatFormatting.valueOf(new ChatColor(str.charAt(0)).getName());
    			if(main.debugMode) System.out.println("DEBUG: lastInEnum=" + lastInEnum.toString());
    			boolean isSpecial = false;
    			switch(lastInEnum) {
    			case BOLD:
    				isSpecial = true;
    			case OBFUSCATED:
    				isSpecial = true;
    			case RESET:
    				isSpecial = true;
    			case UNDERLINE:
    				isSpecial = true;
    			case STRIKETHROUGH:
    				isSpecial = true;
				default:
					break;
    			}
    			IChatComponent comp = null;
    			if(isSpecial) {
    				if(main.debugMode) System.out.println("DEBUG: isSpecial == TRUE");
    				comp = new ChatComponentText(str.substring(1));
    				ChatStyle style = new ChatStyle();
    				boolean reset = false;
    				if(lastInEnum == EnumChatFormatting.BOLD) {
    					style.setBold(true);
    				}else if(lastInEnum == EnumChatFormatting.ITALIC) {
    					style.setItalic(true);
    				}else if(lastInEnum == EnumChatFormatting.STRIKETHROUGH) {
    					style.setStrikethrough(true);
    				}else if(lastInEnum == EnumChatFormatting.OBFUSCATED) {
    					style.setObfuscated(true);
    				}else if(lastInEnum == EnumChatFormatting.UNDERLINE) {
    					style.setUnderlined(true);
    				}else {
    					// Reset
    					reset = true;
    				}
    				if(last != null) style.setColor(EnumChatFormatting.valueOf(last.getName()));
    				if(reset) style.setColor(EnumChatFormatting.RESET);
    				comp.setChatStyle(style);
    			}else {
    				if(main.debugMode) System.out.println("DEBUG: isSpecial == FALSE");
    				comp = new ChatComponentText(str.substring(1));
    				last = new ChatColor(str.charAt(0));
    				comp.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.valueOf(last.getName())));
    			}
    			if(main.debugMode) System.out.println("DEBUG: Sibling.add(Text:'" + comp.getUnformattedText() + "',Color:'" + comp.getChatStyle().getColor() + "',BOLD:" + comp.getChatStyle().getBold() + ",ITALIC:" + comp.getChatStyle().getItalic() + ",STRIKETHROUGH:" + comp.getChatStyle().getStrikethrough() + ",MAGIC:" + comp.getChatStyle().getObfuscated());
    			siblings.add(comp);
    		}
    	}
    	if(main.debugMode) System.out.println("DEBUG: List has been put together!");
    	IChatComponent full = new ChatComponentText("");
    	for(int i = 0; i < siblings.size(); i++) {
    		IChatComponent sibling = siblings.get(i);
    		if(i == siblings.size() - 1 && sibling.getUnformattedText().equalsIgnoreCase("&" + Character.toString(Minecraft.getMinecraft().thePlayer.getName().charAt(0)))) continue;
    		if(sibling.getUnformattedText().contains("&" + Character.toString(Minecraft.getMinecraft().thePlayer.getName().charAt(0)))) {
    			ChatStyle style = sibling.getChatStyle();
    			sibling = new ChatComponentText(sibling.getUnformattedText().replace("&" + Character.toString(Minecraft.getMinecraft().thePlayer.getName().charAt(0)), ""));
    			sibling.setChatStyle(style);
    		}
    		if(sibling.getUnformattedText().contains("&y")) {
    			ChatStyle style = sibling.getChatStyle();
    			sibling = new ChatComponentText(sibling.getUnformattedText().replace("&y", ""));
    			sibling.setChatStyle(style);
    		}
    		full.appendSibling(sibling);
    	}
    	return full;
    }
	@SubscribeEvent
	public void tick(ClientTickEvent e) {
		if(e.phase == Phase.END && enabled) {
			try {
				Field field = null;
				try {
					field = NetHandlerPlayClient.class.getDeclaredField("field_147310_i");
				}catch(Exception e1) {
					try {
						field = NetHandlerPlayClient.class.getDeclaredField("playerInfoMap");
					}catch(Exception ignored) {}
				}
				field.setAccessible(true);
				NetHandlerPlayClient net = Minecraft.getMinecraft().getNetHandler();
				Map<UUID, NetworkPlayerInfo> map = (Map<UUID, NetworkPlayerInfo>) field.get(net);
				if(map.size() == 0) return;
				Map<UUID, NetworkPlayerInfo> modifications = Maps. <UUID, NetworkPlayerInfo> newHashMap();
				for(Map.Entry<UUID, NetworkPlayerInfo> entry : map.entrySet()) {
					NetworkPlayerInfo info = entry.getValue();
					if(main.containsIgnoreCase(nickNames, info.getGameProfile().getName())) {
						info.setDisplayName(getChatWithColorCode(main.getIgnoreCase(nickNames, info.getGameProfile().getName())));
						modifications.put(entry.getKey(), info);
					}
				}
				for(Map.Entry<UUID, NetworkPlayerInfo> entry : modifications.entrySet()) {
					map.remove(entry.getKey());
					map.put(entry.getKey(), entry.getValue());
				}
				field.set(net, map);
			}catch(Exception ignored) {}
		}
	}
	@Override
	public String getCommandName() {
		return "rename";
	}
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "Usage: /rename <player> <nickname>: Renames that player name (Original, not nick) and replaces it with the nick. Use 'reset' for nickname to reset them\n"
				+ "Usage: /rename <on/off>: This is to toggle whether this feature is enabled";
	}
	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if(args == null) sendChat(sender, this.getCommandUsage(sender), EnumChatFormatting.RED);
		else if(args.length == 0) sendChat(sender, this.getCommandUsage(sender), EnumChatFormatting.RED);
		else if(args.length < 2) {
			if(args[0].equalsIgnoreCase("off")) {
				enabled = false;
				sendChat(sender, "Successfully turned off the feature!", EnumChatFormatting.GREEN);
			}else if(args[0].equalsIgnoreCase("on")) {
				enabled = true;
				sendChat(sender, "Successfully turned on the feature!", EnumChatFormatting.GREEN);
			}else sendChat(sender, this.getCommandUsage(sender), EnumChatFormatting.RED);
		}else {
			String name = args[0];
			String nick = "";
			for(int i = 1; i < args.length; i++) {
				nick += args[i] + " ";
			}
			nick = nick.substring(0, nick.length() - 1);
			if(nick.equalsIgnoreCase("reset")) {
				if(main.containsIgnoreCase(nickNames, name)) {
					nickNames = main.removeIgnoreCase(nickNames, name);
				}else sendChat(sender, "That player is not in the nicknames!", EnumChatFormatting.RED);
				return;
			}
			if(main.containsIgnoreCase(nickNames, name)) nickNames = main.removeIgnoreCase(nickNames, name);
			nickNames.put(name, nick);
			sendChat(sender, "Successfully set '" + name + "' nickname to '" + nick + "'", EnumChatFormatting.GREEN);
		}
	}
	public void sendChat(ICommandSender sender, String chat, EnumChatFormatting... optionalColor) {
		IChatComponent comp = new ChatComponentText(chat);
		if(optionalColor != null) if(optionalColor.length != 0) comp.setChatStyle(new ChatStyle().setColor(optionalColor[0]));
		sender.addChatMessage(comp);
	}
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}
	protected static class ChatColor {
    	public static final ChatColor BLACK = new ChatColor('0');
    	public static final ChatColor DARK_BLUE = new ChatColor('1');
    	public static final ChatColor DARK_GREEN = new ChatColor('2');
    	public static final ChatColor DARK_AQUA = new ChatColor('3');
    	public static final ChatColor DARK_RED = new ChatColor('4');
    	public static final ChatColor DARK_PURPLE = new ChatColor('5');
    	public static final ChatColor GOLD = new ChatColor('6');
    	public static final ChatColor GRAY = new ChatColor('7');
    	public static final ChatColor DARK_GRAY = new ChatColor('8');
    	public static final ChatColor BLUE = new ChatColor('9');
    	public static final ChatColor GREEN = new ChatColor('a');
    	public static final ChatColor AQUA = new ChatColor('b');
    	public static final ChatColor RED = new ChatColor('c');
    	public static final ChatColor LIGHT_PURPLE = new ChatColor('d');
    	public static final ChatColor YELLOW = new ChatColor('e');
    	public static final ChatColor WHITE = new ChatColor('f');
    	
    	public static final ChatColor OBFUSCATED = new ChatColor('k');
    	public static final ChatColor BOLD = new ChatColor('l');
    	public static final ChatColor STRIKETHROUGH = new ChatColor('m');
    	public static final ChatColor UNDERLINE = new ChatColor('n');
    	public static final ChatColor ITALIC = new ChatColor('o');
    	public static final ChatColor RESET = new ChatColor('r');
    	
    	private static final char[] colorChars = "0123456789abcdefklmnor".toCharArray();
    	
    	public static boolean isColorChar(char c) {
    		for(char chars : colorChars) {
    			if(c == chars) return true;
    		}
    		return false;
    	}
    	
    	public String getName() {
    		try {
    			for(Field f : ChatColor.class.getDeclaredFields()) {
    				if(Modifier.isStatic(f.getModifiers()) && Modifier.isPublic(f.getModifiers()) && Modifier.isFinal(f.getModifiers())) {
    					ChatColor c = (ChatColor) f.get(null);
    					if(c.c == this.c) return f.getName();
    				}
    			}
    		}catch(Exception ignored) {}
    		return null;
    	}
    	
    	public boolean equals(ChatColor c) {
    		return this.c == c.c;
    	}
    	
    	private String toString;
    	private char c;
    	public ChatColor(char c) {
    		toString = RenameCommand.COLOR_CHAR + c;
    		this.c = c;
    	}
    	@Override
    	public String toString() {
    		return toString;
    	}
    	@Override
    	protected ChatColor clone() {
    		return new ChatColor(c);
    	}
    }
}
