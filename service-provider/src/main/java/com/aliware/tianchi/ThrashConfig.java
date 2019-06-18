package com.aliware.tianchi;

import java.util.concurrent.Semaphore;

/**
 * @author guohaoice@gmail.com
 */
public class ThrashConfig {
    static final ThrashConfig INIT_CONFIG = new ThrashConfig(0, 1600, 50);
    final long durationInSec;
    final double avg_rtt;
    final Semaphore permit;

    public ThrashConfig(long durationInSec, int max_concurrency, double avg_rtt) {
        this.durationInSec = durationInSec;
        this.avg_rtt = avg_rtt;
        this.permit = new Semaphore(max_concurrency);
    }

    @Override
    public String toString() {
        return "Duration :" + durationInSec + " averageRTT:" + avg_rtt + " maxConcurrency:" + permit.availablePermits();
    }
}
