package org.obiba.mica.core.domain;

import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import org.obiba.core.util.StringUtil;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import sun.util.locale.LanguageTag;

public class LocalizedString extends TreeMap<String, String> {

  private static final long serialVersionUID = 5813178087884887246L;

  public LocalizedString() {}

  public LocalizedString(@NotNull Locale locale, @NotNull String str) {
    this();
    put(locale, str);
  }

  @Override
  public String put(@Nullable String locale, String value) {
    if(Strings.isNullOrEmpty(value)) return null;
    return super.put(locale == null ? LanguageTag.UNDETERMINED : Locale.forLanguageTag(locale).toLanguageTag(), value);
  }

  public String put(@NotNull Locale locale, @NotNull String str) {
    if(Strings.isNullOrEmpty(str)) return null;
    return super.put(locale == null ? LanguageTag.UNDETERMINED : locale.toLanguageTag(), str);
  }

  public LocalizedString forLocale(@NotNull Locale locale, @NotNull String str) {
    put(locale.toLanguageTag(), str);
    return this;
  }

  public LocalizedString forLanguageTag(@Nullable String locale, @NotNull String str) {
    return forLocale(locale == null ? Locale.forLanguageTag("und") : Locale.forLanguageTag(locale), str);
  }

  public LocalizedString forEn(@NotNull String str) {
    return forLocale(Locale.ENGLISH, str);
  }

  public LocalizedString forFr(@NotNull String str) {
    return forLocale(Locale.FRENCH, str);
  }

  public boolean contains(@Nullable Locale locale) {
    return containsKey(locale);
  }

  public LocalizedString merge(LocalizedString values) {
    if(values == null) return this;
    values.entrySet().forEach(entry -> put(entry.getKey(), entry.getValue()));
    return this;
  }

  public String asString() {
    List<String> values = Lists.newArrayList();
    values().forEach(value -> {
      String normalized = value.replace(' ', '-');
      if(!values.contains(normalized)) values.add(normalized);
    });
    return StringUtil.collectionToString(values, "-");
  }

  //
  // Static methods
  //

  public static LocalizedString en(@NotNull String str) {
    return new LocalizedString(Locale.ENGLISH, str);
  }

  public static LocalizedString fr(@NotNull String str) {
    return new LocalizedString(Locale.FRENCH, str);
  }

}
