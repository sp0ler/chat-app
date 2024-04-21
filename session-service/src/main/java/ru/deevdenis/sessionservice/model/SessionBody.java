package ru.deevdenis.sessionservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionBody implements Serializable {
    private String id;
    private String host;
    private String text;
}
