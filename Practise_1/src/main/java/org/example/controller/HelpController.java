package org.example.controller;

import org.example.controller.annotation.Controller;
import org.example.controller.annotation.RequestMapping;
import org.example.model.SupportPhrase;
import org.example.proxy.annotation.Logged;

@Controller
public interface HelpController {
  @Logged
  @RequestMapping(method = HttpMethod.GET, path = "/")
  SupportPhrase getSupportPhrase();
}
