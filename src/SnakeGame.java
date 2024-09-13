import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;


public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private class Tile {
        int x;
        int y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    int boardWidth;
    int boardHeight;
    int tileSize = 25;

    //Snake
    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    //Food
    Tile food;
    Random random;

    //Game Logic
    Timer gameLoop;
    int velocityX;
    int velocityY;
    boolean gameOver = false;
    ArrayList<Integer> moveQue = new ArrayList<>();

    SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);


        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<Tile>();


        food = new Tile(0, 0);
        random = new Random();
        placeFood();

        velocityX = 0;
        velocityY = 0;

        gameLoop = new Timer(100, this);
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
//        //Grid
//        for (int i = 0; i < boardWidth / tileSize; i++) {
//            g.drawLine(i * tileSize, 0, i * tileSize, boardHeight);
//            g.drawLine(0, i * tileSize, boardWidth, i * tileSize);
//        }

        //Food
        g.setColor(Color.red);
        g.fillRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize);

        //Snake Head
        g.setColor(Color.green);
//        g.fillRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize);
        g.fill3DRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize, true);

        //Snake Body
        for (int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);
//            g.fillRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize);
            g.fill3DRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize, true);
        }

        //Score
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth("Press [R] to restart");
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        if (gameOver) {
            g.setColor(Color.red);
            g.drawString("Game Over: " + String.valueOf(snakeBody.size()), tileSize - 16, tileSize);
            g.drawString("Press [R] to restart", (boardWidth-textWidth)/2, boardHeight/2);
        } else {
            g.drawString("Score: " + String.valueOf(snakeBody.size()), tileSize - 16, tileSize);
        }
    }

    public void placeFood() {
        food.x = random.nextInt(boardWidth / tileSize);
        food.y = random.nextInt(boardHeight / tileSize);

    }

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public void move() {
        //eat food
        if (collision(snakeHead, food)) {
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
        }

        //Snake Body
        for (int i = snakeBody.size() - 1; i >= 0; i--) {
            Tile snakePart = snakeBody.get(i);
            if (i == 0) {
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            } else {
                Tile prevSnakePart = snakeBody.get(i - 1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        //Snake Head
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        //game over cons
        for (int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);
            //collide with body
            if (collision(snakeHead, snakePart)) {
                gameOver = true;
            }
        }
        if (snakeHead.x * tileSize < 0 || snakeHead.x * tileSize > boardWidth ||
                snakeHead.y * tileSize < 0 || snakeHead.y * tileSize > boardHeight) {
            gameOver = true;
        }


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!moveQue.isEmpty()) {
            processNextMove();
        }
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    public void processNextMove() {
        if (!moveQue.isEmpty()) {
            int nextMove = moveQue.removeFirst();
            switch (nextMove) {
                case 1:
                    if (velocityY != 1) {
                        velocityX = 0;
                        velocityY = -1;
                    }
                    break;
                case 2:
                    if (velocityY != -1) {
                        velocityX = 0;
                        velocityY = 1;
                    }
                    break;
                case 3:
                    if (velocityX != 1) {
                        velocityX = -1;
                        velocityY = 0;
                    }
                    break;
                case 4:
                    if (velocityX != -1) {
                        velocityX = 1;
                        velocityY = 0;
                    }
                    break;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int direction = 0;

        if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_UP) {
            direction = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_DOWN) {
            direction = 2;
        } else if (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_LEFT) {
            direction = 3;
        } else if (e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_RIGHT) {
            direction = 4;
        }

        if (gameOver){
            if (e.getKeyCode() == KeyEvent.VK_R){
                gameOver= false;
                snakeBody = new ArrayList<Tile>();
                snakeHead.x= 5;
                snakeHead.y= 5;
                velocityX= 0;
                velocityY= 0;
                placeFood();
                moveQue = new ArrayList<>();
                gameLoop.start();
            }
        }

        if (direction != 0) {
            if (moveQue.size() > 2){
                moveQue.removeLast();
            }
            moveQue.add(direction);
        }
    }


    // Dont Need
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }


}
