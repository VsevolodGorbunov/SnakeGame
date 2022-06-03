package com.example.lab3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Main extends Application {
    static int speed = 5; //скорость
    static int foodcolor = 0; //цвет еды
    static int width = 60; //ширина поля (максимум 60)
    static int height = 30; //высота поля (не трогать)
    static int foodX = 0;
    static int foodY = 0;
    static int cornersize = 25;
    static List<Corner> snake = new ArrayList<>();
    static Dir direction = Dir.left;
    static boolean gameOver = false;
    static boolean gameWin = false;
    static Random rand = new Random();

    public enum Dir {
        left, right, up, down
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
            });

            // добавление частей змейки
            snake.add(new Corner(width / 2, height / 2));

            primaryStage.setScene(scene);
            primaryStage.setTitle("Змейка");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // тик
    public static void tick(GraphicsContext gc) {
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
    public static void newFood() {
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