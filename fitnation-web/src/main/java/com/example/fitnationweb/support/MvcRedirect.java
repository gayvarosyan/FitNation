package com.example.fitnationweb.support;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public record MvcRedirect(String redirectView, String message, String error) {

    public static MvcRedirect to(String path, String message) {
        return new MvcRedirect("redirect:" + normalizePath(path), message, null);
    }

    public static MvcRedirect failure(String path, String error) {
        return new MvcRedirect("redirect:" + normalizePath(path), null, error);
    }

    private static String normalizePath(String path) {
        return path.startsWith("/") ? path : "/" + path;
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
