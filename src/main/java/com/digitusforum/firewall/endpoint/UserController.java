package com.digitusforum.firewall.endpoint;

import com.digitusforum.firewall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vo.UserVO;

import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
public class UserController  {
    @Autowired
    UserService userService;
    //todo user´s crud
    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public ResponseEntity handle() {
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/user/create")
    public Object create() {
        return "";
    }

    @RequestMapping(value = "/user/retrieve")
    public Object retrieve(@PathVariable Optional<String> id) {
        return "hi dad";
    }

    @RequestMapping(value = "/user/{id}/retrieve")
    public Object retrieve() {
        return "hi dad";
    }

    @RequestMapping(value = "/user/{id}/update")
    public Object update() {
        return "hi dad";
    }

    @RequestMapping(value = "/user/{id}/delete")
    public Object delete() {
        return "hi dad";
    }

    @RequestMapping(value = "/user/{id}/validate")
    public Object validate() {
        return "hi dad";
    }

    @RequestMapping(value = "/user/login")
    public Object create(@RequestHeader(defaultValue = "en_us") String locale,
                         @RequestBody UserVO user) {
        return null;// userService.loginWithEmailAndPassword(user, locale);
    }


}
