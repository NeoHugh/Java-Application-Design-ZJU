import javax.swing.*;
import java.awt.*;
import java.io.*;

public class Boot{
    public static Controller control;
    public static Model model;
    public static JFrame window;
    public static Canvas myCanvas;
    public static Tools myTool;
    static {
        control=new Controller();
        model = new Model();
        myCanvas= new Canvas(control,model);
        window = new JFrame("MiniCad - by Neo");
        myTool=new Tools(control,model);
    }
    public static void des(String path, String fileName) throws FileNotFoundException, IOException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(path + File.separator + fileName));

        try {
            model.getList().clear();
            model = (Model) in.readObject();
            control = (Controller) in.readObject();
            myCanvas.reset(control,model);
            control.setDrawingShape(listShapes.defaultShape);
            myTool.reset(control,model);
        } catch (ClassNotFoundException exc) {
            exc.printStackTrace();
        }
        in.close();
        myCanvas.repaint();
    }
    public static void ser(String path, String fileName) throws IOException{
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path + File.separator + fileName));
        out.writeObject(model);
        out.writeObject(control);
        out.close();
    }
    public static void main(String[] args) {
        window.setSize(800, 600);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(new BorderLayout());
        window.add(myTool, BorderLayout.EAST);
        window.add(myCanvas, BorderLayout.CENTER);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        window.setFocusable(true);
    }
}