package com.rafaelfiume.raibow.acceptance.experimental.support;

import com.rafaelfiume.raibow.endtoend.StatusPageWalkingSkeletonIT;
import org.springframework.stereotype.Component;

@Component
public class SimpleEndToEndTestsFinder implements EndToEndTestsFinder {

    // TODO RF 30/09/15 Retrieve this indirectly (using a component) via reflection 
    private final Class<?> statusPageWalkingSkeletonITClass = StatusPageWalkingSkeletonIT.class;

    @Override
    public Class<?> testClasses() {
        return statusPageWalkingSkeletonITClass;
    }

}
