package com.rafaelfiume.raibow.acceptance.experimental.support;

import com.rafaelfiume.raibow.acceptance.experimental.fixture.ATestThatIsGoingToRunProgrammatically;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class FakeEndToEndTestsFinder implements EndToEndTestsFinder{

    public Class<?> testClasses() {
        return ATestThatIsGoingToRunProgrammatically.class;
    }

}
