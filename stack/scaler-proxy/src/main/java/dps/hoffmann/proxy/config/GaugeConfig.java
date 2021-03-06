package dps.hoffmann.proxy.config;

import dps.hoffmann.proxy.model.LogicalService;
import dps.hoffmann.proxy.model.ScalingDirection;
import dps.hoffmann.proxy.properties.ScalingProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicInteger;

import static dps.hoffmann.proxy.properties.ScalingProperties.TIER_CNT;

@Configuration
public class GaugeConfig {

    @Autowired
    private ScalingProperties scalingProperties;

    @Bean("overall-average")
    public AtomicInteger[] overallAverages() {
        LogicalService[] services = LogicalService.values();
        AtomicInteger[] values = new AtomicInteger[services.length];
        for (LogicalService curr : services) {
            values[curr.ordinal()] = new AtomicInteger(0);
        }
        return values;
    }

    @Bean("node-specific-average")
    public AtomicInteger[] nodeSpecificAverage() {
        int len = scalingProperties.getHighestContainerBound();
        AtomicInteger[] values = new AtomicInteger[len];
        for (int i = 0; i < len; i++) {
            values[i] = new AtomicInteger(0);
        }
        return values;
    }

    @Bean("spring-specific-average")
    public AtomicInteger[] springSpecificAverage() {
        int len = scalingProperties.getHighestContainerBound();
        AtomicInteger[] values = new AtomicInteger[len];
        for (int i = 0; i < len; i++) {
            values[i] = new AtomicInteger(0);
        }
        return values;
    }

    @Bean("node-tier-average")
    public AtomicInteger[] nodeTierAverage() {
        int len = TIER_CNT;
        AtomicInteger[] values = new AtomicInteger[len];
        for (int i = 0; i < len; i++) {
            values[i] = new AtomicInteger(0);
        }
        return values;
    }

    @Bean("spring-tier-average")
    public AtomicInteger[] springTierAverage() {
        int len = TIER_CNT;
        AtomicInteger[] values = new AtomicInteger[len];
        for (int i = 0; i < len; i++) {
            values[i] = new AtomicInteger(0);
        }
        return values;
    }

//    @Bean("specific-average")
//    public AtomicInteger[] specificAverages() {
//        int totalNumberOfGaugeRefs =
//                scalingProperties.getHighestContainerBound() * LogicalService.values().length;
//        AtomicInteger[] values = new AtomicInteger[totalNumberOfGaugeRefs];
//        for (int i = 0; i < values.length; i++) {
//            values[i] = new AtomicInteger(0);
//        }
//        return values;
//    }
}
