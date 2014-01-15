package com.etiennek.rq.api.dtos;

import static com.google.common.base.Preconditions.checkNotNull;

public class HeldMessage extends Message {

  private String id;

  public HeldMessage(String id, String messageBody) {
    super(messageBody);

    checkNotNull(id, "id");

    this.id = id;
  }

  public String getId() {
    return id;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    HeldMessage other = (HeldMessage) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

}
