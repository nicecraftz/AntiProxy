package tech.nicecraftz.antiproxy;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import org.slf4j.Logger;

public class AntiProxy {

    @Inject private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
    }
}
