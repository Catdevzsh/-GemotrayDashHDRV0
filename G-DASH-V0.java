import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeometryDash extends JFrame {
    private GamePanel gamePanel;
    private Timer gameTimer;

    public GeometryDash() {
        setTitle("Geometry Dash");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        gamePanel = new GamePanel();
        add(gamePanel);

        gameTimer = new Timer(16, new ActionListener() { // ~60 FPS
            @Override
            public void actionPerformed(ActionEvent e) {
                gamePanel.updateGame();
                gamePanel.repaint();
            }
        });

        gameTimer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                GeometryDash game = new GeometryDash();
                game.setVisible(true);
            }
        });
    }
}

class GamePanel extends JPanel {
    private Player player;
    private List<Obstacle> obstacles;
    private int score;
    private int obstacleSpawnTimer;
    private Random random;
    private boolean gameRunning;

    public GamePanel() {
        setBackground(Color.BLACK);
        player = new Player();
        obstacles = new ArrayList<>();
        score = 0;
        obstacleSpawnTimer = 0;
        random = new Random();
        gameRunning = true;

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    player.jump();
                }
            }
        });
        setFocusable(true);
    }

    public void updateGame() {
        if (gameRunning) {
            player.update();
            updateObstacles();
            checkCollisions();
            score++;
        }
    }

    private void updateObstacles() {
        obstacleSpawnTimer++;
        if (obstacleSpawnTimer > 100) {
            obstacles.add(new Obstacle(getWidth(), random.nextInt(getHeight() - 50)));
            obstacleSpawnTimer = 0;
        }

        for (int i = 0; i < obstacles.size(); i++) {
            Obstacle obstacle = obstacles.get(i);
            obstacle.update();
            if (obstacle.isOffScreen()) {
                obstacles.remove(i);
                i--;
            }
        }
    }

    private void checkCollisions() {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.getBounds().intersects(player.getBounds())) {
                gameOver();
            }
        }
    }

    private void gameOver() {
        gameRunning = false;
        JOptionPane.showMessageDialog(this, "Game Over! Your score: " + score);
        System.exit(0);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        player.draw(g);
        for (Obstacle obstacle : obstacles) {
            obstacle.draw(g);
        }

        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);
    }
}

class Player {
    private int x, y;
    private int width, height;
    private int yVelocity;
    private static final int GRAVITY = 1;
    private static final int JUMP_STRENGTH = 15;
    private String[] asciiArt = {
            "  ____  ",
            " /    \\ ",
            "|  ()  |",
            " \\____/ "
    };

    public Player() {
        x = 50;
        y = 300;
        width = 30;
        height = 30;
        yVelocity = 0;
    }

    public void update() {
        yVelocity += GRAVITY;
        y += yVelocity;
        if (y > 300) {
            y = 300;
            yVelocity = 0;
        }
    }

    public void jump() {
        if (y == 300) {
            yVelocity = -JUMP_STRENGTH;
        }
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        for (int i = 0; i < asciiArt.length; i++) {
            g.drawString(asciiArt[i], x, y + i * 15);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}

class Obstacle {
    private int x, y;
    private int width, height;
    private static final int SPEED = 5;
    private String[] asciiArt = {
            "  ____  ",
            " |    | ",
            " |____| "
    };

    public Obstacle(int startX, int startY) {
        x = startX;
        y = startY;
        width = 20;
        height = 50;
    }

    public void update() {
        x -= SPEED;
    }

    public boolean isOffScreen() {
        return x + width < 0;
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        for (int i = 0; i < asciiArt.length; i++) {
            g.drawString(asciiArt[i], x, y + i * 15);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
