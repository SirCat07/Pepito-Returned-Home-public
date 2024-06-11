package utils;

public class Notification {
    public float progress = 200;
    public String string;
    byte speed = 1;

    public Notification(String string) {
        this.string = string;
        StaticLists.notifs.add(this);
    }
    public Notification(String string, int speed) {
        this.string = string;
        this.speed = (byte) speed;
        StaticLists.notifs.add(this);
    }

    public void go() {
        progress -= speed;
        if(progress <= 0) {
            StaticLists.notifs.remove(this);
        }
    }
}
