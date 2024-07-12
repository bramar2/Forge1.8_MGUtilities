package bramar.mgutilities.gui;

import java.util.List;

import bramar.mgutilities.MGUtilities;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class ConfigGUI extends GuiConfig {
	public ConfigGUI(GuiScreen parent) {
		super(parent,
				new ConfigElement(MGUtilities.instance.config.getCategory("general")).getChildElements(),
				MGUtilities.MODID,
				false,
				false,
				"Minigame Utilities Config GUI",
				"File Path: " + MGUtilities.instance.config.getConfigFile().getPath());
	}
	
	public boolean getDefaultBoolean(String key) {
		return false;
	}
	public int getDefaultInt(String key) {
		return 0;
	}
	public String getDefaultString(String key) {
		return "";
	}
	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		// Save config
		for(IConfigElement element : configElements) {
			Object val = element.get();
			if(val instanceof Double) MGUtilities.instance.config.get("general", element.getName(), 0.0D).set((Double) val);
			else if(val instanceof double[]) MGUtilities.instance.config.get("general", element.getName(), new double[] {}).set((double[]) val);
			else if(val instanceof Boolean) MGUtilities.instance.config.get("general", element.getName(), getDefaultBoolean(element.getName())).set((Boolean) val);
			else if(val instanceof boolean[]) MGUtilities.instance.config.get("general", element.getName(), new boolean[] {}).set((boolean[]) val);
			else if(val instanceof Integer) MGUtilities.instance.config.get("general", element.getName(), getDefaultInt(element.getName())).set((Integer) val);
			else if(val instanceof int[]) MGUtilities.instance.config.get("general", element.getName(), new int[] {}).set((int[]) val);
			else if(val instanceof String[]) MGUtilities.instance.config.get("general", element.getName(), new String[] {}).set((String[]) val);
			else MGUtilities.instance.config.get("general", element.getName(), getDefaultString(element.getName())).set(val + "");
		}
		MGUtilities.instance.config.save();
		MGUtilities.instance.loadConfigElements(); // Refresh
	}

}
