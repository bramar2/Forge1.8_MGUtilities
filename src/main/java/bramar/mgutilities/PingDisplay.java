package bramar.mgutilities;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class PingDisplay extends CommandBase {
	// /ping Command
	
	@Override
	public String getCommandName() {
		return "ping";
	}
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "Usage: /ping [player]: Checks that player's ping OR your ping";
	}
	
	private void sendChat(ICommandSender sender, String str, EnumChatFormatting color) {
		sender.addChatMessage(new ChatComponentText(str).setChatStyle(new ChatStyle().setColor(color)));
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		boolean a = args != null;
		if(a && args.length == 0) {
			NetworkPlayerInfo i = mc.getNetHandler().getPlayerInfo(mc.getNetHandler().getGameProfile().getId());
			if(i == null) sendChat(sender, "An error occurred while getting ping", EnumChatFormatting.RED);
			else sendChat(sender, "Your ping is " + i.getResponseTime() + "ms", EnumChatFormatting.DARK_AQUA);
		}else if(a) {
			NetworkPlayerInfo i = mc.getNetHandler().getPlayerInfo(args[0]);
			if(i == null) sendChat(sender, "Player not found!", EnumChatFormatting.RED);
			else sendChat(sender, args[0] + "'s ping is " + i.getResponseTime() + "ms", EnumChatFormatting.DARK_AQUA);
		}
	}
	
	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		if(args.length <= 2) {
			List<String> list = mc.getNetHandler().getPlayerInfoMap().stream().map(NetworkPlayerInfo::getGameProfile).map(GameProfile::getName).collect(Collectors.toList());
			Collections.sort(list);
			return list;
		}
		return null;
	}
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}
	
	//
	private final long ticks;
	private long currTick = 0;
	private final Minecraft mc = Minecraft.getMinecraft();
	private final Pattern PING_PATTERN = Pattern.compile("([ ]+)?\\(\\d([\\-]+)?ms\\)([ ]+)?");
	private final String RESET1 = EnumChatFormatting.RESET.toString();
	private final String RESET2 = RESET1 + RESET1;
	PingDisplay() {
		this.ticks = 100;
		MinecraftForge.EVENT_BUS.register(this);
		ClientCommandHandler.instance.registerCommand(this);
	}
	
	@SubscribeEvent
	public void clientTick(ClientTickEvent e) {
		if(e.phase == Phase.END && MGUtilities.displayPing && !mc.isSingleplayer() && mc.thePlayer != null) {
			if(++currTick >= ticks + 1) {
				currTick = 0;
				for(EntityPlayer p : mc.theWorld.playerEntities) {
					p.refreshDisplayName();
				}
			}
		}
	}
	
	
	
	
	private String replaceAll(String formatted) {
		StringBuilder builder = new StringBuilder(formatted);
		while(builder.toString().contains(RESET2)) {
			builder = new StringBuilder(builder.toString().replace(RESET2, RESET1));
		}
		return builder.toString();
	}
	private Timer timer = new Timer(true);
	private void schedule(Runnable run, long delay) {
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				run.run();
				cancel();
			}
		}, delay, delay);
	}
	@SubscribeEvent
	public void onRefresh(PlayerEvent.NameFormat e) {
		if(MGUtilities.displayPing) {
			try {
				String newName = PING_PATTERN.matcher(e.displayname).replaceAll("");
				NetworkPlayerInfo i = mc.getNetHandler().getPlayerInfo(e.username);
				e.displayname = newName + " (" + i.getResponseTime() + "ms)";
			}catch(Exception ignored) {}
		}
	}
}