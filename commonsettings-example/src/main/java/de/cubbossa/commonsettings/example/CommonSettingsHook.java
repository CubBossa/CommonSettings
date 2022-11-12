package de.cubbossa.commonsettings.example;

import de.cubbossa.commonsettings.NamespacedKey;
import de.cubbossa.commonsettings.Setting;
import de.cubbossa.commonsettings.SettingBuilder;
import de.cubbossa.commonsettings.SettingsAPI;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CommonSettingsHook {

	private static final HashMap<UUID, Boolean> BOOL = new HashMap<>();
	private static final HashMap<UUID, String> STRING = new HashMap<>();
	private static final HashMap<UUID, Material> ENUM = new HashMap<>();


	public void register(JavaPlugin plugin) {

		SettingsAPI.getInstance().registerSetting(new SettingBuilder<>(Boolean.class, new NamespacedKey(plugin.getName(), "boolean"))
				.withTitle("Boolean Setting")
				.withGetter(uuid -> BOOL.getOrDefault(uuid, false))
				.withSetter((uuid, newValue) -> {
					BOOL.put(uuid, newValue);
					return CompletableFuture.completedFuture(Setting.SettingChangeResult.SUCCESS);
				})
				.build());

		SettingsAPI.getInstance().registerSetting(new SettingBuilder<>(String.class, new NamespacedKey(plugin.getName(), "string"))
				.withTitle("String Setting")
				.withAsyncGetter(uuid -> CompletableFuture.completedFuture(STRING.getOrDefault(uuid, "")))
				.withSetter((uuid, newValue) -> {
					STRING.put(uuid, newValue);
					return CompletableFuture.completedFuture(Setting.SettingChangeResult.SUCCESS);
				})
				.build());

		SettingsAPI.getInstance().registerSetting(new SettingBuilder<>(Material.class, new NamespacedKey(plugin.getName(), "material"))
				.withTitle("String Setting")
				.withGetter(uuid -> ENUM.getOrDefault(uuid, Material.AIR))
				.withSetter((uuid, newValue) -> {
					ENUM.put(uuid, newValue);
					return CompletableFuture.completedFuture(Setting.SettingChangeResult.SUCCESS);
				})
				.build());
	}
}
