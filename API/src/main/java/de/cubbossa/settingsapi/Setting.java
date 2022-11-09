package de.cubbossa.settingsapi;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a setting of a specific plugin. This setting is most likely a player setting, but the uuid owner could
 * also be a world or other UUID holders.
 * Settings do not serve as access to data storages. They must not be used to make data accessible if it is not meant to
 * be used e.g. in setting GUIs or commands.
 *
 * @param <T> is the type of the value that is represented by the setting. Try to keep the type as a primitive. Primitives
 *            are supported by most accessing plugins, like rendering a GUI or providing commands or using settings for a
 *            webinterface.
 */
public interface Setting<T> {

	/**
	 * @return The type of the value that is represented by the setting
	 */
	Class<T> getType();

	/**
	 * @return The unique {@link NamespacedKey} for this setting.
	 */
	NamespacedKey getKey();

	/**
	 * Flags represent a set of meta information for this setting. This includes whether the setting is readonly,
	 * threadsafe or nullable.
	 *
	 * @return An instance of the flags object
	 */
	Flags getFlags();

	/**
	 * Tags are handled by the setting provider (the plugin that implements the setting).
	 * They are a bundle of keywords that allow sorting or easier case handling.
	 * <br>
	 * Tags must satisfy the following regular expression: [a-z0-9_-:]
	 * Possible tags could be: "pvp", "chat", "login", "sound", "particle"
	 * Some plugins that sort their settings via tags might be providing a list of most used tags.
	 *
	 * @return The immutable tags collection
	 */
	Collection<String> getTags();

	/**
	 * Display options offer a possibility to the setting provider to handle meta information like the display name,
	 * short- and long descriptions and the display item. The implementation of these values is optional and may be used
	 * by other plugins to render GUIs etc.
	 *
	 * @return An instance of the display options object
	 */
	DisplayOptions getDisplayOptions();

	/**
	 * An optional permission node that can be used by other plugins to check against. You may want to use the same
	 * permission node that is also required to change the setting within your plugin (e.g. to execute the command).
	 *
	 * @return The permission node string or null, if no permission is set.
	 */
	@Nullable
	default String getPermission() {
		return null;
	}

	/**
	 * Returns the setting value for the provided UUID.
	 * This does happen synchronously and will block your main thread!
	 * You may want to use {@link #requestValue(UUID)} instead.
	 *
	 * @param uuid The UUID key to get the setting value for
	 * @return The setting value of the settings type
	 */
	T getValue(UUID uuid);

	/**
	 * Requests the setting value for the provided UUID.
	 *
	 * @param uuid The UUID key to get the setting value for
	 * @return A completable future that completes when the setting value is fetched.
	 */
	CompletableFuture<T> requestValue(UUID uuid);

	/**
	 * Sets the setting value for a specific uuid. This value must only be null if the flag "nullable" is set.
	 *
	 * @param uuid  The uuid to store the setting value for
	 * @param value The value to store
	 * @return A {@link  CompletableFuture} of type {@link SettingChangeResult} that completes when the storage of the setting
	 * has finished or failed. The according SettingChangeResult must be returned.
	 */
	CompletableFuture<SettingChangeResult> setValue(UUID uuid, T value);


	/**
	 * Resets the setting to its default value.
	 *
	 * @param uuid The UUID to store the setting value for
	 * @return A {@link  CompletableFuture} of type {@link SettingChangeResult} that completes when the storage of the setting
	 * has finished or failed. The according SettingChangeResult must be returned.
	 */
	CompletableFuture<SettingChangeResult> reset(UUID uuid);

	interface Flags {
		/**
		 * @return true if the Setter of this setting does not update the value of the setting, otherwise false.
		 */
		boolean readonly();

		/**
		 * @return true if the value of this setting is meant to be the setting type or null.
		 */
		boolean nullable();

		/**
		 * @return true, if the setting can be changed from any thread without running into thread safety issues.
		 */
		boolean threadSafe();
	}

	interface DisplayOptions {

		Component getTitle();
		Component getShortDescription();
		Component getLongDescription();

	}

	enum SettingChangeResult {
		/**
		 * If the value of the setting was successfully changed
		 */
		SUCCESS,
		/**
		 * If the value was not changed because the setting is flagged as readonly.
		 */
		FAIL_READ_ONLY,
		/**
		 * If the value was not changed due to a cancelled SettingsChangeEvent
		 */
		FAIL_EVENT_CANCELLED,
		/**
		 * If the value was not changed due to an invalid UUID
		 */
		FAIL_INVALID_UUID,
		/**
		 * If the value was not changed because it was not valid for the applied setting.
		 * This includes a null value if the setting doesn't have the nullable flag.
		 */
		FAIL_INVALID_VALUE,
		/**
		 * If the value was not changed due to unknown reasons.
		 */
		FAIL_OTHER
	}
}
