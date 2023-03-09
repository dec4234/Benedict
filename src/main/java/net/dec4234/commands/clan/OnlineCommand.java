package net.dec4234.commands.clan;

import net.dec4234.framework.commands.SlashCommand;
import net.dec4234.framework.misc.CachedValue;
import net.dec4234.javadestinyapi.material.clan.ClanMember;
import net.dec4234.javadestinyapi.material.user.BungieUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.awt.*;
import java.util.List;


public class OnlineCommand extends SlashCommand {

    public OnlineCommand() {
        super("online", "Get a list of all online clan members");
    }

    @Override
    public void onCommand(SlashCommandEvent event, MessageChannel messageChannel, Member member) {
        List<ClanMember> online = CachedValue.getClan().getOnlineMembers();

        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.ORANGE).setTitle("Online Members");
        String desc = "There are currently " + online.size() + " online members:\n";

        for(BungieUser bungieUser : online) {
            desc += "\n" + bungieUser.getGlobalDisplayName();
        }

        embedBuilder.setDescription(desc);
        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
