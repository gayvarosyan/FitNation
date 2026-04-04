package com.example.fitnationweb.support;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public record MvcRedirect(String redirectView, String message, String error) {

    public static MvcRedirect to(String path, String message) {
        String p = path.startsWith("/") ? path : "/" + path;
        return new MvcRedirect("redirect:" + p, message, null);
    }

    public static MvcRedirect failure(String path, String error) {
        String p = path.startsWith("/") ? path : "/" + path;
        return new MvcRedirect("redirect:" + p, null, error);
    }

    public void applyTo(RedirectAttributes redirectAttributes) {
        if (message != null) {
            redirectAttributes.addFlashAttribute("message", message);
        }
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
        }
    }
}
