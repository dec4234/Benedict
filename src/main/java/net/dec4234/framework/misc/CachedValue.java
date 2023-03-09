package net.dec4234.framework.misc;

import net.dec4234.javadestinyapi.material.clan.Clan;
import net.dec4234.src.BenedictMain;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.text.SimpleDateFormat;

public class CachedValue {

    private static Clan clan;

    public static Guild getGuild() {
        return BenedictMain.getJDA().getGuildById("432198675087753217");
    }

    public static String getDiscordInvite() {
        return "https://discord.gg/UX47Zyn";
    }

    public static Clan getClan() {
        if (clan == null) {
            clan = new Clan("Heavenly Mayhem");
        }

        return clan;
    }

    // Roles
    public static Role getElderRole() {
        return BenedictMain.getJDA().getRoleById("432199389109551110");
    }

    public static Role getMemberRole() {
        return BenedictMain.getJDA().getRoleById("432199470906867723");
    }

    public static Role getLFGRole() {
        return BenedictMain.getJDA().getRoleById("434147551646842890");
    }

    public static Role getVerifiedRole() {
        return BenedictMain.getJDA().getRoleById("743489124945625189");
    }

    // Channels
    public static TextChannel getLog2Channel() {
        return getGuild().getTextChannelById("478599141149376512");
    }

    public static TextChannel getRaidChannel() {
        return getGuild().getTextChannelById("509848730531528704");
    }

    public static TextChannel getAllChatChannel() {
        return getGuild().getTextChannelById("432201536966361099");
    }

    public static TextChannel getSpamLogChannel() {
        return getGuild().getTextChannelById("947487114038226994");
    }

    // Emojis
    public static Emote getGreenCheckMark() {
        return getGuild().getEmoteById("517808157368647690");
    }

    // Misc
    public static String getDec4234ID() {
        return "252508637753376779";
    }

    // Date Formats
    public static SimpleDateFormat getSDF() {
        return new SimpleDateFormat("MM-dd-yyyy hh:mm a z");
    }

    public static SimpleDateFormat getFancySDF() {
        return new SimpleDateFormat("EEEEEEEEE, MMMMMMMMMM dd, yyyy hh:mm a z");
    }
}
