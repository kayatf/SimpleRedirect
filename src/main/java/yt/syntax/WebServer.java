package yt.syntax;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import io.javalin.Context;
import io.javalin.Extension;
import io.javalin.Javalin;
import lombok.var;
import org.eclipse.jetty.http.HttpStatus;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * created on 17.02.19 / 14:20
 *
 * @author Daniel Riegler
 */
@SuppressWarnings("WeakerAccess")
public class WebServer extends Javalin {

    private final Map<String, Template> templates;

    public WebServer(final int port, final Extension... extensions) {
        super.disableStartupBanner();
        super.disableRequestCache();
        super.dontIgnoreTrailingSlashes();
        super.enableCaseSensitiveUrls();
        super.port(port);

        super.defaultContentType("text/html");

        this.templates = this.initTemplates("error", "redirect");

        super.error(HttpStatus.INTERNAL_SERVER_ERROR_500, this::renderError);

        super.exception(Exception.class, (exception, context) -> {
            context.req.setAttribute("error", exception.toString());
            context.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
        });

        Arrays.asList(extensions).forEach(super::register);
    }

    @SuppressWarnings("all")
    public void renderError(final Context context) {

        final var errorContext = new HashMap<String, Object>();
        errorContext.put("code", context.status());
        errorContext.put("route", context.path());

        final var error = context.req.getAttribute("error") != null ? context.req.getAttribute("error").toString() : null;
        errorContext.put("message", StringUtil.isEmpty(error) ? HttpStatus.getMessage(context.status()) : error);

        final var future = (CompletableFuture<String>) context.resultFuture();

        try {
            future.complete(this.templates.get("error").apply(errorContext));
        } catch (final IOException e) {
            future.completeExceptionally(e);
        }
    }

    @SuppressWarnings("all")
    public void renderRedirect(final Context context, final String url) {

        final var redirectContext = new HashMap<>();
        redirectContext.put("url", url);

        final var future = (CompletableFuture<String>) context.resultFuture();

        try {
            future.complete(this.templates.get("redirect").apply(redirectContext));
        } catch (final IOException e) {
            future.completeExceptionally(e);
        }

    }

    private Map<String, Template> initTemplates(final String... templates) {
        final var map = new HashMap<String, Template>();
        final var handlebars = new Handlebars(new ClassPathTemplateLoader("/templates"));
        Arrays.asList(templates).forEach(template -> {
            try {
                map.put(template, handlebars.compile(template));
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        });
        return map;
    }

}
