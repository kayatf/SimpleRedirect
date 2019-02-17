package yt.syntax.commands;

import yt.syntax.commands.impl.Command;

/**
 * created on 17.02.19 / 16:33
 *
 * @author Daniel Riegler
 */
public class ClearCommand implements Command {

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public void execute(final String[] args) {
        for (int i = 0; i < 250; i++)
            System.out.println("\b");
    }

}
