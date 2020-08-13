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

package tr.com.infumia.kekotemplate.file;

import co.aikar.idb.DB;
import co.aikar.idb.DatabaseOptions;
import co.aikar.idb.HikariPooledDatabase;
import co.aikar.idb.PooledDatabaseOptions;
import io.github.portlek.configs.annotations.Config;
import io.github.portlek.configs.annotations.Instance;
import io.github.portlek.configs.annotations.Property;
import io.github.portlek.configs.annotations.Section;
import io.github.portlek.configs.bukkit.BukkitManaged;
import io.github.portlek.configs.bukkit.BukkitSection;
import io.github.portlek.configs.bukkit.util.ColorUtil;
import io.github.portlek.configs.type.YamlFileType;
import io.github.portlek.replaceable.Replaceable;
import io.github.portlek.replaceable.rp.RpString;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

@Config(
    name = "config",
    type = YamlFileType.class,
    // TODO: Change the plugin data folder as you want.
    location = "%basedir%/KekoTemplate"
)
public final class ConfigFile extends BukkitManaged {

    @Instance
    public final ConfigFile.Saving saving = new ConfigFile.Saving();

    // TODO: Change the plugin prefix as you want.
    @Property
    public RpString plugin_prefix = Replaceable.from("&6[&eKekoTemplate&6]")
        .map(ColorUtil::colored);

    @Property
    public String plugin_language = "en";

    @Property
    public boolean check_for_update = true;

    @Override
    public void onLoad() {
        this.setAutoSave(true);
    }

    @NotNull
    public void createSQL() {
        final PooledDatabaseOptions poolOptions;
        if (this.isMySQL()) {
            poolOptions = PooledDatabaseOptions
                .builder()
                .options(DatabaseOptions
                    .builder()
                    .poolName("KekoTemplate DB")
                    .logger(Logger.getLogger("KekoTemplate"))
                    .mysql(this.saving.mysql.username, this.saving.mysql.password, this.saving.mysql.database,
                        this.saving.mysql.host + ':' + this.saving.mysql.port)
                    .useOptimizations(true)
                    .build())
                .build();
        } else {
            poolOptions = PooledDatabaseOptions
                .builder()
                .options(DatabaseOptions
                    .builder()
                    .poolName("KekoTemplate DB")
                    .logger(Logger.getLogger("KekoTemplate"))
                    .sqlite("plugins/KekoTemplate/store.db")
                    .useOptimizations(true)
                    .build())
                .build();
        }
        DB.setGlobalDatabase(new HikariPooledDatabase(poolOptions));
    }

    private boolean isMySQL() {
        return "mysql".equalsIgnoreCase(this.saving.storage_type) ||
            "remote".equalsIgnoreCase(this.saving.storage_type) ||
            "net".equalsIgnoreCase(this.saving.storage_type);
    }

    @Section("saving")
    public static final class Saving extends BukkitSection {

        @Instance
        public final ConfigFile.Saving.MySQL mysql = new ConfigFile.Saving.MySQL();

        @Property
        public String storage_type = "sqlite";

        @Property
        public boolean save_when_plugin_disable = true;

        @Property
        public boolean auto_save = true;

        @Property
        public long auto_save_time = 60L;

        @Section("mysql")
        public static final class MySQL extends BukkitSection {

            @Property
            public String host = "localhost";

            @Property
            public int port = 3306;

            @Property
            public String database = "database";

            @Property
            public String username = "username";

            @Property
            public String password = "password";

        }

    }

}
