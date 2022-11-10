package de.cubbossa.commonsettings.plugin;

import de.cubbossa.commonsettings.NamespacedKey;
import de.cubbossa.commonsettings.SettingBuilder;
import de.cubbossa.commonsettings.SettingsAPI;
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

		SettingsAPI.getInstance().registerSetting(new SettingBuilder<>(String.class, NamespacedKey.fromString("example:a"))
				.withGetter(uuid -> "abc")
				.build());
		SettingsAPI.getInstance().registerSetting(new SettingBuilder<>(String.class, NamespacedKey.fromString("example:b"))
				.withGetter(uuid -> "abc")
				.build());
		SettingsAPI.getInstance().registerSetting(new SettingBuilder<>(String.class, NamespacedKey.fromString("example:c"))
				.withGetter(uuid -> "abc")
				.build());

		CommandAPI.onEnable(this);
		new CommonSettingsCommand();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}
}
