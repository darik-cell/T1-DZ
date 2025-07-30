package org.example.dz_001.configuration;

import org.example.dz_001.MessageRepository;
import org.example.dz_001.Repository;
import org.example.dz_001.controller.SupportControllerImpl;

@Configuration
public class AppConfig {

  @Instance
  public Repository repository() {
    return new MessageRepository();
  }

  @Instance
  public SupportControllerImpl supportController(Repository repository) {
    return new SupportControllerImpl(repository);
  }
}
