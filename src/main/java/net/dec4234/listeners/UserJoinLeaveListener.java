package net.dec4234.listeners;

import net.dec4234.database.collections.UserDatabase;
import net.dec4234.database.collections.UserManagement;
import net.dec4234.database.framework.ClanMember;
import net.dec4234.database.framework.MongoKey;
import net.dec4234.framework.listeners.EventHandler;
import net.dec4234.framework.listeners.Listener;
import net.dec4234.framework.misc.CachedValue;
import net.dec4234.framework.util.MessageUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;

import java.awt.*;

public class UserJoinLeaveListener implements Listener {

	private UserDatabase userDatabase = new UserDatabase();

	@EventHandler
	public void onJoin(GuildMemberJoinEvent event) {
		Member member = event.getMember();

		EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.ORANGE)
													  .setTitle("Welcome to " + event.getGuild().getName())
													  .setDescription("Destiny 2 Clan Discord Server")
													  .setThumbnail(event.getGuild().getIconUrl())
													  .addField("\uD83D\uDCF0 Terms of Membership", "- Don't spam and treat others how you would like to be treated", false)
													  .addField("✌️Interested in joining?", "Respond with **!apply** to start the application process", false);

		MessageUtil.sendPrivateMessage(member.getUser(), embedBuilder);

		event.getGuild().addRoleToMember(member.getId(), CachedValue.getLFGRole()).queue();

		UserDatabase userDatabase = new UserDatabase();

		if(userDatabase.hasDataDiscord(member.getId())) {
			ClanMember clanMember = userDatabase.getClanMember(userDatabase.getDoc(MongoKey.DISCORDID.getKey(), member.getId()).getString(MongoKey.BUNGIEID.getKey()));
			if(CachedValue.getClan().isMember(clanMember.getBungieID())) {
				event.getGuild().addRoleToMember(member.getId(), CachedValue.getMemberRole()).queue();
				event.getGuild().removeRoleFromMember(member.getId(), CachedValue.getLFGRole()).queue();
			}
		}
	}

	@EventHandler
	public void onLeave(GuildMemberRemoveEvent event) {
		Member member = event.getMember();

		if(userDatabase.hasDataDiscord(member.getId())) {
			ClanMember clanMember = userDatabase.getClanMember(userDatabase.getUserDocumentDiscord(member.getId()).getString(MongoKey.BUNGIEID.getKey()));
			if(clanMember.isMember()) {
				if(!CachedValue.getClan().isMember(clanMember.getBungieID())) {
					new UserManagement().userLeft(clanMember.getBungieID(), "User Left Discord");
				} else {
					EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Clan Member Left Discord").setColor(Color.ORANGE);
					embedBuilder.setThumbnail(member.getUser().getEffectiveAvatarUrl());
					embedBuilder.addField(member.getEffectiveName(), "<@" + member.getId() + ">", false);
					embedBuilder.setFooter("!kickmember " + clanMember.getBungieID());

					CachedValue.getLog2Channel().sendMessage(embedBuilder.build()).queue();
				}
			}
		}
	}
}
