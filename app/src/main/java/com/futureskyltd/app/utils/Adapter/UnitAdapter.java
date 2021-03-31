package com.futureskyltd.app.utils.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.futureskyltd.app.fantacyseller.R;
import com.futureskyltd.app.utils.District.District;
import com.futureskyltd.app.utils.Unit;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UnitAdapter extends ArrayAdapter<Unit> {
    private ArrayList<Unit> unitArrayList;
    private Context context;
    public UnitAdapter(@NonNull Context context, ArrayList<Unit> unitArrayList) {
        super(context, 0, unitArrayList);
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
        Unit unitName = getItem(position);


        if (unitName != null) {
            productTypeName.setText(unitName.getUnit());
        }

        return convertView;
    }
}
