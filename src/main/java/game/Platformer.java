package game;

import main.GamePanel;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class Platformer {
    GamePanel g;

    // Game constants
    public final int TILE_SIZE = 48;
    private final int PLAYER_WIDTH = 20;
    private final int PLAYER_HEIGHT = 40;
    private final float GRAVITY = 0.5f;
    private final float JUMP_FORCE = -12.5f;
    private final float MOVE_SPEED = 4f;

    private final int LETHAL_HITBOX_SIZE = 16; // New 16x16 lethal hitbox
    private final int LETHAL_OFFSET_X = 0;    // Offset 8px right
    private final int LETHAL_OFFSET_Y = 0;    // Offset 8px down

    // Game objects
    private int[][] tiles;
    private float playerX, playerY;
    private float playerVelX, playerVelY;
    private boolean isJumping;
    private int width, height;

    // Movement state
    private boolean movingLeft = false;
    private boolean movingRight = false;

    public Platformer(GamePanel g, int width, int height) {
        this.g = g;
        this.width = width;
        this.height = height;

        playerX = 100;
        playerY = 100;
        playerVelX = 0;
        playerVelY = 0;
        isJumping = false;

        g.everyFixedUpdate.put("platformer", this::update);
        generateTileset();
    }

    List<Integer> screenSwaps = new ArrayList<>(List.of(1080, 1080*2, 1080*3, 1080*4));
    public int screenXOffset = 0;

    private void generateTileset() {
        tiles = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if(Math.random() < 0.25) {
                    tiles[y][x] = 1;
                }
                if(Math.random() < 0.05) {
                    tiles[y][x] = 2;
                }
                if(Math.random() < 0.05) {
                    tiles[y][x] = 3;
                }
            }
        }
        tiles[2][2] = 0; // Clear spawn area
        tiles[2][1] = 2; // add restart brick

        for (int x = 0; x < width; x++) {
            tiles[14][x] = 2;
        }
    }

    public void update() {
        // Apply gravity
        playerVelY += GRAVITY;

        // Update horizontal velocity based on input state
        playerVelX = 0;
        if (movingLeft) playerVelX -= MOVE_SPEED;
        if (movingRight) playerVelX += MOVE_SPEED;

        // Store previous position
        float oldX = playerX;
        float oldY = playerY;

        // Calculate new position
        float newX = playerX + playerVelX;
        float newY = playerY + playerVelY;

        // Handle movement with solid collision
        moveX(oldX, newX);
        moveY(oldY, newY);

        // Check lethal collision (tiles = 2 or 3)
        if (checkLethalCollision(playerX, playerY)) {
            die();
        }

        // Only enforce horizontal bounds
        playerX = Math.max(0, Math.min(playerX, width * TILE_SIZE - PLAYER_WIDTH));
        // No vertical bounds enforcement
        
        try {
            if (playerX > screenSwaps.get(0)) {
                screenXOffset -= 1080;
                screenSwaps.remove(0);

                if (screenSwaps.isEmpty()) {
                    System.out.println("YOU WONNED!");

                    g.music.stop();
                    g.music.play(g.menuSong, 0.15, true);
                    
                    g.backToMainMenu();
                    g.sound.playRate("platWompWomp", 0.1, 0.8F + Math.random() / 10F * 4F);
                    
                    g.everyFixedUpdate.remove("platformer");
                }
            }
        } catch (Exception ignored) { }
    }

    private void moveX(float oldX, float newX) {
        float step = Math.signum(playerVelX) * TILE_SIZE;
        float currentX = oldX;

        while (Math.abs(currentX - newX) > Math.abs(step)) {
            currentX += step;
            if (checkSolidCollision(currentX, playerY)) {
                if (playerVelX > 0) {
                    playerX = ((int)(currentX + PLAYER_WIDTH - 1) / TILE_SIZE) * TILE_SIZE - PLAYER_WIDTH;
                } else {
                    playerX = ((int)currentX / TILE_SIZE + 1) * TILE_SIZE;
                }
                playerVelX = 0;
                return;
            }
        }

        if (!checkSolidCollision(newX, playerY)) {
            playerX = newX;
        } else {
            if (playerVelX > 0) {
                playerX = ((int)(newX + PLAYER_WIDTH - 1) / TILE_SIZE) * TILE_SIZE - PLAYER_WIDTH;
            } else {
                playerX = ((int)newX / TILE_SIZE + 1) * TILE_SIZE;
            }
            playerVelX = 0;
        }
    }

    private void moveY(float oldY, float newY) {
        float step = Math.signum(playerVelY) * TILE_SIZE;
        float currentY = oldY;

        while (Math.abs(currentY - newY) > Math.abs(step)) {
            currentY += step;
            if (checkSolidCollision(playerX, currentY)) {
                if (playerVelY > 0) { // Moving down
                    playerY = ((int)(currentY + PLAYER_HEIGHT - 1) / TILE_SIZE) * TILE_SIZE - PLAYER_HEIGHT;
                    isJumping = false;
                } else { // Moving up
                    playerY = ((int)currentY / TILE_SIZE + 1) * TILE_SIZE;
                }
                playerVelY = 0;
                return;
            }
        }

        if (!checkSolidCollision(playerX, newY)) {
            playerY = newY; // Allow free movement up and down
        } else {
            if (playerVelY > 0) { // Moving down
                playerY = ((int)(newY + PLAYER_HEIGHT - 1) / TILE_SIZE) * TILE_SIZE - PLAYER_HEIGHT;
                isJumping = false;
            } else { // Moving up
                playerY = ((int)newY / TILE_SIZE + 1) * TILE_SIZE;
            }
            playerVelY = 0;
        }
    }

    // Solid collision (tiles = 1)
    private boolean checkSolidCollision(float x, float y) {
        Rectangle playerHitbox = new Rectangle(
                (int)x, (int)y, PLAYER_WIDTH, PLAYER_HEIGHT);

        int leftTile = (int)x / TILE_SIZE;
        int rightTile = (int)(x + PLAYER_WIDTH - 1) / TILE_SIZE;
        int topTile = (int)y / TILE_SIZE;
        int bottomTile = (int)(y + PLAYER_HEIGHT - 1) / TILE_SIZE;

        leftTile = Math.max(0, Math.min(leftTile, width - 1));
        rightTile = Math.max(0, Math.min(rightTile, width - 1));
        topTile = Math.max(0, Math.min(topTile, height - 1));
        bottomTile = Math.max(0, Math.min(bottomTile, height - 1));

        for (int ty = topTile; ty <= bottomTile; ty++) {
            for (int tx = leftTile; tx <= rightTile; tx++) {
                if (tiles[ty][tx] == 1) {
                    Rectangle tileRect = new Rectangle(
                            tx * TILE_SIZE, ty * TILE_SIZE,
                            TILE_SIZE, TILE_SIZE);
                    if (playerHitbox.intersects(tileRect)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Lethal collision (tiles = 2 or 3)
    private boolean checkLethalCollision(float x, float y) {
        // Calculate the center of the lethal hitbox
        float centerX = x + LETHAL_OFFSET_X + LETHAL_HITBOX_SIZE / 2F;
        float centerY = y + LETHAL_OFFSET_Y + LETHAL_HITBOX_SIZE / 2F;

        // If player is above the top of the map, no lethal collision possible
        if (centerY < 0) {
            return false;
        }

        int tileX = (int)centerX / TILE_SIZE;
        int tileY = (int)centerY / TILE_SIZE;

        tileX = Math.max(0, Math.min(tileX, width - 1));
        tileY = Math.max(0, Math.min(tileY, height - 1));

        return tiles[tileY][tileX] == 2 || tiles[tileY][tileX] == 3;
    }

    // Placeholder for your implementation
    public void die() {
        // Add your death logic here
        System.out.println("Player died!"); // Temporary debug output
        g.startPlatformer();
    }

    // Player controls
    public void setMovingLeft(boolean moving) { movingLeft = moving; }
    public void setMovingRight(boolean moving) { movingRight = moving; }
    public void jump() {
        if (!isJumping) {
            playerVelY = JUMP_FORCE;
            isJumping = true;
        }
    }

    // Getters for rendering
    public int[][] getTiles() { return tiles; }
    public float getPlayerX() { return playerX; }
    public float getPlayerY() { return playerY; }
}