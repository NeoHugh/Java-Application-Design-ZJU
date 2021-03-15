import java.awt.*;
import java.io.Serializable;

public class Controller implements Serializable {
    private boolean drawing;
    private Color selectedColor;
    private Shapes selectedElement;
    private Shapes drawingElement;
    private transient listShapes drawingShape;

    Controller() {
        drawing = false;
        selectedColor = Color.black;
        selectedElement = null;
        drawingElement = null;
        drawingShape = listShapes.defaultShape;
    }

    public void setColor(Color clr) {
        selectedColor = clr;
    }

    public void setDrawingShape(listShapes newShape) {
        drawingShape = newShape;
    }

    public Shapes setDrawingElement(Shapes ele) {
        return drawingElement = ele;
    }

    public void setDrawingFlag(boolean flag) {
        drawing = flag;
    }

    public Shapes getSelectedElement() {
        return selectedElement;
    }

    public boolean isDrawing() {
        return drawing;
    }

    public void emptySelected() {
        if (!(selectedElement == null)) {
            if (selectedElement instanceof Text)
                ((Text) selectedElement).fontMinusReal();
            else {
                selectedElement.fontMinus();
                selectedElement.fontMinus();
                selectedElement.fontMinus();
            }
            selectedElement = null;
        }
    }

    public void setSelected(Shapes ele) {
        emptySelected();
        selectedElement = ele;
        if (ele instanceof Text) {
            ((Text) ele).fontPlusReal();
        } else {
            ele.fontPlus();
            ele.fontPlus();
            ele.fontPlus();
        }
    }

    public listShapes getDrawingShape() {
        return drawingShape;
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public Shapes getDrawingElement() {
        return drawingElement;
    }

    @Override
    public String toString() {
        return "Controller{" +
                "drawing=" + drawing +
                ", selectedColor=" + selectedColor +
                ", selectedElement=" + selectedElement +
                ", drawingElement=" + drawingElement +
                '}';
    }
}

enum listShapes implements Serializable {
    circleShape, rectangleShape, lineShape, string, defaultShape;
}