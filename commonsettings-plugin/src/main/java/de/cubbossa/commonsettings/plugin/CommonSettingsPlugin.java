package de.cubbossa.commonsettings.plugin;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import org.bukkit.plugin.java.JavaPlugin;

public class CommonSettingsPlugin extends JavaPlugin {

	@Override
	public void onLoad() {
		CommandAPI.onLoad(new CommandAPIConfig().silentLogs(true));
	}

	@Override
	public void onEnable() {
		super.onEnable();

		CommandAPI.onEnable(this);
		new CommonSettingsCommand();
	}

	@Override
	public void onDisable() {
		super.onDisable();

		CommandAPI.unregister("commandsettings");
		CommandAPI.onDisable();
	}
}
