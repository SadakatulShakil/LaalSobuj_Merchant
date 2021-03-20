package com.futureskyltd.app.utils.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.futureskyltd.app.fantacyseller.R;
import com.futureskyltd.app.utils.District.District;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DistrictAdapter extends ArrayAdapter<District> {
    private ArrayList<District> districtArrayList;
    private Context context;
    public DistrictAdapter(@NonNull Context context, ArrayList<District> districtArrayList) {
        super(context, 0, districtArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }


    @Override
    public boolean isEnabled(int position) {
        return position != -1;
    }

    private View initView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_view, parent, false);

        }

        TextView productTypeName = convertView.findViewById(R.id.spinnerView);
        District district = getItem(position);


        if (district != null) {
            productTypeName.setText(district.getDistrict());
        }

        return convertView;
    }
}
