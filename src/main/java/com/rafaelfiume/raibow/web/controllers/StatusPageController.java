package com.rafaelfiume.raibow.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class StatusPageController {

    @RequestMapping(value = "/test/end-to-end", method = GET, produces = "text/plain")
    public ResponseEntity<String> handle() {

        final String body = new StringBuilder("OK").toString();

        return new ResponseEntity<>(body, OK);
    }

}
