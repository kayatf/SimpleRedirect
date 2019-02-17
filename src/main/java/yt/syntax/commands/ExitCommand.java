package yt.syntax.commands;

import yt.syntax.commands.impl.Command;

/**
 * created on 17.02.19 / 15:40
 *
 * @author Daniel Riegler
 */
public class ExitCommand implements Command {

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public String getDescription() {
        return "Shuts down the software.";
    }

    @Override
    public void execute(final String[] args) {
        System.exit(0);
    }

}
