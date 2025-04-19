package game;

import utils.PepitoImage;

import java.awt.*;

public class Door {
    Polygon hitbox;
    Point astartaEyesPos;
    Point buttonLocation;

    PepitoImage closedDoorTexture;
    Point closedDoorLocation;

    boolean closed = false;
    short blockade = 0;
    boolean hovering = false;
    
    float percentClosed = 0;
    
    float visualSize = 1F;

    public Door(Point buttonLocation, PepitoImage closedDoorTexture, Point closedDoorLocation, Polygon hitbox, Point astartaEyesPos) {
        this.hitbox = hitbox;
        this.astartaEyesPos = astartaEyesPos;
        this.buttonLocation = buttonLocation;
        this.closedDoorTexture = closedDoorTexture;
        this.closedDoorLocation = closedDoorLocation;
    }
    
    public boolean isLocked() {
        return closed || blockade > 0;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public short getBlockade() {
        return blockade;
    }

    public void addBlockade(int i) {
        this.blockade += (short) i;
    }

    public Polygon getHitbox() {
        return hitbox;
    }

    public boolean isHovering() {
        return hovering;
    }

    public void setHovering(boolean hovering) {
        this.hovering = hovering;
    }

    public Point getButtonLocation() {
        return buttonLocation;
    }

    public Rectangle getButtonHitbox(int offsetX, int maxOffset) {
        return new Rectangle(offsetX - maxOffset + buttonLocation.x, buttonLocation.y, 51, 51);
    }

    public PepitoImage getClosedDoorTexture() {
        return closedDoorTexture;
    }

    public Point getClosedDoorLocation() {
        return closedDoorLocation;
    }

    public Point getAstartaEyesPos() {
        return astartaEyesPos;
    }
    
    public float getPercentClosed() {
        return percentClosed;
    }

    public void setPercentClosed(float percentClosed) {
        this.percentClosed = percentClosed;
    }

    public void setVisualSize(float visualSize) {
        this.visualSize = visualSize;
    }

    public float getVisualSize() {
        return visualSize;
    }
    
}