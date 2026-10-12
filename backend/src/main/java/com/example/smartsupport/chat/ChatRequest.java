package com.example.smartsupport.chat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record ChatRequest(@NotBlank @Size(max=2000) String message, @Size(max=100) String conversationId) {}
