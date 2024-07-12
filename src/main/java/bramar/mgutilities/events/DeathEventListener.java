package bramar.mgutilities.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.function.Supplier;
import java.util.logging.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

// Listens for Player Death
// (Multiplayer-compatible which means it works on singleplayer and multiplayer)
public class DeathEventListener {
	private Minecraft mc = Minecraft.getMinecraft();
	private Timer t = new Timer(true);
	private Logger log = Logger.getLogger("Debugging");
	private final int BLOOD_CHECK_TRIES = 8; // Tick amount
	public DeathEventListener(boolean autoRegister) {
		if(autoRegister) MinecraftForge.EVENT_BUS.register(this);
//		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//			try(PrintWriter writer = new PrintWriter("C:%fUsers%fKurniati%fDesktop%fMinigameUtilities.log".replace("%f", File.separator))) {
//				writer.write(logfile.toString());
//			}catch(IOException io) {
//				io.printStackTrace();
//			}
//		}));
	}
//	public void schedule(Runnable run, long delay) {
//		t.schedule(new TimerTask() {
//			public void run() { run.run(); cancel(); }
//		}, delay, delay);
//	}
//	private final StringBuilder logfile = new StringBuilder();
//	private void log(String str) {
//		log.info(str);
//		logfile.append("\n").append(str);
//	}
	private final List<Supplier<Boolean>> after = new ArrayList<>();
	@SubscribeEvent
	public void onTick(ClientTickEvent e) {
		if(e.phase == Phase.START) {
			for(Supplier<Boolean> supplier : new ArrayList<>(after)) {
				if(supplier.get()) after.remove(supplier);
			}
		}
	}
	@SubscribeEvent
	public void onAttack(AttackEntityEvent e) {
		if(e.isCanceled()) return;
		boolean a, b;
		a = e.target.isDead;
		b = e.target instanceof EntityLivingBase && ((EntityLivingBase) e.target).getHealth() <= 0f;
		final int tickAlive = e.target.ticksExisted;
		if(a || b)
			fireEvent(e, e.target.posX, e.target.posY, e.target.posZ);
		else {
			int tries[] = new int[] {BLOOD_CHECK_TRIES - 1};
			after.add(() -> {
				boolean aa, ab, ac;
				aa = e.target.isDead;
				ab = e.target instanceof EntityLivingBase && ((EntityLivingBase) e.target).getHealth() <= 0f;
				ac = e.target.ticksExisted < tickAlive;
				if(aa || ab || ac) {
					fireEvent(e, e.target.posX, e.target.posY, e.target.posZ);
					return true;
				}else return --tries[0] == 0;
			});
		}
	}
	void fireEvent(AttackEntityEvent e, double x, double y, double z) {
		boolean sp;
//		System.out.println("Firing AttackEntityEvent");
		KillEntityEvent event = new KillEntityEvent(e.target, e.entityPlayer, sp = mc.isSingleplayer(), x, y, z);
		MinecraftForge.EVENT_BUS.post(event);
		if(event.isCanceled() && sp /* cancelable */) e.setCanceled(true);
	}
}
