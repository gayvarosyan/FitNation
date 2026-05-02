package com.example.fitnationweb.endpoint;

import com.example.fitnationuser.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootMvcEndpoint {

    @GetMapping("/")
    public String home() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User user)) {
            return "redirect:/login";
        }
        return switch (user.getRole()) {
            case ADMIN -> "redirect:/admin/trainers";
            case CLIENT, TRAINER -> "redirect:/portal";
        };
    }
}
