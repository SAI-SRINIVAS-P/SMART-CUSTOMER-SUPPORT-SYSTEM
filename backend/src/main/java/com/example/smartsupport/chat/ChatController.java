package com.example.smartsupport.chat;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController @RequestMapping("/api")
public class ChatController {
    private final AgentService agent;
    public ChatController(AgentService agent){this.agent=agent;}
    @PostMapping("/chat") public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        String answer=agent.answer(request.message());
        String id=(request.conversationId()==null || request.conversationId().isBlank()) ? UUID.randomUUID().toString() : request.conversationId();
        return new ChatResponse(answer==null || answer.isBlank() ? "I could not generate an answer just now. Please try again." : answer,id);
    }
}
