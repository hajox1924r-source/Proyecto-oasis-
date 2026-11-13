package com.farmaciaoasis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Muestra la página de inicio de sesión. El envío del formulario lo procesa Spring Security. */
@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
