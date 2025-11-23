package com.voting.common;

import java.io.Serializable;

/**
 * DTO (Data Transfer Object) para enviar um voto do cliente ao servidor.
 * Precisa ser Serializable para ser enviado via ObjectStream.
 */
public class Vote implements Serializable {
  private static final long serialVersionUID = 1L;

  private final String cpf;
  private final String chosenOption;

  public Vote(String cpf, String chosenOption) {
    this.cpf = cpf;
    this.chosenOption = chosenOption;
  }

  public String getCpf() {
    return cpf;
  }

  public String getChosenOption() {
    return chosenOption;
  }
}