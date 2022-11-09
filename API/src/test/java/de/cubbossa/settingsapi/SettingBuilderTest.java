package de.cubbossa.settingsapi;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SettingBuilderTest {

	@Test
	public void createSetting1() {

		Setting<String> setting = new SettingBuilder<>(String.class, new NamespacedKey("plugin", "setting"))
				.withGetter(uuid -> "value")
				.build();
		assertNotNull(setting);
		assertEquals(new NamespacedKey("plugin", "setting"), setting.getKey());
		assertEquals("value", setting.getValue(UUID.randomUUID()));
	}


}
