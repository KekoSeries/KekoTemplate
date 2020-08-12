package io.github.plugintemplate.bukkittemplate;

import co.aikar.idb.DB;
import io.github.plugintemplate.bukkittemplate.file.ConfigFile;
import io.github.plugintemplate.bukkittemplate.file.LanguageFile;
import io.github.plugintemplate.bukkittemplate.util.FileElement;
import io.github.plugintemplate.bukkittemplate.util.ListenerBasic;
import io.github.plugintemplate.bukkittemplate.util.UpdateChecker;
import io.github.portlek.configs.CfgSection;
import io.github.portlek.smartinventory.SmartInventory;
import io.github.portlek.smartinventory.manager.BasicSmartInventory;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

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
        CfgSection.addProvidedClass(FileElement.class, new FileElement.Provider());
        this.languageFile.load();
        this.configFile.load();
        this.configFile.createSQL();
        if (first) {
            ListenerUtilities.register(
                PlayerJoinEvent.class,
                event -> event.getPlayer().hasPermission("bukkittemplate.version"),
                event -> this.checkForUpdate(event.getPlayer()),
                this.kekoTemplate
            );
            // TODO: Listeners should be here.
        }
        this.kekoTemplate.getServer().getScheduler().cancelTasks(this.kekoTemplate);
        if (this.configFile.saving.auto_save) {
            this.kekoTemplate.getServer().getScheduler().runTaskTimer(
                this.kekoTemplate,
                () -> {
                    // TODO Add codes for saving data as automatic
                },
                this.configFile.saving.auto_save_time * 20L,
                this.configFile.saving.auto_save_time * 20L
            );
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
        } catch (final Exception exception) {
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
