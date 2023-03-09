package net.dec4234.framework.commands;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public interface IBendedictCommand {

	void onCommand(MessageChannel messageChannel, User user, String[] args);

	default boolean isDec4234(User user) {
		return user.getId().equals("252508637753376779");
	}
}
