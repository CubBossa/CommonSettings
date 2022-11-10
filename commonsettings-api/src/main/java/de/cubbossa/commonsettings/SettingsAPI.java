package de.cubbossa.commonsettings;

import java.util.*;

public class SettingsAPI {

	private static class Holder {
		private static final SettingsAPI INSTANCE = new SettingsAPI();
	}

	public static SettingsAPI getInstance() {
		return Holder.INSTANCE;
	}

	private final HashMap<NamespacedKey, Setting<?>> registeredSettings;

	protected SettingsAPI() {

		registeredSettings = new HashMap<>();
	}

	public <S extends Setting<?>> void registerSetting(S setting) throws DuplicateKeyException {
		if (registeredSettings.containsKey(setting.getKey())) {
			throw new DuplicateKeyException(String.format("Another setting with the key \"%s\" is already registered.", setting.getKey()));
		}
		registeredSettings.put(setting.getKey(), setting);
	}

	public <S extends Setting<?>> void unregisterSetting(S setting) {
		unregisterSetting(setting.getKey());
	}

	public void unregisterSetting(NamespacedKey key) {
		registeredSettings.remove(key);
	}

	public List<Setting<?>> getSettings() {
		return new ArrayList<>(registeredSettings.values());
	}

	public List<Setting<?>> getSettings(String plugin) {
		List<Setting<?>> settings = new ArrayList<>();
		for (Setting<?> setting : registeredSettings.values()) {
			if (setting.getKey().getNamespace().equals(plugin)) {
				settings.add(setting);
			}
		}
		return settings;
	}

	public <S extends Setting<T>, T> List<S> getSettings(Class<T> type) {
		List<S> settings = new ArrayList<>();
		for (Setting<?> setting : registeredSettings.values()) {
			if (type.equals(setting.getType())) {
				settings.add((S) setting);
			}
		}
		return settings;
	}

	public <T> Setting<T> getSetting(String plugin, String key) {
		return getSetting(new NamespacedKey(plugin, key));
	}

	public <T> Setting<T> getSetting(NamespacedKey key) {
		Setting<T> setting = (Setting<T>) registeredSettings.get(key);
		if (setting == null) {
			throw new NoSuchElementException(String.format("Could not find setting with key \"%s\".", key));
		}
		return setting;
	}
}
