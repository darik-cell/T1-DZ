package org.example.dz_001;

import java.util.List;

public interface Repository {

  List<String> findAll();
  void save(String msg);
}
