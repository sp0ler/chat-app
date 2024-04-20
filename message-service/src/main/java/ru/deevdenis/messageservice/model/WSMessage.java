package ru.deevdenis.messageservice.model;

public record WSMessage(
        String messageId,
        String senderLoginId,
        String recipientLoginId,
        String message
) {}
