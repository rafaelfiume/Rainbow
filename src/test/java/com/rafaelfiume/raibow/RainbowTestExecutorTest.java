package com.rafaelfiume.raibow;

import com.googlecode.yatspec.junit.SpecRunner;
import com.rafaelfiume.raibow.executor.RainbowTestExecutor;
import com.rafaelfiume.raibow.test.support.ATestThatIsGoingToRunProgrammatically;
import com.rafaelfiume.raibow.test.support.AnotherTestThatIsGoingToRunProgrammatically;
import com.rafaelfiume.raibow.test.support.FailingTestThatIsGoingToRunProgrammatically;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.rafaelfiume.raibow.executor.RainbowTestExecutor.Outcome.FAIL;
import static com.rafaelfiume.raibow.executor.RainbowTestExecutor.Outcome.OK;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpecRunner.class)
public class RainbowTestExecutorTest {

    private final RainbowTestExecutor executor = new RainbowTestExecutor();

    @Test
    public void runGreenTests() {
        assertThat(outcomeOfExecutorWhenRunningGreenTests(), is(OK));
    }

    @Test
    public void runRedTests() {
        assertThat(outcomeOfExecutorWhenRunningSomeFailingTest(), is(FAIL));
    }

    private RainbowTestExecutor.Outcome outcomeOfExecutorWhenRunningGreenTests() {
        return executor.run(ATestThatIsGoingToRunProgrammatically.class, AnotherTestThatIsGoingToRunProgrammatically.class);
    }

    private RainbowTestExecutor.Outcome outcomeOfExecutorWhenRunningSomeFailingTest() {
        return executor.run(ATestThatIsGoingToRunProgrammatically.class, FailingTestThatIsGoingToRunProgrammatically.class);
    }


}
