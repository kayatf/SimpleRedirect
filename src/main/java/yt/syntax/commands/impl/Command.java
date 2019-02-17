package yt.syntax.commands.impl;

/**
 * created on 17.02.19 / 15:37
 *
 * @author Daniel Riegler
 */
public interface Command {

    String getName();

    default String getDescription() {
        return null;
    }

    void execute(final String[] args);

}
