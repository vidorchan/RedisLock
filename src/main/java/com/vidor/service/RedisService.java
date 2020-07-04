package com.vidor.service;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/*
Redisson 分布式特性
集群问题：从节点同步，主节点挂掉
 */
@Service
public class RedisService {
    @Autowired
    private Redisson redisson;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private static AtomicInteger STOCK = new AtomicInteger(50);

    //2个进程/8080 8081会发生意想不到的错误 synchronized只适用在一个进程中
    public void deduct() {
        synchronized (this){
            int stock = Integer.parseInt(redisTemplate.opsForValue().get("stock"));
            if (stock > 0) {
                int cu = stock - 1;
                redisTemplate.opsForValue().set("stock", cu + "");
                System.out.println("减少库存至：" + cu);

            } else {
                System.out.println("库存为负数了：");
            }
        }
    }

    public void deduct1() {
        String lockName = "product_01";
        String uuid = UUID.randomUUID().toString();
        try {
            //定时 1/3 * timeout，续命 timeout
            Boolean result = redisTemplate.opsForValue().setIfAbsent(lockName, uuid, 10, TimeUnit.SECONDS);//10秒过期 stenx
            if (!result) return;
            int stock = Integer.parseInt(redisTemplate.opsForValue().get("stock"));
            if (stock > 0) {
                int cuStock = stock - 1;
                redisTemplate.opsForValue().set("stock", cuStock + "");
                System.out.println("减少库存至：" + cuStock);
            } else {
                System.out.println("库存为负数了：");
            }
        } finally {
            //误删除别人的锁
            if (uuid.equals(redisTemplate.opsForValue().get(lockName))) {
                redisTemplate.delete(lockName);//释放锁
            }
        }

    }

    //Redis RQS 10万
    //Redis 集群问题
    // 强一致性：
    // 1.zookeeper 性能不行 需要同步到slave才会返回结果，所以能保证下一个master会存在这个锁
    // 2.RedLock: 超过半数Redis节点加锁成功才算加锁成功。性能低，其他失败需要回滚成功的case
    public void deduct2() {
        String lockName = "product_02";
        RLock redissonLock = redisson.getLock(lockName);
        try {
            redissonLock.lock(10, TimeUnit.SECONDS);
            int stock = Integer.parseInt(redisTemplate.opsForValue().get("stock"));
            if (stock > 0) {
                int cu = stock - 1;
                redisTemplate.opsForValue().set("stock", cu + "");
                System.out.println("减少库存至：" + cu);
            } else {
                System.out.println("库存为负数了：");
            }
        } finally {
            redissonLock.unlock();
        }
    }
}
