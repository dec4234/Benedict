package net.dec4234.commands.clan;

import net.dec4234.database.collections.UserDatabase;
import net.dec4234.framework.commands.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.awt.*;

public class DatabaseStatsCommand extends SlashCommand {

    public DatabaseStatsCommand() {
        super("databasestats", "Get some stats about the database");
    }

    @Override
    public void onCommand(SlashCommandEvent event, MessageChannel messageChannel, Member member) {
        EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Database Stats").setColor(Color.MAGENTA);
        embedBuilder.addField("Users Tracked: " + new UserDatabase().getAllDocuments().size(), "", false);

        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
