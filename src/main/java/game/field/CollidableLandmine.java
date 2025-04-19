package game.field;

import javafx.scene.shape.Circle;

public class CollidableLandmine {
    public int array; // -1, 0, 1
    public int index;
    public Circle hitbox;
    
    public CollidableLandmine(int array, int index, Circle hitbox) {
        this.array = array;
        this.index = index;
        this.hitbox = hitbox;
    }
}
