package com.zeekling.redis.lock;

/**
 * 实现的分布式锁
 */
public interface DistributedLock {

  /**
   * 获取锁
   *
   * @return 锁的标识
   */
  String lock();

  /**
   * 通过锁的标识释放锁
   *
   * @param identify 唯一标识
   * @return 释放锁是否成功
   */
  boolean unlock(String identify);

}
