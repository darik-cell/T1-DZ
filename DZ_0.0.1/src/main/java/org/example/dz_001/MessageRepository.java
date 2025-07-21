package org.example.dz_001;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageRepository {
  private final String url;
  private static final String USER = "sa";
  private static final String PASS = "";

  static {
    try {
      Class.forName("org.h2.Driver");   // <‑‑ ключевая строка
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException("H2 driver not found on classpath", e);
    }
  }

  public MessageRepository() {
    this.url = System.getenv().getOrDefault("H2_DB_URL", "jdbc:h2:/opt/h2-data/support;AUTO_SERVER=TRUE");
    initSchema();
  }

  public List<String> findAll() {
    try (Connection c = DriverManager.getConnection(url, USER, PASS);
         PreparedStatement ps = c.prepareStatement("SELECT text FROM messages ORDER BY id");
         ResultSet rs = ps.executeQuery()) {

      List<String> list = new ArrayList<>();
      while (rs.next()) list.add(rs.getString(1));
      return list;
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }

  public void save(String msg) {
    try (Connection c = DriverManager.getConnection(url, USER, PASS);
         PreparedStatement ps = c.prepareStatement("INSERT INTO messages(text) VALUES (?)")) {
      ps.setString(1, msg);
      ps.executeUpdate();
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }

  MessageRepository(String urlForTest) {
    this.url = urlForTest;
    initSchema();
  }

  private void initSchema() {
    try (Connection c = DriverManager.getConnection(url, USER, PASS);
         Statement st = c.createStatement()) {

      st.executeUpdate("""
              CREATE TABLE IF NOT EXISTS messages(
                  id IDENTITY PRIMARY KEY,
                  text VARCHAR(255) NOT NULL
              )
              """);

      try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM messages")) {
        rs.next();
        if (rs.getInt(1) == 0) {
          st.executeUpdate("INSERT INTO messages(text) VALUES ('Ты справишься!')");
        }
      }
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }
}
