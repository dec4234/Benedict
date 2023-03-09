package net.dec4234.framework.chainCommands;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public interface IChainCommand {

	void onCommand(MessageChannel messageChannel, User user, String message);
}
