package net.dec4234.framework.commands;

import lombok.Getter;
import net.dec4234.framework.misc.CachedValue;

import java.util.HashMap;

public abstract class BenedictCommand implements IBendedictCommand {

	// Command // Class
	@Getter private static HashMap<String, BenedictCommand> commandMap = new HashMap<>();
	@Getter private String name;
	@Getter private static String prefix = "!";
	@Getter private String description = null;

	public BenedictCommand(String name) {
		this.name = name;

		if (commandMap.containsKey(prefix + name)) { // IF the comamnd trying to be registered has already been registered, alert operator
			try {
				throw new RegisteredDuplicateCommandException();
			} catch (RegisteredDuplicateCommandException e) {
				e.printStackTrace();
			}
		} else { // IF it passes checks, register command to command map
			commandMap.put(prefix + name, this);
		}
	}

	public BenedictCommand(String name, String description) {
		this.name = name;
		this.description = description;

		if (commandMap.containsKey(name)) { // IF the comamnd trying to be registered has already been registered, alert operator
			try {
				throw new RegisteredDuplicateCommandException();
			} catch (RegisteredDuplicateCommandException e) {
				e.printStackTrace();
			}
		} else { // IF it passes checks, register command to command map
			commandMap.put(name, this);
			CachedValue.getGuild().upsertCommand(name, description)
					   .setDefaultEnabled(true)
					   .queue();
		}
	}
}
