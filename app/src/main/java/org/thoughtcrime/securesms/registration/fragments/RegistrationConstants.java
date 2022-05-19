package org.thoughtcrime.securesms.registration.fragments;

final class RegistrationConstants {

  private RegistrationConstants() {
  }

  static String API_BASE = "https://api.chapii.com";
  static final String TERMS_AND_CONDITIONS_URL = API_BASE + "/api/v1/static_html/user/terms_and_conditions/";
  static final String SIGNAL_CAPTCHA_URL       = "https://signalcaptchas.org/registration/generate.html";
  static final String SIGNAL_CAPTCHA_SCHEME    = "signalcaptcha://";

}
