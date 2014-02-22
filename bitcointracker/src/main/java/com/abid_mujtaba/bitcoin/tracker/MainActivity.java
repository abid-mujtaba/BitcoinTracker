package com.abid_mujtaba.bitcoin.tracker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.abid_mujtaba.bitcoin.tracker.services.FetchPriceService;
import static com.abid_mujtaba.bitcoin.tracker.Resources.Logd;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;


public class MainActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        graph();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.stop_fetch:

                stop_fetch_service();
        }

        return super.onOptionsItemSelected(item);
    }


    private void graph()
    {
        GraphViewSeries series = new GraphViewSeries(new GraphViewData[] {

                new GraphViewData(1, 2.0d),
                new GraphViewData(2, 1.5d),
                new GraphViewData(3, 2.5d),
                new GraphViewData(4, 1.0d)
        });

        GraphView graphView = new LineGraphView(this, "BitCoin Prices");
        graphView.addSeries(series);

        LinearLayout layout = (LinearLayout) findViewById(R.id.container);
        layout.addView(graphView);
    }


    static public class GraphViewData implements GraphViewDataInterface
    {
        public final double valueX;
        public final double valueY;


        public GraphViewData(double valueX, double valueY)
        {
            super();
            this.valueX = valueX;
            this.valueY = valueY;
        }

        @Override
        public double getX() { return valueX; }

        @Override
        public double getY() { return valueY; }
    }


    private void stop_fetch_service()           // Method used to stop the alarm from scheduling repeated FetchPriceService invocation
    {
        Intent intent = new Intent(this, FetchPriceService.class);                      // Intent to launch service
        PendingIntent alarmIntent = PendingIntent.getService(this, 0, intent, 0);       // PendingIntent required by AlarmManager. This gives the AlarmManager permission to launch this Intent as if it were being launched by this application

        AlarmManager amgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        amgr.cancel(alarmIntent);

        Logd("Stopping FetchPriceService.");
    }
}
