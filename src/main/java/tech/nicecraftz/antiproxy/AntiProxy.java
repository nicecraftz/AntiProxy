package tech.nicecraftz.antiproxy;

import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import lombok.SneakyThrows;
import tech.nicecraftz.antiproxy.address.NetAddress;
import tech.nicecraftz.antiproxy.listener.JoinListener;
import tech.nicecraftz.antiproxy.database.Dao;
import tech.nicecraftz.antiproxy.database.DataSource;
import tech.nicecraftz.antiproxy.database.NetAddressDao;
import tech.nicecraftz.antiproxy.database.H2DataSource;
import tech.nicecraftz.antiproxy.address.NetAddressService;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(
        id = "antiproxy",
        name = "AntiProxy",
        version = "1.0"
)
@Getter
public class AntiProxy {
    private final Logger logger;
    private final ProxyServer proxyServer;
    private final Path dataDirectory;
    private Toml tomlRoot;

    private DataSource dataSource;
    private Dao<String, NetAddress> netAddressDao;
    private NetAddressService netAddressService;

    @Inject
    public AntiProxy(Logger logger, ProxyServer proxyServer, @DataDirectory Path dataDirectory) {
        this.logger = logger;
        this.proxyServer = proxyServer;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        ensureFileExists();
        dataSource = new H2DataSource(new File(dataDirectory.toFile(), "antiproxy.h2"));
        dataSource.connect();

        netAddressDao = new NetAddressDao(dataSource);
        netAddressService = new NetAddressService(netAddressDao);

        proxyServer.getEventManager().register(this, new JoinListener(this));
        logger.info("AntiProxy has been enabled!");
    }

    @SneakyThrows
    private void ensureFileExists() {
        File file = new File(dataDirectory.toFile(), "config.toml");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.toml");
            if (inputStream == null) throw new NullPointerException("config.toml not found in resources");
            Files.copy(inputStream, file.toPath());
        }
        tomlRoot = new Toml().read(file);
    }

    public void punish(String command, String username, String address) {
        command = command.replace("%player%", username).replace("%ip%", address);
        execute(command);
    }

    public void execute(String command) {
        proxyServer.getCommandManager().executeAsync(proxyServer.getConsoleCommandSource(), command);
    }


}
