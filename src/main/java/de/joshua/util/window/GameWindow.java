package de.joshua.util.window;

import de.joshua.configuration.file.YamlConfiguration;
import de.joshua.gameobjects.Missile;
import de.joshua.gameobjects.Tower;
import de.joshua.util.Coordinate;
import de.joshua.util.NamedImage;
import de.joshua.util.handler.JMenuItemHandler;
import de.joshua.util.handler.ResourceImageHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import static java.awt.event.KeyEvent.VK_F11;


public class GameWindow extends JPanel {
    public static double height;
    public static double width;
    private final JFrame mainFrame;
    public JMenuItem points;
    public Timer timer;
    public double tick;
    public Tower mainPlayerTower;
    public Coordinate mainPlayerTowerCoordinate;
    public String movingDirection = "+";
    Image backgroundImage;
    NamedImage[] images;
    JMenuBar mainMenuBar;
    double spinningSpeed = 0.05;
    File file = new File("config/config.yml");
    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

    public GameWindow(JFrame frame, int height, int width) throws IOException {
        this.mainFrame = frame;
        GameWindow.height = height;
        GameWindow.width = width;
        gameWindowInitialisation();
        gamePanelInitialisation();
    }

    public void gameWindowInitialisation() {
        addF11KeyListener();
        registerWindowListener();

        createMenuBar();
    }

    public void gamePanelInitialisation() {
        initGame();
        mainFrame.add(this);
        repaint();
    }

    public void createMenuBar() {
        mainMenuBar = new JMenuBar();
        mainFrame.setJMenuBar(mainMenuBar);

        mainMenuBar.add(createSettingsItem(mainMenuBar));
        mainMenuBar.add(addPointsItem(mainMenuBar));
    }

    public JMenuItem addPointsItem(JMenuBar mainMenuBar) {
        points = new JMenuItem("Points: " + tick);
        mainMenuBar.add(points);
        return points;
    }

    public JMenuItem createSettingsItem(JMenuBar mainMenuBar) {
        JMenu settings = new JMenu("Game Menu");

        settings.add(new JMenuItemHandler("Close Game", () -> System.exit(0)));
        
        JMenu fullGameWindow = new JMenu("Full Game Window");
        JMenu infoItem = new JMenu("Important: To get the Menu back, press F11!!!");
        fullGameWindow.add(infoItem);
        infoItem.add(new JMenuItemHandler("  X", () -> {
            mainMenuBar.setVisible(false);
            mainFrame.dispose();
            mainFrame.setUndecorated(true);
            mainFrame.setVisible(true);
        }));
        settings.add(fullGameWindow);

        JMenu background = new JMenu("Background");
        images = ResourceImageHandler.getImagesFromResources("images/");
        for (NamedImage image : images) {
            background.add(new JMenuItemHandler(image.getName(), () -> {
                backgroundImage = image.getImage();
                configuration.set("defaults.backgroundimagename", "images/" + image.getName());
                try {
                    configuration.save(file);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                repaint();
            }));
        }
        settings.add(background);

        JMenuItem startItem = new JMenuItem("Continue Game");
        JMenuItem stopItem = new JMenuItem("Stop Game");
        startItem.setVisible(false);

        startItem.addActionListener(e -> {
            continueGame();
            startItem.setVisible(false);
            stopItem.setVisible(true);
        });
        settings.add(startItem);

        stopItem.addActionListener(e -> {
            stopGame();
            stopItem.setVisible(false);
            startItem.setVisible(true);
        });
        settings.add(stopItem);

        return settings;
    }

    public void addF11KeyListener() {
        mainFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == VK_F11) {
                    if (mainMenuBar.isVisible()) {
                        mainMenuBar.setVisible(false);
                        mainFrame.dispose();
                        mainFrame.setUndecorated(true);
                        mainFrame.setVisible(true);
                    } else {
                        mainFrame.dispose();
                        mainFrame.setUndecorated(false);
                        mainFrame.setVisible(true);
                        mainMenuBar.setVisible(true);
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (movingDirection.equals("+")) {
                        movingDirection = "-";
                    } else {
                        movingDirection = "+";
                    }
                }

            }
        });

    }

    private void registerWindowListener() {
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                stopGame();
            }

            @Override
            public void windowActivated(WindowEvent e) {
                continueGame();
            }
        });
    }

    private void initGame() {
        createGameGameObjects();
        tick = 0;
        timer = new Timer(5, e -> doOnTick());
        timer.start();
    }

    public void doOnTick() {
        tick++;
        points.setText("Points: " + tick);
        mainPlayerTower.setDeltaMovingAngle(movingDirection, spinningSpeed);
        mainPlayerTower.shoot();
        // Methode um die gradzahl der Missel zu setzen
        repaint();
    }

    public void stopTimer() {
        timer.stop();
    }

    public void continueTimer() {
        try {
            timer.start();
        } catch (Exception ignored) {
        }
    }

    public void stopGame() {
        stopTimer();
    }

    public void continueGame() {
        continueTimer();
    }

    public void createGameGameObjects() {
        mainPlayerTowerCoordinate = new Coordinate(GameWindow.width / 2 - 50, GameWindow.height / 2 - 50);
        mainPlayerTower = new Tower(mainPlayerTowerCoordinate, 100, 100);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (!configuration.isSet("defaults.backgroundimagename")) {
            configuration.set("defaults.backgroundimagename", "images/" + images[0].getName());
            backgroundImage = images[0].getImage();
            try {
                configuration.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                backgroundImage = ImageIO.read(getClass().getClassLoader().getResource(configuration.getString("defaults.backgroundimagename")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        int heightImage = backgroundImage.getHeight(null);
        int widthImage = backgroundImage.getWidth(null);
        for (int x = 0; x < GameWindow.width; x += widthImage) {
            for (int y = 0; y < GameWindow.height; y += heightImage) {
                g2d.drawImage(backgroundImage, x, y, null);
            }
        }
        mainPlayerTower.paintMe(g);
    }
}
