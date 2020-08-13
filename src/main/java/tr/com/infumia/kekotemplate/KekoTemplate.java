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

import co.aikar.commands.*;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tr.com.infumia.kekotemplate.commands.KekoTemplateCommand;
import tr.com.infumia.kekoutil.TaskUtilities;

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
            new IllegalStateException("You cannot be used KekoTemplate plugin before its start!"));
    }

    private void setInstance(@NotNull final KekoTemplate instance) {
        if (Optional.ofNullable(KekoTemplate.instance).isPresent()) {
            throw new IllegalStateException("You can't use KekoTemplate#setInstance method twice!");
        }
        synchronized (KekoTemplate.LOCK) {
            KekoTemplate.instance = instance;
        }
    }

    @NotNull
    public static KekoTemplateAPI getAPI() {
        return Optional.ofNullable(KekoTemplate.api).orElseThrow(() ->
            new IllegalStateException("You cannot be used KekoTemplate plugin before its start!"));
    }

    private void setAPI(@NotNull final KekoTemplateAPI loader) {
        if (Optional.ofNullable(KekoTemplate.api).isPresent()) {
            throw new IllegalStateException("You can't use KekoTemplate#setAPI method twice!");
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
        TaskUtilities.sync(() ->
            TaskUtilities.async(() ->
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
