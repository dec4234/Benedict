package net.dec4234.commands.stats.clan;

import net.dec4234.framework.commands.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.awt.*;

public class ClanStatsCommand extends SlashCommand {

	public ClanStatsCommand() {
		super("clanstats", "Track member counts for the past 30 days");
	}

	@Override
	public void onCommand(SlashCommandEvent event, MessageChannel messageChannel, Member member) {
		EmbedBuilder loading = new EmbedBuilder().setTitle("Loading").setColor(Color.YELLOW).setDescription("Loading, please wait");

		event.replyEmbeds(loading.build()).queue();

		EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Results").setColor(Color.ORANGE);

		embedBuilder.setImage(new ClanStats().getPastDaysChart(30));

		event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
	}
}
