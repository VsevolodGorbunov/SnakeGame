package com.example.lab3;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class Menu {

    @FXML public TextField fieldX;
    @FXML public TextField fieldY;

    public void play() {
        var game = new Game(Integer.parseInt(fieldX.getText()),Integer.parseInt(fieldY.getText()));
        game.start(new Stage());
        ((Stage)fieldX.getScene().getWindow()).close();
    }
}
