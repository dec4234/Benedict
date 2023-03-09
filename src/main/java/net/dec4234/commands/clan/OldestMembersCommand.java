package net.dec4234.commands.clan;

import net.dec4234.framework.commands.SlashCommand;
import net.dec4234.framework.misc.CachedValue;
import net.dec4234.javadestinyapi.material.clan.ClanMember;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class OldestMembersCommand extends SlashCommand {

    public OldestMembersCommand() {
        super("oldestmembers", "Oldest continuous members of the clan");
    }

    @Override
    public void onCommand(SlashCommandEvent event, MessageChannel messageChannel, Member member) {
        event.deferReply().queue();

        List<ClanMember> members = CachedValue.getClan().getMembers();
        List<ClanMember> sorted = new LinkedList<>();

        ClanMember oldest = null;

        for(int i = 0; i < 10; i++) {
            for(ClanMember inner : members) {
                if(inner != oldest && !sorted.contains(inner)) {
                    if(oldest == null || inner.getJoinDate().getTime() < oldest.getJoinDate().getTime()) {
                        oldest = inner;
                    }
                }
            }

            sorted.add(oldest);
            oldest = null;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Oldest Members").setColor(Color.CYAN);

        for(ClanMember clanMember : sorted) {
            String name = clanMember.getSupplementalDisplayName();

            // If someone has not logged on since Season of the Splicer or some other reason they don't have a bungie name here
            if(!name.contains("#")) {
                name = clanMember.getDisplayName();
            }

            embedBuilder.addField("" + name, "" + CachedValue.getSDF().format(clanMember.getJoinDate()), false);
        }

        event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
    }
}
