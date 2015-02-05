package cz.fs.proto1.infinispan;

import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import org.infinispan.commons.api.BasicCacheContainer;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.TransactionMode;
import org.infinispan.transaction.lookup.GenericTransactionManagerLookup;
import org.infinispan.util.concurrent.IsolationLevel;

import cz.fs.proto1.ConfigUtil;
import cz.fs.proto1.service.WebManager;

/**
 * Modified by Filip Sivak
 * 
 * Implementation of {@link CacheContainerProvider} creating a programmatically configured DefaultCacheManager.
 * Libraries of Infinispan need to be bundled with the application - this is called "library" mode.
 *
 * @author Martin Gencur
 */
@ApplicationScoped
public class CacheContainerProvider {
    private Logger log = Logger.getLogger(this.getClass().getName());

    private BasicCacheContainer manager;
    
    public BasicCacheContainer getCacheContainer() {
        if (manager == null) {

            GlobalConfiguration glob = new GlobalConfigurationBuilder()
                    .nonClusteredDefault()
                    .build();

            Configuration defaultConfig = new ConfigurationBuilder()
                    .transaction().transactionMode(TransactionMode.TRANSACTIONAL)
                    .invocationBatching().enable()
                    .persistence().passivation(false).addSingleFileStore()
                    	.fetchPersistentState(true)
                    	.ignoreModifications(false)
                    	.purgeOnStartup(false)
                    	.location(ConfigUtil.getConfig("INFINISPAN_LOCATION"))
                    .build();  //default config

            Configuration webCacheConfig = new ConfigurationBuilder().jmxStatistics().enable()
                    .clustering().cacheMode(CacheMode.LOCAL)
                    .invocationBatching().enable()
                    .transaction().transactionMode(TransactionMode.TRANSACTIONAL).autoCommit(false)
                    .lockingMode(LockingMode.OPTIMISTIC).transactionManagerLookup(new GenericTransactionManagerLookup())
                    .locking().isolationLevel(IsolationLevel.REPEATABLE_READ)
                    //.eviction().maxEntries(4).strategy(EvictionStrategy.LRU)
                    .persistence().passivation(true).addSingleFileStore().purgeOnStartup(true)
                    //.indexing().enable().addProperty("default.directory_provider", "ram")
                    .build();

            manager = new DefaultCacheManager(glob, defaultConfig);
            ((DefaultCacheManager) manager).defineConfiguration(WebManager.CACHE_NAME, webCacheConfig);
            manager.start();
            log.info("=== Using DefaultCacheManager (library mode) ===");
        }
        return manager;
    }

    @PreDestroy
    public void cleanUp() {
        manager.stop();
        manager = null;
    }
}