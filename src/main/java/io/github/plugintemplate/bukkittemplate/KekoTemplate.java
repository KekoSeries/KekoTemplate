package io.github.plugintemplate.bukkittemplate;

import co.aikar.commands.*;
import io.github.plugintemplate.bukkittemplate.commands.KekoTemplateCommand;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// TODO Change the class name as you want.
public final class KekoTemplate extends JavaPlugin {

    private static final Object LOCK = new Object();

    @Nullable
    private static KekoTemplateAPI api;

    @Nullable
    private static KekoTemplate instance;

    @NotNull
    public static KekoTemplate getInstance() {
        return Optional.ofNullable(KekoTemplate.instance).orElseThrow(() ->
            new IllegalStateException("You cannot be used BukkitTemplate plugin before its start!"));
    }

    private void setInstance(@NotNull final KekoTemplate instance) {
        if (Optional.ofNullable(KekoTemplate.instance).isPresent()) {
            throw new IllegalStateException("You can't use BukkitTemplate#setInstance method twice!");
        }
        synchronized (KekoTemplate.LOCK) {
            KekoTemplate.instance = instance;
        }
    }

    @NotNull
    public static KekoTemplateAPI getAPI() {
        return Optional.ofNullable(KekoTemplate.api).orElseThrow(() ->
            new IllegalStateException("You cannot be used BukkitTemplate plugin before its start!"));
    }

    private void setAPI(@NotNull final KekoTemplateAPI loader) {
        if (Optional.ofNullable(KekoTemplate.api).isPresent()) {
            throw new IllegalStateException("You can't use BukkitTemplate#setAPI method twice!");
        }
        synchronized (KekoTemplate.LOCK) {
            KekoTemplate.api = loader;
        }
    }

    @Override
    public void onLoad() {
        this.setInstance(this);
        this.setAPI(new KekoTemplateAPI(this));
    }

    @Override
    public void onDisable() {
        Optional.ofNullable(KekoTemplate.api).ifPresent(KekoTemplateAPI::disablePlugin);
    }

    @Override
    public void onEnable() {
        this.getServer().getScheduler().runTask(this, () ->
            this.getServer().getScheduler().runTaskAsynchronously(this, () ->
                KekoTemplate.getAPI().reloadPlugin(true)));
        final BukkitCommandManager manager = new BukkitCommandManager(this);
        final CommandConditions<BukkitCommandIssuer, BukkitCommandExecutionContext, BukkitConditionContext> conditions =
            manager.getCommandConditions();
        conditions.addCondition(String[].class, "player", (context, exec, value) -> {
            if (value == null || value.length == 0) {
                return;
            }
            final int arg = context.getConfigValue("arg", 0);
            if (arg >= value.length) {
                return;
            }
            final String name = value[arg];
            if (context.hasConfig("arg") && Bukkit.getPlayer(name) == null) {
                throw new ConditionFailedException(KekoTemplate.getAPI().languageFile.errors.player_not_found.get()
                    .build("%player_name%", () -> name));
            }
        });
        manager.registerCommand(new KekoTemplateCommand());
    }

}
