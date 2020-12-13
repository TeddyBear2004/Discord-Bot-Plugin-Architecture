package com.wetterquarz.permissions;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PermissionManager<p> {
    private final Map<Double, p> permissions;
    private final List<Integer> ids;

    public PermissionManager(Map<Double, p> permissions){
        this(permissions, 0d);
    }

    public PermissionManager(Map<Double, p> permissions, double d){
        this.permissions = permissions;
        this.ids = new ArrayList<>();

        if(d == 0)return;
        int maxId = 0;

        double lastMaxPow;
        while((lastMaxPow = Math.pow(2, maxId)) <= d)
            maxId++;

        if(d < lastMaxPow)
            maxId--;

        for(int i = maxId; i > 0; i--){
            double cache = Math.pow(2, i);

            if(maxId <= cache){
                maxId -= cache;
                this.ids.add((int)lb(cache));
            }
        }
    }

    public boolean isTrue(int i){
        return ids.contains(i);
    }

    public double export(){
        return ids.stream().mapToDouble(integer -> Math.pow(2, integer)).sum();
    }

    private static double lb( double x ){
        return Math.log( x ) / Math.log( 2.0 );
    }
}
