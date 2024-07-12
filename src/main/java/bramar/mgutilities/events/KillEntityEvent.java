package bramar.mgutilities.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * Fired when an entity got killed by the client (player)<br>
 * Field <code>dead</code> is the dead player<br>
 * while the field <code>entityPlayer</code> is the client player/killer (can also use <code>Minecraft.getMinecraft#thePlayer</code>)<br><br>
 * Cancellable ONLY IF SINGLEPLAYER, even with reflection it wont work (if multiplayer)
 */
@Cancelable
public class KillEntityEvent extends PlayerEvent {
	public final Entity dead;
	public final boolean canCancel;
	public final double lastX, lastY, lastZ;
	public KillEntityEvent(Entity dead, EntityPlayer killer, boolean isSingleplayer, double x, double y, double z) {
		super(killer);
		this.dead = dead;
		this.canCancel = isSingleplayer;
		this.lastX = x;
		this.lastY = y;
		this.lastZ = z;
	}
	
	public boolean isMultiplayer() {
		return !canCancel;
	}
	public boolean isSingleplayer() {
		return canCancel;
	}
	
	
	@Override
	public boolean isCancelable() {
		return canCancel;
	}
	
	@Override
	public void setCanceled(boolean cancel) {
		if(!isCancelable()) throw new IllegalArgumentException("Attempted to cancel a multiplayer-sided event");
		super.setCanceled(cancel);
	}
}
