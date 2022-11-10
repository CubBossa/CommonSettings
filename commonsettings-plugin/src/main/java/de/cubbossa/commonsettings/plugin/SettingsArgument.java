package de.cubbossa.commonsettings.plugin;

import de.cubbossa.commonsettings.NamespacedKey;
import de.cubbossa.commonsettings.Setting;
import de.cubbossa.commonsettings.SettingsAPI;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.NamespacedKeyArgument;

public class SettingsArgument extends CustomArgument<Setting<?>, org.bukkit.NamespacedKey> {

	public SettingsArgument(String node) {
		super(new NamespacedKeyArgument(node), info -> {
			NamespacedKey key = new NamespacedKey(info.currentInput().getNamespace(), info.currentInput().getKey());
			Setting<?> setting = SettingsAPI.getInstance().getSetting(key);
			if (setting == null) {
				throw new CustomArgumentException(String.format("No setting found with name '%s'", info.currentInput().toString()));
			}
			return setting;
		});
		includeSuggestions((suggestionInfo, suggestionsBuilder) -> {
			SettingsAPI.getInstance().getSettings().stream()
					.map(Setting::getKey)
					.map(NamespacedKey::toString)
					.forEach(suggestionsBuilder::suggest);
			return suggestionsBuilder.buildFuture();
		});
	}
}
