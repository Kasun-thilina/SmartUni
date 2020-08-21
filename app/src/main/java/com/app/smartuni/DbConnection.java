package com.app.smartuni;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbConnection {
  static Connection connect = null;

  public static Connection getConnect() {
    try {
      // This will load the MySQL driver, each DB has its own driver
      Class.forName("com.mysql.jdbc.Driver");
      connect = DriverManager
              .getConnection("jdbc:mysql://192.168.43.12:3306/university_students?useUnicode=yes",
                      "root", "root");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return connect;
  }
}
