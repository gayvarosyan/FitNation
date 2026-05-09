package com.example.fitnationweb.controller;

import com.example.fitnationweb.service.MvcChatService;
import com.example.fitnationweb.support.CurrentUserAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/portal/chat")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('CLIENT','TRAINER')")
public class ChatMvcController {

    private final MvcChatService mvcChatService;
    private final CurrentUserAccessor currentUserAccessor;

    @GetMapping
    public String page(
            @RequestParam(required = false) Long conversationId,
            Model model) {
        var user = currentUserAccessor.requireUser();
        mvcChatService.populatePageModel(user.getId(), conversationId, model);
        return "portal/chat";
    }

    @PostMapping("/open")
    public String open(@RequestParam Long otherUserId, RedirectAttributes redirectAttributes) {
        var user = currentUserAccessor.requireUser();
        var result = mvcChatService.openConversation(user.getId(), user.getRole(), otherUserId);
        result.applyTo(redirectAttributes);
        return result.redirectView();
    }
}
