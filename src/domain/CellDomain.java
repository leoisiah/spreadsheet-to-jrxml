package domain;

/**
 * Created with IntelliJ IDEA.
 * User: IPC_Server
 * Date: 8/26/14
 * Time: 3:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class CellDomain {
    private int top;
    private int left;
    private int width;
    private int height = 20;
    private String horizontalAlign = "Center";
    private String verticalAlign = "Middle";
    private String text = "";
    private double borderTop = 0;
    private double borderBottom = 0;
    private double borderLeft = 0;
    private double borderRight = 0;
    private boolean isBold = false;

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getHorizontalAlign() {
        return horizontalAlign;
    }

    public void setHorizontalAlign(String horizontalAlign) {
        this.horizontalAlign = horizontalAlign;
    }

    public String getVerticalAlign() {
        return verticalAlign;
    }

    public void setVerticalAlign(String verticalAlign) {
        this.verticalAlign = verticalAlign;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text.replaceAll("[ ]+", " ");
    }

    public double getBorderTop() {
        return borderTop;
    }

    public void setBorderTop(double borderTop) {
        this.borderTop = borderTop;
    }

    public double getBorderBottom() {
        return borderBottom;
    }

    public void setBorderBottom(double borderBottom) {
        this.borderBottom = borderBottom;
    }

    public double getBorderLeft() {
        return borderLeft;
    }

    public void setBorderLeft(double borderLeft) {
        this.borderLeft = borderLeft;
    }

    public double getBorderRight() {
        return borderRight;
    }

    public void setBorderRight(double borderRight) {
        this.borderRight = borderRight;
    }

    public boolean isBold() {
        return isBold;
    }

    public void setBold(boolean bold) {
        isBold = bold;
    }
}
