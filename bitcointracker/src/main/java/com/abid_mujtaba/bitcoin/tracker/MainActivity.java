package com.abid_mujtaba.bitcoin.tracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.abid_mujtaba.bitcoin.tracker.data.Data;
import com.abid_mujtaba.bitcoin.tracker.exceptions.DataException;
import com.abid_mujtaba.bitcoin.tracker.services.FetchPriceService;

import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MainActivity extends Activity
{
    private LinearLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mLayout = (LinearLayout) findViewById(R.id.container);

        graph(1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.start_fetch:

                FetchPriceService.start(this);
                Toast.makeText(this, "FetchPriceService started.", Toast.LENGTH_SHORT).show();
                break;

            case R.id.stop_fetch:

                FetchPriceService.stop(this);
                Toast.makeText(this, "FetchPriceService stopped.", Toast.LENGTH_SHORT).show();
                break;

            case R.id.change_interval:

                change_interval();
                break;

            case R.id.clear_data:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Clear Data")
                       .setMessage("Are you sure?");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {         // If OK is pressed we clear data.

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        if ( Data.clear() )         // Returns true if the deletion is successful
                        {
                            Toast.makeText(MainActivity.this, "Data cleared.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Failed to clear data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {     // Do nothing if Cancel is pressed
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                break;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Source: http://android-graphview.org/
     */
    private void graph(int factor)            // Method for reading the data and drawing the graph from it.
    {
        try
        {
            LineGraphView graphView = new LineGraphView(this, "BitCoin Prices");            // This is the view that is added to the layout to display the graph
            graphView.setScalable(true);                                                // Allows the graph to be both scalable and scrollable
            graphView.setScrollable(true);
            graphView.setDrawDataPoints(true);
            graphView.setDataPointsRadius(5f);
            graphView.setCustomLabelFormatter(labelFormatter);

            List<String> lines = Data.read();                                   // Read in the lines in the data file.

            int length = lines.size();
            String line;
            String[] components;
            long time;
            float buy_price, sell_price;

            int reduced_length = length / factor;               // reduced length must be ceil( length / factor )
            if (length % factor != 0) { reduced_length++; }

            GraphViewData[] data_buy = new GraphViewData[ reduced_length ];               // Array used to populate the graph series
            GraphViewData[] data_sell = new GraphViewData[ reduced_length ];

            for (int ii = 0, jj = 0; ii < length; ii += factor, jj++)         // We use factor to jump through the lines. Increasing factor means we introduce gaps in the data-points.
            {
                line = lines.get(ii);
                components = line.split(" ");                           // split line in to components delimited by space

                time = Long.parseLong(components[0]);                   // Read in the data in the correct format (type)
                buy_price = Float.parseFloat(components[1]);
                sell_price = Float.parseFloat(components[2]);

                data_buy[jj] = new GraphViewData(time, buy_price);
                data_sell[jj] = new GraphViewData(time, sell_price);
            }

            GraphViewSeries.GraphViewSeriesStyle buy_style = new GraphViewSeries.GraphViewSeriesStyle(Color.rgb(250, 50, 0), 2);            // Style to be used by graph
            GraphViewSeries buy_series = new GraphViewSeries("Buy", buy_style, data_buy);

            GraphViewSeries.GraphViewSeriesStyle sell_style = new GraphViewSeries.GraphViewSeriesStyle(Color.rgb(50, 250, 0), 2);
            GraphViewSeries sell_series = new GraphViewSeries("SEll", sell_style, data_sell);

            graphView.addSeries(buy_series);
            graphView.addSeries(sell_series);

            mLayout.addView(graphView);
        }
        catch (DataException e) { e.log(); }
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


    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM-dd kk:mm");

    private CustomLabelFormatter labelFormatter = new CustomLabelFormatter() {

        @Override
        public String formatLabel(double value, boolean isValueX)
        {
            if (isValueX)
            {
                long time = (long) value * 1000;

                return dateFormatter.format( new Date(time) );
            }
            return null;
        }
    };


    private int mChosenIntervalIndex = 0;             // The currently chosen interval
    private AlertDialog intervalDialog;
    private final String[] intervals = {"5 min", "10 min", "15 min", "30 min", "1 hr", "3 hr", "6 hr", "12 hr", "24 hr", "1 week", "1 month"};
    private final int[] factors = {1, 2, 3, 6, 12, 36, 72, 144, 288, 2016, 8640};            // We define the multiplicative factor between the interval of time and the smallest interval defined: 5 min

    private void change_interval()          // Method called when the user clicks the "Change Interval" button on the ActionBar menu
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Interval");
        builder.setSingleChoiceItems(intervals, mChosenIntervalIndex, intervalListener);     // We set items in the dialog as well as the item to be shown chosen

        intervalDialog = builder.create();
        intervalDialog.show();
    }

    private DialogInterface.OnClickListener intervalListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialogInterface, int index)
        {
            intervalDialog.dismiss();

            if (index != mChosenIntervalIndex)
            {
                mLayout.removeAllViews();               // Remove GraphView from LinearLayout

                mChosenIntervalIndex = index;
                int factor = factors[index];

                graph(factor);          // Redraw graph with the new factor
            }
        }
    };
}