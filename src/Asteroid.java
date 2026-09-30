import java.awt.*;

public class Asteroid {
    public int x, y;
    public int size;
    public int speed;
    public boolean active = true;

    public Asteroid(int startX, int startY, int size, int speed) {
        this.x = startX;
        this.y = startY;
        // ปรับขนาดขั้นต่ำให้อ่านรายละเอียดชั้นอุกกาบาตได้ชัดเจน
        this.size = Math.max(size, 45);
        this.speed = speed;
    }

    public void update(int screenHeight) {
        y += speed;
        if (y > screenHeight) active = false;
    }

    public void draw(Graphics2D g2) {
        // บันทึกค่าการวาดเดิมไว้
        Object oldAntialias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. ชั้นนอกสุด: ขอบออร่าความร้อนสีแดง[cite: 10]
        g2.setColor(new Color(210, 60, 40));
        g2.fillOval(x, y, size, size);

        // 2. ชั้นกลาง: วงสีส้ม[cite: 10]
        int orangeMargin = (int) (size * 0.06);
        int orangeSize = size - (orangeMargin * 2);
        g2.setColor(new Color(235, 150, 55));
        g2.fillOval(x + orangeMargin, y + orangeMargin, orangeSize, orangeSize);

        // 3. ชั้นขอบเข้มของหินอุกกาบาต[cite: 10]
        int darkOutlineMargin = (int) (size * 0.12);
        int darkOutlineSize = size - (darkOutlineMargin * 2);
        g2.setColor(new Color(15, 35, 48));
        g2.fillOval(x + darkOutlineMargin, y + darkOutlineMargin, darkOutlineSize, darkOutlineSize);

        // 4. ตัวหินอุกกาบาตสีฟ้าอมเทา[cite: 10]
        int coreMargin = (int) (size * 0.16);
        int coreSize = size - (coreMargin * 2);
        g2.setColor(new Color(40, 90, 110));
        g2.fillOval(x + coreMargin, y + coreMargin, coreSize, coreSize);

        // 5. หลุมอุกกาบาต (Craters) สีเข้ม[cite: 10]
        g2.setColor(new Color(15, 30, 42));

        // หลุมที่ 1 (ซ้ายบน)
        g2.fillOval((int)(x + size * 0.32), (int)(y + size * 0.28), (int)(size * 0.20), (int)(size * 0.20));

        // หลุมที่ 2 (ขวาบน - หลุมใหญ่)
        g2.fillOval((int)(x + size * 0.54), (int)(y + size * 0.26), (int)(size * 0.24), (int)(size * 0.24));

        // หลุมที่ 3 (ซ้ายล่าง)
        g2.fillOval((int)(x + size * 0.28), (int)(y + size * 0.52), (int)(size * 0.22), (int)(size * 0.22));

        // หลุมที่ 4 (ขวาล่าง)
        g2.fillOval((int)(x + size * 0.56), (int)(y + size * 0.56), (int)(size * 0.16), (int)(size * 0.16));

        // หลุมเล็กตรงกลาง
        g2.fillOval((int)(x + size * 0.46), (int)(y + size * 0.46), (int)(size * 0.10), (int)(size * 0.10));

        // คืนค่าการวาดเดิม
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntialias);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }
}