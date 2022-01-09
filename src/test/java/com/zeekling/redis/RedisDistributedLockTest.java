package com.zeekling.redis;

import com.zeekling.redis.lock.RedisDistributedLockImpl;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

public class RedisDistributedLockTest {

  static int n = 500;

  public static void secKill() {
    System.out.println("商品数量：" + --n);
  }

  public static void main(String[] args) {
    Runnable runnable = () -> {
      RedisDistributedLockImpl lock = null;
      String unLockIdentify = null;
      try {
        Set<HostAndPort> set = new HashSet<>();
        set.add(new HostAndPort("127.0.0.1", 30001));
        JedisPoolConfig config = new JedisPoolConfig();
        JedisCluster cluster = new JedisCluster(set, config);
        lock = new RedisDistributedLockImpl(cluster, "test1", 20000);
        unLockIdentify = lock.lock();
        System.out.println(Thread.currentThread().getName() + "正在运行");
        secKill();
      } finally {
        if (lock != null) {
          lock.unlock(unLockIdentify);
        }
      }
    };
    System.out.println("------------------开始--------------");
    for (int i = 0; i < 10; i++) {
      Thread t = new Thread(runnable);
      t.start();
    }
  }

}
