// src/mgr/TrafficSegment.java
package mgr;

import java.util.List;

public class TrafficSegment {
    public final String street;
    public final String id;
    public final double length;
    public final int functionalClass;
    public final double speed;
    public final double freeFlow;
    public final double jam;
    public final double confidence;
    public final List<Punkt> points;   // <-- współrzędne

    public TrafficSegment(String street, String id, double length, int functionalClass,
                          double speed, double freeFlow, double jam, double confidence,
                          List<Punkt> points) {
        this.street = street;
        this.id = id;
        this.length = length;
        this.functionalClass = functionalClass;
        this.speed = speed;
        this.freeFlow = freeFlow;
        this.jam = jam;
        this.confidence = confidence;
        this.points = points;
    }
}
