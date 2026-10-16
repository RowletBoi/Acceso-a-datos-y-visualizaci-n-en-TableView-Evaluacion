module org.example.evaluacionaccesodedatos {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.evaluacionaccesodedatos to javafx.fxml;
    opens org.example.evaluacionaccesodedatos.database to javafx.fxml;
    opens org.example.evaluacionaccesodedatos.controller to javafx.fxml;
    opens org.example.evaluacionaccesodedatos.model to javafx.fxml;

    exports org.example.evaluacionaccesodedatos;
    exports org.example.evaluacionaccesodedatos.controller;
    exports org.example.evaluacionaccesodedatos.database;
    exports org.example.evaluacionaccesodedatos.model;
}