package net.dec4234.framework.commands;

import lombok.Getter;
import net.dec4234.src.BenedictMain;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.internal.requests.restaction.CommandListUpdateActionImpl;

import java.util.HashMap;

public abstract class SlashCommand implements ISlashCommand{

	@Getter private static HashMap<String, SlashCommand> slashCommandMap = new HashMap<>();
	@Getter private static CommandListUpdateAction commands = BenedictMain.getJDA().updateCommands();

	@Getter private String name;
	@Getter private String description;
	@Getter private OptionData[] optionData;

	public SlashCommand(String name, String description, OptionData... optionData) {
		this.name = name;
		this.description = description;
		this.optionData = optionData;

		slashCommandMap.put(name, this);

		addCommand();
	}

	private void addCommand() {
		CommandData commandData = new CommandData(getName(), getDescription())
				.addOptions(getOptionData()).setDefaultEnabled(true);

		commands = commands.addCommands(commandData);

	}

	public static void registerSlashCommands() {
		commands.queue();
	}

}
