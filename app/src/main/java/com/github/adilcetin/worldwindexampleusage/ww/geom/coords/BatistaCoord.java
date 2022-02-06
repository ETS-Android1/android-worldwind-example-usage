package com.github.adilcetin.worldwindexampleusage.ww.geom.coords;

public class BatistaCoord {

    public static String yawToBatista(int yaw){
        String batista = null;

        if(yaw > -90 && yaw < 0){
            batista = "N " + (-1 * yaw) + "° " + "W";
        }
        else if(yaw > 0 && yaw < 90){
            batista = "N " + yaw + "° " + "E";
        }
        else if(yaw > 90 && yaw < 180){
            batista = "S " + (180 - yaw) + "° " + "E";
        }
        else if(yaw > -180 && yaw < -90){
            batista = "S " + (180 + yaw) + "° " + "W";
        }

        if(yaw == 0){
            batista = "N 0°";
        }
        else if(yaw == 90){
            batista = "E 0°";
        }
        else if(yaw == 180 || yaw == -180){
            batista = "S 0°";
        }
        else if(yaw == -90){
            batista = "W 0°";
        }
        return batista;
    }
}
