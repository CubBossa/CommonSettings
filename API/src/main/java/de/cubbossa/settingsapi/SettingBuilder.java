package de.cubbossa.settingsapi;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SettingBuilder<T> {

	private final Class<T> type;
	private final NamespacedKey key;
	private String permission;
	private Component title;
	private Component shortDescription;
	private Component longDescription;
	private boolean nullable = false;
	private boolean threadsafe = true;
	private Function<UUID, T> getter;
	private Function<UUID, CompletableFuture<T>> asyncGetter;
	private BiFunction<UUID, T, CompletableFuture<Setting.SettingChangeResult>> setter;
	private T defaultValue;
	private final Collection<String> tags = new HashSet<>();

	public SettingBuilder(Class<T> type, NamespacedKey key) {
		this.type = type;
		this.key = key;
	}

	public SettingBuilder(Class<T> type, String plugin, String key) {
		this(type, new NamespacedKey(plugin, key));
	}

	public SettingBuilder<T> withTitle(String title) {
		this.title = Component.text(title);
		return this;
	}

	public SettingBuilder<T> withDescription(String shortDescription) {
		this.shortDescription = Component.text(shortDescription);
		return this;
	}

	public SettingBuilder<T> withDescription(String shortDescription, String longDescription) {
		this.shortDescription = Component.text(shortDescription);
		this.longDescription = Component.text(longDescription);
		return this;
	}

	public SettingBuilder<T> withTitle(Component title) {
		this.title = title;
		return this;
	}

	public SettingBuilder<T> withDescription(Component shortDescription) {
		this.shortDescription = shortDescription;
		return this;
	}

	public SettingBuilder<T> withDescription(Component shortDescription, Component longDescription) {
		this.shortDescription = shortDescription;
		this.longDescription = longDescription;
		return this;
	}

	public SettingBuilder<T> withFlagNullable() {
		this.nullable = true;
		return this;
	}

	public SettingBuilder<T> withFlagNotThreadSafe() {
		this.threadsafe = false;
		return this;
	}

	public SettingBuilder<T> withPermissionNode(String permission) {
		this.permission = permission;
		return this;
	}

	public SettingBuilder<T> withDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}

	public SettingBuilder<T> withGetter(Function<UUID, T> getter) {
		this.getter = getter;
		return this;
	}

	public SettingBuilder<T> withAsyncGetter(Function<UUID, CompletableFuture<T>> getter) {
		this.asyncGetter = getter;
		return this;
	}

	public SettingBuilder<T> withSetter(BiFunction<UUID, T, CompletableFuture<Setting.SettingChangeResult>> setter) {
		this.setter = setter;
		return this;
	}

	public SettingBuilder<T> withTags(String... tags) {
		this.tags.addAll(List.of(tags));
		return this;
	}

	public Setting<T> build() {

		if (getter == null && asyncGetter == null) {
			throw new RuntimeException("A setting must have at least one getter, either sync or async.");
		}

		Function<UUID, T> getter = this.getter != null ? this.getter :
				uuid -> {
					try {
						return this.asyncGetter.apply(uuid).get();
					} catch (InterruptedException | ExecutionException e) {
						throw new RuntimeException(e);
					}
				};
		Function<UUID, CompletableFuture<T>> asyncGetter = this.asyncGetter != null ? this.asyncGetter :
				uuid -> CompletableFuture.completedFuture(getter.apply(uuid));

		return new SimpleSetting<>(
				type,
				key,
				new SimpleFlags(setter != null, nullable, threadsafe),
				permission,
				tags,
				new SimpleDisplayOptions(title, shortDescription, longDescription)
		) {

			@Override
			public T getValue(UUID uuid) {
				return getter.apply(uuid);
			}

			@Override
			public CompletableFuture<T> requestValue(UUID uuid) {
				return asyncGetter.apply(uuid);
			}

			@Override
			public CompletableFuture<Setting.SettingChangeResult> setValue(UUID uuid, T value) {
				if (setter != null) {
					return setter.apply(uuid, value);
				} else {
					return CompletableFuture.completedFuture(SettingChangeResult.FAIL_READ_ONLY);
				}
			}

			@Override
			public CompletableFuture<Setting.SettingChangeResult> reset(UUID uuid) {
				if (defaultValue == null) {
					if (nullable) {
						return setValue(uuid, null);
					} else {
						return CompletableFuture.completedFuture(SettingChangeResult.FAIL_INVALID_VALUE);
					}
				}
				return setValue(uuid, defaultValue);
			}
		};
	}

	private record SimpleFlags(boolean readonly, boolean nullable, boolean threadSafe) implements Setting.Flags {
	}

	@Getter
	@RequiredArgsConstructor
	private static class SimpleDisplayOptions implements Setting.DisplayOptions {
		private final Component title;
		private final Component shortDescription;
		private final Component longDescription;
	}

	@Getter
	private abstract static class SimpleSetting<T> implements Setting<T> {
		private final Class<T> type;
		private final NamespacedKey key;
		private final Setting.Flags flags;
		private final String permission;
		private final Collection<String> tags;
		private final DisplayOptions displayOptions;

		public SimpleSetting(Class<T> type, NamespacedKey key, Flags flags, String permission, Collection<String> tags, DisplayOptions displayOptions) {
			this.type = type;
			this.key = key;
			this.flags = flags;
			this.permission = permission;
			this.tags = Collections.unmodifiableCollection(tags);
			this.displayOptions = displayOptions;
		}
	}
}
