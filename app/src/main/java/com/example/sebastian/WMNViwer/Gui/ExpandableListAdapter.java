package com.example.sebastian.WMNViwer.Gui;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.sebastian.mapbox.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sebastian on 26.01.17.
 */



public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader;
    private HashMap<String, String> _listDataChild;

    private List<String> originalListHeader;
    private HashMap<String, String> originalListChild;



    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, String> listChildData) {
        this._context = context;
        this._listDataHeader = new ArrayList<String>();
        this._listDataHeader.addAll(listDataHeader);
        this._listDataChild = new HashMap<>();
        this._listDataChild.putAll(listChildData);

        this.originalListHeader = new ArrayList<String>();
        this.originalListHeader.addAll(listDataHeader);
        this.originalListChild = new HashMap<>();
        this.originalListChild.putAll(listChildData);

    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition));
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        //return this._listDataChild.get(this._listDataHeader.get(groupPosition)).length();
       // this._listDataChild.get(this._listDataHeader.get(groupPosition))
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * method  filters the data in the listview. Only elements that contain the query are shown in the list.
     * @param query
     */
    public void filterData(String query) {

        query = query.toLowerCase();

        _listDataChild.clear();
        _listDataHeader.clear();


        if (query.isEmpty()) {
            _listDataHeader.addAll(originalListHeader);
            _listDataChild.putAll(originalListChild);
        } else {

            for (Map.Entry<String, String> childEntry : originalListChild.entrySet()) {
                System.out.println(childEntry.getKey() + " :: " + childEntry.getValue());


                if (childEntry.getValue().toLowerCase().contains(query)) {

                    String value = childEntry.getValue();

                    _listDataHeader.add(childEntry.getKey());
                    _listDataChild.put(childEntry.getKey(), value);
                }
            }
            notifyDataSetChanged();
        }
    }

}
