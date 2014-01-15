package com.etiennek.rq.api.dtos;

import static com.google.common.base.Preconditions.checkNotNull;

public class Message {

  private String messageBody;

  public Message(String messageBody) {
    checkNotNull(messageBody, "messageBody");
    this.messageBody = messageBody;
  }

  public String getMessageBody() {
    return messageBody;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((messageBody == null) ? 0 : messageBody.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Message other = (Message) obj;
    if (messageBody == null) {
      if (other.messageBody != null)
        return false;
    } else if (!messageBody.equals(other.messageBody))
      return false;
    return true;
  }

}
