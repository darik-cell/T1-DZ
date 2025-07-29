package org.example.dz_001.controller;

import org.example.dz_001.Repository;
import org.example.dz_001.controller.annotation.Controller;
import org.example.dz_001.controller.annotation.GetMapping;
import org.example.dz_001.controller.annotation.PostMapping;

import java.util.List;

@Controller
public class SupportController {

  private final Repository repo;

  public SupportController(Repository repository) {
    this.repo = repository;
  }

  @GetMapping("/v1/support")
  public List<String> getAll() {
    return repo.findAll();
  }

  @PostMapping("/v1/support")
  public void add(String message) {
    if (message == null || message.isBlank()) throw new IllegalArgumentException("Body is empty");
    repo.save(message);
  }
}
