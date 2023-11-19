package tech.nicecraftz.antiproxy.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import tech.nicecraftz.antiproxy.AntiProxy;
import tech.nicecraftz.antiproxy.address.NetAddress;
import tech.nicecraftz.antiproxy.util.RequestUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

@RequiredArgsConstructor
public class JoinListener {
    private final AntiProxy antiProxy;

    @Subscribe
    public void onPlayerJoin(PostLoginEvent postLoginEvent) {
        Player player = postLoginEvent.getPlayer();
        String username = player.getUsername();

        InetSocketAddress inetSocketAddress = player.getRemoteAddress();
        InetAddress inetAddress = inetSocketAddress.getAddress();
        String address = inetAddress.getHostAddress();

        String key = antiProxy.getTomlRoot().getTable("proxy").getString("key");
        antiProxy.getLogger().info("Player " + username + " is trying to join with IP " + address + ".");

        antiProxy.getNetAddressService().get(address).whenComplete((netAddress, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            if (netAddress == null) {
                antiProxy.getLogger().info("Player " + username + " is not cached, checking if they are using a proxy...");
                netAddress = new NetAddress(address, RequestUtil.usesProxy(key, address));
            }

            if (netAddress.proxy()) {
                player.disconnect(MiniMessage.miniMessage().deserialize(antiProxy.getTomlRoot().getTable("punishments").getString("denied")));
                antiProxy.getLogger().info("Player " + username + " is using a proxy!");
                List<String> commands = antiProxy.getTomlRoot().getTable("punishments").getList("commands");
                for (String punishmentCommand : commands) {
                    antiProxy.punish(punishmentCommand, username, address);
                }
            }

            if (!antiProxy.getNetAddressService().contains(address)) antiProxy.getNetAddressService().add(netAddress);
        });
    }
}
