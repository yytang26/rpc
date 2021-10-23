package com.tyy.rpc.cluster;

import com.tyy.rpc.registry.ServiceURL;
import org.apache.zookeeper.Op;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * @author:tyy
 * @date:2021/7/10
 */
public class RandomLoadBalancer implements LoadBalancer {

    private Random random = new Random();

    @Override
    public String name() {
        return LoadBalancerEnum.RANDOM.getCode();
    }

    @Override
    public ServiceURL selectOne(List<ServiceURL> addresses) {
        int length = addresses.size();
        int maxWeight = 0;
        int minWeight = 0;

        int totalWeight = 0;
        for (int i = 0; i < length; i++) {
            int weight = Optional.ofNullable(addresses.get(i).getWeight()).orElse(0);
            maxWeight = Math.max(weight, maxWeight);
            minWeight = Math.min(minWeight, weight);
        }

        if (maxWeight > 0 && maxWeight != minWeight) {
            int offset = this.random.nextInt(totalWeight);
            for (int i = 0; i < length; i++) {
                int weight = Optional.ofNullable(addresses.get(i).getWeight()).orElse(0);
                offset -= weight;
                if (offset < 0) {
                    return addresses.get(i);
                }
            }
        }
        return addresses != null && addresses.size() != 0 ? addresses.get(this.random.nextInt(addresses.size())) : null;
    }
}
