package bramar.mgutilities;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import bramar.mgutilities.events.KillEntityEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BloodParticle {
	private MGUtilities main;
	private Minecraft mc = Minecraft.getMinecraft();
	private Timer timer = new Timer(true);
	BloodParticle(MGUtilities main) {
		this.main = main;
		MinecraftForge.EVENT_BUS.register(this);
	}
	private void schedule(Runnable run) {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				run.run();
				cancel();
			}
		}, 70, 70);
	}
	@SubscribeEvent
    public void onEntityDeath(LivingDeathEvent e) {
    	if(e.source instanceof EntityDamageSource && main.bloodParticle) {
    		EntityDamageSource source = (EntityDamageSource) e.source;
    		EntityPlayerSP p = mc.thePlayer;
    		if(source.getEntity().getUniqueID().equals(p.getUniqueID()) || source.getEntity().equals(p)) {
    			bloodParticle(e.entityLiving.posX, e.entityLiving.posY, e.entityLiving.posZ, e.entityLiving instanceof EntityPlayer ? 0.65d : 0.5d, 10);
    		}
    	}
    }
	@SubscribeEvent
	public void onEntityKilled(KillEntityEvent e) {
		if(e.isMultiplayer() && main.bloodParticleMP && !e.dead.getUniqueID().equals(mc.thePlayer.getUniqueID())) bloodParticle(e.lastX, e.lastY, e.lastZ, e.dead instanceof EntityPlayer ? 0.65d : 0.5d, 15);
	}
	public float safelyConvert(double a) {
		return (float) Math.max(Float.MIN_VALUE, Math.min(Float.MAX_VALUE, a));
	}
	public void bloodParticle(double x, double y, double z, double offset, int particleAmount) {
    	float fX = safelyConvert(x);
    	float fY = safelyConvert(y);
    	float fZ = safelyConvert(z);
    	if(particleAmount <= 0) throw new IllegalArgumentException("amount must be higher than 0");
		final EntityPlayerSP p = mc.thePlayer;
		mc.renderGlobal.spawnParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(), false, x, y + offset, z, 0, 0, 0, 152 /* block id of block crack (break)*/);;
		Random r = new Random();
		if(particleAmount > 1) for(int i = 0; i < particleAmount-1; i++) {
			double offsetX = r.nextDouble(),
					offsetY = r.nextDouble(),
					offsetZ = r.nextDouble();
			mc.renderGlobal.spawnParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(), false, x, y + offset, z, offsetX, offsetY, offsetZ, 152 /* block id of block crack (break)*/);;
		}
		schedule(() -> mc.getSoundHandler().playSound(new BloodSound(fX, fY, fZ)));
    }
	private class BloodSound implements ISound {
		float x, y, z;
		BloodSound(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		@Override
		public float getZPosF() { return x; }
		
		@Override
		public float getYPosF() { return y; }
		
		@Override
		public float getXPosF() { return z; }
		
		@Override
		public float getVolume() { return 2.0f; }
		
		@Override
		public ResourceLocation getSoundLocation() {
			return new ResourceLocation("minecraft:dig.stone");
		}
		
		@Override
		public int getRepeatDelay() { return 0; } // canRepeat() == false
		
		@Override
		public float getPitch() { return 1.2f; }
		
		@Override
		public AttenuationType getAttenuationType() { return AttenuationType.NONE; }
		@Override
		public boolean canRepeat() { return false; }
	}
}
