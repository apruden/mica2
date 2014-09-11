package org.obiba.mica.core.domain;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class Attribute implements Serializable {

  private static final long serialVersionUID = 8869937335553092873L;

  @NotNull
  private String name;

  private String namespace;

  private LocalizedString values;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public void setValues(LocalizedString values) {
    this.values = values;
  }

  public LocalizedString getValues() {
    return values;
  }

  @JsonIgnore
  public String getMapKey() {
    return getMapKey(name, namespace);
  }

  public boolean isLocalisedWith(@Nullable Locale locale) {
    return values != null && values.contains(locale);
  }

  public static String getMapKey(String name, @Nullable String namespace) {
    return Strings.isNullOrEmpty(namespace) ? name : namespace + "__" + name;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, namespace, values);
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj) return true;
    if(obj == null || getClass() != obj.getClass()) return false;
    Attribute other = (Attribute) obj;
    return Objects.equals(name, other.name) && Objects.equals(namespace, other.namespace) &&
        Objects.equals(values, other.values);
  }

  @Override
  public String toString() {
    return com.google.common.base.Objects.toStringHelper(this).omitNullValues().add("name", name)
        .add("namespace", namespace).add("values", values).toString();
  }

  @SuppressWarnings("ParameterHidesMemberVariable")
  public static class Builder {

    private final Attribute attribute;

    private Builder(@NotNull String name) {
      Preconditions.checkArgument(name != null, "name cannot be null");
      attribute = new Attribute();
      attribute.setName(name);
    }

    private Builder() {
      attribute = new Attribute();
    }

    public static Builder newAttribute(String name) {
      if(!name.contains("__")) return new Builder(name);
      else {
        String namespace = name.substring(0, name.indexOf("__"));
        return new Builder(name.substring(name.indexOf("__") + 2)).namespace(namespace);
      }
    }

    public static Builder newAttribute(org.obiba.magma.Attribute attr) {
      Builder builder = new Builder(attr.getName());
      if(attr.hasNamespace()) builder.namespace(attr.getNamespace());

      if(attr.getValue() != null) {
        if(attr.isLocalised()) builder.value(attr.getLocale(), attr.getValue().toString());
        else builder.value(Locale.forLanguageTag("und"), attr.getValue().toString());
      }
      return builder;
    }

    public Builder namespace(String namespace) {
      attribute.namespace = namespace;
      return this;
    }

    public Builder values(LocalizedString values) {
      if(attribute.values == null) attribute.values = values;
      else attribute.values.merge(values);
      return this;
    }

    public Builder value(Locale locale, String value) {
      if(attribute.values == null) {
        attribute.values = new LocalizedString();
      }
      attribute.values.merge(locale.toLanguageTag(), value, (oldValue, newValue) -> newValue);
      return this;
    }

    public Attribute build() {
      return attribute;
    }

  }
}