package game.field;

import utils.NoiseGenerator;

public enum FieldBiome {
    STRAIGHT,
    UPS_AND_DOWNS,
    TRAILS,
    MINEFIELD,
    SERPENTINE,
    WILD_MOUNTAINS,
    RAVINE;
//    WALLED;
    
    
    public float getX(NoiseGenerator noise, int i, float seed) {
        switch (this) {
            case TRAILS -> {
                return Math.round(2000 + 4000 * noise.smoothNoise(i / 40F, 100, seed));
            }
            case SERPENTINE -> {
                return (float) (3400 * Math.sin(i / 13F - 2));
            }
            case MINEFIELD -> {
                return Math.round(600 + 1200 * noise.smoothNoise(i / 16F, 100, seed));
            }
            case WILD_MOUNTAINS -> {
                return ((float) (4000 * Math.sin(i / 13F - 2)) + (Math.round(2000 + 4000 * noise.smoothNoise(i / 40F, 100, seed)))) / 2;
            }
            case RAVINE -> {
                return Math.round(350 + 700 * noise.smoothNoise(i / 40F, 100, seed));
            }
        }
        return 0;
    }

    public float getY(NoiseGenerator noise, int i, float seed) {
        switch (this) {
            case UPS_AND_DOWNS -> {
                return Math.round(750 + 1500 * noise.smoothNoise(i / 12F, 0, seed));
            }
            case TRAILS -> {
                return Math.round(2000 + 4000 * noise.smoothNoise(i / 40F, 0, seed));
            }
            case WILD_MOUNTAINS -> {
                return -30000 + Math.round(2000 + 4000 * noise.smoothNoise(i / 40F, 0, seed));
            }
            case RAVINE -> {
                return 5000 + Math.round(400 + 800 * noise.smoothNoise(i / 40F, 0, seed));
            }
        }
        return 0;
    }
    

    
    public float getPathWidth() {
        switch (this) {
            case MINEFIELD -> {
                return -540;
            }
            case RAVINE -> {
//                return (800)*2;
                return 200;
            }
        }
        return 0;
    }
    
    public float getPathY() {
        if(this == RAVINE) {
            return -2280 - 540;
        }
        return 0;
    }

    public float getFarModifier() {
        if(this == RAVINE) {
            return 2;
        }
        return 3;
    }


    public float getRoadWidth() {
        switch (this) {
            case MINEFIELD -> {
                return 800;
            }
//            case WALLED -> {
//                return 1200;
//            }
        }
        return 600;
    }

    public float getLandmineChanceMultiplier() {
        switch (this) {
            case UPS_AND_DOWNS -> {
                return 1.3F;
            }
            case MINEFIELD -> {
                return 8.5F;
            }
            case WILD_MOUNTAINS, RAVINE -> {
                return 0.8F;
            }
//            case WALLED -> {
//                return 0.2F;
//            }
        }
        return 1;
    }
}
