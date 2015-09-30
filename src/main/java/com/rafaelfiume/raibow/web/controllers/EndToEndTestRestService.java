package com.rafaelfiume.raibow.web.controllers;

import com.rafaelfiume.raibow.endtoend.StatusPageWalkingSkeletonIT;
import com.rafaelfiume.raibow.executor.RainbowTestExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class EndToEndTestRestService {

    private final RainbowTestExecutor testExecutor = new RainbowTestExecutor();

    // TODO RF 30/09/15 Retrieve this indirectly (using a component) via reflection
    private final Class<?> statusPageWalkingSkeletonITClass = StatusPageWalkingSkeletonIT.class;

    @RequestMapping(value = "/test/end-to-end", method = GET, produces = "text/plain")
    public ResponseEntity<String> handle() {

        return new ResponseEntity<>(
                testExecutor.run(statusPageWalkingSkeletonITClass).name(),
                OK
        );
    }

}
