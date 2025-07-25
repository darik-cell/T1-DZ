package org.example.dz_001.configuration;

import org.example.dz_001.MessageRepository;
import org.example.dz_001.Repository;

@Configuration
public class AppConfig {

  @Instance
  public Repository repository() {
    return new MessageRepository();
  }
}
