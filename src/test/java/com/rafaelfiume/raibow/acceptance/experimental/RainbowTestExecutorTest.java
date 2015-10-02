package com.rafaelfiume.raibow.acceptance.experimental;

import com.googlecode.yatspec.junit.SpecRunner;
import com.rafaelfiume.raibow.acceptance.experimental.support.RainbowTestExecutor;
import com.rafaelfiume.raibow.acceptance.experimental.fixture.ATestThatIsGoingToRunProgrammatically;
import com.rafaelfiume.raibow.acceptance.experimental.fixture.AnotherTestThatIsGoingToRunProgrammatically;
import com.rafaelfiume.raibow.acceptance.experimental.fixture.FailingTestThatIsGoingToRunProgrammatically;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.rafaelfiume.raibow.acceptance.experimental.support.RainbowTestExecutor.Outcome.FAIL;
import static com.rafaelfiume.raibow.acceptance.experimental.support.RainbowTestExecutor.Outcome.OK;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@Ignore // ... for now: Experimental feature
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
