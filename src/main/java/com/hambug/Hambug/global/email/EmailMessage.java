package com.hambug.Hambug.global.email;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EmailMessage {
    private String to;
    private String subject;
    private String body;
    @Builder.Default
    private boolean html = true;
    private List<String> cc;
    private List<String> bcc;
}
