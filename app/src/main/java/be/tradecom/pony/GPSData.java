package be.tradecom.pony;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by jonathan on 29/08/2016.
 */
public class GPSData implements Serializable {
    private double mLongitude;
    private double mLatitude;
    private String mClientId;

    public GPSData(String data) {
        String[] dataPieces = data.split(";");

        try {
            NumberFormat formatter = NumberFormat.getInstance(Locale.FRANCE);
            if (dataPieces[1].contains(".")) {
                formatter = NumberFormat.getInstance(Locale.UK);

            }

            Number number = formatter.parse(dataPieces[1]);
            mLongitude = number.doubleValue();

            number = formatter.parse(dataPieces[2]);
            mLatitude = number.doubleValue();
        } catch (Exception e) {

        }
    }

    @Override
    public String toString() {
        return mLongitude + ";" + mLatitude;
    }

    public String getClientId() {
        return mClientId;
    }

    public void setmClientId(String s) {
        mClientId = s;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }
}
