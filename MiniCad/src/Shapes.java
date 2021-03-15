import java.awt.*;
import java.io.Serializable;

public abstract class Shapes implements Serializable {
    private Color color;
    private float Font;
    public static final int Infinity = 100000000;

    public Shapes() {
        color = Color.BLACK;
        Font = 2.0f;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color newColor) {
        color = newColor;
    }

    public float getFont() {
        return Font;
    }

    public void setFont(float newFont) {
        Font = newFont;
    }

    public void fontPlus() {
        Font += 1;
    }

    public void fontMinus() {
        Font = Math.max(Font - 1, 1);
    }


    abstract double distance(int mouseX, int mouseY);

    abstract void changePosition(int x, int y);

    abstract void draw(Graphics2D g2d);

    abstract int getX();

    abstract int getY();

    @Override
    public String toString() {
        return "Shapes{" +
                "color=" + color +
                ", Font=" + Font +
                '}';
    }
}

class Circle extends Shapes implements Serializable {
    private int centerX;
    private int centerY;
    private int radius;

    public Circle(int x, int y) {
        super();
        centerX = x;
        centerY = y;
    }


    public void changeShape(int newRadius) {
        radius = newRadius;
    }

    @Override
    void draw(Graphics2D g2d) {
        g2d.setColor(this.getColor());
        g2d.setStroke(new BasicStroke(this.getFont()));
        g2d.drawOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
        g2d.drawLine(centerX, centerY, centerX, centerY);
    }

    @Override
    void changePosition(int dx, int dy) {
        centerY = dy;
        centerX = dx;
    }

    @Override
    double distance(int mouseX, int mouseY) {
        double temp = Math.sqrt(Math.pow(centerX - mouseX, 2) + Math.pow(centerY - mouseY, 2));
        if (temp > radius) return Infinity;
        else return 0;
    }

    @Override
    int getX() {
        return centerX;
    }

    @Override
    int getY() {
        return centerY;
    }

    @Override
    public String toString() {
        return "Circle{" +
                "centerX=" + centerX +
                ", centerY=" + centerY +
                ", radius=" + radius +
                "} " + super.toString();
    }
}

class Rectangle extends Shapes implements Serializable {
    private int topLeftX;
    private int topLeftY;
    private int hei;
    private int wid;

    public Rectangle(int x, int y) {
        super();
        topLeftX = x;
        topLeftY = y;
        hei = wid = 0;
    }

    @Override
    void changePosition(int x, int y) {
        int centerX = topLeftX + wid / 2;
        int centerY = topLeftY + hei / 2;
        topLeftX += x - centerX;
        topLeftY += y - centerY;
    }

    @Override
    void draw(Graphics2D g2d) {
        g2d.setColor(this.getColor());
        g2d.setStroke(new BasicStroke(this.getFont()));
        if (wid >= 0 && hei >= 0)
            g2d.drawRect(topLeftX, topLeftY, wid, hei);
        else {
            int trueX = topLeftX, trueY = topLeftY;
            if (wid < 0)
                trueX = topLeftX + wid;
            if (hei < 0)
                trueY = topLeftY + hei;
            g2d.drawRect(trueX, trueY, Math.abs(wid), Math.abs(hei));
        }
    }

    @Override
    double distance(int mouseX, int mouseY) {

        boolean in;
        if (wid >= 0 && hei >= 0)
            in = (mouseX >= topLeftX && mouseX <= topLeftX + wid) && (mouseY >= topLeftY && mouseY <= topLeftY + hei);
        else {
            int trueX = topLeftX, trueY = topLeftY;
            if (wid < 0)
                trueX = trueX + wid;
            if (hei < 0)
                trueY = trueY + hei;
            in = (mouseX >= trueX && mouseX <= trueX + Math.abs(wid)) && (mouseY >= trueY && mouseY <= trueY + Math.abs(hei));
        }
        if (in) return 0;
        else return Infinity;
    }

    @Override
    int getX() {
        return topLeftX;
    }

    @Override
    int getY() {
        return topLeftY;
    }

    public void changeShape(int newWid, int newHei) {

        wid = newWid;
        hei = newHei;

    }

    @Override
    public String toString() {
        return "Rectangle{" +
                "topLeftX=" + topLeftX +
                ", topLeftY=" + topLeftY +
                ", hei=" + hei +
                ", wid=" + wid +
                "} " + super.toString();
    }
}

class Line extends Shapes implements Serializable {
    private int startX, startY;
    private int endX, endY;

    public Line(int x, int y) {
        super();
        startX = endX = x;
        startY = endY = y;
    }

    public void changeShape(int x, int y) {
        endX = x;
        endY = y;
    }

    @Override
    void draw(Graphics2D g2d) {
        g2d.setColor(this.getColor());
        g2d.setStroke(new BasicStroke(this.getFont()));
        g2d.drawLine(startX, startY, endX, endY);
    }

    @Override
    void changePosition(int x, int y) {
        int centerX = (startX + endX) / 2;
        int centerY = (startY + endY) / 2;
        startX += x - centerX;
        startY += y - centerY;
        endX += x - centerX;
        endY += y - centerY;

    }

    @Override
    double distance(int mouseX, int mouseY) {
        if (startY == endY) {
            return Math.abs(startY - mouseY);
        } else if (startX == endX) {
            return Math.abs(startX - mouseX);
        } else {
            double k = (endY - startY) * 1.0 / (endX - startX);
            double b = endY - k * endX;
            return Math.abs(k * mouseX + b - mouseY) / Math.sqrt(Math.pow(k, 2) + 1);
        }
    }

    @Override
    int getX() {
        return startX;
    }

    @Override
    int getY() {
        return startY;
    }

    @Override
    public String toString() {
        return "Line{" +
                "startX=" + startX +
                ", startY=" + startY +
                ", endX=" + endX +
                ", endY=" + endY +
                "} " + super.toString();
    }
}

class Text extends Shapes implements Serializable {
    private int startX, startY;
    private final String content;
    private Font characterFont;
    private int fontSize;
    private FontMetrics measure;
    private int style;

    public Text(int x, int y, String s) {
        startX = x;
        startY = y;
        content = s;
        fontSize = 20;
        style = java.awt.Font.PLAIN;
        characterFont = new Font("SansSarif", style, fontSize);

    }

    public void fontPlusReal() {
        if (style== java.awt.Font.PLAIN)
            style= java.awt.Font.ITALIC;
    }

    public void fontMinusReal() {

        if (style== java.awt.Font.ITALIC)
            style= java.awt.Font.PLAIN;
    }

    @Override
    public void fontPlus() {
        fontSize += 2;
        characterFont = new Font("SansSarif", style, fontSize);
    }

    @Override
    public void fontMinus() {
        fontSize = Math.max(20, fontSize - 2);
        characterFont = new Font("SansSarif", style, fontSize);
    }

    @Override
    void changePosition(int x, int y) {
        startY = y;
        startX = x;
    }

    @Override
    void draw(Graphics2D g2d) {
        g2d.setColor(this.getColor());
        characterFont=new Font("SansSarif", style, fontSize);
        measure = g2d.getFontMetrics(characterFont);
//        g2d.setStroke(new BasicStroke(this.getFont()));
        g2d.setFont(characterFont);
        g2d.drawString(content, startX, startY);
    }

    @Override
    double distance(int mouseX, int mouseY) {
        if (mouseX >= startX && mouseX <= startX + measure.stringWidth(content) && mouseY <= startY && mouseY >= -measure.getHeight() + startY)
            return 0;
        else return Infinity;
    }

    @Override
    int getX() {
        return startX;
    }

    @Override
    int getY() {
        return startY;
    }

    @Override
    public String toString() {
        return "Text{" +
                "startX=" + startX +
                ", startY=" + startY +
                ", content='" + content + '\'' +
                ", characterFont=" + characterFont +
                ", fontSize=" + fontSize +
                ", measure=" + measure +
                ", style=" + style +
                "} " + super.toString();
    }
}