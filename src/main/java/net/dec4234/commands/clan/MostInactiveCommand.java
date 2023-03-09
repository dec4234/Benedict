package net.dec4234.commands.clan;

import net.dec4234.framework.commands.SlashCommand;
import net.dec4234.framework.misc.CachedValue;
import net.dec4234.javadestinyapi.material.clan.ClanMember;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.awt.*;

public class MostInactiveCommand extends SlashCommand {

    public MostInactiveCommand() {
        super("mostinactive", "Lists the most inactive members of the clan");
    }

    @Override
    public void onCommand(SlashCommandEvent event, MessageChannel messageChannel, Member member) {
        event.deferReply().queue();

        java.util.List<ClanMember> list = CachedValue.getClan().getMostInactiveMembers(8, "4611686018494271874", "4611686018470828611");

        EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Most Inactive Members").setColor(Color.ORANGE);

        for(ClanMember clanMember : list) {
            embedBuilder.addField(clanMember.getGlobalDisplayName() + " - " + clanMember.getID(), clanMember.getDaysSinceLastPlayed() + " days", false);
        }

        event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
    }
}
