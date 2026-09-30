import java.awt.*;

public class ItemDrop {
    public float x, y;
    public int type; // 0 = Ammo, 1 = Repair Kit
    public boolean active = true;
    public int size = 24;

    // ตัวแปรสำหรับทำเอฟเฟกต์ลอยขยับขึ้นลง (Hover Effect)
    private float floatOffset = 0;
    private float floatSpeed = 0.15f;

    public ItemDrop(float x, float y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void update(int screenHeight) {
        y += 2.0f; // เลื่อนลงเรื่อยๆ

        // คำนวณการลอยขึ้นลงเบาๆ ให้ดูมีชีวิตชีวา
        floatOffset += floatSpeed;
        if (floatOffset > 5 || floatOffset < -5) {
            floatSpeed = -floatSpeed;
        }

        if (y > screenHeight) {
            active = false; // หลุดจอให้ลบออก
        }
    }

    public void draw(Graphics2D g2) {
        // บันทึกสถานะเดิมของ Graphics2D
        Composite oldComposite = g2.getComposite();

        int drawY = (int)(y + floatOffset); // จุด Y ที่วาดจริง (รวมการลอย)

        if (type == 0) {
            // --- วาดกระสุน (Ammo) สีเหลืองเรืองแสง ---

            // เอฟเฟกต์เรืองแสง (Glow)
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
            g2.setColor(new Color(255, 215, 0)); // สีเหลืองทอง
            g2.fillOval((int)x - 6, drawY - 6, size + 12, size + 12);

            // กรอบด้านใน
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            g2.setColor(new Color(255, 140, 0)); // สีส้ม
            g2.fillRoundRect((int)x, drawY, size, size, 8, 8);

            // รูปทรงกระสุนตรงกลาง
            g2.setColor(Color.YELLOW);
            int[] px = {(int)x + 8, (int)x + 16, (int)x + 16, (int)x + 8};
            int[] py = {drawY + 4, drawY + 8, drawY + 18, drawY + 18};
            g2.fillPolygon(px, py, 4);

        } else if (type == 1) {
            // --- วาดกล่องพยาบาล (Repair Kit) สีเขียวเรืองแสง ---

            // เอฟเฟกต์เรืองแสง (Glow)
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
            g2.setColor(new Color(0, 255, 0));
            g2.fillOval((int)x - 6, drawY - 6, size + 12, size + 12);

            // กรอบด้านใน
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            g2.setColor(new Color(34, 139, 34)); // สีเขียวเข้ม (Forest Green)
            g2.fillRoundRect((int)x, drawY, size, size, 8, 8);
            g2.setColor(Color.WHITE);
            g2.drawRoundRect((int)x, drawY, size, size, 8, 8);

            // เครื่องหมายบวก (Cross) ตรงกลาง
            g2.setColor(Color.GREEN);
            g2.fillRect((int)x + 10, drawY + 5, 4, 14); // แนวตั้ง
            g2.fillRect((int)x + 5, drawY + 10, 14, 4); // แนวนอน
        }

        // คืนค่า Composite กลับเป็นปกติ
        g2.setComposite(oldComposite);
    }

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, size, size);
    }
}