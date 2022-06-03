package com.example.lab3;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game extends Application {
    int speed = 5; //скорость
    int foodcolor = 0; //цвет еды
    int width = 60; //ширина поля (максимум 60)
    int height = 30; //высота поля (не трогать)
    int foodX = 0;
    int foodY = 0;
    int cornersize = 25;
    List<Corner> snake = new ArrayList<>();
    Dir direction = Dir.left;
    boolean gameOver = false;
    boolean gameWin = false;
    Random rand = new Random();

    public enum Dir {
        left, right, up, down
    }

    public Game(){}
    public Game(int w,int h) {
        width = w;
        height = h;
    }

    public static class Corner {
        int x;
        int y;

        public Corner(int x, int y) {
            this.x = x;
            this.y = y;
        }

    }

    public void start(Stage primaryStage) {
        try {
            newFood();

            VBox root = new VBox();
            Canvas c = new Canvas(width * cornersize, height * cornersize);
            GraphicsContext gc = c.getGraphicsContext2D();
            root.getChildren().add(c);

            new AnimationTimer() {
                long lastTick = 0;

                public void handle(long now) {
                    if (lastTick == 0) {
                        lastTick = now;
                        tick(gc);
                        return;
                    }

                    if (now - lastTick > 1000000000 / speed) {
                        lastTick = now;
                        tick(gc);
                    }
                }

            }.start();

            Scene scene = new Scene(root, width * cornersize, height * cornersize);

            // управление
            scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
                if ((key.getCode() == KeyCode.W || key.getCode() == KeyCode.UP) && direction != Dir.down) {
                    direction = Dir.up;
                }
                if ((key.getCode() == KeyCode.A || key.getCode() == KeyCode.LEFT) && direction != Dir.right) {
                    direction = Dir.left;
                }
                if ((key.getCode() == KeyCode.S || key.getCode() == KeyCode.DOWN) && direction != Dir.up) {
                    direction = Dir.down;
                }
                if ((key.getCode() == KeyCode.D || key.getCode() == KeyCode.RIGHT) && direction != Dir.left) {
                    direction = Dir.right;
                }
                if (key.getCode() == KeyCode.ESCAPE) {
                    toMenu();
                    primaryStage.close();
                }
            });

            // добавление частей змейки
            snake.add(new Corner(width / 2, height / 2));

            primaryStage.setOnCloseRequest(windowEvent -> {
                toMenu();
                primaryStage.close();});

            primaryStage.setScene(scene);
            primaryStage.setTitle("Змейка");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void toMenu(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menu.fxml"));
        try {
            Scene menu = new Scene(fxmlLoader.load(), 400, 300);
            var stage = new Stage();
            stage.setTitle("Меню");
            stage.setScene(menu);
            stage.show();
        } catch (IOException e) {e.printStackTrace();}
    }
    // тик
    public  void tick(GraphicsContext gc) {
        if (gameOver) {
            gc.setFill(Color.RED);
            gc.setFont(new Font("", 50));
            gc.fillText("Игра окончена", 100, 250);
            return;
        }

        if (gameWin) {
            gc.setFill(Color.GREEN);
            gc.setFont(new Font("", 50));
            gc.fillText("Победа", 100, 250);
            return;
        }

        for (int i = snake.size() - 1; i >= 1; i--) {
            snake.get(i).x = snake.get(i - 1).x;
            snake.get(i).y = snake.get(i - 1).y;
        }

        switch (direction) {
            case up:
                snake.get(0).y--;
                if (snake.get(0).y < 0) {
                    gameOver = true;
                }
                break;
            case down:
                snake.get(0).y++;
                if (snake.get(0).y > height) {
                    gameOver = true;
                }
                break;
            case left:
                snake.get(0).x--;
                if (snake.get(0).x < 0) {
                    gameOver = true;
                }
                break;
            case right:
                snake.get(0).x++;
                if (snake.get(0).x > width) {
                    gameOver = true;
                }
                break;

        }

        // змейка ест еду
        if (foodX == snake.get(0).x && foodY == snake.get(0).y) {
            snake.add(new Corner(-1, -1));
            newFood();
            if (speed == 16)
                gameWin = true;
        }

        // самоуничтожение
        for (int i = 1; i < snake.size(); i++) {
            if (snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y) {
                gameOver = true;
            }
        }

        // заполнение заднего фона
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width * cornersize, height * cornersize);

        // счёт
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("", 30));
        gc.fillText("Счёт: " + (speed - 6), 10, 30);

        // рандомный цвет еды
        Color cc = Color.WHITE;

        switch (foodcolor) {
            case 0:
                cc = Color.PURPLE;
                break;
            case 1:
                cc = Color.LIGHTBLUE;
                break;
            case 2:
                cc = Color.YELLOW;
                break;
            case 3:
                cc = Color.PINK;
                break;
            case 4:
                cc = Color.ORANGE;
                break;
        }
        gc.setFill(cc);
        gc.fillOval(foodX * cornersize, foodY * cornersize, cornersize, cornersize);

        // змея
        for (Corner c : snake) {
            gc.setFill(Color.LIGHTGREEN);
            gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 1, cornersize - 1);
            gc.setFill(Color.GREEN);
            gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 2, cornersize - 2);

        }

    }

    // еда
    public void newFood() {
        start: while (true) {
            foodX = rand.nextInt(width);
            foodY = rand.nextInt(height);

            for (Corner c : snake) {
                if (c.x == foodX && c.y == foodY) {
                    continue start;
                }
            }
            foodcolor = rand.nextInt(5);
            speed++;
            break;

        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}