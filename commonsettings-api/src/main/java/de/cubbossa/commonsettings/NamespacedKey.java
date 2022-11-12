package de.cubbossa.commonsettings;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A custom version of the namespaced key used in Bukkit.
 * The namespace resembles a plugin and has to be unique for each using plugin.
 * The key serves as an identifier. So a plugin that registers a setting would use its name + setting name, e.g. PlotSquared:plots.toggle.chat
 */
@Getter
public class NamespacedKey {

	private final String namespace;
	private final String key;

	public NamespacedKey(String namespace, String key) {
		this.namespace = namespace.toLowerCase();
		this.key = key.toLowerCase();
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toString() {
		return namespace + ":" + key;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj instanceof NamespacedKey keyObj) {
			return toString().equals(keyObj.toString());
		}
		return false;
	}

	public static NamespacedKey fromString(String namespacedKey) {
		if (!namespacedKey.matches("[0-9a-zA-Z_]+:[0-9a-zA-Z_]+")) {
			throw new IllegalArgumentException("A namespaced key must be of format: <namespace>:<key>");
		}
		String[] splits = namespacedKey.split(":");
		String namespace = splits[0];
		String key = splits[1];
		return new NamespacedKey(namespace, key);
	}
}
