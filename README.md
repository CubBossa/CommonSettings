# CommonSettings

A settings API for minecraft that allows plugins to view and modify other plugins player settings

## Why should you use it?

The CommonSettings API serves as a connecting point for all plugins that handle player specific settings.
This applies to "providing" plugins, which have player settings (e.g. PlotSquared with /p toggle titles) but
also to "consuming" plugins like setting GUIs.

To use a convention for settings is a huge help to server administrators that want to offer their users
one clean way to manage their settings. No need to make GUIs which run "/p toggle titles" as command but cannot
visualize the actual value of the setting. No need to have every single plugin with playersettings as dependency.

Plugins register settings, other plugins use settings without dealing with other plugins code :D

## Installation

```Xml
<repositories>
    <repository>
        <id>cubbossa</id>
        <url>https://nexus.leonardbausenwein.de/repository/maven-public</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>de.cubbossa</groupId>
        <artifactId>commonsettings-api</artifactId>
        <version>1.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

## How to Use

### Register a Setting

If your server has player settings, you can easily register them to CommonSettings.

First, you may want to make sure that CommonSettings is a soft depend, meaning that your plugin
also runs without.

```yml
# in plugin.yml
softdepend: [CommonSettings]
```

```Java
class MyPlugin {
	void onEnable() {
		// Check if the plugin is installed, otherwise you will run into ClassNotFound errors.
		if (Bukkit.getPluginManager().getPlugin("CommonSettings") != null) {
			new Tutorial().register();
		}
	}
}
```

Within this new class, you can register all your settings.
You can implement the Setting interface by yourself or use the SettingsBuilder, that
does some tasks for you.

If you want your settings to be async you can register an async getter.
You can also register both. By default, the sync getter of the SettingsBuilder calls the async getter thread blocking
and the async getter calls the sync getter async, depending on what's implemented.

```Java
class Tutorial {
	void register() {
		// Use the setting type and a unique NamespacedKey to register a setting.
		// The type matches the getter and setter value, this might be a boolean or enum, in this case a Material.
		// The NamespacedKey is necessary to identify your setting among others.
		SettingsAPI.getInstance().registerSetting(new SettingBuilder<>(Material.class, NamespacedKey.fromString("tutorial:material"))
				// set the title with
				.withTitle("My Setting")
                // or
				.withTitle(Component.text("My Setting"))
				// Set a short and long description
				.withDescription("Just an example", "This is just an example to explain the usage of the SettingsBuilder class.")
				// Set tags to sort your setting into categories
				.withTags("example", "chat", "fly", "speed", "particles", "what ever else comes to your mind")
				// Mark your Setting as not thread-safe, meaning the setter has to be called in the mainthread.
				.withFlagNotThreadSafe()
				// Mark your Setting as nullable -> null is a valid value for setter and getter 
				.withFlagNullable()
				// Handle the getter call of the setting -> return your own setting value.
				.withGetter(uuid -> MyOwnSettingsHandler.getTutorialMaterial(uuid))
				.withAsyncGetter(uuid -> CompletableFuture.completedFuture(MyOwnSettingsHandler.getTutorialMaterial(uuid)))
				// Handle the setter call of this setting.
				// You have to return a completable future of the result.
				.withSetter((uuid, material) -> {
					MyOwnSettingsHandler.setTutorialMaterial(uuid, material);
					return CompletableFuture.completedFuture(Setting.SettingChangeResult.SUCCESS);
				})
				// finally, build it :D
				.build());
	}
}
```

### Using Other Plugins Settings

First of all, make sure that CommonSettings is installed and listed as dependency in your plugins.yml.

To use a setting that was registered by another plugin, you have to search it by its NamespacedKey.
You can also search settings by type, plugin and tags. 

```Java
class Tutorial {
	// Retrieve Settings. Only cast settings to their generic type if you are sure about it
	// If you don't know what type a setting is and the setting provider did not document anything about it,
	// you can get the Type via Setting#getType()
	<T> Setting<T> getTutorialSetting(NamespacedKey key) {
		return (Setting<T>) SettingsAPI.getInstance().getSettign(key);
	}

	void printTutorialMaterial() {
		// Get the setting from the handler and
		Setting<Material> setting = getTutorialSetting(NamespacedKey.fromString("tutorial:material"));
		// Example of an asynchronous setting call.
		setting.getValue().thenAccept(material -> {
			getLogger().log(Level.DEBUG, material);
		});
	}
}
```