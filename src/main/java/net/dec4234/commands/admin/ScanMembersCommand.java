package net.dec4234.commands.admin;

import net.dec4234.database.collections.UserManagement;
import net.dec4234.framework.commands.BenedictCommand;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class ScanMembersCommand extends BenedictCommand {

	public ScanMembersCommand() {
		super("scanmembers");
	}

	@Override
	public void onCommand(MessageChannel messageChannel, User user, String[] args) {
		if(!isDec4234(user)) {
			return;
		}

		messageChannel.sendMessage("Conducting scan of all members").queue();
		UserManagement userManagement = new UserManagement();
		userManagement.searchForMissingMembers();
		userManagement.updateDatabaseNames();
		userManagement.updateDiscordUsernames();
	}
}
