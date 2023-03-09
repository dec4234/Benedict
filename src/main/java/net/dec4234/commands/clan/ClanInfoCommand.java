package net.dec4234.commands.clan;

import net.dec4234.framework.commands.SlashCommand;
import net.dec4234.framework.misc.CachedValue;
import net.dec4234.javadestinyapi.material.clan.Clan;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.awt.*;

public class ClanInfoCommand extends SlashCommand {

	public ClanInfoCommand() {
		super("claninfo", "Retrieve information about the clan");
	}

	@Override
	public void onCommand(SlashCommandEvent event, MessageChannel messageChannel, Member member) {
		Clan clan = CachedValue.getClan();

		EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Clan Info").setThumbnail(CachedValue.getGuild().getIconUrl()).setColor(Color.BLUE);
		embedBuilder.addField(clan.getClanName(),
							  clan.getClanDescription(), false);
		embedBuilder.addField(clan.getMemberCount() + " current members", clan.getClanManagement().getPendingMembers().size() + " pending members", false);

		embedBuilder.setFooter("https://www.bungie.net/en/ClanV2?groupid=3074427");

		event.replyEmbeds(embedBuilder.build()).queue();
	}
}
