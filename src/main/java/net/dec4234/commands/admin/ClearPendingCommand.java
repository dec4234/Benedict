package net.dec4234.commands.admin;

import net.dec4234.framework.commands.BenedictCommand;
import net.dec4234.framework.misc.CachedValue;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class ClearPendingCommand extends BenedictCommand {

	public ClearPendingCommand() {
		super("clearpending");
	}

	@Override
	public void onCommand(MessageChannel messageChannel, User user, String[] args) {
		if (!isDec4234(user)) {
			return;
		}

		int amount = CachedValue.getClan().getClanManagement().getPendingMembers().size();
		CachedValue.getClan().getClanManagement().denyAllPendingMembers();

		messageChannel.sendMessage("Cleared " + amount + " pending requests.").queue();
	}
}
