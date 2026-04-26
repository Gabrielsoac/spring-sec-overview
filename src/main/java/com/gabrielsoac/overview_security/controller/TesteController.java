package com.gabrielsoac.overview_security.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/test")
public class TesteController {
    
    @GetMapping()    
    public String test(){
        return "testing...";
    }
}
