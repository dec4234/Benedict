package net.dec4234.commands.admin;

import net.dec4234.framework.commands.BenedictCommand;
import net.dec4234.javadestinyapi.material.DestinyAPI;
import net.dec4234.javadestinyapi.material.user.BungieUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;

public class GetDataCommand extends BenedictCommand {

	public GetDataCommand() {
		super("userdata");
	}

	@Override
	public void onCommand(MessageChannel messageChannel, User user, String[] args) {
		if(!isDec4234(user)) {
			return;
		}

		if(args.length >= 1) {
			String name = args[0].replace("%20", " "); // TO-DO: Investigate if this is needed anymore
			java.util.List<BungieUser> potential = DestinyAPI.searchUsers(args[0]);

			if(potential.size() != 0) {
				BungieUser bungieUser = potential.get(0);
				EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.BLUE);
				embedBuilder.setTitle(bungieUser.getDisplayName());
				embedBuilder.addField("Details:", "BungieID: " + bungieUser.getID() + "\nTime Played: " + bungieUser.getTimePlayed() / 60, false);

				messageChannel.sendMessage(embedBuilder.build()).queue();
			} else {
				messageChannel.sendMessage("No users with that name found").queue();
			}
		}
	}
}
