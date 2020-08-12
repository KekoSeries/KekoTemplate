package io.github.plugintemplate.bukkittemplate.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import io.github.plugintemplate.bukkittemplate.KekoTemplate;
import io.github.portlek.configs.bukkit.util.ColorUtil;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

// TODO Change class, command, permission name as you want.
@CommandAlias("kekotemplate|bt")
public final class KekoTemplateCommand extends BaseCommand {

    @Default
    @CommandPermission("kekotemplate.command.main")
    public void defaultCommand(final CommandSender sender) {
        sender.sendMessage(KekoTemplate.getAPI().languageFile.help_messages.get().build());
    }

    @Subcommand("help")
    @CommandPermission("kekotemplate.command.help")
    public void helpCommand(final CommandSender sender) {
        this.defaultCommand(sender);
    }

    @Subcommand("reload")
    @CommandPermission("kekotemplate.command.reload")
    public void reloadCommand(final CommandSender sender) {
        final long millis = System.currentTimeMillis();
        KekoTemplate.getAPI().reloadPlugin(false);
        sender.sendMessage(KekoTemplate.getAPI().languageFile.generals.reload_complete.get()
            .build("%ms%", () -> String.valueOf(System.currentTimeMillis() - millis)));
    }

    @Subcommand("version")
    @CommandPermission("kekotemplate.command.version")
    public void versionCommand(final CommandSender sender) {
        KekoTemplate.getAPI().checkForUpdate(sender);
    }

    @Subcommand("message")
    @CommandPermission("kekotemplate.command.message")
    @CommandCompletion("@players <message>")
    public void messageCommand(final CommandSender sender, @Conditions("player:arg=0") final String[] args) {
        if (args.length < 1) {
            return;
        }
        final StringBuilder builder = new StringBuilder();
        for (int index = 1; index < args.length; index++) {
            builder.append(ColorUtil.colored(args[index]));
            if (index < args.length - 1) {
                builder.append(' ');
            }
        }
        // player cannot be null cause @Conditions("player:arg=0") this condition checks
        // if args[0] is in the server.
        Optional.ofNullable(Bukkit.getPlayer(args[0])).ifPresent(player ->
            player.sendMessage(builder.toString()));
    }

}
