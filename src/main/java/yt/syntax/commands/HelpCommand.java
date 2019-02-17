package yt.syntax.commands;

import yt.syntax.SimpleRedirect;
import yt.syntax.StringUtil;
import yt.syntax.commands.impl.Command;

/**
 * created on 17.02.19 / 15:49
 *
 * @author Daniel Riegler
 */
public class HelpCommand implements Command {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public void execute(final String[] args) {
        System.out.println("Available commands:");
        SimpleRedirect.getInstance().getCommands().forEach(command -> {
            if (!StringUtil.isEmpty(command.getDescription()))
                System.out.printf("- %s : %s\n", command.getName(), command.getDescription());
        });
    }
}
