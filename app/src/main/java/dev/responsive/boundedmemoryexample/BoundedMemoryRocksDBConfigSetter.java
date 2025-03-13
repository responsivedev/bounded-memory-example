package dev.responsive.boundedmemoryexample;

import java.util.Map;
import org.apache.kafka.streams.state.RocksDBConfigSetter;
import org.rocksdb.BlockBasedTableConfig;
import org.rocksdb.Cache;
import org.rocksdb.Env;
import org.rocksdb.LRUCache;
import org.rocksdb.Options;
import org.rocksdb.WriteBufferManager;

public class BoundedMemoryRocksDBConfigSetter implements RocksDBConfigSetter {
  private static Cache cache;
  private static WriteBufferManager writeBufferManager;
  private static boolean initialized = false;
  private static final Object lock = new Object();

  @Override
  public void setConfig(
      final String storeName,
      final Options options,
      final Map<String, Object> configs
  ) {
    initialize(configs);
    final BlockBasedTableConfig tableConfig = (BlockBasedTableConfig) options.tableFormatConfig();
    tableConfig.setBlockCache(cache);
    tableConfig.setCacheIndexAndFilterBlocks(true);
    // if you want to set this, you need to set the high priority pool ratio > 0.0
    // when creating the cache instance.
    tableConfig.setCacheIndexAndFilterBlocksWithHighPriority(true);
    options.setWriteBufferManager(writeBufferManager);
    // all options share the same global env, so this thread pool is shared across all rocksdbs
    options.setEnv(Env.getDefault());
    options.getEnv().setBackgroundThreads(
        getLongConfig("bounded_memory_example.rocksdb.bg_threads", configs, 4).intValue());
    options.setTableFormatConfig(tableConfig);
  }

  private static void initialize(final Map<String, Object> configs) {
    synchronized (lock) {
      if (initialized) {
        return;
      }
      final var memoryLimit = getLongConfig(
          "bounded_memory_example.rocksdb.memory_limit_bytes",
          configs,
          512L * 1024 * 1024
      );
      final var memtableMemoryLimit = getLongConfig(
          "bounded_memory_example.rocksdb.memtable_memory_limit_bytes",
          configs,
          256L * 1024 * 1024
      );
      // See https://github.com/facebook/rocksdb/wiki/Block-Cache for details about these
      // parameters.
      cache = new LRUCache(
          memoryLimit,
          -1,
          // If this is true, rocksdb will try to enforce a strict memory limit. In practice
          // we've observed this causing some unexpected errors
          false,
          // Sets the percentage of the cache to reserve for "high-priory" blocks i.e. filters
          // and indexes if so configured. Set to a non-zero value if you want to use
          // setCacheIndexAndFilterBlocksWithHighPriority
          0.25
      );
      writeBufferManager = new WriteBufferManager(memtableMemoryLimit, cache);
      initialized = true;
    }
  }

  @Override
  public void close(String s, Options options) {
  }

  private static Long getLongConfig(
      final String key,
      final Map<String, Object> configs,
      final long defaultValue
  ) {
    final Object val = configs.getOrDefault(key, defaultValue);
    if (val instanceof Number) {
      return ((Number) val).longValue();
    }
    if (val instanceof String) {
      return Long.valueOf((String) val);
    }
    throw new IllegalArgumentException(
        String.format("invalid config for %s: %s", key, val.toString()));
  }
}
