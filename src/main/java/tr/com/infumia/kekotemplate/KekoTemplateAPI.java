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

package tr.com.infumia.kekotemplate;

import co.aikar.idb.DB;
import io.github.portlek.configs.CfgSection;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.kekotemplate.file.ConfigFile;
import tr.com.infumia.kekotemplate.file.LanguageFile;
import tr.com.infumia.kekoutil.FileElement;
import tr.com.infumia.kekoutil.ListenerUtilities;
import tr.com.infumia.kekoutil.TaskUtilities;
import tr.com.infumia.kekoutil.UpdateChecker;

// TODO Change the class name as you want.
@RequiredArgsConstructor
public final class KekoTemplateAPI {

    @NotNull
    public final ConfigFile configFile = new ConfigFile();

    @NotNull
    public final LanguageFile languageFile = new LanguageFile(this.configFile);

    @NotNull
    public final KekoTemplate kekoTemplate;

    public void reloadPlugin(final boolean first) {
        this.languageFile.load();
        this.configFile.load();
        this.configFile.createSQL();
        if (first) {
            ListenerUtilities.register(
                PlayerJoinEvent.class,
                event -> event.getPlayer().hasPermission("kekotemplate.version"),
                event -> this.checkForUpdate(event.getPlayer()),
                this.kekoTemplate);
            // TODO: Listeners should be here.
        }
        this.kekoTemplate.getServer().getScheduler().cancelTasks(this.kekoTemplate);
        if (this.configFile.saving.auto_save) {
            TaskUtilities.asyncTimerLater(
                this.configFile.saving.auto_save_time * 20L,
                this.configFile.saving.auto_save_time * 20L,
                () -> {
                    // TODO Add codes for saving data as automatic.
                });
        }
        this.checkForUpdate(this.kekoTemplate.getServer().getConsoleSender());
    }

    public void checkForUpdate(@NotNull final CommandSender sender) {
        if (!this.configFile.check_for_update) {
            return;
        }
        // TODO Change the UpdateChecker resource id as you want.
        final UpdateChecker updater = new UpdateChecker(this.kekoTemplate, 11111);

        try {
            if (updater.checkForUpdates()) {
                sender.sendMessage(this.languageFile.generals.new_version_found.get()
                    .build("%version%", updater::getNewVersion));
            } else {
                sender.sendMessage(this.languageFile.generals.latest_version.get()
                    .build("%version%", updater::getNewVersion));
            }
        } catch (final IOException exception) {
            this.kekoTemplate.getLogger().warning("Update checker failed, could not connect to the API.");
        }
    }

    public void disablePlugin() {
        if (this.configFile.saving.save_when_plugin_disable) {
            // TODO Add codes for saving data
        }
        DB.close();
    }

}
