package game.cornfield;

import javafx.geometry.Point3D;
import main.GamePanel;
import main.KeyHandler;
import main.SoundMP3;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class CornField3D {
    short width = 540;
    short height = 320;

    
    KeyHandler keyHandler;
    public Player player = new Player();
    
    SoundMP3 sound;

    public CornField3D(KeyHandler keyHandler, SoundMP3 sound) {
        this.keyHandler = keyHandler;
        this.sound = sound;
        
        BTextures.initialize(this);

        int lastWallsEnd = 0;

        int s;
        int v1 = 0;
        int v2 = 0;
        for(s = 0; s < sectorsAmount; s++) {
            sectors[s] = new Sector();
            sectors[s].wallsStart = loadSectors[v1];
            sectors[s].wallsEnd = loadSectors[v1 + 1];
            sectors[s].z1 = loadSectors[v1 + 2];
            sectors[s].z2 = loadSectors[v1 + 3];
            lastWallsEnd = sectors[s].wallsEnd;

            sectors[s].z2 -= sectors[s].z1;
            v1 += 4;

            for(int w = sectors[s].wallsStart; w < sectors[s].wallsEnd; w++) {
                walls[w] = new Wall();
                walls[w].x1 = loadWalls[v2];
                walls[w].y1 = loadWalls[v2 + 1];
                walls[w].x2 = loadWalls[v2 + 2];
                walls[w].y2 = loadWalls[v2 + 3];
                walls[w].color = loadWalls[v2 + 4];
                v2 += 5;
            }
        }

        for(int i = 0; i < 25; i++) {
            sectors[s] = new Sector();
            sectors[s].wallsStart = lastWallsEnd + 1;
            sectors[s].wallsEnd = lastWallsEnd + 5;
            sectors[s].z1 = 0;
            sectors[s].z2 = 40;
            lastWallsEnd = sectors[s].wallsEnd;

            int w = sectors[s].wallsStart;
            walls[w] = new Wall();
            walls[w].x1 = 0;
            walls[w].y1 = 32 * i - 32 - 256;
            walls[w].x2 = 32;
            walls[w].y2 = 32 * i - 32 - 256;

            walls[w + 1] = new Wall();
            walls[w + 1].x1 = 32;
            walls[w + 1].y1 = 32 * i - 32 - 256;
            walls[w + 1].x2 = 32;
            walls[w + 1].y2 = 32 * i - 256;

            walls[w + 2] = new Wall();
            walls[w + 2].x1 = 32;
            walls[w + 2].y1 = 32 * i - 256;
            walls[w + 2].x2 = 0;
            walls[w + 2].y2 = 32 * i - 256;

            walls[w + 3] = new Wall();
            walls[w + 3].x1 = 0;
            walls[w + 3].y1 = 32 * i - 256;
            walls[w + 3].x2 = 0;
            walls[w + 3].y2 = 32 * i - 32 - 256;

            s++;
        }
        sectorsAmount += 25;


        for(int i = 25; i < 50; i++) {
            int h = i - 25;
            sectors[s] = new Sector();
            sectors[s].wallsStart = lastWallsEnd + 1;
            sectors[s].wallsEnd = lastWallsEnd + 5;
            sectors[s].z1 = 0;
            sectors[s].z2 = 40;
            lastWallsEnd = sectors[s].wallsEnd;

            sectors[s].z2 -= sectors[s].z1;

            int w = sectors[s].wallsStart;
            walls[w] = new Wall();
            walls[w].x1 = 64;
            walls[w].y1 = 32 * h - 32 - 256;
            walls[w].x2 = 96;
            walls[w].y2 = 32 * h - 32 - 256;

            walls[w + 1] = new Wall();
            walls[w + 1].x1 = 96;
            walls[w + 1].y1 = 32 * h - 32 - 256;
            walls[w + 1].x2 = 96;
            walls[w + 1].y2 = 32 * h - 256;

            walls[w + 2] = new Wall();
            walls[w + 2].x1 = 96;
            walls[w + 2].y1 = 32 * h - 256;
            walls[w + 2].x2 = 64;
            walls[w + 2].y2 = 32 * h - 256;

            walls[w + 3] = new Wall();
            walls[w + 3].x1 = 64;
            walls[w + 3].y1 = 32 * h - 256;
            walls[w + 3].x2 = 64;
            walls[w + 3].y2 = 32 * h - 32 - 256;

            s++;
        }
        sectorsAmount += 25;
    }
    
    String wallsId = "corn";
    

    public void update(float delta) {
        float dx = (float) (Math.sin(Math.toRadians(player.yaw)) * 0.025F * delta);
        float dy = (float) (Math.cos(Math.toRadians(player.yaw)) * 0.025F * delta);

        float[] curPlayerPos = new float[] {player.x, player.y};
        movePlayer(curPlayerPos, dx, dy);

        int uses = 0;
        while(checkForCollision(curPlayerPos)) {
            movePlayer(curPlayerPos, -dx, -dy);
            uses++;
            if(uses > 100) {
                return;
            }
        }

        player.x = curPlayerPos[0];
        player.y = curPlayerPos[1];
        
        if(!isRed) {
            if (player.y > 0) {
                isRed = true;
                
                shadows = new Color(180, 0, 0);
                BTextures.initialize(this);
                sound.play("imHere", 0.2);
                sound.play("explode", 0.2);
                
                sound.play("cornfieldAmbient2", 0.12, true);
            }
        } else if(player.y > 400) {
            if (yetToPlayCornSiren) {
                wallsId = "scaryDoor";
                sound.play("cornSiren", 0.1);
                
                yetToPlayCornSiren = false;
            }
        }
    }
    
    boolean yetToPlayCornSiren = true;

    public void movePlayer(float[] curPlayerPos, float dx, float dy) {
        if(keyHandler.holdingW) {
            curPlayerPos[0] += dx;
            curPlayerPos[1] += dy;
        }
        if(keyHandler.holdingS) {
            curPlayerPos[0] -= dx;
            curPlayerPos[1] -= dy;
        }
        if(keyHandler.holdingD) {
            curPlayerPos[0] += dy;
            curPlayerPos[1] -= dx;
        }
        if(keyHandler.holdingA) {
            curPlayerPos[0] -= dy;
            curPlayerPos[1] += dx;
        }
    }

    public boolean checkForCollision(float[] curPlayerPos) {
        for(int s = 0; s < sectorsAmount; s++) {
            Sector sector = sectors[s];
            if (sector == null) continue;

            Polygon bounds = new Polygon();

            for (int w = sector.wallsStart; w < sector.wallsEnd; w++) {
                Wall wall = walls[w];
                if (wall == null) continue;

                bounds.addPoint(wall.x1, wall.y1);
            }

            if(bounds.contains(new Point((int) curPlayerPos[0], (int) curPlayerPos[1]))) {
                if(player.z > sector.z1 && player.z < sector.z2) {
                    return true;
                }
            }
        }
        return false;
    }
    
    boolean isRed = false;
    public boolean displayHydro = false;
    
    
    public void paint(Graphics2D graphics2D) {
        if(!displayHydro) {
            BufferedImage small = new BufferedImage(540, 320, BufferedImage.TYPE_INT_RGB);
            Graphics2D smallGraphics = (Graphics2D) small.getGraphics();

            smallGraphics.setColor(shadows);
            smallGraphics.fillRect(0, 0, 540, 320);

            draw3D(smallGraphics);

            smallGraphics.dispose();
            graphics2D.drawImage(small, 0, 0, 1080, 640, null);
            
        } else {
            graphics2D.drawImage(GamePanel.pishPishCompressed.request(), 487, 240, null);
        }
        
        
        graphics2D.setColor(Color.WHITE);
        
        if(isRed) {
            graphics2D.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
            graphics2D.drawString(GamePanel.getString("imHere"), 540 - halfTextLength(graphics2D, GamePanel.getString("imHere")), 500);
        }
    }

    public int halfTextLength(Graphics2D graphics2D, String string) {
        return (int) (graphics2D.getFontMetrics().stringWidth(string) * 0.5);
    }

    public void drawWall(Graphics2D graphics2D, int x1, int x2, int b1, int b2, int t1, int t2, int distance) {
        // b - bottom; t - top
        int deltaYBottom = b2 - b1;
        int deltaYTop = t2 - t1;
        int deltaX = x2 - x1;
        if(deltaX == 0) deltaX = 1;

        int xStart = x1;

        if(x1 < 0) x1 = 0;
        if(x2 < 0) x2 = 0;
        if(x1 > width - 1) x1 = width - 1;
        if(x2 > width - 1) x2 = width - 1;

        int xRelative = x1 - xStart;

        int interp = (int) (Math.min(255, distance * 2) / 255F * 8F);
        interp = Math.min(7, Math.max(0, interp));

        for(int x = x1; x < x2; x++) {
            int y1 = (int) Math.round(deltaYBottom * (x - xStart + 0.5) / deltaX + b1);
            int y2 = (int) Math.round(deltaYTop * (x - xStart + 0.5) / deltaX + t1);
            
            if(y2 < 0) y2 = 0;
            if(y2 > height - 1) y2 = height - 1;
            
            try {
                graphics2D.drawImage(BTextures.wallImagesArray.get(wallsId)[interp][(int) Math.floor((float) BTextures.wallImages.get(wallsId).getWidth() / (x2 - xStart) * xRelative)], x * 2, y1 * 2, 2, (y2 - y1) * 2, null);
            } catch (ArrayIndexOutOfBoundsException ignored) { }
            
            xRelative++;
        }
    }


    int sectorsAmount = 2;

    public void draw3D(Graphics2D graphics2D) {
        int[] wx = new int[4];
        int[] wy = new int[4];
        int[] wz = new int[4];

        float cos = (float) Math.cos(Math.toRadians(player.yaw));
        float sin = (float) Math.sin(Math.toRadians(player.yaw));

        for(int s = 0; s < sectorsAmount - 1; s++) {
            for(int w = 0; w < sectorsAmount - s - 1; w++) {
                if(sectors[w] == null) continue;
                if(sectors[w].distance < sectors[w + 1].distance) {
                    Sector temp = sectors[w];
                    sectors[w] = sectors[w + 1];
                    sectors[w + 1] = temp;
                }
            }
        }

        for(int s = 0; s < sectorsAmount; s++) {
            Sector sector = sectors[s];
            if(sector == null) continue;
            sector.distance = 0;
            List<Point> points = new ArrayList<>();
            
            for (int w = sector.wallsStart; w < sector.wallsEnd; w++) {
                Wall wall = walls[w];
                if (wall == null) continue;

                int x1 = Math.round(wall.x1 - player.x);
                int y1 = Math.round(wall.y1 - player.y);
                int x2 = Math.round(wall.x2 - player.x);
                int y2 = Math.round(wall.y2 - player.y);

                int z1 = x1;
                int z2 = y1;
                x1 = x2;
                y1 = y2;
                x2 = z1;
                y2 = z2;

                //x
                wx[0] = Math.round(x1 * cos - y1 * sin);
                wx[1] = Math.round(x2 * cos - y2 * sin);
                wx[2] = wx[0];
                wx[3] = wx[1];
                //y
                wy[0] = Math.round(y1 * cos + x1 * sin);
                wy[1] = Math.round(y2 * cos + x2 * sin);
                wy[2] = wy[0];
                wy[3] = wy[1];


                sector.distance += (int) Math.round(distance(new Point(0, 0), new Point((wx[0] + wx[1]) / 2, (wy[0] + wy[1]) / 2)));

                //z
                wz[0] = (int) Math.round(sector.z1 - player.z + ((player.pitch * wy[0]) / 32.0));
                wz[1] = (int) Math.round(sector.z1 - player.z + ((player.pitch * wy[1]) / 32.0));
                wz[2] = wz[0] + sector.z2;
                wz[3] = wz[1] + sector.z2;

                if (wy[0] < 1 && wy[1] < 1) {
                    continue;
                }
                if (wy[0] < 1) {
                    int[] array = clipBehindPlayer(wx[0], wy[0], wz[0], wx[1], wy[1], wz[1]);
                    wx[0] = array[0];
                    wy[0] = array[1];
                    wz[0] = array[2];

                    int[] array2 = clipBehindPlayer(wx[2], wy[2], wz[2], wx[3], wy[3], wz[3]);
                    wx[2] = array2[0];
                    wy[2] = array2[1];
                    wz[2] = array2[2];
                }
                if (wy[1] < 1) {
                    int[] array = clipBehindPlayer(wx[1], wy[1], wz[1], wx[0], wy[0], wz[0]);
                    wx[1] = array[0];
                    wy[1] = array[1];
                    wz[1] = array[2];

                    int[] array2 = clipBehindPlayer(wx[3], wy[3], wz[3], wx[2], wy[2], wz[2]);
                    wx[3] = array2[0];
                    wy[3] = array2[1];
                    wz[3] = array2[2];
                }

                int SW2 = width / 4;
                int SH2 = height / 8;

                wx[0] = wx[0] * fov / wy[0] + SW2;
                wx[1] = wx[1] * fov / wy[1] + SW2;
                wx[2] = wx[2] * fov / wy[2] + SW2;
                wx[3] = wx[3] * fov / wy[3] + SW2;

                wy[0] = wz[0] * fov / wy[0] + SH2;
                wy[1] = wz[1] * fov / wy[1] + SH2;
                wy[2] = wz[2] * fov / wy[2] + SH2;
                wy[3] = wz[3] * fov / wy[3] + SH2;

                graphics2D.setColor(new Color((15 + shadows.getRed()) / 3, (9 + shadows.getGreen()) / 3, (6 + shadows.getBlue()) / 3));


                if (player.z - sector.z1 < 0) {
                    points.add(new Point(wx[1] * 2, wy[1] * 2));
                    points.add(new Point(wx[0] * 2, wy[0] * 2));
                }
                if (player.z - sector.z1 < 0) {
                    Polygon polygon = GamePanel.getPolygon(points);

                    graphics2D.fillPolygon(polygon);
                }

                if (sector.z1 != 40) {
                    drawWall(graphics2D, wx[0], wx[1], wy[0], wy[1], wy[2], wy[3], sector.distance / 4);
                }
            }

            sector.distance /= (sector.wallsEnd - sector.wallsStart);
        }

    }
    Color shadows = new Color(0, 0, 0);

    int[] loadSectors = new int[] {
            // ws, we, z1, z2
            0, 4, 40, 80,
            4, 8, 0, 40
    };

    int[] loadWalls = new int[] {
            // x1, y1, x2, y2, color
            0, -320, 100, -320, 8,
            100, -320, 100, 520, 9,
            100, 520, 0, 520, 8,
            0, 520, 0, -320, 9,

            32, -288, 64, -288, 1,
            64, -288, 64, -256, 1,
            64, -256, 32, -256, 1,
            32, -256, 32, -288, 1
    };

    Wall[] walls = new Wall[280];
    Sector[] sectors = new Sector[60];

    double distance(Point first, Point second) {
        return first.distance(second);
    }
    double distance(Point3D first, Point3D second) {
        return first.distance(second);
    }

    int[] clipBehindPlayer(int x1, int y1, int z1, int x2, int y2, int z2) {
        float distance = (float) y1 - (float) y2;
        if(distance == 0) distance = 1;
        float s = (float) y1 / (y1 - y2);
        x1 = (int) (x1 + s * (x2 - x1));
        y1 = (int) (y1 + s * (y2 - y1));
        if(y1 == 0) y1 = 1;
        z1 = (int) (z1 + s * (z2 - z1));

        return new int[] {x1, y1, z1};
    }
    
    int fov = 180;
}
