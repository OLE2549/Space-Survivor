import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable, KeyListener {
    final int screenWidth = 800;
    final int screenHeight = 600;
    int FPS = 60;
    Thread gameThread;

    // --- สถานะของเกม ---
    enum GameState { MENU, HOW_TO_PLAY, PLAYING, GAME_OVER }
    GameState gameState = GameState.MENU;
    int menuOption = 0; // ตัวเลือกในเมนู

    Player player;
    ArrayList<Bullet> bullets = new ArrayList<>();
    ArrayList<Asteroid> asteroids = new ArrayList<>();
    ArrayList<ItemDrop> items = new ArrayList<>();

    // เลเยอร์ละอองดาว
    ArrayList<Star> stars = new ArrayList<>();
    Random random = new Random();

    double scoreDistance = 0;
    boolean gameOver = false;

    private BufferedImage bg1Image;
    private BufferedImage bg2Image;
    private BufferedImage bg3Image;

    class Star {
        float x, y, speed;
        int size, alpha;
        public Star(float x, float y, float speed, int size, int alpha) {
            this.x = x; this.y = y; this.speed = speed; this.size = size; this.alpha = alpha;
        }
    }

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setDoubleBuffered(true);
        this.addKeyListener(this);
        this.setFocusable(true);

        generateCustomBackgrounds();

        for (int i = 0; i < 150; i++) {
            stars.add(new Star(
                    random.nextInt(screenWidth), random.nextInt(screenHeight),
                    1.5f + random.nextFloat() * 3.0f, random.nextInt(3) + 1, 100 + random.nextInt(155)
            ));
        }
    }

    // ฟังก์ชันสำหรับรีเซ็ตเกมเมื่อเริ่มเล่นใหม่
    private void resetGame() {
        player = new Player(screenWidth / 2 - 32, screenHeight - 120);
        bullets.clear();
        asteroids.clear();
        items.clear();
        scoreDistance = 0;
        gameOver = false;
        gameState = GameState.PLAYING;
    }

    private void generateCustomBackgrounds() {
        bg1Image = createStage1Background();
        bg2Image = createStage2Background();
        bg3Image = createStage3Background();
    }

    // --- ด่านที่ 1: ดาราจักรวน (Blue Swirling Galaxy) ---
    private BufferedImage createStage1Background() {
        BufferedImage img = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(6, 10, 28));
        g2.fillRect(0, 0, screenWidth, screenHeight);
        int cx = screenWidth / 2, cy = screenHeight / 2 - 30;
        RadialGradientPaint coreGlow = new RadialGradientPaint(cx, cy, 320,
                new float[]{0.0f, 0.35f, 0.8f, 1.0f},
                new Color[]{ new Color(140, 235, 255, 200), new Color(30, 120, 220, 120), new Color(10, 40, 110, 50), new Color(0, 0, 0, 0) }
        );
        g2.setPaint(coreGlow);
        g2.fillOval(cx - 320, cy - 320, 640, 640);
        g2.dispose();
        return img;
    }

    // --- ด่านที่ 2: เนบิวลาสีม่วง-ชมพู (Purple Nebula) ---
    private BufferedImage createStage2Background() {
        BufferedImage img = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint base = new GradientPaint(0, 0, new Color(22, 6, 38), 0, screenHeight, new Color(12, 3, 22));
        g2.setPaint(base);
        g2.fillRect(0, 0, screenWidth, screenHeight);
        int[][] nebulaClouds = {{550, 480, 360, 255, 90, 210}, {280, 320, 300, 220, 70, 230}, {620, 180, 260, 255, 130, 220}, {180, 520, 320, 200, 60, 240}};
        for (int[] c : nebulaClouds) {
            RadialGradientPaint p = new RadialGradientPaint(c[0], c[1], c[2], new float[]{0.0f, 0.45f, 1.0f},
                    new Color[]{ new Color(255, 235, 255, 170), new Color(c[3], c[4], c[5], 110), new Color(0, 0, 0, 0) }
            );
            g2.setPaint(p);
            g2.fillOval(c[0] - c[2], c[1] - c[2], c[2] * 2, c[2] * 2);
        }
        g2.dispose();
        return img;
    }

    // --- ด่านที่ 3: อวกาศสีดำสนิท & หลุมดำ ---
    private BufferedImage createStage3Background() {
        BufferedImage img = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, screenWidth, screenHeight);
        int cx = screenWidth / 2, cy = screenHeight / 2 - 30;
        RadialGradientPaint diskGlow = new RadialGradientPaint(cx, cy, 280,
                new float[]{0.2f, 0.45f, 0.7f, 1.0f},
                new Color[]{ new Color(255, 255, 255, 255), new Color(255, 140, 0, 200), new Color(100, 10, 40, 80), new Color(0, 0, 0, 0) }
        );
        g2.setPaint(diskGlow);
        g2.fillOval(cx - 280, cy - 280, 560, 560);
        g2.setColor(Color.BLACK);
        g2.fillOval(cx - 120, cy - 120, 240, 240);
        g2.setColor(new Color(255, 230, 180, 200));
        g2.setStroke(new BasicStroke(3.0f));
        g2.drawOval(cx - 120, cy - 120, 240, 240);
        g2.dispose();
        return img;
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update() {
        // ละอองดาวเลื่อนตลอดเวลาทุกหน้าจอ
        for (Star s : stars) {
            s.y += s.speed;
            if (s.y > screenHeight) {
                s.y = 0; s.x = random.nextInt(screenWidth);
            }
        }

        if (gameState != GameState.PLAYING) return; // ถ้าไม่ได้เล่นอยู่ ไม่ต้องอัปเดตตัวเกม

        if (gameOver) {
            gameState = GameState.GAME_OVER;
            menuOption = 0;
            return;
        }

        scoreDistance += 0.1;
        player.update(screenWidth, screenHeight);

        int spawnRate = Math.max(10, 50 - (int)(scoreDistance / 20));
        if (random.nextInt(spawnRate) == 0) {
            int size = 30 + random.nextInt(35);
            int speed = 3 + random.nextInt(4);
            asteroids.add(new Asteroid(random.nextInt(screenWidth - size), -50, size, speed));
        }

        Iterator<Bullet> bIter = bullets.iterator();
        while (bIter.hasNext()) {
            Bullet b = bIter.next();
            b.update();
            if (!b.active) { bIter.remove(); continue; }
            Iterator<Asteroid> aIter = asteroids.iterator();
            while (aIter.hasNext()) {
                Asteroid a = aIter.next();
                if (b.getBounds().intersects(a.getBounds())) {
                    b.active = false; a.active = false;
                    int dropChance = random.nextInt(100);
                    if (dropChance < 15) items.add(new ItemDrop(a.x, a.y, 0));
                    else if (dropChance >= 15 && dropChance < 22) items.add(new ItemDrop(a.x, a.y, 1));
                    aIter.remove(); break;
                }
            }
        }

        Iterator<Asteroid> aIter = asteroids.iterator();
        while (aIter.hasNext()) {
            Asteroid a = aIter.next();
            a.update(screenHeight);
            if (!a.active) { aIter.remove(); continue; }
            if (a.getBounds().intersects(player.getBounds())) {
                player.hp -= 20; aIter.remove();
                if (player.hp <= 0) gameOver = true;
            }
        }

        Iterator<ItemDrop> iIter = items.iterator();
        while (iIter.hasNext()) {
            ItemDrop item = iIter.next();
            item.update(screenHeight);
            if (!item.active) { iIter.remove(); continue; }
            if (item.getBounds().intersects(player.getBounds())) {
                if (item.type == 0) player.ammo += 10;
                else if (item.type == 1) player.repairKits++;
                iIter.remove();
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // วาดฉากหลังพื้นฐาน
        if (gameState == GameState.MENU || gameState == GameState.HOW_TO_PLAY) {
            g2.drawImage(bg1Image, 0, 0, null); // เมนูใช้ฉากแรก
        } else {
            drawSmoothBackgrounds(g2); // ตอนเล่นใช้ระบบเฟดฉาก
        }

        // วาดละอองดาว
        for (Star s : stars) {
            g2.setColor(new Color(255, 255, 255, s.alpha));
            g2.fillOval((int)s.x, (int)s.y, s.size, s.size);
        }

        // แยกการวาดตามสถานะหน้าจอ
        if (gameState == GameState.MENU) {
            drawMenuScreen(g2);
        } else if (gameState == GameState.HOW_TO_PLAY) {
            drawHowToPlayScreen(g2);
        } else if (gameState == GameState.PLAYING) {
            for (ItemDrop item : items) item.draw(g2);
            for (Bullet b : bullets) b.draw(g2);
            for (Asteroid a : asteroids) a.draw(g2);
            player.draw(g2);
            drawInGameUI(g2);
        } else if (gameState == GameState.GAME_OVER) {
            drawGameOverScreen(g2);
        }

        g2.dispose();
    }

    private void drawMenuScreen(Graphics2D g2) {
        g2.setFont(new Font("Arial", Font.BOLD, 60));
        g2.setColor(Color.WHITE);
        drawCenteredString(g2, "SPACE SURVIVOR", screenHeight / 3);

        g2.setFont(new Font("Arial", Font.BOLD, 24));
        String[] options = {"START GAME", "HOW TO PLAY", "QUIT"};
        for (int i = 0; i < options.length; i++) {
            if (i == menuOption) {
                g2.setColor(Color.YELLOW);
                drawCenteredString(g2, "> " + options[i] + " <", screenHeight / 2 + (i * 50));
            } else {
                g2.setColor(Color.LIGHT_GRAY);
                drawCenteredString(g2, options[i], screenHeight / 2 + (i * 50));
            }
        }
    }

    private void drawHowToPlayScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(50, 50, screenWidth - 100, screenHeight - 100);
        g2.setColor(Color.WHITE);
        g2.drawRect(50, 50, screenWidth - 100, screenHeight - 100);

        g2.setFont(new Font("Arial", Font.BOLD, 40));
        g2.setColor(Color.CYAN);
        drawCenteredString(g2, "HOW TO PLAY", 120);

        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        g2.setColor(Color.WHITE);
        int y = 180;
        g2.drawString("- W A S D or Arrows : Move your ship", 100, y);
        g2.drawString("- SPACE : Shoot", 100, y += 40);
        g2.drawString("- R : Use Repair Kit (Heal 30 HP)", 100, y += 40);
        g2.drawString("- Avoid Asteroids to survive!", 100, y += 40);

        g2.setColor(Color.YELLOW);
        g2.drawString("- Yellow Items : Ammo Drops (+10)", 100, y += 40);
        g2.setColor(Color.GREEN);
        g2.drawString("- Green Items : Repair Kits (+1)", 100, y += 40);

        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.setColor(Color.YELLOW);
        drawCenteredString(g2, "Press [ENTER] to Go Back", screenHeight - 100);
    }

    private void drawGameOverScreen(Graphics2D g2) {
        // ฉากหลังเบลอๆ ดำๆ
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        g2.setColor(Color.RED);
        g2.setFont(new Font("Arial", Font.BOLD, 60));
        drawCenteredString(g2, "GAME OVER", screenHeight / 3);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 24));
        drawCenteredString(g2, "Final Distance: " + (int)scoreDistance + " LY", screenHeight / 2 - 20);

        g2.setFont(new Font("Arial", Font.BOLD, 24));
        String[] options = {"RESTART", "MAIN MENU"};
        for (int i = 0; i < options.length; i++) {
            if (i == menuOption) {
                g2.setColor(Color.YELLOW);
                drawCenteredString(g2, "> " + options[i] + " <", screenHeight / 2 + 50 + (i * 50));
            } else {
                g2.setColor(Color.LIGHT_GRAY);
                drawCenteredString(g2, options[i], screenHeight / 2 + 50 + (i * 50));
            }
        }
    }

    private void drawInGameUI(Graphics2D g2) {
        // Panel มุมซ้ายบน (HP, Ammo, Repair)
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(10, 10, 240, 100, 15, 15);
        g2.setColor(new Color(255, 255, 255, 100));
        g2.drawRoundRect(10, 10, 240, 100, 15, 15);

        // หลอด HP แบบสวยงาม
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.setColor(Color.WHITE);
        g2.drawString("HP", 20, 35);

        g2.setColor(Color.DARK_GRAY);
        g2.fillRoundRect(50, 22, 180, 18, 8, 8);
        Color hpColor = player.hp > 50 ? new Color(50, 205, 50) : (player.hp > 25 ? Color.ORANGE : Color.RED);
        g2.setColor(hpColor);
        g2.fillRoundRect(50, 22, (int)(Math.max(0, player.hp) * 1.8), 18, 8, 8);

        // ข้อมูล Ammo & Repair
        g2.setColor(Color.YELLOW);
        g2.drawString("AMMO: " + player.ammo, 20, 65);
        g2.setColor(Color.GREEN);
        g2.drawString("REPAIR [R]: " + player.repairKits, 20, 90);

        // Panel มุมขวาบน (คะแนน/ระยะทาง และชื่อด่าน)
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(screenWidth - 250, 10, 230, 60, 15, 15);
        g2.setColor(new Color(255, 255, 255, 100));
        g2.drawRoundRect(screenWidth - 250, 10, 230, 60, 15, 15);

        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.setColor(Color.CYAN);
        g2.drawString("Distance: " + (int)scoreDistance + " LY", screenWidth - 235, 35);

        String stageName = "Stage 1: Blue Galaxy";
        if (scoreDistance >= 600) stageName = "Stage 3: The Black Hole";
        else if (scoreDistance >= 300) stageName = "Stage 2: Purple Nebula";
        g2.setColor(new Color(255, 105, 180));
        g2.drawString(stageName, screenWidth - 235, 55);

        // ระบบนับถอยหลัง (วาดตรงกลาง)
        String countText = "";
        if (scoreDistance >= 270 && scoreDistance < 300) countText = "ENTERING STAGE 2 IN " + (3 - (int)((scoreDistance - 270) / 10)) + "...";
        else if (scoreDistance >= 570 && scoreDistance < 600) countText = "ENTERING STAGE 3 IN " + (3 - (int)((scoreDistance - 570) / 10)) + "...";

        if (!countText.isEmpty()) {
            g2.setFont(new Font("Arial", Font.BOLD, 36));
            g2.setColor(new Color(255, 255, 255, 220));
            drawCenteredString(g2, countText, screenHeight / 2 - 50);
        }
    }

    private void drawSmoothBackgrounds(Graphics2D g2) {
        g2.drawImage(bg1Image, 0, 0, null);
        if (scoreDistance >= 300) {
            float alpha = Math.min(1.0f, (float)((scoreDistance - 300) / 20.0));
            Composite old = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.drawImage(bg2Image, 0, 0, null);
            g2.setComposite(old);
        }
        if (scoreDistance >= 600) {
            float alpha = Math.min(1.0f, (float)((scoreDistance - 600) / 20.0));
            Composite old = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.drawImage(bg3Image, 0, 0, null);
            g2.setComposite(old);
        }
    }

    // Helper method สำหรับวาดตัวอักษรให้อยู่กึ่งกลางหน้าจอ
    private void drawCenteredString(Graphics2D g2, String text, int y) {
        FontMetrics metrics = g2.getFontMetrics();
        int x = (screenWidth - metrics.stringWidth(text)) / 2;
        g2.drawString(text, x, y);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (gameState == GameState.MENU) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                menuOption--; if (menuOption < 0) menuOption = 2;
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                menuOption++; if (menuOption > 2) menuOption = 0;
            }
            if (code == KeyEvent.VK_ENTER) {
                if (menuOption == 0) resetGame();
                else if (menuOption == 1) gameState = GameState.HOW_TO_PLAY;
                else if (menuOption == 2) System.exit(0);
            }
        }
        else if (gameState == GameState.HOW_TO_PLAY) {
            if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_ESCAPE) {
                gameState = GameState.MENU;
                menuOption = 1;
            }
        }
        else if (gameState == GameState.GAME_OVER) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                menuOption--; if (menuOption < 0) menuOption = 1;
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                menuOption++; if (menuOption > 1) menuOption = 0;
            }
            if (code == KeyEvent.VK_ENTER) {
                if (menuOption == 0) resetGame();
                else if (menuOption == 1) { gameState = GameState.MENU; menuOption = 0; }
            }
        }
        else if (gameState == GameState.PLAYING) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) player.up = true;
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) player.down = true;
            if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) player.left = true;
            if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) player.right = true;

            if (code == KeyEvent.VK_SPACE) {
                if (player.ammo > 0) {
                    bullets.add(new Bullet(player.x + player.width / 2 - 3, player.y));
                    player.ammo--;
                }
            }
            if (code == KeyEvent.VK_R) player.repair();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (gameState == GameState.PLAYING) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) player.up = false;
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) player.down = false;
            if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) player.left = false;
            if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) player.right = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}