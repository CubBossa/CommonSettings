package de.cubbossa.settingsapi;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SettingsAPITest {

	@BeforeEach
	void setUp() {
	}

	@Test
	@Order(1)
	void getInstance() {
		assertNotNull(SettingsAPI.getInstance());
	}

	@Test @Order(2)
	void getSettings() {
		assertNotNull(SettingsAPI.getInstance().getSettings());
	}

	@Test @Order(3)
	void registerSetting() {
		int current = SettingsAPI.getInstance().getSettings().size();
		NamespacedKey key = NamespacedKey.fromString("test:name");
		Setting<Boolean> setting = new DummySetting<>(
				Boolean.class,
				key,
				new SimpleFlags(false, false, true),
				List.of("test")
		);
		SettingsAPI.getInstance().registerSetting(setting);
		assertEquals(current + 1, SettingsAPI.getInstance().getSettings().size());
		assertNotNull(SettingsAPI.getInstance().getSetting(key));
		assertEquals(setting, SettingsAPI.getInstance().getSetting(key));
	}

	@Test @Order(4)
	void registerSettingDuplicateKey() {
		NamespacedKey key = NamespacedKey.fromString("test:name");
		Setting<Boolean> setting = new DummySetting<>(
				Boolean.class,
				key,
				new SimpleFlags(false, false, true),
				List.of("test")
		);
		assertDoesNotThrow(() -> SettingsAPI.getInstance().registerSetting(setting));
		assertThrows(DuplicateKeyException.class, () -> SettingsAPI.getInstance().registerSetting(setting));
	}

	@Test @Order(5)
	void unregisterSetting() {
		int current = SettingsAPI.getInstance().getSettings().size();
		NamespacedKey key = NamespacedKey.fromString("test:name");
		Setting<Boolean> setting = new DummySetting<>(
				Boolean.class,
				key,
				new SimpleFlags(false, false, true),
				List.of("test")
		);
		SettingsAPI.getInstance().registerSetting(setting);
		assertEquals(current + 1, SettingsAPI.getInstance().getSettings().size());
		SettingsAPI.getInstance().unregisterSetting(key);
		assertEquals(current, SettingsAPI.getInstance().getSettings().size());
		SettingsAPI.getInstance().unregisterSetting(key);
		assertEquals(current, SettingsAPI.getInstance().getSettings().size());
	}

	@Test @Order(6)
	void testUnregisterSetting() {
		int current = SettingsAPI.getInstance().getSettings().size();
		Setting<Boolean> setting = new DummySetting<>(
				Boolean.class,
				NamespacedKey.fromString("test:name"),
				new SimpleFlags(false, false, true),
				List.of("test")
		);
		SettingsAPI.getInstance().registerSetting(setting);
		assertEquals(current + 1, SettingsAPI.getInstance().getSettings().size());
		SettingsAPI.getInstance().unregisterSetting(setting);
		assertEquals(current, SettingsAPI.getInstance().getSettings().size());
		SettingsAPI.getInstance().unregisterSetting(setting);
		assertEquals(current, SettingsAPI.getInstance().getSettings().size());
	}

	@Test @Order(7)
	void testGetSettings() {
		int current = SettingsAPI.getInstance().getSettings().size();
		int currentBool = SettingsAPI.getInstance().getSettings(Boolean.class).size();
		Setting<Boolean> test = newTestSetting(Boolean.class, "bool1");
		SettingsAPI.getInstance().registerSetting(test);
		SettingsAPI.getInstance().registerSetting(newTestSetting(String.class, "str1"));
		SettingsAPI.getInstance().registerSetting(newTestSetting(String.class, "str2"));
		assertEquals(current + 3, SettingsAPI.getInstance().getSettings().size());

		Class<Boolean> type = Boolean.class;
		List<Setting<Boolean>> settings = SettingsAPI.getInstance().getSettings(type);
		assertEquals(currentBool + 1, settings.size());
		assertTrue(settings.contains(test));
	}

	@Test @Order(8)
	void testGetSettings1() {
	}

	@Test @Order(9)
	void getSetting() {
	}

	@Test @Order(10)
	void testGetSetting() {
	}

	private static <T> Setting<T> newTestSetting(Class<T> type, String name) {
		return new DummySetting<>(
				type,
				NamespacedKey.fromString("setting:" + name),
				new SimpleFlags(false, false, true),
				List.of("abc")
		);
	}

	private record SimpleFlags(boolean readonly, boolean nullable, boolean threadSafe) implements Setting.Flags {
	}

	@RequiredArgsConstructor
	@Getter
	private static class DummyDisplayOptions implements Setting.DisplayOptions {
		private final Component title;
		private final Component shortDescription;
		private final Component longDescription;

		public DummyDisplayOptions(String title, String shortDescription, String longDescription) {
			this.title = Component.text(title);
			this.shortDescription = Component.text(shortDescription);
			this.longDescription = Component.text(longDescription);
		}
	}

	@RequiredArgsConstructor
	@Getter
	private static class DummySetting<T> implements Setting<T> {

		private final Map<UUID, T> data = new HashMap<>();
		private final Class<T> type;
		private final NamespacedKey key;
		private final Flags flags;
		private final List<String> tags;
		private final DisplayOptions displayOptions = new DummyDisplayOptions("a", "b", "c");

		@Override
		public CompletableFuture<T> requestValue(UUID uuid) {
			return CompletableFuture.completedFuture(data.get(uuid));
		}

		@Override
		public T getValue(UUID uuid) {
			return data.get(uuid);
		}

		@Override
		public CompletableFuture<SettingChangeResult> setValue(UUID uuid, T value) {
			data.put(uuid, value);
			return CompletableFuture.completedFuture(SettingChangeResult.SUCCESS);
		}

		@Override
		public CompletableFuture<SettingChangeResult> reset(UUID uuid) {
			data.put(uuid, null);
			return CompletableFuture.completedFuture(SettingChangeResult.SUCCESS);
		}
	}
}