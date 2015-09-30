package com.rafaelfiume.raibow.test.support;

import com.googlecode.yatspec.junit.SpecRunner;
import org.junit.Assert;
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
