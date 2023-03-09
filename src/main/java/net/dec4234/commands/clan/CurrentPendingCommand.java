package net.dec4234.commands.clan;

import net.dec4234.framework.commands.SlashCommand;
import net.dec4234.framework.misc.CachedValue;
import net.dec4234.javadestinyapi.material.user.BungieUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.awt.*;
import java.util.List;

public class CurrentPendingCommand extends SlashCommand {

    public CurrentPendingCommand() {
        super("currentpending", "Get a list of all currently pending members");
    }

    @Override
    public void onCommand(SlashCommandEvent event, MessageChannel messageChannel, Member member) {

        List<BungieUser> pending = CachedValue.getClan().getClanManagement().getPendingMembers();

        EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Pending Members");

        if(pending.size() > 0) {
            embedBuilder.setColor(Color.CYAN);

            for(BungieUser bungieUser : pending) {
                embedBuilder.addField("" + bungieUser.getGlobalDisplayName(), "", false);
            }
        } else {
            embedBuilder.setColor(Color.RED);
            embedBuilder.setDescription("No current pending requests");
        }

        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
