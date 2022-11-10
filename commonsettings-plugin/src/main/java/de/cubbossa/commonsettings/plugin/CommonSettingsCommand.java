package de.cubbossa.commonsettings.plugin;

import de.cubbossa.commonsettings.Setting;
import de.cubbossa.commonsettings.SettingsAPI;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;

import java.util.Map;
import java.util.UUID;

public class CommonSettingsCommand {

	private final Map<Class<?>, Parser<?>> parsers = Map.of(
			Boolean.class, input -> {
				if (!input.matches("(?i)true|false")) {
					throw new SettingValueParseException();
				}
				return Boolean.parseBoolean(input);
			},
			Integer.class, input -> {
				try {
					return Integer.parseInt(input);
				} catch (NumberFormatException e) {
					throw new SettingValueParseException();
				}
			},
			Double.class, input -> {
				try {
					return Double.parseDouble(input);
				} catch (NumberFormatException e) {
					throw new SettingValueParseException();
				}
			},
			Float.class, input -> {
				try {
					return Float.parseFloat(input);
				} catch (NumberFormatException e) {
					throw new SettingValueParseException();
				}
			}
	);

	public CommonSettingsCommand() {
		new CommandTree("commonsettings")
				.withAliases("csettings", "cset")
				.executes((commandSender, objects) -> {

				})
				.then(new LiteralArgument("list")
						.executes((commandSender, objects) -> {
							SettingsAPI.getInstance().getSettings().forEach(setting -> commandSender.sendMessage(setting.getKey().toString()));
						})
						.then(new IntegerArgument("page").executes((commandSender, objects) -> {

						}))
				)
				.then(new LiteralArgument("info")
						.executes((commandSender, objects) -> {

						})
						.then(new SettingsArgument("setting").executes((commandSender, objects) -> {

						}))
				)
				.then(new LiteralArgument("set")
						.executes((commandSender, objects) -> {

						})
						.then(new SettingsArgument("setting").then(new GreedyStringArgument("value").executesPlayer((player, objects) -> {
							handleSet((Setting<?>) objects[0], player.getUniqueId(), (String) objects[1]);
						})))
				)
				.register();
	}

	private <T> void handleSet(Setting<T> setting, UUID uuid, String value) {
		try {
			setting.setValue(uuid, parseSettingInput(setting, value)).thenAccept(settingChangeResult -> {

				// TODO user feedback
			});
		} catch (SettingValueParseException e) {
			// TODO user feedback
		}
	}

	private <T> T parseSettingInput(Setting<T> setting, String input) throws SettingValueParseException {
		Parser<T> parser = (Parser<T>) parsers.getOrDefault(setting.getType(), i -> { throw new SettingValueParseException(); });
		return parser.parse(input);
	}

	private interface Parser<T> {
		T parse(String input);
	}
}
