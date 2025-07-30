package org.example;

import org.example.controller.annotation.Controller;
import org.example.proxy.annotation.Logged;

@Controller
public interface SupportService {

  @Logged
  String getPhrase();
}
