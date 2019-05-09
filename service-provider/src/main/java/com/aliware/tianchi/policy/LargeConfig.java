package com.aliware.tianchi.policy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.aliware.tianchi.ThrashConfig;

/**
 * @author guohaoice@gmail.com
 */
public class LargeConfig extends BaseConfig {
    private final int maxConcurrency = 580;
    private final int normalCurrency = 500;
    private final int minConcurrency = 420;
    private final ThrashConfig warmUp = new ThrashConfig(warmUpInSec + onePeriodInSec, maxConcurrency, normalRTTInMs);
    private final ThrashConfig config0 = new ThrashConfig(onePeriodInSec,minConcurrency,normalRTTInMs);
    private final ThrashConfig config1 = new ThrashConfig(onePeriodInSec,normalCurrency,maxRTTInMs);
    private final ThrashConfig config2 = new ThrashConfig(onePeriodInSec,maxConcurrency,minRTTInMs);
    public final List<ThrashConfig> allConfig = Collections.unmodifiableList(Arrays.asList(warmUp, config0, config1, config2));

    public LargeConfig() {
        super(600);
    }

    @Override
    public List<ThrashConfig> getConfigs() {
        return allConfig;
    }
}
