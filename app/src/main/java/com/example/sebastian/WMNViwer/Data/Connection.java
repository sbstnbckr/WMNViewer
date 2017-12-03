package com.example.sebastian.WMNViwer.Data;

/**
 * Created by sebastian on 13.12.16.
 */
public class Connection {

    private String mac;
    private int mcs;
    private String tx;
    private String rx;
    private double lat1;
    private double lat2;
    private double lng1;
    private double lng2;

    /**
     * Constructor to create new Connection to a node
     *
     * the Geolocation for drawing the connection as a polyline is set in the getConnection() method in the Class AllNodes
     *
     * @param mac Mac address of the neigbor node
     * @param mcs MCS index of the connection
     * @param tx Transmit rate of the connection
     * @param rx receive rate of the connection
     */
    public Connection(String mac, int mcs, String tx, String rx) {
        this.mac = mac;
        this.mcs = mcs;
        this.tx = tx;
        this.rx = rx;
    }

    public double getLat1() {
        return lat1;
    }

    public void setLat1(double lat1) {
        this.lat1 = lat1;
    }

    public double getLat2() {
        return lat2;
    }

    public void setLat2(double lat2) {
        this.lat2 = lat2;
    }

    public double getLng1() {
        return lng1;
    }

    public void setLng1(double lng1) {
        this.lng1 = lng1;
    }

    public double getLng2() {
        return lng2;
    }

    public void setLng2(double lng2) {
        this.lng2 = lng2;
    }


    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setMcs(int mcs) {
        this.mcs = mcs;
    }

    public void setTx(String tx) {
        this.tx = tx;
    }

    public void setRx(String rx) {
        this.rx = rx;
    }

    public String getMac() {
        return mac;
    }

    public int getMcs() {
        return mcs;
    }

    public String getTx() {
        return tx;
    }

    public String getRx() {
        return rx;
    }
}
