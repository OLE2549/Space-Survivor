import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Player {
    public int x, y;
    public int width = 64;
    public int height = 64;
    public int speed = 5;

    public int hp = 100;
    public int ammo = 30;
    public int repairKits = 0;

    public boolean up, down, left, right;

    private BufferedImage shipImage;

    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        loadImages();
    }

    private void loadImages() {
        try {
            // โหลดรูปภาพตรงๆ ตามไฟล์จริง โดยไม่ผ่านการตัดพื้นหลัง
            shipImage = ImageIO.read(new File("res/player_ship.png"));
        } catch (IOException e) {
            System.out.println("ไม่พบไฟล์ res/player_ship.png");
        }
    }

    public void update(int screenWidth, int screenHeight) {
        if (up && y > 0) y -= speed;
        if (down && y < screenHeight - height) y += speed;
        if (left && x > 0) x -= speed;
        if (right && x < screenWidth - width) x += speed;
    }

    public void repair() {
        if (repairKits > 0 && hp < 100) {
            hp += 30;
            if (hp > 100) hp = 100;
            repairKits--;
        }
    }

    public void draw(Graphics2D g2) {
        // 1. วาดไฟไอพ่นด้านท้ายยาน
        g2.setColor(new Color(255, 120, 0, 220));
        g2.fillOval(x + width / 2 - 8, y + height - 5, 16, 12 + (int)(Math.random() * 8));

        // 2. วาดภาพยานอวกาศ (หมุน 180 องศา ให้หันหัวขึ้น)
        if (shipImage != null) {
            AffineTransform oldTransform = g2.getTransform();

            // หมุนภาพ 180 องศา
            g2.rotate(Math.toRadians(180), x + width / 2.0, y + height / 2.0);
            g2.drawImage(shipImage, x, y, width, height, null);

            g2.setTransform(oldTransform);
        } else {
            g2.setColor(Color.WHITE);
            g2.fillRect(x, y, width, height);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x + 8, y + 8, width - 16, height - 16);
    }
}