package com.google.cloud.servicebroker.examples.wiki.servicebrokerexamplewiki;

import java.util.Map;

public class Tiddler {
  private String title;
  private String created;
  private String creator;
  private Map<String, String> fields;
  private String modified;
  private String modifier;
  private String[] tags;
  private String text;
  private String type;

  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public Map<String, String> getFields() {
    return fields;
  }

  public void setFields(Map<String, String> fields) {
    this.fields = fields;
  }

  public String getModified() {
    return modified;
  }

  public void setModified(String modified) {
    this.modified = modified;
  }

  public String[] getTags() {
    return tags;
  }

  public void setTags(String[] tags) {
    this.tags = tags;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getModifier() {
    return modifier;
  }

  public void setModifier(String modifier) {
    this.modifier = modifier;
  }
}
