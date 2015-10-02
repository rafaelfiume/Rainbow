package com.rafaelfiume.raibow.acceptance.experimental.fixture;

import com.googlecode.yatspec.junit.SpecRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.fail;

@RunWith(SpecRunner.class)
public class FailingTestThatIsGoingToRunProgrammatically {

    @Test
    public void testSucceed() {
        youAreUglyAndIDontLikeYouSoFail();
    }

    private void youAreUglyAndIDontLikeYouSoFail() {
        fail();
    }

}
