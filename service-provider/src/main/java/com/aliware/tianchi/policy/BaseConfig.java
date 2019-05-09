package com.aliware.tianchi.policy;

import java.util.List;
import com.aliware.tianchi.ThrashConfig;

/**
 * @author guohaoice@gmail.com
 */
public abstract class BaseConfig {
    final int maxThreadCount ;
    final int onePeriodInSec = 20;
    final int warmUpInSec = 30;
    final int minRTTInMs = 40;
    final int normalRTTInMs = 50;
    final int maxRTTInMs = 60;

    protected BaseConfig(int maxThreadCount) {
        this.maxThreadCount = maxThreadCount;
    }

    public int getMaxThreadCount(){
        return maxThreadCount;
    }

    public abstract List<ThrashConfig> getConfigs();
}
