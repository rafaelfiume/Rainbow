package com.rafaelfiume.raibow.support;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.rafaelfiume.raibow.support.RainbowTestExecutor.Outcome.FAIL;
import static com.rafaelfiume.raibow.support.RainbowTestExecutor.Outcome.OK;

public class RainbowTestExecutor {

    public enum Outcome {
        OK, FAIL
    }

    private static final Logger LOG = LoggerFactory.getLogger(RainbowTestExecutor.class);

    public Outcome run(Class<?>... testClass) {
        Result result = new JUnitCore().run(testClass);

        logIfTheresErrorOn(result);

        return (result.getFailureCount() == 0) ? OK : FAIL;
    }

    private void logIfTheresErrorOn(Result result) {
        for (Failure f : result.getFailures()) {
            LOG.error(f.getMessage(), f.getException());
        }
    }

}
