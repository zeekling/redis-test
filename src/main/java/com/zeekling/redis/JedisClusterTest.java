package com.zeekling.redis;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

public class JedisClusterTest {

    private JedisCluster cluster;

    private Jedis jedis;

    public JedisClusterTest(){
        cluster = new JedisCluster(new HostAndPort("127.0.0.1", 30001));
        jedis = new Jedis("127.0.0.1", 30001);
    }

    public void countKeyInSlot(int slot){
        jedis.clusterCountKeysInSlot(slot);
    }

    public static void main(String[] args) {
        JedisClusterTest test = new JedisClusterTest();
        test.countKeyInSlot(4);
        test.countKeyInSlot(16384);
    }

}
