import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class Model implements Serializable {
    private ArrayList<Shapes> list;
    Model(){
        list = new ArrayList<>();
    }
    public ArrayList<Shapes> getList(){return list;}
    public void add(Shapes ele){list.add(ele);}
    public void del(Shapes ele){list.remove(ele);}
    public void draw(Graphics2D g2d){
        for (var ele : list){
            ele.draw(g2d);
        }
    }

    @Override
    public String toString() {
        return "Model{" +
                "list=" + list +
                '}';
    }
}
