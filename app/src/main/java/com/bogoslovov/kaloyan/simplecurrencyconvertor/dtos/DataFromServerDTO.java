package com.bogoslovov.kaloyan.simplecurrencyconvertor.dtos;

import java.io.BufferedReader;

/**
 * Created by kaloqn on 4/11/17.
 */

public class DataFromServerDTO {

  private int responseCode;

  private BufferedReader body;

  public int getResponseCode() {
    return responseCode;
  }

  public void setResponseCode(int responseCode) {
    this.responseCode = responseCode;
  }

  public BufferedReader getBody() {
    return body;
  }

  public void setBody(BufferedReader body) {
    this.body = body;
  }
}
