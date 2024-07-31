package com.at;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

public class RedisTest {
    public static void main(String[] args) throws Exception {

        // 定义 Redis 集群节点
        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
        jedisClusterNodes.add(new HostAndPort("10.211.55.102", 6379));
        jedisClusterNodes.add(new HostAndPort("10.211.55.102", 6380));
        jedisClusterNodes.add(new HostAndPort("10.211.55.102", 6381));

        // 创建 JedisCluster 实例
        JedisCluster jedisCluster = new JedisCluster(jedisClusterNodes) ;
        // 执行 Redis 命令
        jedisCluster.set("foo", "bar");
        String value = jedisCluster.get("foo");
        System.out.println("Value for 'foo': " + value);

        // 删除键
        jedisCluster.del("foo");
        value = jedisCluster.get("foo");
        System.out.println("Value for 'foo' after deletion: " + value);

        jedisCluster.set("111","json.get(\"age\")");

    }
}
