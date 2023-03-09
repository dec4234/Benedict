package net.dec4234.commands.admin;

import net.dec4234.database.collections.UserManagement;
import net.dec4234.framework.commands.BenedictCommand;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class ValidateCommand extends BenedictCommand {

	public ValidateCommand() {
		super("validate");
	}

	@Override
	public void onCommand(MessageChannel messageChannel, User user, String[] args) {
		if(!isDec4234(user)) {
			return;
		}

		new UserManagement().validateDatabase();
		messageChannel.sendMessage("Validated Database").queue();
	}
}
