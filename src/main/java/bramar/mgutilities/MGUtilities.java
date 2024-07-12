package bramar.mgutilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Maps;

import bramar.mgutilities.commands.APCommand;
import bramar.mgutilities.commands.GetLevelCommand;
import bramar.mgutilities.commands.RenameCommand;
import bramar.mgutilities.events.DeathEventListener;
import bramar.mgutilities.gui.ConfigFactory;
import bramar.mgutilities.gui.UtilGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@Mod(modid = MGUtilities.MODID, version = MGUtilities.VERSION, guiFactory = "bramar.mgutilities.gui.ConfigFactory")
public class MGUtilities {
    public static final String MODID = "mgutilities";
    public static final String VERSION = "1.0";
    public static final String LPATH = "config" + File.separator + "jartex_levels.db";
    public HashMap<String, Integer> levelData = Maps. <String , Integer> newHashMap();
    public HashMap<Integer, String> autoParty = Maps. <Integer , String> newHashMap();
    public File levels;
    public File ap;
    public Configuration config;
    private File configFile;
    public static final String category = "Minigame Utilities";
    public final boolean debugMode = false;
    public static MGUtilities instance;
    public static KeyBinding gui;
    public <K> K getIgnoreCase(HashMap<String, K> map, final String stringToSearch) {
    	for(Map.Entry<String, K> entry : map.entrySet()) {
    		if(entry.getKey().equalsIgnoreCase(stringToSearch)) return entry.getValue();
    	}
    	return null;
    }
    public <K> HashMap<String, K> removeIgnoreCase(final HashMap<String, K> map, final String str) {
    	for(Map.Entry<String, K> s : ((HashMap<String, K>) map.clone()).entrySet()) {
    		if(s.getKey().equalsIgnoreCase(str)) map.remove(str);
    	}
    	return map;
    }
    public <K> boolean containsIgnoreCase(HashMap<String, K> map, final String stringToSearch) {
    	for(Map.Entry<String, K> entry : map.entrySet()) {
    		if(entry.getKey().equalsIgnoreCase(stringToSearch)) return true;
    	}
    	return false;
    }
    public <K> boolean containsIgnoreCase2(HashMap<K, String> map, final String stringToSearch) {
    	for(Map.Entry<K, String> entry : map.entrySet()) {
    		if(entry.getValue().equalsIgnoreCase(stringToSearch)) return true;
    	}
    	return false;
    }
    
    public int getLevel(final String s) {
    	for(Map.Entry<String, Integer> entry : levelData.entrySet()) {
    		if(entry.getKey().equalsIgnoreCase(s)) return entry.getValue();
    	}
    	return -1;
    }
    
    public List<CommandBase> commands = new ArrayList<CommandBase>();
    public void loadConfig() throws IOException {
    	configFile = new File("config" + File.separator + "minigame_utilities.cfg");
		config = new Configuration(configFile);
		if(!configFile.exists()) configFile.createNewFile();
		config.load();
		config.save();
		
		// Elements
		loadConfigElements();
    }
    protected static boolean displayPing = false;
    protected static boolean jartexLevelHead = false;
    public void loadConfigElements() {
    	config = new Configuration(configFile);
    	config.load();
    	try {
    		bloodParticle = config.get("general", "SPBloodParticle", false, "Whether or not the mod shows blood particles on singleplayer").getBoolean();
    		displayPing = config.get("general", "DisplayPing", false, "Whether or not the mod shows ping on the tablist. May lag on players with more than 100 players").getBoolean();
    		autoSprint = config.get("general", "AutoSprint", false, "Whether or not the mod automatically makes the player sprint").getBoolean();
    		jartexLevelHead = config.get("general", "JartexLevelHead", false, "Whether or not the mod shows jartex level above people's head (needs to relog)").getBoolean();
    		bloodParticleMP = config.get("general", "MPBloodParticle", false, "Whether or not the mod shows blood particles on multiplayer").getBoolean();
    		config.save();
    	}catch(Exception e1) {
    		e1.printStackTrace();
    	}
    }
    public static boolean bloodParticle = false;
    public static boolean bloodParticleMP = false;
    
    public static void main(String[] args) {
    	int a = 1;
		int b = 1;
		System.out.println(++a);
		System.out.println(b++);
		System.out.println(a + " " + b);
	}
    
    private Minecraft mc = Minecraft.getMinecraft();
    private static boolean autoSprint = false;
    private boolean wPressed = false;
    private int n = 0;
    
    @SubscribeEvent
    public void onClientTick(ClientTickEvent e) {
    	if(e.phase == Phase.START && mc.thePlayer != null && autoSprint && !wPressed) {
    		if((wPressed = mc.gameSettings.keyBindForward.isKeyDown())) KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
    	}
    	if(wPressed) {
    		if(mc.thePlayer == null) {
    			wPressed = false;
    			n = 0;
    			KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
    		}else {
    			if(n == 3) {
    				wPressed = false;
    				n = 0;
    				KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
    			}else n++;
    		}
    	}
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	try {
    		instance = this;
    		loadConfig();
    		levels = new File(LPATH);
    		ap = new File("config" + File.separator + "autoparty.db");
        	loadLevel();
        	loadAP();
        	gui = new KeyBinding("Open GUI", Keyboard.KEY_M, category);
        	ClientRegistry.registerKeyBinding(gui);
        	MinecraftForge.EVENT_BUS.register(this);
        	commands.add(new GetLevelCommand(this, true));
        	commands.add(new APCommand(this, true));
        	commands.add(new RenameCommand(this, true));
        	commands.add(new MGHelp(true));
        	commands.add(new PingDisplay());
//        	new JartexLevelHead(this);
        	new DeathEventListener(true);
        	new BloodParticle(this);
        	System.out.println("MinigameUtilities successfully initialized!");
    	}catch(Exception e1) {
    		e1.printStackTrace();
    		System.out.println("WARNING: MinigameUtilities has failed to register! Things may break!");
    	}
    }
    private class MGHelp extends CommandBase {
    	
    	private MGHelp(boolean autoRegister) {
    		if(autoRegister) ClientCommandHandler.instance.registerCommand(this);
    	}
    	
		public String getCommandName() {
			return "mghelp";
		}
		public String getCommandUsage(ICommandSender sender) {
			return "Usage: /mghelp <command>: Sends usage of that command [Minigame-Utilities only].";
		}
		private boolean added = false;
		public void processCommand(ICommandSender sender, String[] args) throws CommandException {
			if(args.length == 0) {sendChat(sender, getCommandUsage(null), EnumChatFormatting.RED);
			return;}
			for(CommandBase cmd : commands) {
				String commandName = cmd.getCommandName();
				List<String> commandAliases = cmd.getCommandAliases();
				boolean b1 = commandName.equalsIgnoreCase(args[0]) || (commandName.startsWith("/") ? commandName.substring(1).equalsIgnoreCase(args[0]) : false);
				boolean b2 = false;
				for(String str : commandAliases) {
					if(!b2) {
						if(str.equalsIgnoreCase(args[0]) || (str.startsWith("/") ? str.substring(1).equalsIgnoreCase(args[0]) : false)) {
							b2 = true;
							break;
						}
					}
				}
				if(b1 || b2) {
					final StringBuilder builder = new StringBuilder();
					final boolean[] bool = new boolean[] {false};
					if(cmd.getCommandAliases() != null) cmd.getCommandAliases().stream().forEach(new Consumer<String>() {
						@Override
						public void accept(String t) {
							bool[0] = true;
							builder.append(t + ", ");
						}
					});
					String alias = builder.toString();
					if(bool[0]) alias = alias.substring(0, alias.length() - 2);
					sendChat(sender, cmd.getCommandUsage(sender) + (bool[0] ? ". The aliases: " + alias : ""), EnumChatFormatting.DARK_AQUA);
					return;
				}
			}
			sendChat(sender, "Command not found!", EnumChatFormatting.GREEN);
		}
		public boolean canCommandSenderUseCommand(ICommandSender sender) {
			return true;
		}
		public void sendChat(ICommandSender sender, String chat, EnumChatFormatting... optionalColor) {
			IChatComponent comp = new ChatComponentText(chat);
			if(optionalColor != null) if(optionalColor.length != 0) comp.setChatStyle(new ChatStyle().setColor(optionalColor[0]));
			sender.addChatMessage(comp);
		}
    	
    }
    public enum ArrayToStringMode {
		COMMA_SPACE(", "),
		COMMA(","),
		DOT("."),
		PARANTHESIS("[", "", "]"),
		BLANK(""),
		SPACE(" "),
		STAND_SLASH("|"),
		SLASH("/"),
		BACKSLASH("\\"),
		STAND_SLASHV2(" | "),
		SLASHV2(" / "),
		BACKSLASHV2(" \\ ")
		;
		public final String separator;
		public final String prefix;
		public final String suffix;
		private ArrayToStringMode(String separator) {
			this(null, separator, null);
		}
		private ArrayToStringMode(String prefix, String separator, String suffix) {
			this.prefix = prefix;
			this.separator = separator;
			this.suffix = suffix;
		}
	}
    private boolean isGuiPressed = false;
    @SubscribeEvent
    public void onTick(ClientTickEvent e) {
    	if(e.phase == Phase.END) {
    		if(gui.isKeyDown() && !isGuiPressed) {
    			mc.displayGuiScreen(new UtilGUI(this));
    			isGuiPressed = true;
    		}
    		if(!gui.isKeyDown() && isGuiPressed) isGuiPressed = false;
    	}
    }
    
    public String toString(char[] a) {
    	String s = "";
    	
    	for(char c : a) {
    		s += "'" + c + "', ";
    	}
    	s = s.substring(0, s.length() - 2);
    	s = "[" + s + "]";
    	return s;
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST) // So it does not get replaced by the original
    public void onChatReceived(ClientChatReceivedEvent e) {
    	try {
    		// Format: "  <RankOrName> [Level]"
    		String str = removeUnicode(e.message.getUnformattedText());
    		if(debugMode) System.out.println("[DEBUG/MGUtilities] String without unicode: \"" + str + "\"");
    		if(debugMode) System.out.println("[DEBUG/MGUtilities] String charArray without unicode: " + toString(str.toCharArray()));
    		
    		if(!(str.startsWith("| "))) return;
    		str = str.substring(2);
    		String[] args = str.split(" ");
    		String name = (args[0].contains("[") || args[0].contains("]") ? args[1] : args[0]);
    		int lvl = Integer.parseInt(removeUnicode(args[0].contains("[") || args[0].contains("]") ? args[2] : args[1] ).replace("[", "").replace("]", "").replace(":", ""));
    		if(levelData.containsKey(name)) levelData.remove(name);
    		levelData.put(name, lvl);
    		if(saveLevel()) {
    			System.out.println("Successfully set " + name + " level to " + lvl);
    		}else System.out.println("Failed to set a player's level.");
    	}catch(Exception ignored) {}
    }
    
    public String removeUnicode(String str) {
    	String output1 = str.replaceAll("[^\\x00-\\x7F]", "|");
    	String output2 = str.replaceAll("[^\\x00-\\x7F]", "|");
    	return output1.equalsIgnoreCase(str) ? output2 : output1;
    }
    
    public void loadLevel() {
    	try {
    		FileInputStream in = new FileInputStream(levels);
    		ObjectInputStream stream = new ObjectInputStream(in);
    		levelData = (HashMap<String, Integer>) stream.readObject();
    		stream.close();
    		in.close();
    	}catch(Exception e) {
    		System.out.println("Failed to load levels, maybe the file doesn't exist? Using an empty one and saving it...");
    		if(!levels.exists()) saveLevel();
    	}
    }
    public void loadAP() {
    	try {
    		FileInputStream in = new FileInputStream(ap);
    		ObjectInputStream stream = new ObjectInputStream(in);
    		autoParty = (HashMap<Integer, String>) stream.readObject();
    		stream.close();
    		in.close();
    	}catch(Exception e) {
    		System.out.println("Failed to load levels, maybe the file doesn't exist? Using an empty one and saving it...");
    		if(!ap.exists())
				try {
					saveAP();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
    	}
    }
    public void saveAP() throws Exception {
    	if(!ap.exists()) ap.createNewFile();
		FileOutputStream out = new FileOutputStream(ap);
		ObjectOutputStream stream = new ObjectOutputStream(out);
		stream.writeObject(autoParty);
		stream.close();
		out.close();
    }
    
    public boolean saveLevel() {
    	try {
    		if(!levels.exists()) levels.createNewFile();
    		FileOutputStream out = new FileOutputStream(levels);
    		ObjectOutputStream stream = new ObjectOutputStream(out);
    		stream.writeObject(levelData);
    		stream.close();
    		out.close();
    		return true;
    	}catch(Exception e) {
    		System.out.println("Failed to save levels: " + e.getClass().getSimpleName() + ": " + e.getMessage());
    		return false;
    	}
    }
    
}
