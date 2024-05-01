package ru.deevdenis.sessionservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table
@NoArgsConstructor
public class SessionBodyDTO {

    @Id
    private UUID id;

    private String senderLoginId;

    private String recipientLoginId;

    private String baseUrl;

    private LocalDateTime createdTime = LocalDateTime.now();
}
