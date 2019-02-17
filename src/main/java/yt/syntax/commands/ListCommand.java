package yt.syntax.commands;

import lombok.extern.slf4j.Slf4j;
import yt.syntax.SimpleRedirect;
import yt.syntax.commands.impl.Command;

/**
 * created on 17.02.19 / 16:34
 *
 * @author Daniel Riegler
 */
@Slf4j
public class ListCommand implements Command {

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "Lists all redirects an it's destination.";
    }

    @Override
    public void execute(final String[] args) {
        SimpleRedirect.getInstance().getDatabaseManager().list().whenComplete((list, x) -> {
            if (list == null || list.isEmpty())
                log.info("Could not list any redirects.");
            else {
                System.out.printf("Available redirects (%s):\n", list.size());
                list.forEach(doc -> System.out.printf("- %s : %s\n", doc.getString("key"), doc.getString("url")));
            }
        });
    }
}
