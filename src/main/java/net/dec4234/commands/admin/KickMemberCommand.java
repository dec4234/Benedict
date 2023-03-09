package net.dec4234.commands.admin;

import net.dec4234.database.collections.UserManagement;
import net.dec4234.framework.commands.BenedictCommand;
import net.dec4234.framework.misc.CachedValue;
import net.dec4234.javadestinyapi.material.clan.ClanManagement;
import net.dec4234.javadestinyapi.material.user.BungieUser;
import net.dec4234.javadestinyapi.material.user.DestinyPlatform;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class KickMemberCommand extends BenedictCommand {

	private UserManagement userManagement = new UserManagement();
	private ClanManagement clanManagement = CachedValue.getClan().getClanManagement();

	public KickMemberCommand() {
		super("kickmember");
	}

	@Override
	public void onCommand(MessageChannel messageChannel, User user, String[] args) {
		if(!user.getId().equals(CachedValue.getDec4234ID())) {
			return;
		}
		if(args.length < 1) {
			return;
		}
		String bungieID = args[0];
		if(userManagement.hasData(bungieID)) {
			if(CachedValue.getClan().isMember(bungieID)) {
				clanManagement.kickPlayer(new BungieUser(bungieID));
				userManagement.userLeft(bungieID, "Member Kicked By Admin");
			} else {
				if(userManagement.getClanMember(bungieID).isMember()) {
					userManagement.userLeft(bungieID, "KickMemberCommand 2");
				} else {
					messageChannel.sendMessage("User is not a current member " + bungieID).queue();
				}
			}
		} else {
			BungieUser bungieUser = new BungieUser(bungieID, DestinyPlatform.STEAM);
			clanManagement.kickPlayer(bungieUser);
			messageChannel.sendMessage("Kicked " + bungieUser.getDisplayName() + " No records of this user.").queue();
		}
	}
}
