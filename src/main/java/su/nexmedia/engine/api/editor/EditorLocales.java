package su.nexmedia.engine.api.editor;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static su.nexmedia.engine.utils.Colors2.*;

public class EditorLocales {

    public static final EditorLocale CLOSE         = EditorLocale.of("Editor.Generic.Close", RED + "(✕) Exit");
    public static final EditorLocale RETURN        = EditorLocale.of("Editor.Generic.Return", GRAY + "(↓) " + WHITE + "Return");
    public static final EditorLocale NEXT_PAGE     = EditorLocale.of("Editor.Generic.NextPage", GRAY + "(→) " + WHITE + "Next Page");
    public static final EditorLocale PREVIOUS_PAGE = EditorLocale.of("Editor.Generic.PreviousPage", GRAY + "(←) " + WHITE + "Previous Page");

    protected static final String LMB       = "Left-Click";
    protected static final String RMB       = "Right-Click";
    protected static final String DROP_KEY  = "[Q] Drop Key";
    protected static final String SWAP_KEY  = "[F] Swap Key";
    protected static final String SHIFT_LMB = "Shift-Left";
    protected static final String SHIFT_RMB = "Shift-Right";
    protected static final String DRAG_DROP = "Drag & Drop";

    @NotNull
    protected static Builder builder(@NotNull String key) {
        return new Builder(key);
    }

    protected static final class Builder {

        private final String key;
        private       String       name;
        private final List<String> lore;

        public Builder(@NotNull String key) {
            this.key = key;
            this.name = "";
            this.lore = new ArrayList<>();
        }

        @NotNull
        public EditorLocale build() {
            return new EditorLocale(this.key, this.name, this.lore);
        }

        @NotNull
        public Builder name(@NotNull String name) {
            this.name = YELLOW + BOLD + name;
            return this;
        }

        @NotNull
        public Builder text(@NotNull String... text) {
            return this.addLore(GRAY, text);
        }

        @NotNull
        public Builder textRaw(@NotNull String... text) {
            return this.addLore("", text);
        }

        @NotNull
        public Builder currentHeader() {
            return this.addLore(YELLOW + BOLD, "Current:");
        }

        @NotNull
        public Builder current(@NotNull String type, @NotNull String value) {
            return this.addLore(YELLOW + "▪ " + GRAY, type + ": " + YELLOW + value);
        }

        @NotNull
        @Deprecated
        public Builder warningHeader() {
            return this.addLore(RED + BOLD, "Warning:");
        }

        @NotNull
        @Deprecated
        public Builder warning(@NotNull String... info) {
            return this.addLore(RED + "▪ " + GRAY, info);
        }

        @NotNull
        @Deprecated
        public Builder noteHeader() {
            return this.addLore(ORANGE + BOLD, "Notes:");
        }

        @NotNull
        @Deprecated
        public Builder notes(@NotNull String... info) {
            return this.addLore(ORANGE + "▪ " + GRAY, info);
        }

        @NotNull
        @Deprecated
        public Builder actionsHeader() {
            return this.addLore(GREEN + BOLD, "Actions:");
        }

        @NotNull
        @Deprecated
        public Builder action(@NotNull String click, @NotNull String action) {
            return this.addLore(GREEN + "▪ " + GRAY, click + ": " + GREEN + action);
        }

        @NotNull
        public Builder click(@NotNull String click, @NotNull String action) {
            return this.addLore("", GRAY + "(" + WHITE + click + GRAY + " to " + action + GRAY + ")");
        }

        @NotNull
        @Deprecated
        public Builder breakLine() {
            return this.emptyLine();
        }

        @NotNull
        public Builder emptyLine() {
            return this.addLore("", "");
        }

        @NotNull
        private Builder addLore(@NotNull String prefix, @NotNull String... text) {
            for (String str : text) {
                this.lore.add(prefix + str);
            }
            return this;
        }
    }
}
