package su.nexmedia.engine.utils;

import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.EngineConfig;
import su.nexmedia.engine.utils.random.Rnd;
import su.nexmedia.engine.utils.regex.TimedMatcher;

import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class StringUtil {

    private static final Pattern ID_PATTERN = Pattern.compile("[<>\\%\\$\\!\\@\\#\\^\\&\\*\\(\\)\\,\\.\\'\\:\\;\\\"\\}\\]\\{\\[\\=\\+\\`\\~\\\\]");//Pattern.compile("[^a-zA-Zа-яА-Я_0-9]");
    private static final Pattern ID_STRICT_PATTERN = Pattern.compile("[^a-zA-Zа-яА-Я_0-9]");

    @NotNull
    public static String oneSpace(@NotNull String str) {
        return str.trim().replaceAll("\\s+", " ");
    }

    @NotNull
    public static String noSpace(@NotNull String str) {
        return str.trim().replaceAll("\\s+", "");
    }

    @NotNull
    @Deprecated
    public static List<String> replace(@NotNull List<String> orig, @NotNull String placeholder, boolean keep, String... replacer) {
        return StringUtil.replace(orig, placeholder, keep, Arrays.asList(replacer));
    }

    @NotNull
    @Deprecated
    public static List<String> replace(@NotNull List<String> orig, @NotNull String placeholder, boolean keep, List<String> replacer) {
        List<String> replaced = new ArrayList<>();
        for (String line : orig) {
            if (line.contains(placeholder)) {
                if (!keep) {
                    replaced.addAll(replacer);
                }
                else {
                    replacer.forEach(lineRep -> replaced.add(line.replace(placeholder, lineRep)));
                }
                continue;
            }
            replaced.add(line);
        }

        return replaced;
    }

    @NotNull
    public static List<String> replaceInList(@NotNull List<String> orig, @NotNull String placeholder, String... replacer) {
        return StringUtil.replaceInList(orig, placeholder, Arrays.asList(replacer));
    }

    @NotNull
    public static List<String> replaceInList(@NotNull List<String> orig, @NotNull String placeholder, @NotNull List<String> replacer) {
        List<String> replaced = new ArrayList<>();
        for (String line : orig) {
            if (line.equalsIgnoreCase(placeholder)) {
                replaced.addAll(replacer);
            }
            else replaced.add(line);
        }
        return replaced;
    }

    @NotNull
    public static String replaceEach(@NotNull String text, @NotNull List<Pair<String, Supplier<String>>> replacements) {
        if (text.isEmpty() || replacements.isEmpty()) {
            return text;
        }

        final int searchLength = replacements.size();
        // keep track of which still have matches
        final boolean[] noMoreMatchesForReplIndex = new boolean[searchLength];

        // index on index that the match was found
        int textIndex = -1;
        int replaceIndex = -1;
        int tempIndex;

        // index of replace array that will replace the search string found
        // NOTE: logic duplicated below START
        for (int i = 0; i < searchLength; i++) {
            if (noMoreMatchesForReplIndex[i]) {
                continue;
            }
            tempIndex = text.indexOf(replacements.get(i).getFirst());

            // see if we need to keep searching for this
            if (tempIndex == -1) {
                noMoreMatchesForReplIndex[i] = true;
            }
            else if (textIndex == -1 || tempIndex < textIndex) {
                textIndex = tempIndex;
                replaceIndex = i;
            }
        }
        // NOTE: logic mostly below END

        // no search strings found, we are done
        if (textIndex == -1) {
            return text;
        }

        int start = 0;
        final StringBuilder buf = new StringBuilder();
        while (textIndex != -1) {
            for (int i = start; i < textIndex; i++) {
                buf.append(text.charAt(i));
            }
            buf.append(replacements.get(replaceIndex).getSecond().get());

            start = textIndex + replacements.get(replaceIndex).getFirst().length();

            textIndex = -1;
            replaceIndex = -1;
            // find the next earliest match
            // NOTE: logic mostly duplicated above START
            for (int i = 0; i < searchLength; i++) {
                if (noMoreMatchesForReplIndex[i]) {
                    continue;
                }
                tempIndex = text.indexOf(replacements.get(i).getFirst(), start);

                // see if we need to keep searching for this
                if (tempIndex == -1) {
                    noMoreMatchesForReplIndex[i] = true;
                } else if (textIndex == -1 || tempIndex < textIndex) {
                    textIndex = tempIndex;
                    replaceIndex = i;
                }
            }
            // NOTE: logic duplicated above END

        }
        final int textLength = text.length();
        for (int i = start; i < textLength; i++) {
            buf.append(text.charAt(i));
        }
        return buf.toString();
    }

    public static double getDouble(@NotNull String input, double def) {
        return getDouble(input, def, false);
    }

    public static double getDouble(@NotNull String input, double def, boolean allowNegative) {
        try {
            double amount = Double.parseDouble(input);
            if (Double.isNaN(amount) || Double.isInfinite(amount)) return def;

            return (amount < 0D && !allowNegative ? def : amount);
        }
        catch (NumberFormatException ex) {
            return def;
        }
    }

    public static int getInteger(@NotNull String input, int def) {
        return getInteger(input, def, false);
    }

    public static int getInteger(@NotNull String input, int def, boolean allowNegative) {
        return (int) getDouble(input, def, allowNegative);
    }

    public static int[] getIntArray(@NotNull String str) {
        String[] split = noSpace(str).split(",");
        int[] array = new int[split.length];
        for (int index = 0; index < split.length; index++) {
            try {
                array[index] = Integer.parseInt(split[index]);
            }
            catch (NumberFormatException e) {
                array[index] = 0;
            }
        }
        return array;
    }

    @NotNull
    public static <T extends Enum<T>> Optional<T> getEnum(@NotNull String str, @NotNull Class<T> clazz) {
        try {
            return Optional.of(Enum.valueOf(clazz, str.toUpperCase()));
        }
        catch (Exception ex) {
            return Optional.empty();
        }
    }

    @NotNull
    public static Color parseColor(@NotNull String colorRaw) {
        String[] rgb = colorRaw.split(",");
        int red = StringUtil.getInteger(rgb[0], 0);
        if (red < 0) red = Rnd.get(255);

        int green = rgb.length >= 2 ? StringUtil.getInteger(rgb[1], 0) : 0;
        if (green < 0) green = Rnd.get(255);

        int blue = rgb.length >= 3 ? StringUtil.getInteger(rgb[2], 0) : 0;
        if (blue < 0) blue = Rnd.get(255);

        return Color.fromRGB(red, green, blue);
    }

    @NotNull
    public static String lowerCaseUnderscore(@NotNull String str) {
        return lowerCaseUnderscore(str, -1);
    }

    @NotNull
    public static String lowerCaseUnderscore(@NotNull String str, int length) {
        return lowerCaseAndClean(str, ID_PATTERN, length);
    }

    @NotNull
    public static String lowerCaseUnderscoreStrict(@NotNull String str) {
        return lowerCaseUnderscoreStrict(str, -1);
    }

    @NotNull
    public static String lowerCaseUnderscoreStrict(@NotNull String str, int length) {
        return lowerCaseAndClean(str, ID_STRICT_PATTERN, length);
    }

    @NotNull
    private static String lowerCaseAndClean(@NotNull String str, @NotNull Pattern pattern, int length) {
        String clean = Colorizer.restrip(str).toLowerCase().replace(" ", "_");
        if (length > 0 && clean.length() > length) {
            clean = clean.substring(0, length);
        }

        TimedMatcher matcher = TimedMatcher.create(pattern, clean, 200);
        //Matcher matcher = RegexUtil.getMatcher(ID_STRICT_PATTERN, clean);
        return matcher.replaceAll("");
    }

    @NotNull
    public static String capitalizeUnderscored(@NotNull String str) {
        return capitalizeFully(str.replace("_", " "));
    }

    @NotNull
    public static String capitalizeFully(@NotNull String str) {
        if (str.length() == 0) return str;

        return capitalize(str.toLowerCase());
    }

    @NotNull
    public static String capitalize(@NotNull String str) {
        if (str.length() == 0) return str;

        int length = str.length();
        StringBuilder builder = new StringBuilder(length);
        boolean capitalizeNext = true;

        for (int index = 0; index < length; ++index) {
            char letter = str.charAt(index);
            if (Character.isWhitespace(letter)) {
                builder.append(letter);
                capitalizeNext = true;
            }
            else if (capitalizeNext) {
                builder.append(Character.toTitleCase(letter));
                capitalizeNext = false;
            }
            else {
                builder.append(letter);
            }
        }
        return builder.toString();
    }

    @NotNull
    public static String capitalizeFirstLetter(@NotNull String original) {
        if (original.isEmpty()) return original;
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    /**
     * @param original List to remove empty lines from.
     * @return A list with no multiple empty lines in a row.
     */
    @NotNull
    public static List<String> stripEmpty(@NotNull List<String> original) {
        List<String> stripped = new ArrayList<>();
        for (int index = 0; index < original.size(); index++) {
            String line = original.get(index);
            if (line.isEmpty()) {
                String last = stripped.isEmpty() ? null : stripped.get(stripped.size() - 1);
                if (last == null || last.isEmpty() || index == (original.size() - 1)) continue;
            }
            stripped.add(line);
        }
        return stripped;
    }

    /**
     * Kinda half-smart completer like in IDEA by partial word matches.
     * @param results A list of all completions.
     * @param input A string to find partial matches for.
     * @param steps Part's size.
     * @return A list of completions that has partial matches to the given string.
     */
    @NotNull
    public static List<String> getByPartialMatches(@NotNull List<String> results, @NotNull String input, int steps) {
        if (input.length() > EngineConfig.TAB_COMPLETER_REGEX_MAX_LENGTH.get()) {
            return copyPartialMatches(input, results);
        }

        StringBuilder builder = new StringBuilder();
        for (char letter : input.toLowerCase().toCharArray()) {
            builder.append(Pattern.quote(String.valueOf(letter))).append("(?:.*)");
        }

        Pattern pattern = Pattern.compile(builder.toString());
        return new ArrayList<>(results.stream()
            .filter(orig -> {
                TimedMatcher matcher = TimedMatcher.create(pattern, orig.toLowerCase(), EngineConfig.TAB_COMPLETER_REGEX_TIMEOUT.get());
                return matcher.find();
                //pattern.matcher(orig.toLowerCase()).find()
            })
            .sorted(String::compareTo)
            .toList());
    }

    @NotNull
    private static List<String> copyPartialMatches(@NotNull String token, @NotNull Collection<String> originals) {
        List<String> collection = new ArrayList<>();

        for (String string : originals) {
            if (startsWithIgnoreCase(string, token)) {
                collection.add(string);
            }
        }

        return collection;
    }

    private static boolean startsWithIgnoreCase(@NotNull String string, @NotNull String prefix) {
        return string.length() >= prefix.length() && string.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    @NotNull
    public static String extractCommandName(@NotNull String command) {
        String commandName = Colorizer.strip(command).split(" ")[0].substring(1);

        String[] pluginPrefix = commandName.split(":");
        if (pluginPrefix.length == 2) {
            commandName = pluginPrefix[1];
        }

        return commandName;
    }
}
