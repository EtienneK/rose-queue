package com.etiennek.rq.api.dtos;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Message {

  private String id;
  private String messageBody;

  protected Message() {
  }

  public Message(String messageBody) {
    checkNotNull(messageBody, "messageBody");
    this.messageBody = messageBody;
  }

  public Message createCopyWithId(String id) {
    checkNotNull(id, "id");
    Message copy = new Message(messageBody);
    copy.id = id;
    return copy;
  }

  @XmlAttribute
  public String getId() {
    return id;
  }

  @XmlAttribute
  public String getMessageBody() {
    return messageBody;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
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
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (messageBody == null) {
      if (other.messageBody != null)
        return false;
    } else if (!messageBody.equals(other.messageBody))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Message [id=" + id + ", messageBody=" + messageBody + "]";
  }

}
