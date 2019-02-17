package yt.syntax.commands;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.bson.Document;
import yt.syntax.SimpleRedirect;
import yt.syntax.StringUtil;
import yt.syntax.commands.impl.Command;

/**
 * created on 17.02.19 / 15:54
 *
 * @author Daniel Riegler
 */
@Slf4j
public class CreateCommand implements Command {

    private static final int KEY_LENGTH = 8;

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Creates a redirect. (Params: <URL,[KEY]>)";
    }

    @Override
    @SuppressWarnings("all")
    public void execute(final String[] args) {
        if (args.length < 1 || args.length > 2)
            log.info("Please issue an url-string.");
        else if (!StringUtil.isURL(args[0]))
            log.info("The issued url-string isn't valid.");
        else {
            final String key;
            if (args.length == 1 || StringUtil.isEmpty(args[1]))
                key = StringUtil.getRandom(KEY_LENGTH);
            else {
                if (!StringUtil.isAlphanumeric(args[1])) {
                    log.info("The issued key isn't alphanumeric.");
                    return;
                }
                key = args[1];
            }
            SimpleRedirect.getInstance().getDatabaseManager().has(key).thenAccept(bool -> {
                if (bool)
                    log.info("There is already a redirect assigned to the issued key.");
                else {
                    final var document = new Document("key", key).append("url", args[0]);
                    SimpleRedirect.getInstance().getDatabaseManager().insertOrUpdate(key, document).whenComplete((o, x) -> {
                        if (x != null)
                            log.error("Couldn't create the redirect.", x);
                        else
                            log.info(String.format("Created the redirect : %s", key));
                    });
                }
            });
        }
    }
}
