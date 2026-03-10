package com.example.demo.controller;

import com.example.demo.entity.submission;
import com.example.demo.service.CompilerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/compile")

@AllArgsConstructor
public class controller {

    private CompilerService compilerService;
    @PostMapping("/")
    public String compile(@RequestBody submission request) throws Exception {
        return compilerService.compileSubmission(request);
    }
    @PostMapping("/test")
    public String test(@RequestBody submission request) throws Exception {
        return compilerService.compileAndTest(request);
    }
}