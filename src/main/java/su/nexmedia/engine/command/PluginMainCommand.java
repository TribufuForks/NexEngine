package su.nexmedia.engine.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.NexPlugin;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;

public class PluginMainCommand<P extends NexPlugin<P>> extends GeneralCommand<P> {

    public PluginMainCommand(@NotNull P plugin) {
        super(plugin, plugin.getLabels());
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {

    }
}
