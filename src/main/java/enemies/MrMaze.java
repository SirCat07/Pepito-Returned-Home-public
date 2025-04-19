package enemies;

import main.GamePanel;
import utils.GameEvent;
import utils.Vector2D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class MrMaze {
    GamePanel g;

    public int waterHeight = 640;
    public float fogOpacity = 0;
    
    public float mazeAnim = 0;

    public MrMaze(GamePanel g) {
        this.g = g;
    }
    
    public void spawn() {
        g.fixedUpdatesAnim = 0;
        
        waterHeight = 640;
        fogOpacity = 0;
        mazeAnim = 0;
        
        distance = 25;

        g.basementSound.stop();
        g.music.stop();
        g.sound.stop();
        
        g.music.play("yourEventualDemise", 0.2, true);

        g.getNight().getPepito().scare();
        g.getNight().getAstarta().leaveEarly();
        g.getNight().getMaki().scare();
        
        g.getNight().setEvent(GameEvent.MR_MAZE);


//        mazeSize = (int) (Math.random() * 100) + 5;
//        if(mazeSize % 2 == 0) {
//            mazeSize++;
//        }
        mazeSize = 81;
        
        maze = new byte[mazeSize][mazeSize];

        int halfBoundsSize = 250;
        cellSize = (halfBoundsSize * 2) / mazeSize;
        
        fillMaze(1, 0, mazeSize - 1, mazeSize - 1);
        generateImage();
        
        playerX = cellSize * 1.5d;
        playerY = cellSize * 1.5d;
        
        

        g.keyHandler.defaultCursor = g.getCursor();
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");
        g.setCursor(blankCursor);
    }
    
    public float distance = 25;
    
    public int untilNextSound = 240;


    public double moveX = 0;
    public double moveY = 0;
    
    Vector2D movementVector = new Vector2D(0, 0);
    
    public double playerX = 1;
    public double playerY = 0;
    
    
    public BufferedImage mazeImage = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
    
    public int cellSize = 1;
    
    public void generateImage() {
        mazeImage = new BufferedImage(cellSize * mazeSize, cellSize * mazeSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = (Graphics2D) mazeImage.getGraphics();
        
        for(int i = 0; i < mazeSize; i++) {
            for(int j = 0; j < mazeSize; j++) {
                byte currentCell = maze[i][j];

                switch (currentCell) {
                    case 0 -> {
                        continue;
                    }
                    case 1 -> graphics2D.setColor(new Color(128, 128, 128));
                    case 2 -> graphics2D.setColor(new Color(0, 255, 0));
                    case 3 -> graphics2D.setColor(new Color(255, 0, 0));
                }

                graphics2D.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
            }
        }
        
        graphics2D.dispose();
    }
    
    
    
    public int mazeSize = 81;
    
    public byte[][] maze = new byte[mazeSize][mazeSize];

    private Random rand = new Random();


    public void fillMaze(int startX, int startY, int endX, int endY) {
        // Initialize maze with walls (1)
        for (int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++) {
                maze[i][j] = 1;
            }
        }

        // Ensure start and end coordinates are valid and on odd numbers
        startX = clamp(startX, 1, mazeSize - 2) | 1;  // Force odd
        startY = clamp(startY, 1, mazeSize - 2) | 1;  // Force odd
        endX = clamp(endX, 1, mazeSize - 2) | 1;      // Force odd
        endY = clamp(endY, 1, mazeSize - 2) | 1;      // Force odd

        // List to store frontier cells
        java.util.ArrayList<int[]> frontier = new java.util.ArrayList<>();

        // Mark start position and add its neighbors to frontier
        maze[startX][startY] = 0;
        addFrontier(startX, startY, frontier);

        // Prim's algorithm
        while (!frontier.isEmpty()) {
            // Pick a random frontier cell
            int index = rand.nextInt(frontier.size());
            int[] cell = frontier.remove(index);
            int x = cell[0];
            int y = cell[1];

            // If this cell is still a wall
            if (maze[x][y] == 1) {
                // Connect to a random visited neighbor
                java.util.ArrayList<int[]> neighbors = getVisitedNeighbors(x, y);
                if (!neighbors.isEmpty()) {
                    int[] neighbor = neighbors.get(rand.nextInt(neighbors.size()));
                    int nx = neighbor[0];
                    int ny = neighbor[1];

                    // Carve passage between cells
                    int midX = (x + nx) / 2;
                    int midY = (y + ny) / 2;
                    maze[x][y] = 0;
                    maze[midX][midY] = 0;
                }
                // Add new frontier cells
                addFrontier(x, y, frontier);
            }
        }

        // Set start and end points
        maze[startX][startY] = 2;
        maze[endX][endY] = 3;
    }

    private void addFrontier(int x, int y, java.util.ArrayList<int[]> frontier) {
        int[][] directions = {{0, 2}, {2, 0}, {0, -2}, {-2, 0}}; // Check 2 steps in each direction
        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            if (isValid(newX, newY) && maze[newX][newY] == 1) {
                frontier.add(new int[]{newX, newY});
            }
        }
    }

    private java.util.ArrayList<int[]> getVisitedNeighbors(int x, int y) {
        java.util.ArrayList<int[]> neighbors = new java.util.ArrayList<>();
        int[][] directions = {{0, 2}, {2, 0}, {0, -2}, {-2, 0}};
        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            if (isValid(newX, newY) && maze[newX][newY] == 0) {
                neighbors.add(new int[]{newX, newY});
            }
        }
        return neighbors;
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < mazeSize && y >= 0 && y < mazeSize;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }


    public boolean checkCollision(double x, double y) {
        // Convert world coordinates to map coordinates
        int mapX = (int) Math.floor(x / cellSize);
        int mapY = (int) Math.floor(y / cellSize);

        // Check if point is outside map boundaries
        if (mapX < 0 || mapX >= maze.length ||
                mapY < 0 || mapY >= maze[0].length) {
            return true; // Treat out-of-bounds as collision
        }

        // Check if the map cell contains a wall (1)
        return maze[mapX][mapY] == 1;
    }

    public boolean checkWin(double x, double y) {
        // Convert world coordinates to map coordinates
        int mapX = (int) Math.floor(x / cellSize);
        int mapY = (int) Math.floor(y / cellSize);

        // Check if point is outside map boundaries
        if (mapX < 0 || mapX >= maze.length ||
                mapY < 0 || mapY >= maze[0].length) {
            return false; // Treat out-of-bounds as collision
        }
        
        return maze[mapX][mapY] == 3;
    }


    public void updatePosition(double inputX, double inputY) {
        movementVector.add(inputX, inputY);

        double speed = movementVector.distance(0, 0);
        if (speed > 1.2d) {
            double scale = 1.2d / speed;
            movementVector.multiply(scale);
        }

        // Calculate map rotation angle (in radians)
        double rotationAngle = g.fixedUpdatesAnim / 300F * 0.01745329;

        // Counteract rotation by rotating movement vector in opposite direction
        double adjustedX = movementVector.x * Math.cos(-rotationAngle) - movementVector.y * Math.sin(-rotationAngle);
        double adjustedY = movementVector.x * Math.sin(-rotationAngle) + movementVector.y * Math.cos(-rotationAngle);

        // Use adjusted vector for stepping
        int steps = 8;
        double stepX = adjustedX / steps;
        double stepY = adjustedY / steps;


        // Move step by step
        for (int i = 0; i < steps; i++) {
            double nextX = playerX + stepX;
            double nextY = playerY + stepY;

            if (!checkCollision(nextX, nextY)) {
                playerX = nextX;
                playerY = nextY;
            } else {
                // Collision: try sliding, reset velocity in blocked direction
                if (!checkCollision(nextX, playerY)) {
                    playerX = nextX;
                    movementVector.y = 0;  // Stop vertical movement
                } else if (!checkCollision(playerX, nextY)) {
                    playerY = nextY;
                    movementVector.x = 0;  // Stop horizontal movement
                } else {
                    movementVector.x = 0;
                    movementVector.y = 0;  // Stop all movement
                    break;
                }
            }
        }
        
        movementVector.multiply(0.99);
    }

    public void lose() {
        leave();
    }
    
    public void win() {
        leave();
    }
    
    public void leave() {
        g.getNight().setEvent(GameEvent.NONE);
        
        g.music.stop();
        
        if(g.getNight().getType().isBasement()) {
            g.restartBasementSong();
        }

        g.setCursor(g.keyHandler.defaultCursor);
    }
}
