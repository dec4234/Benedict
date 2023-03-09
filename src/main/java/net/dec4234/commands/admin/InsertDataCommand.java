package net.dec4234.commands.admin;

import net.dec4234.database.collections.UserDatabase;
import net.dec4234.database.framework.ClanMember;
import net.dec4234.framework.commands.BenedictCommand;
import net.dec4234.framework.misc.CachedValue;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class InsertDataCommand extends BenedictCommand {

	public InsertDataCommand() {
		super("insertdata");
	}

	@Override
	public void onCommand(MessageChannel messageChannel, User user, String[] args) {
		if(!isDec4234(user)) {
			return;
		}

		if(args.length >= 3) {
			String bungieID = args[0];
			String discordID = args[1];
			boolean isMember = Boolean.parseBoolean(args[2]);

			ClanMember clanMember = new ClanMember(bungieID, discordID, isMember);

			new UserDatabase().save(clanMember);

			messageChannel.sendMessage("Saved data on " + clanMember.getDestinyName() + "/" + clanMember.getDiscordName()).queue();

			Guild guild = CachedValue.getGuild();
			Member member = guild.getMemberById(discordID);

			if(member != null) {
				guild.removeRoleFromMember(member, CachedValue.getLFGRole()).queue();
				guild.addRoleToMember(member, CachedValue.getMemberRole()).queue();
			}
		}
	}
}
