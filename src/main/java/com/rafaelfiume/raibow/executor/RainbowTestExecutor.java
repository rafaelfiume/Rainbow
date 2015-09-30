package com.rafaelfiume.raibow.executor;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import static com.rafaelfiume.raibow.executor.RainbowTestExecutor.Outcome.FAIL;
import static com.rafaelfiume.raibow.executor.RainbowTestExecutor.Outcome.OK;

public class RainbowTestExecutor {

    public Outcome run(Class<?>... testClass) {
        Result result = new JUnitCore().run(testClass);
        return (result.getFailureCount() == 0) ? OK : FAIL;
    }

    public enum Outcome {
        OK, FAIL
    }

}
