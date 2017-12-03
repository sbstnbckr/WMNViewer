package com.example.sebastian.WMNViwer.Data;

import java.util.ArrayList;

/**
 * Created by sebastian on 13.12.16.
 */
public class AllNodes {

    private static AllNodes instance;
    private ArrayList<Node> nodes = new ArrayList<>();



    /**
     * AllNodes Singleton Object creation
     * when no AllNodes-Object exist it creates one otherwise it returns the already existing Object
     *
     * @return AllNodes-Singleton-Object
     */
    public static AllNodes getInstance() {
        if (AllNodes.instance == null) {
            AllNodes.instance = new AllNodes();
        }
        return AllNodes.instance;

    }

    /**
     * Adds a Node to the List of existing Nodes
     *
     * @param n Node that is to be added
     */
    public void addNode(Node n) {
        nodes.add(n);
    }


    /**
     * iterates through the nodes and creates connection objects with the latitude and longitude values for displaying them as lines in the mapview
     */
    public void getConnections() {

        ArrayList<Connection> connectedTo;
        Double lng1;
        Double lat1;
        Double lng2;
        Double lat2;
        String neighborMAC;
        for (int i = 0; i < nodes.size(); i++) { // i:laufvariable für einzelne nodes objekte
            lng1 = nodes.get(i).getLng();
            lat1 = nodes.get(i).getLat();
            connectedTo = nodes.get(i).getConnectedTo();     //
            //System.out.println(nodes.get(i).getLat());
            for (int j = 0; j < connectedTo.size(); j++) {         // j: laufvariable für einzelne Connection objekte
                neighborMAC = connectedTo.get(j).getMac();     //23:...
                for (int k = 0; k < nodes.size(); k++) {
                    if (nodes.get(k).getMac().equalsIgnoreCase(neighborMAC)) {
                        lng2 = nodes.get(k).getLng();
                        lat2 = nodes.get(k).getLat();
                        nodes.get(i).getConnectedTo().get(j).setLat1(lat1);
                        nodes.get(i).getConnectedTo().get(j).setLat2(lat2);
                        nodes.get(i).getConnectedTo().get(j).setLng1(lng1);
                        nodes.get(i).getConnectedTo().get(j).setLng2(lng2);
                    }
                }
            }
        }
    }


    /**
     * method to get a List with all nodes
     *
     * @return a list with all nodes
     */
    public ArrayList<Node> getNodes() {
        return nodes;
    }

}
