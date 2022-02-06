package com.github.adilcetin.worldwindexampleusage.ww.milstd2525;

import com.github.adilcetin.worldwindexampleusage.WorldwindMapManager;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public enum MilStd2525Sybols {

    MILSTD_POI_ENEMY (0,"SHGPU-----*****"),
    MILSTD_POI_VEHICLE (1, "SUGPEV----*****"),
    MILSTD_POI_UNKNOWN (2, "SUGPU-----*****"),
    MILSTD_POI_NEUTRAL(3, "SNZP------*****"),
    MILSTD_POI_FRIEND(4, "SFGP------*****");

    int id;
    String code;

    public static MilStd2525Sybols getById(int id) {
        return Arrays.stream(MilStd2525Sybols.values())
                .filter(obj -> obj.id == id)
                .collect(Collectors.toList()).get(0);
    }

    MilStd2525Sybols(int id, String code) {
        this.id = id;
        this.code = code;
    }


    public static String getRandomly() {
        int id = new Random().nextInt(5);
        return MilStd2525Sybols.getById(id).code;
    }
}
