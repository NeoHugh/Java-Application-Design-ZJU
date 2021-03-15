import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Canvas extends JPanel {
    private Controller cHandler;
    private Model mHandler;
    static final double Infinity;
    static final double upperBound;

    static {
        Infinity = 1000000.0;
        upperBound = 10.0;
    }

    Canvas(Controller c, Model m) {
        cHandler = c;
        mHandler = m;
        setFocusable(true);
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                double min = Infinity;
                Shapes selected = null;
                requestFocus();
                for (var ele : mHandler.getList()) {
                    double temp = ele.distance(e.getX(), e.getY());
                    if (temp < min && temp < upperBound) {
                        min = temp;
                        selected = ele;
                    }
                }
                if (min == Infinity) {
//                    cHandler.setDrawingShape(listShapes.defaultShape);
                    cHandler.emptySelected();
                    if (cHandler.getDrawingShape().equals(listShapes.string)) {
                        String con = JOptionPane.showInputDialog("Please enter the text: ");
                        if (con != null) {
                            Shapes handler = new Text(e.getX(), e.getY(), con);
                            cHandler.setDrawingElement(handler);
                            cHandler.setSelected(handler);
                            mHandler.add(handler);
                        }
                    }

                } else {
                    cHandler.emptySelected();
                    cHandler.setSelected(selected);
                }
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (!cHandler.getDrawingShape().equals(listShapes.defaultShape)) {
                    Shapes Handler = null;
                    switch (cHandler.getDrawingShape()) {
                        case circleShape -> Handler = new Circle(e.getX(), e.getY());
                        case lineShape -> Handler = new Line(e.getX(), e.getY());
                        case rectangleShape -> Handler = new Rectangle(e.getX(), e.getY());
                    }
                    if (Handler != null) {
                        cHandler.emptySelected();
                        Handler.setColor(cHandler.getSelectedColor());
                        mHandler.add(Handler);
                        cHandler.setDrawingElement(Handler);
                        cHandler.setDrawingFlag(true);
                    }
                }
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (cHandler.isDrawing()) {
                    cHandler.emptySelected();
//                    mHandler.add(cHandler.getDrawingElement());
                    cHandler.setDrawingElement(null);
                    cHandler.setDrawingFlag(false);
                }
                repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (cHandler.isDrawing()) {
                    Shapes handler = cHandler.getDrawingElement();
                    switch (cHandler.getDrawingShape()) {
                        case lineShape -> ((Line) handler).changeShape(e.getX(), e.getY());
                        case circleShape -> ((Circle) handler).changeShape(Math.toIntExact(Math.round(Math.sqrt(Math.pow(e.getX() - handler.getX(), 2) + Math.pow((e.getY() - handler.getY()), 2)))));
                        case rectangleShape -> ((Rectangle) handler).changeShape(e.getX() - handler.getX(), e.getY() - handler.getY());
                    }

                } else if (cHandler.getSelectedElement() != null) {
                    Shapes temp = cHandler.getSelectedElement();
                    temp.changePosition(e.getX(), e.getY());
                }
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
//                repaint();
            }
        });

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (cHandler.getSelectedElement() != null) {
                    switch (e.getKeyChar()) {
                        case '+', '=' -> cHandler.getSelectedElement().fontPlus();
                        case '-', '_' -> cHandler.getSelectedElement().fontMinus();
                        case 'd', 'D', 127 -> {
                            Shapes temp = cHandler.getSelectedElement();
                            mHandler.del(temp);
                            cHandler.emptySelected();
                        }
                    }
                    repaint();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    public void reset(Controller c, Model m) {
        cHandler = c;
        mHandler = m;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        mHandler.draw((Graphics2D) g);
    }

}
