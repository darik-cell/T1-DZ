package org.example.dz_001.configuration;

import org.example.dz_001.MessageRepository;
import org.example.dz_001.Repository;
import org.example.dz_001.controller.SupportController;

@Configuration
public class AppConfig {

  @Instance
  public Repository repository() {
    return new MessageRepository();
  }

  @Instance
  public SupportController supportController(Repository repository) {
    return new SupportController(repository);
  }
}
