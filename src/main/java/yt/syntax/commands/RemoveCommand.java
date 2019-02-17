package yt.syntax.commands;

import lombok.extern.slf4j.Slf4j;
import yt.syntax.SimpleRedirect;
import yt.syntax.StringUtil;
import yt.syntax.commands.impl.Command;

/**
 * created on 17.02.19 / 16:26
 *
 * @author Daniel Riegler
 */
@Slf4j
public class RemoveCommand implements Command {

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "Removes a redirect. (Params: <KEY>)";
    }

    @Override
    public void execute(final String[] args) {
        if (args.length != 1 || StringUtil.isEmpty(args[0]))
            log.info("Please issue a key.");
        else {
            SimpleRedirect.getInstance().getDatabaseManager().has(args[0]).thenAccept(bool -> {
                if (!bool)
                    log.info("There isn't a redirect assigned to the issued key.");
                else {
                    SimpleRedirect.getInstance().getDatabaseManager().delete(args[0]).whenComplete((res, x) -> {
                        if (x != null)
                            log.error("Couldn't remove the redirect.", x);
                        else
                            log.info(String.format("Removed the redirect : %s", args[0]));
                    });
                }
            });
        }
    }

}
