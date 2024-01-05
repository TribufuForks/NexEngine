package su.nexmedia.engine.api.placeholder;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.Pair;
import su.nexmedia.engine.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class PlaceholderMap {

    private final List<Pair<String, Supplier<String>>> keys;

    public PlaceholderMap() {
        this(new ArrayList<>());
    }

    public PlaceholderMap(@NotNull PlaceholderMap other) {
        this(other.getKeys());
    }

    public PlaceholderMap(@NotNull List<Pair<String, Supplier<String>>> keys) {
        this.keys = new ArrayList<>(keys);
    }

    @NotNull
    public static PlaceholderMap fusion(@NotNull PlaceholderMap... others) {
        PlaceholderMap map = new PlaceholderMap();
        for (PlaceholderMap other : others) {
            map.add(other);
        }
        return map;
    }

    @NotNull
    public static PlaceholderMap fusion(@NotNull Placeholder... others) {
        PlaceholderMap map = new PlaceholderMap();
        for (Placeholder other : others) {
            map.add(other.getPlaceholders());
        }
        return map;
    }

    @NotNull
    public List<Pair<String, Supplier<String>>> getKeys() {
        return keys;
    }

    @NotNull
    public PlaceholderMap add(@NotNull PlaceholderMap other) {
        this.getKeys().addAll(other.getKeys());
        return this;
    }

    @NotNull
    public PlaceholderMap add(@NotNull String key, @NotNull String replacer) {
        this.add(key, () -> replacer);
        return this;
    }

    @NotNull
    public PlaceholderMap add(@NotNull String key, @NotNull Supplier<String> replacer) {
        this.getKeys().add(Pair.of(key, replacer));
        return this;
    }

    public void clear() {
        this.getKeys().clear();
    }

    @NotNull
    public UnaryOperator<String> replacer() {
        return str -> StringUtil.replaceEach(str, this.getKeys());
    }
}
