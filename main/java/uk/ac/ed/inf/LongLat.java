package uk.ac.ed.inf;

import com.mapbox.geojson.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LongLat class, containing the Latitude and Longitude position of the drone, with methods to calculate distance to other areas,
 * the drones next position etc.
 */

public class LongLat {
    public double longitude;
    public double latitude;

    /**
     * @param p1 latitude position of drone
     * @param p2 longitude position of drone
     */
    public LongLat(double p1, double p2) {
        longitude = p1;
        latitude = p2;
    }

    /**
     * @param dummy LongLat Object
     */
    public LongLat(LongLat dummy){
        longitude = dummy.longitude;
        latitude = dummy.latitude;
    }


    /**
     * check if Longlat class is confined withing boundaries set out in specification
     *
     * @return true if within latitude and longitude boundaries, otherwise false
     */
    public boolean isConfined() {
        if ((this.longitude >= -3.192473) & (this.longitude <= -3.184319) & (this.latitude >= 55.942617) & (this.latitude <= 55.946233)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * find distance between this LongLat and another
     *
     * @param comparisonPoint, point used to find distance
     * @return distance from this LongLat to comparisonPoint
     */
    public double distanceTo(LongLat comparisonPoint) {
        double longs = (this.longitude - comparisonPoint.longitude) * (this.longitude - comparisonPoint.longitude);
        double lats = (this.latitude - comparisonPoint.latitude) * (this.latitude - comparisonPoint.latitude);
        double dist = Math.sqrt(longs + lats); //Using pythagoras to calculate point to point distance

        return dist;
    }

    /**
     * used to see if 2 LongLat classes are within in 0.00015 degrees of each other
     *
     * @param comparisonPoint, point used to check if current LongLat is within 0.00015degrees
     * @return true if within threshold, false otherwise
     */
    public boolean closeTo(LongLat comparisonPoint) {
        double distance = distanceTo(comparisonPoint);

        if (distance <= 0.00015) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * used to find cos and sin of angle to be used for finding vector components
     *
     * @param quadrantAngle acute angle within quadrant calculate vector components
     * @return components of quadrant angle, (cosign through the angle, sin opposite)
     */
    private static List VectorComponents(double quadrantAngle) {
        double quadrantAngleRad = Math.toRadians(quadrantAngle);
        double cosineAngle = Math.cos(quadrantAngleRad);
        double sinAngle = Math.sin(quadrantAngleRad);

        List<Double> values = new ArrayList<Double>();
        values.add(cosineAngle);
        values.add(sinAngle);

        return values;
    }

    /**
     * used to find the final position of drone after rotation and one movement, or hover
     *
     * @param angle, angle from east moving anticlockwise, convention noted in coursework document
     * @return final position of LongLat after rotation and movement
     */
    public LongLat nextPosition(int angle) {
        double dist = 0.00015; //constant distance

        //scenarios where the drone moves along one axis
        if (Arrays.asList(0, 90, 180, 270).contains(angle)) {
            if (angle == 0) {
                this.latitude = this.latitude + dist;
                return this;
            } else if (angle == 180) {
                this.latitude = this.latitude - dist;
                return this;
            } else if (angle == 90) {
                this.longitude = this.longitude + dist;
                return this;
            } else if (angle == 270) {
                this.longitude = this.longitude - dist;
                return this;
            }
        }

        //ensures parameter data follows convention 10 degrees
        else if (angle % 10 == 0) {
            //scenario when drone rotates in quadrant 1, uses trig to find vector components of movement
            if (angle < 90 && angle >= 0) {
                double quadrantAngleDeg = angle;
                List values = VectorComponents(quadrantAngleDeg);
                this.latitude = this.latitude + (dist * ((double) values.get(0)));
                this.longitude = this.longitude + (dist * ((double) values.get(1)));
                return this;
            }

            //scenario when drone rotates in quadrant 2
            else if (angle < 180 && angle >= 90) {
                double quadrantAngleDeg = angle - 90;
                List values = VectorComponents(quadrantAngleDeg);
                this.longitude = this.longitude + (dist * (double) values.get(0));
                this.latitude = this.latitude - (dist * (double) values.get(1));
                return this;
            }

            //scenario when drone rotates in quadrant 3
            else if (angle < 270 && angle >= 180) {
                double quadrantAngleDeg = angle - 180;
                List values = VectorComponents(quadrantAngleDeg);
                this.longitude = this.longitude - (dist * (double) values.get(1));
                this.latitude = this.latitude - (dist * (double) values.get(0));
                return this;
            }

            //scenario when drone rotates in quadrant 4
            else if (angle <= 359 && angle >= 270) {
                double quadrantAngleDeg = angle - 270;
                List values = VectorComponents(quadrantAngleDeg);
                this.longitude = this.longitude - (dist * (double) values.get(0));
                this.latitude = this.latitude + (dist * (double) values.get(1));
                return this;
            }
        }

        //angle -999 indicates hover which is a convention outlined in coursework document.
        else if (angle == -999) {
            return this;
        }

        //system exit in instance of invalid parameter data
        System.err.println("Invalid Angle");
        System.exit(1);
        return null;
    }

    /**
     * @param end LongLat of Coordinate to calculate angle between
     * @return integer of angle from this object and param end
     */
    public int angleBetweenRounded(LongLat end){
        double latitude1 = Math.toRadians(this.latitude);
        double latitude2 = Math.toRadians(end.latitude);
        double longDiff= Math.toRadians(end.longitude - this.longitude);
        double y = Math.sin(longDiff)*Math.cos(latitude2);
        double x =Math.cos(latitude1)*Math.sin(latitude2)-Math.sin(latitude1)*Math.cos(latitude2)*Math.cos(longDiff);
        double bearing =  (Math.toDegrees(Math.atan2(y, x))+360)%360;

        /*if (bearing >= 0 && bearing < 90){
            bearing = Math.abs(90 - bearing);
        }
        else if (bearing >= 90 && bearing < 180){
            bearing = Math.abs(bearing - 90);
            bearing = 270 + bearing;
        }
        else if (bearing >= 180 && bearing < 270){
            bearing = 90 -  Math.abs(bearing - 180);
            bearing = 180 + bearing;
        }
        else if (bearing >= 270 && bearing < 360){
            bearing = 90 - Math.abs(bearing - 270);
            bearing = 90 + bearing;
        }
        else{
            System.err.println("Angle calculation error");
            bearing = 0.0;
        }*/


        int roundedBearing = (int) (Math.round(bearing/10.0) * 10);
        if (roundedBearing== 360){
            roundedBearing = 0;
        }

        return roundedBearing;

    }

    /**
     * @param zones Array List of NoFlyZones
     * @return Boolean if this is close to any of the NoFlyZones
     */
    public boolean closeToPolygon(ArrayList<NoFlyZone> zones){
        boolean bool = false;
        for(NoFlyZone zone : zones){
            for(List<Point> xs : zone.zone.coordinates()){
                for (Point x : xs){
                    LongLat comparison = new LongLat(x.longitude(), x.latitude());
                    if (this.closeTo(comparison)){
                        bool = true;
                    }
                }
            }
        }
        return bool;
    }


}

