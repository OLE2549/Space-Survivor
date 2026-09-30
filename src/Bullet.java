import java.awt.*;

public class Bullet {
    public int x, y;
    public int width = 6, height = 15;
    public int speed = 10;
    public boolean active = true;

    public Bullet(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    public void update() {
        y -= speed;
        if (y < 0) active = false; // กระสุนออกนอกจอ
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.YELLOW);
        g2.fillRoundRect(x, y, width, height, 5, 5);
        // แสงเรืองรอง (Glow effect)
        g2.setColor(new Color(255, 255, 0, 100));
        g2.fillRoundRect(x - 2, y - 2, width + 4, height + 4, 5, 5);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}