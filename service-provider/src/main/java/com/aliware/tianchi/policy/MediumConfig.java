package com.aliware.tianchi.policy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.aliware.tianchi.ThrashConfig;

/**
 * @author guohaoice@gmail.com
 */
public class MediumConfig extends BaseConfig {
    private final int maxConcurrency = 380;
    private final int normalCurrency = 360;
    private final int minConcurrency = 320;
    private final ThrashConfig warmUp = new ThrashConfig(warmUpInSec + onePeriodInSec, maxConcurrency, normalRTTInMs);
    private final ThrashConfig config0 = new ThrashConfig(onePeriodInSec,minConcurrency,maxRTTInMs);
    private final ThrashConfig config1 = new ThrashConfig(onePeriodInSec,maxConcurrency, normalRTTInMs);
    private final ThrashConfig config2 = new ThrashConfig(onePeriodInSec,normalCurrency,minRTTInMs);
    private final List<ThrashConfig> allConfig = Collections.unmodifiableList(Arrays.asList(warmUp, config0, config1, config2));

    public MediumConfig() {
        super(400);
    }

    @Override
    public List<ThrashConfig> getConfigs() {
        return allConfig;
    }
}
