package com.algaworks.algasensors.device.management.common;

import io.hypersistence.tsid.TSID;

import java.util.Optional;

public class IdGenerator {
    private static final TSID.Factory factory;

    static {
        Optional.ofNullable(System.getenv("TSID_NODE"))
                .ifPresent(tsidNode -> System.setProperty("tsid.node", tsidNode));
        Optional.ofNullable(System.getenv("TSID_NODE_COUNT"))
                .ifPresent(tsidNodeCount -> System.setProperty("tsid.node.count", tsidNodeCount));

        factory = TSID.Factory.builder().build();
    }

    private IdGenerator() {}

    public static TSID generateTSID() {
        return factory.generate();
    }

}
