package com.aliware.tianchi;

import java.util.concurrent.Semaphore;

/**
 * @author guohaoice@gmail.com
 */
public class InternalSemaphore extends Semaphore {
    public InternalSemaphore(int permits) {
        super(permits);
    }

    public InternalSemaphore(int permits, boolean fair) {
        super(permits, fair);
    }

    public void acquire() throws InterruptedException {
        super.acquire();
    }

    void reducePermit(int n) {
        super.reducePermits(n);
        System.out.println("RELEASE PERMIT TO:"+super.availablePermits());
    }

    void addPermit(int n) {
        super.release(n);
        System.out.println("ADD PERMIT to:"+super.availablePermits());
    }
}
