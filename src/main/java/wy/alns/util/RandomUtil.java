package wy.alns.util;

import java.util.Random;

public class RandomUtil {
    private static Random r;

    static {
        r = new Random();
        r.setSeed(-1);
    }

    public static Random getRandom() {
        return r;
    }

}
