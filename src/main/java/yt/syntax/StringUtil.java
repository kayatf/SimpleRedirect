package yt.syntax;

import lombok.experimental.UtilityClass;
import lombok.var;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Random;

/**
 * created on 17.02.19 / 13:23
 *
 * @author Daniel Riegler
 */
@UtilityClass
public class StringUtil {

    private char[] RANDOM_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    private Random RANDOM = new SecureRandom();

    public boolean isEmpty(final String string) {
        return string == null || string.trim().isEmpty();
    }

    public boolean isURL(final String string) {
        if (isEmpty(string)) return false;
        try {
            new URL(string);
        } catch (final MalformedURLException e) {
            return false;
        }
        return true;
    }

    public String getRandom(final int length) {
        final var builder = new StringBuilder();
        for (int i = 0; i < length; i++)
            builder.append(RANDOM_CHARS[RANDOM.nextInt(RANDOM_CHARS.length)]);
        return builder.toString();
    }

    public boolean isAlphanumeric(final String string) {
        return !isEmpty(string) && string.matches("[A-Za-z0-9]+");
    }

}
