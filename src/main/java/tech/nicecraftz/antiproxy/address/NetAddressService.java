package tech.nicecraftz.antiproxy.address;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import tech.nicecraftz.antiproxy.database.Dao;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class NetAddressService {
    private final Dao<String, NetAddress> netAddressDao;
    private final Cache<String, NetAddress> cachedAddresses = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.HOURS).build();

    public void add(NetAddress netAddress) {
        cachedAddresses.put(netAddress.address(), netAddress);
        netAddressDao.create(netAddress.address(), netAddress);
    }

    public boolean contains(String address) {
        return cachedAddresses.asMap().containsKey(address);
    }

    public CompletableFuture<NetAddress> get(String address) {
        NetAddress netAddress = cachedAddresses.getIfPresent(address);
        if (netAddress != null) return CompletableFuture.completedFuture(netAddress);
        return netAddressDao.read(address);
    }
}
