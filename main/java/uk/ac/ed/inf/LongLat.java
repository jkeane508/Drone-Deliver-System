package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LongLat {
    public double longitude;
    public double latitude;

    public LongLat(double p1, double p2) {
        latitude = p1;
        longitude = p2;
    }


    boolean isConfined() {
        if ( (this.latitude >= -3.192473) & (this.latitude <= -3.184319) & (this.longitude >= 55.942617) & (this.longitude <= 55.946233) ) {
            return true;
        }
        else {
            return false;
        }
    }

    double distanceTo(LongLat p2){
        double longs = (this.longitude - p2.longitude) * (this.longitude - p2.longitude);
        double lats = (this.latitude - p2.latitude) * (this.latitude - p2.latitude);
        double pythag = Math.sqrt(longs+lats);

        return pythag;
    }

    boolean closeTo (LongLat p2){
        double distance = distanceTo(p2);

        if (distance <= 0.00015){
            return true;
        }
        else{
            return false;
        }
    }

    public static List VectorComponents(double quadrantAngle){
        double quadrantAngleRad = Math.toRadians(quadrantAngle);
        System.out.println(quadrantAngleRad);
        double cosineAngle = Math.cos(quadrantAngleRad);
        double sinAngle = Math.sin(quadrantAngleRad);

        List<Double> values= new ArrayList<Double>();
        values.add(cosineAngle);
        values.add(sinAngle);

        return values;
    }

    public LongLat nextPosition(int angle) {
        double dist = 0.00015;
        if (Arrays.asList(0, 90, 180, 270).contains(angle)) {
            if (angle == 0){
                this.latitude = this.latitude + dist;
            }
            else if (angle == 180){
                this.latitude = this.latitude - dist;
            }
            else if (angle == 90){
                this.longitude = this.longitude + dist;
            }
            else if (angle == 270){
                this.longitude = this.longitude - dist;
            }
        }

        else if (angle < 90 && angle > 0){
            double quadrantAngleDeg = angle;
            List values = VectorComponents(quadrantAngleDeg);
            this.latitude = this.latitude + ( dist * ((double) values.get(0)) );
            this.longitude = this.longitude + (dist * ((double) values.get(1)) );
            }

        else if (angle < 180 && angle > 90){
            double quadrantAngleDeg = angle - 90;
            List values = VectorComponents(quadrantAngleDeg);
            this.longitude = this.longitude + (dist * (double) values.get(0));
            this.latitude = this.latitude - (dist * (double) values.get(1));
            }

        else if (angle < 270 && angle > 180){
            double quadrantAngleDeg = angle - 180;
            List values = VectorComponents(quadrantAngleDeg);
            this.longitude = this.longitude - (dist * (double) values.get(1));
            this.latitude = this.latitude - (dist * (double) values.get(0));
        }

        else if (angle <= 359 && angle > 270){
            double quadrantAngleDeg = angle - 270;
            List values = VectorComponents(quadrantAngleDeg);
            this.longitude = this.longitude - (dist * (double) values.get(0));
            this.latitude = this.latitude + (dist * (double) values.get(1));
        }
        else{
            return this;
        }
        return this;
    }


}
