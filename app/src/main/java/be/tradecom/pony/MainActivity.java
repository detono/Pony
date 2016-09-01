package be.tradecom.pony;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    private Observable<ArrayList<GPSData>> myObservable;
    private SwipeRefreshLayout swipeLayout;
    private ListView lvwDevices;
    private Subscription mySub;

    @Override
    public void onRefresh() {
        swipeLayout.setRefreshing(true);
        refreshList();
    }

   @Override
   public void onItemClick(AdapterView<?> ad, View v, int pos, long arg3) {
       try {
           GPSData gpsData = (GPSData) ad.getItemAtPosition(pos);
           if (gpsData != null) {
               Intent intent = new Intent(this, MapsActivity.class);
               intent.putExtra("gps_info", gpsData);
               startActivity(intent);
           }
       } catch (Exception e) {

       }
   }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeLayout = (SwipeRefreshLayout) findViewById((R.id.rfsRefresh));
        swipeLayout.setOnRefreshListener(this);

        lvwDevices = (ListView) findViewById(R.id.lvwDevices);
        lvwDevices.setOnItemClickListener(this);

        myObservable = Observable.interval(15, TimeUnit.SECONDS)
                .flatMap(new Func1<Long, Observable<ArrayList<GPSData>>>() {
                    @Override
                    public Observable<ArrayList<GPSData>> call(Long aLong) {
                        return Observable.create(new Observable.OnSubscribe<ArrayList<GPSData>>() {
                            @Override
                            public void call(Subscriber<? super ArrayList<GPSData>> sub) {
                                try {
                                    sub.onNext(getAllData());
                                    sub.onCompleted();
                                } catch (Exception e) {
                                    sub.onError(e);
                                }
                            }

                            private ArrayList<GPSData> getAllData() {
                                try {
                                    if (GPSSocket.INSTANCE.connect("82.143.71.194", 8058)) {
                                        Log.i("Pony", "@GETALL");

                                        GPSSocket.INSTANCE.send("@GETALL");
                                        return process(GPSSocket.INSTANCE.read());
                                    }
                                } catch (Exception e) {
                                    return null;
                                }
                                return null;
                            }

                            private ArrayList<GPSData> process(char[] incomingBuffer) {
                                ArrayList<GPSData> allDevices = new ArrayList<>();

                                if (incomingBuffer != null) {
                                    String[] commands = String.valueOf(incomingBuffer).split("\\|");
                                    for (String cmd : commands) {
                                        cmd = cmd.replace("\0", "");
                                        if (cmd.split(":").length == 2) {
                                            GPSData gps = new GPSData(cmd.split(":")[1]);
                                            gps.setmClientId(cmd.split(":")[0]);

                                            allDevices.add(gps);
                                        }
                                    }
                                }

                                return allDevices;
                            }
                        });
                    }
                });

        refreshList();
        /*myObservable = Observable.create(new Observable.OnSubscribe<ArrayList<GPSData>>() {
            @Override
            public void call(Subscriber<? super ArrayList<GPSData>> sub) {
                try {
                    sub.onNext(getAllData());
                    sub.onCompleted();
                } catch (Exception e) {
                    sub.onError(e);
                }
            }

            private ArrayList<GPSData> getAllData() {
                try {
                    if (GPSSocket.INSTANCE.connect("82.143.71.194", 8058)) {
                        GPSSocket.INSTANCE.send("@GETALL");
                        return process(GPSSocket.INSTANCE.read());
                    }
                } catch (Exception e) {
                    return null;
                }
                return null;
            }

            private ArrayList<GPSData> process(char[] incomingBuffer) {
                ArrayList<GPSData> allDevices = new ArrayList<>();

                if (incomingBuffer != null) {
                    String[] commands = String.valueOf(incomingBuffer).split("\\|");
                    for (String cmd : commands) {
                        cmd = cmd.replace("\0", "");
                        if (cmd.split(":").length == 2) {
                            GPSData gps = new GPSData(cmd.split(":")[1]);
                            gps.setmClientId(cmd.split(":")[0]);

                            allDevices.add(gps);
                        }
                    }
                }

                return allDevices;
            }
        });*/
    }

    @Override
    public void onPause() {
        if (mySub != null && ! mySub.isUnsubscribed()) {
            mySub.unsubscribe();
        }

        super.onPause();
    }

    @Override
    public void onResume() {
        if (mySub == null || mySub.isUnsubscribed()) {
            refreshList();
        }

        super.onResume();
    }

    private void refreshList() {
        mySub = myObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<GPSData>>() {
                    @Override
                    public void call(ArrayList<GPSData> s) {
                        if (s != null) {
                            DeviceAdapter ad = new DeviceAdapter(s);
                            lvwDevices.setAdapter(ad);

                            swipeLayout.setRefreshing(false);
                        }
                    }
                });
    }
}
