import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


public class Tools extends JPanel {
    public ArrayList<JButton> buttons;
    public transient Controller ctrl;
    public transient Model mdl;
    public static final Color[] clr = {Color.black, Color.blue, Color.cyan, Color.darkGray, Color.gray, Color.green,
            Color.lightGray, Color.pink, Color.orange, Color.red, Color.white, Color.yellow};
    public ColorJPanel colorPan;

    class ColorJPanel extends JPanel {
        private JButton[] Colors;

        // 使用表格分布，给每个按钮设置相应的颜色
        public ColorJPanel() {
            this.setLayout(new GridLayout(4, 3));
            Colors = new JButton[12];
            for (int i = 0; i < 12; i++) {
                Colors[i] = new JButton();
                Colors[i].setBackground(clr[i]);
                Colors[i].setOpaque(true);
                Colors[i].setBorderPainted(false);
                int finalI = i;
                Colors[i].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (ctrl.getSelectedElement() != null) {
                            ctrl.getSelectedElement().setColor(clr[finalI]);
                            Boot.myCanvas.repaint();
                        }
                        else
                            ctrl.setColor(clr[finalI]);
                    }
                });
                this.add(Colors[i]);
            }
        }
    }


    Tools(Controller cHandle, Model mHandle) {
        ctrl = cHandle;
        mdl = mHandle;
        colorPan = new ColorJPanel();
        buttons = new ArrayList<>();
        this.setLayout(new GridLayout(7, 1));
        buttons.add(new JButton("Line"));
        buttons.get(0).addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (ctrl.getDrawingShape().equals(listShapes.lineShape))
                    ctrl.setDrawingShape(listShapes.defaultShape);
                else
                    ctrl.setDrawingShape(listShapes.lineShape);
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        this.add(buttons.get(0));

        buttons.add(new JButton("Rectangle"));
        buttons.get(1).addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (ctrl.getDrawingShape().equals(listShapes.rectangleShape))
                    ctrl.setDrawingShape(listShapes.defaultShape);
                else
                    ctrl.setDrawingShape(listShapes.rectangleShape);
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        this.add(buttons.get(1));

        buttons.add(new JButton("Circle"));
        buttons.get(2).addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (ctrl.getDrawingShape().equals(listShapes.circleShape))
                    ctrl.setDrawingShape(listShapes.defaultShape);
                else
                    ctrl.setDrawingShape(listShapes.circleShape);
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        this.add(buttons.get(2));


        buttons.add(new JButton("Text"));
        buttons.get(3).addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (ctrl.getDrawingShape().equals(listShapes.string))
                    ctrl.setDrawingShape(listShapes.defaultShape);
                else
                    ctrl.setDrawingShape(listShapes.string);

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        this.add(buttons.get(3));


        this.add(colorPan);


        buttons.add(new JButton("Save"));
        buttons.get(4).addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

                FileDialog sv = new FileDialog(Boot.window, "Save", FileDialog.SAVE);
                sv.setVisible(true);
                sv.setLocationRelativeTo(null);
                String path = sv.getDirectory();
                String fileName = sv.getFile();
                if (path == null || fileName == null)
                    return;
                try {
                    Boot.ser(path, fileName);
                } catch (IOException exc) {
                    JOptionPane.showMessageDialog(null, "Error", "IO exception!", JOptionPane.ERROR_MESSAGE);
                }
//                Boot.myCanvas.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        this.add(buttons.get(4));


        buttons.add(new JButton("Load"));
        buttons.get(5).addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                FileDialog op = new FileDialog(Boot.window, "Open", FileDialog.LOAD);
                op.setVisible(true);
                op.setLocationRelativeTo(null);
                String path = op.getDirectory();
                String fileName = op.getFile();
                if (path == null || fileName == null)
                    return;
                try {
                    Boot.des(path,fileName);
                } catch (FileNotFoundException exc) {
                    JOptionPane.showMessageDialog(null, "Error", "File not found!", JOptionPane.ERROR_MESSAGE);
                } catch (IOException exc) {
                    JOptionPane.showMessageDialog(null, "Error", "IO exception!", JOptionPane.ERROR_MESSAGE);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        this.add(buttons.get(5));


    }

    public void reset(Controller c,Model m){
        ctrl=c;
        mdl=m;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        mdl.draw((Graphics2D) g);
    }
}
