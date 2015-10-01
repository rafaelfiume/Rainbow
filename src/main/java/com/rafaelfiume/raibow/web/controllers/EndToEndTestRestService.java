package com.rafaelfiume.raibow.web.controllers;

import com.rafaelfiume.raibow.support.EndToEndTestsFinder;
import com.rafaelfiume.raibow.support.RainbowTestExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class EndToEndTestRestService {

    private final RainbowTestExecutor testExecutor = new RainbowTestExecutor();

    private final EndToEndTestsFinder endToEndTestsFinder;

    @Autowired
    public EndToEndTestRestService(EndToEndTestsFinder endToEndTestsFinder) {
        this.endToEndTestsFinder = endToEndTestsFinder;
    }

    @RequestMapping(value = "/test/end-to-end", method = GET, produces = "text/plain")
    public ResponseEntity<String> handle() {

        return new ResponseEntity<>(
                testExecutor.run(endToEndTestsFinder.testClasses()).name(),
                OK
        );
    }

}
