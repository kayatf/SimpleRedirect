package yt.syntax;

import com.mongodb.ConnectionString;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.eclipse.jetty.http.HttpStatus;
import yt.syntax.commands.*;
import yt.syntax.commands.impl.Command;
import yt.syntax.commands.impl.CommandListener;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * created on 17.02.19 / 13:20
 *
 * @author Daniel Riegler
 */
@Slf4j
public class SimpleRedirect {

    @Getter
    private static SimpleRedirect instance;

    @Getter
    private final List<Command> commands = Arrays.asList(
            new ClearCommand(),
            new HelpCommand(),
            new ExitCommand(),
            new CreateCommand(),
            new RemoveCommand(),
            new ListCommand()
    );

    @Getter
    private DatabaseManager databaseManager;

    private WebServer webServer;

    private SimpleRedirect(final String[] args) {

        instance = this;

        if (args.length != 2 || StringUtil.isEmpty(args[0]) || StringUtil.isEmpty(args[1])) {
            System.err.println("Please provide a mongodb connection-uri as your first parameter and an application-port as your second.");
            System.exit(-1);
            return;
        }

        this.databaseManager = new DatabaseManager(new ConnectionString(args[0]));
        this.webServer = new WebServer(Integer.parseInt(args[1]), app -> app.get("*", context -> {
            context.result(new CompletableFuture<String>());
            final var key = !context.splats().isEmpty() ? context.splats().get(0).replace("/", "") : null;
            this.databaseManager.find(key).whenComplete((doc, x) -> {
                if (doc == null)
                    this.webServer.renderError(context.status(HttpStatus.NOT_FOUND_404));
                else
                    this.webServer.renderRedirect(context, doc.getString("url"));
            });
        }));
        this.webServer.start();

        this.listenForCommands();

    }

    private void listenForCommands() {
        new Thread(() -> {

            try {
                Thread.sleep(500L);
            } catch (final InterruptedException ignored) {
            }

            log.info("Now listening to commands, type 'help' for a list of them.");

            final CommandListener listener = new CommandListener();

            try (final Scanner scanner = new Scanner(System.in)) {
                while (scanner.hasNext())
                    listener.accept(scanner.nextLine());
            }

        }, "CommandListener").start();
    }

    public static void main(final String[] args) {
        final var instance = new SimpleRedirect(args);
        Runtime.getRuntime().addShutdownHook(new Thread(instance::shutdown));
    }

    private void shutdown() {
        if (this.webServer != null)
            this.webServer.stop();
        if (this.databaseManager != null) {
            try {
                this.databaseManager.close();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
