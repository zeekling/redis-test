package com.zeekling.redis.lock;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;

/**
 * 基于Redis实现的分布式锁
 */
public class RedisDistributedLockImpl implements DistributedLock {


  private static final String LOCK_SUCCESS = "OK";

  private static final Long UNLOCK_SUCCESS = 1L;

  //
  private static final String UNLOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

  private final JedisCluster cluster;

  private final long lockTimeout;

  private final String lockKey;

  public RedisDistributedLockImpl(JedisCluster cluster, String lockKey, long lockTimeout) {

    this.cluster = cluster;
    this.lockTimeout = lockTimeout;
    this.lockKey = lockKey;
  }

  @Override
  public String lock() {
    long end = System.currentTimeMillis() + lockTimeout;
    String requireToken = String.valueOf(System.currentTimeMillis());
    int expireTime = 300 * 1000;
    SetParams params = new SetParams().nx().px(expireTime);
    while (System.currentTimeMillis() < end) {
      try {
        String result = cluster.set(lockKey, requireToken, params);
        if (LOCK_SUCCESS.equals(result)) {
          System.out.println("lock success");
          return requireToken;
        }
        System.out.println("lock failed trying");
        Thread.sleep(100);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    }
    return requireToken;
  }

  @Override
  public boolean unlock(String identify) {
    if (identify == null || identify.trim().length() == 0) {
      return false;
    }
    Object obj = new Object();
    try {
      obj = cluster.eval(UNLOCK_SCRIPT, Collections.singletonList(lockKey), Collections.singletonList(identify));
      if (UNLOCK_SUCCESS.equals(obj)) {
        System.out.println("release lock success, requestToken:" + identify);
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("release lock failed, requestToken:" + identify + ", result:" + obj);
    return false;
  }
}
