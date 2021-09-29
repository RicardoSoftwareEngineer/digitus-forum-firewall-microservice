package com.digitusforum.firewall.historic;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
public class TheFirstOneController {

    @RequestMapping(value = "/sayhi")
    public Object hi() {
        return "hi dad";
    }
}
