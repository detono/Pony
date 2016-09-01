package be.tradecom.pony;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jonathan on 30/08/2016.
 */
public class DeviceAdapter extends BaseAdapter {
    private final ArrayList<GPSData> mData;

    public DeviceAdapter(ArrayList<GPSData> map) {
        mData = map;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public GPSData getItem(int pos) {
        return mData.get(pos);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result = convertView;

        if (result == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            result = inflater.inflate(R.layout.adapter_device, parent, false);
        }

        GPSData gps = getItem(position);

        if (gps != null) {
            ((TextView) result.findViewById(R.id.txt1)).setText(gps.getClientId());
            ((TextView) result.findViewById(R.id.txt2)).setText(gps.toString());
        }

        return result;
    }
}
