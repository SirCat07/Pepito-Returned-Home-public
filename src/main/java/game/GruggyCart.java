package game;

public class GruggyCart {
    float currentX;
    float addX = 0;

    public GruggyCart() {
        currentX = (int) (Math.random() * 1080);
    }

    public float getCurrentX() {
        return currentX;
    }

    public float getAddX() {
        return addX;
    }

    public void setCurrentX(float currentX) {
        this.currentX = currentX;
    }

    public void setAddX(float addX) {
        this.addX = addX;
    }
}
