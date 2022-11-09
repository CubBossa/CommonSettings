package de.cubbossa.commonsettings.plugin;

import de.cubbossa.settingsapi.Setting;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
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
	}
}
