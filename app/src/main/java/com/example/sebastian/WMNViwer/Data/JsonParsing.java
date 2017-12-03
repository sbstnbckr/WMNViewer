package com.example.sebastian.WMNViwer.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by sebastian on 13.12.16.
 */
public class JsonParsing {

    AllNodes savedNodes = AllNodes.getInstance();

    /**
     * method iterates through the json string, getting all the data about the nodes and creates the node objects with the according data
     */
    public void getNodes(JSONObject data) {
        String hostname;
        String mac;
        double lat;
        double lng;
        int mcs;
        String tx;
        String rx;
        String ipv4;
        String timestamp;
        int clients;
        String connectedStationMac;

        //System.out.println("getnodesmethod");

            savedNodes.getNodes().clear();
            JSONArray nodearray;
            try {
                nodearray = data.getJSONArray("nodes");   //arrayinhalt mit info über einzelne nodes als objekte
            } catch (JSONException e) {
                e.printStackTrace();

                nodearray=new JSONArray();
            }

            for (int j = 0; j < nodearray.length(); j++) {
                ArrayList<Connection> stations = new ArrayList<>();
                HashMap<String, String> optional = new HashMap();
                JSONObject node;
                JSONObject basicInfo;
                JSONArray stationsarray;

                try {
                     node = nodearray.getJSONObject(j);       //einzelne node als object
                } catch (JSONException e) {
                     e.printStackTrace();
                     node=new JSONObject();
                }

                try {
                    basicInfo = node.getJSONObject("basic"); //inhalt des "basic" teils als object
                } catch (JSONException e) {
                    e.printStackTrace();

                    basicInfo = new JSONObject();
                }

                try {
                    hostname = basicInfo.getString("hostname");         //einzelnen key value paare des basic objects abfragen
                } catch (JSONException e) {
                    e.printStackTrace();

                    hostname = "";
                }

                try {
                    mac = basicInfo.getString("mac");
                } catch (JSONException e) {
                    e.printStackTrace();

                    mac = "";
                }

                try {
                    lat = basicInfo.getDouble("pos_lat");
                } catch (JSONException e ){
                    e.printStackTrace();

                    lat=52;
                }

                try {
                    lng = basicInfo.getDouble("pos_lng");
                } catch (JSONException e) {
                    e.printStackTrace();

                    lng =6;
                }

                System.out.println("hostname: "+hostname+"mac: " + mac+" lat "+lat+" lng "+lng);

                try {
                    ipv4 = basicInfo.getString("ipv4");
                } catch (JSONException e){
                    e.printStackTrace();

                    ipv4 = "no Ipv4 available";
                }

                try {
                    timestamp=basicInfo.getString("timestamp");

                } catch (JSONException e){
                    e.printStackTrace();
                    timestamp="no timestamp available";
                }

                try {
                    clients = basicInfo.getInt("clients");
                } catch (JSONException e) {
                    e.printStackTrace();

                    clients =-1;
                }

                try {
                    stationsarray = basicInfo.getJSONArray("stations");   //arrayinfalt mit info über einzelne connections
                } catch (JSONException e){
                    e.printStackTrace();

                    stationsarray = new JSONArray();
                }

                for (int i = 0; i < stationsarray.length(); i++) {

                    JSONObject stationInfo;
                    JSONObject connectedStationInfo;


                    try {
                        stationInfo = stationsarray.getJSONObject(i);      //einzelne connection als object
                    } catch (JSONException e) {
                        e.printStackTrace();

                        stationInfo = new JSONObject();
                    }

                    Iterator<String> stationIterator = stationInfo.keys();
                    connectedStationMac = stationIterator.next();
                    //System.out.println("connection from: "+mac+" to "+connectedStationMac);

                    try {
                        connectedStationInfo = stationInfo.getJSONObject(connectedStationMac);
                    } catch (JSONException e) {
                        e.printStackTrace();

                        connectedStationInfo = new JSONObject();
                    }

                    try {
                        if (connectedStationInfo.getInt("mcs") != -1) {
                            mcs = connectedStationInfo.getInt("mcs");
                        } else {
                            mcs = 6;
                        }
                    } catch (JSONException e ){
                        e.printStackTrace();

                        mcs=-1;
                    }

                    try {
                        if (connectedStationInfo.getString("tx")!=null){
                            tx = connectedStationInfo.getString("tx");
                        } else {tx = "no tx Value";}
                    } catch (JSONException e) {
                        e.printStackTrace();

                        tx= "no tx Value";
                    }


                    try {
                        if (connectedStationInfo.getString("rx") !=null) {
                            rx = connectedStationInfo.getString("rx");
                        } else {
                            rx = "no rx Value";
                        }
                    } catch (JSONException  e){
                        e.printStackTrace();

                        rx="no rx Value";
                    }





                    stations.add(new Connection(connectedStationMac, mcs, tx, rx));
                }

                try {
                    JSONArray optionalInfo = node.getJSONArray("optional");
                    for (int i = 0; i < optionalInfo.length(); i++) {
                        JSONObject info = optionalInfo.getJSONObject(i);
                        Iterator<String> optionalKey = info.keys();
                        String keyoptional = optionalKey.next();
                        String value = info.getString(keyoptional);
                        //System.out.println(keyoptional + " keyvalue " + value);
                        optional.put(keyoptional, value);
                    }
                }catch (JSONException e) {
                    e.printStackTrace();

                    optional = null;
                }


                savedNodes.addNode(new Node(hostname, mac, lat, lng, ipv4, clients, stations, optional,timestamp));
            }
        savedNodes.getConnections();


    }

}
