/*
 * MIT License
 *
 * Copyright (c) 2020 Infumia
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package tr.com.infumia.kekotemplate.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import tr.com.infumia.kekotemplate.KekoTemplate;
import io.github.portlek.configs.bukkit.util.ColorUtil;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

// TODO Change class, command, permission names as you want.
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
        // player cannot be null cause @Conditions("player:arg=0") condition checks if args[0] is in the server.
        Optional.ofNullable(Bukkit.getPlayer(args[0])).ifPresent(player ->
            player.sendMessage(builder.toString()));
    }

}
