package com.aliware.tianchi.policy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.aliware.tianchi.ThrashConfig;

/**
 * @author guohaoice@gmail.com
 */
public class MediumConfig extends BaseConfig {
    private final int maxConcurrency = 450;
    private final int normalCurrency = 350;
    private final int minConcurrency = 300;
    private final ThrashConfig warmUp = new ThrashConfig(warmUpInSec + onePeriodInSec, maxConcurrency, normalRTTInMs);
    private final ThrashConfig min = new ThrashConfig(onePeriodInSec, minConcurrency, maxRTTInMs);
    private final ThrashConfig normal = new ThrashConfig(onePeriodInSec, normalCurrency, normalRTTInMs);
    private final ThrashConfig max = new ThrashConfig(onePeriodInSec, maxConcurrency, minRTTInMs);

    private final List<ThrashConfig> allConfig = Collections.unmodifiableList(Arrays.asList(warmUp, normal, max, min));

    public MediumConfig() {
        super(500, 20870);
    }

    @Override
    public List<ThrashConfig> getConfigs() {
        return allConfig;
    }
}
