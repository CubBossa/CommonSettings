package de.cubbossa.commonsettings.example;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {

	@Override
	public void onEnable() {
		super.onEnable();

		if (Bukkit.getPluginManager().getPlugin("CommonSettings") != null) {
			new CommonSettingsHook().register(this);
		}

	}
}
