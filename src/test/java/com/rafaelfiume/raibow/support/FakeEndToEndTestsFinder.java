package com.rafaelfiume.raibow.support;

import com.rafaelfiume.raibow.fixture.ATestThatIsGoingToRunProgrammatically;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class FakeEndToEndTestsFinder implements EndToEndTestsFinder{

    public Class<?> testClasses() {
        return ATestThatIsGoingToRunProgrammatically.class;
    }

}
