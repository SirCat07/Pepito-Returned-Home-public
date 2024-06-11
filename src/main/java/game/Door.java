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

    public Door(Point buttonLocation, PepitoImage closedDoorTexture, Point closedDoorLocation, Polygon hitbox, Point astartaEyesPos) {
        this.hitbox = hitbox;
        this.astartaEyesPos = astartaEyesPos;
        this.buttonLocation = buttonLocation;
        this.closedDoorTexture = closedDoorTexture;
        this.closedDoorLocation = closedDoorLocation;
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

    public Rectangle getButtonHitbox(int offsetX) {
        return new Rectangle(offsetX - 400 + buttonLocation.x, buttonLocation.y, 51, 51);
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
}