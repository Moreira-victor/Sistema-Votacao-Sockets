package com.voting.common;

import java.io.Serializable;
import java.util.List;

/**
 * DTO (Data Transfer Object) para enviar detalhes da eleição do servidor ao cliente.
 * Precisa ser Serializable para ser enviado via ObjectStream.
 */
public class ElectionData implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String question;
    private final List<String> options;

    public ElectionData(String question, List<String> options) {
        this.question = question;
        this.options = options;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }
}