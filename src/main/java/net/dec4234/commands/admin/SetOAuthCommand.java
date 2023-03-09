package net.dec4234.commands.admin;

import net.dec4234.database.framework.DatabaseConfig;
import net.dec4234.database.framework.MongoKey;
import net.dec4234.framework.commands.BenedictCommand;
import net.dec4234.javadestinyapi.utils.HttpUtils;
import net.dec4234.src.BenedictMain;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class SetOAuthCommand extends BenedictCommand {

	public SetOAuthCommand() {
		super("setoauth");
	}

	@Override
	public void onCommand(MessageChannel messageChannel, User user, String[] args) {
		if(isDec4234(user)) {
			if(args.length >= 1) {
				new HttpUtils(BenedictMain.getApiKey()).setTokenViaAuth(args[0]);
				new DatabaseConfig().setLong(MongoKey.TOKEN_SET_DATE, System.currentTimeMillis());
				messageChannel.sendMessage("Tokens successfully set!").queue();
			}
		}
	}
}
