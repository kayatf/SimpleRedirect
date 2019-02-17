package yt.syntax.commands.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import yt.syntax.SimpleRedirect;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * created on 17.02.19 / 15:39
 *
 * @author Daniel Riegler
 */
@Slf4j
public class CommandListener implements Consumer<String> {
    @Override
    public void accept(final String string) {
        final var args = string.split(" ");
        final var command = SimpleRedirect.getInstance().getCommands().stream().filter(cmd -> cmd.getName().equals(args[0].toLowerCase())).findFirst().orElse(null);
        if (command == null)
            log.info("This command does not exist! Type 'help' for a list of all commands.");
        else
            command.execute(Arrays.copyOfRange(args, 1, args.length));
    }
}
