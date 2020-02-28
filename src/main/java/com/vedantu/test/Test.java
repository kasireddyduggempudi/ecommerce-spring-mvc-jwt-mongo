package com.vedantu.test;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Test {

    @RequestMapping(value="/test")
    public String test(Model m){
        m.addAttribute("name", "kasi");
        return "test";
    }
}
