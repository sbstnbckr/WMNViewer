package com.example.sebastian.WMNViwer.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sebastian on 13.12.16.
 */
public class Node {

    private String hostname;
    private String mac;
    private double lat;
    private double lng;
    private String ipv4;
    private int clients;
    private String timestamp;
    private ArrayList<Connection> connectedTo;
    private HashMap<String, String> optional;

    /**
     * Constructor for creating a node with all the parameters
     *
     * @param hostname    hostname of the node
     * @param mac         mac address of the node
     * @param lat         latitude of the geolocation
     * @param lng         longitude of the geolocation
     * @param ipv4        ipv4 address of the node
     * @param clients     number of connected clients
     * @param connectedTo List of Connections to other nodes
     * @param optional    Hashmap of Key-Value Pairs consisting of optional information about the node
     */
    public Node(String hostname, String mac, double lat, double lng, String ipv4, int clients, ArrayList connectedTo, HashMap optional,String timestamp) {
        this.hostname = hostname;
        this.mac = mac;
        this.lat = lat;
        this.lng = lng;
        this.ipv4 = ipv4;
        this.clients = clients;
        this.timestamp=timestamp;
        this.connectedTo = connectedTo;
        this.optional = optional;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     *
     * @return
     */


    public int getClients() {
        return clients;
    }

    public String getIpv4() {
        return ipv4;
    }

    public String gethostname() {
        return hostname;
    }

    public String getMac() {
        return mac;
    }

    public HashMap<String, String> getOptional() {
        return optional;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public ArrayList<Connection> getConnectedTo() {
        return connectedTo;
    }

    /**
     * overridden toString() method that is called in the TableActivity to display the information about the nodes
     *
     * @return formatted String with all the information
     */
    @Override
    public String toString() {
        String info = "\nhostname: " + this.hostname +
                "\nMac: " + this.getMac() +
                "\nipv4 address: " + this.ipv4 +
                "\nlatitude: " + this.getLat() +
                "\nlongitude " + this.getLng() +
                "\nconnected Clients: " + this.getClients() +
                "\n\nConnected Nodes: ";
        for (int i = 0; i < connectedTo.size(); i++) {
            info = info + "\n  Mac: " + connectedTo.get(i).getMac() +
                    "\n  Mcs: " + connectedTo.get(i).getMcs() +
                    "\n  Tx Rates: " + getConnectedTo().get(i).getTx() +
                    "\n  Rx Rates: " + getConnectedTo().get(i).getRx() +
                    "\n";
        }
        if (optional.size()!=0) {info=info+" \nOptional Information:";

            for (Map.Entry<String, String> entry : optional.entrySet()) {
                info=info+"\n  "+entry.getKey()+" : "+entry.getValue();
            }
        }



        if (optional.size()!=0) {info=info+"\n";}

        return info;
    }
}
