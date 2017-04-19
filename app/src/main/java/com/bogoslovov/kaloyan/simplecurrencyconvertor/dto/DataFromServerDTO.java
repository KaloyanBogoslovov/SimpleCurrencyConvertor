package com.bogoslovov.kaloyan.simplecurrencyconvertor.dto;

/**
 * Created by kaloqn on 4/11/17.
 */

public class DataFromServerDTO {

  private int responseCode;

  private String body;

  public int getResponseCode() {
    return responseCode;
  }

  public void setResponseCode(int responseCode) {
    this.responseCode = responseCode;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }
}
