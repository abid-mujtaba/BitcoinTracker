package com.abid_mujtaba.bitcoin.tracker;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.abid_mujtaba.bitcoin.tracker.data.Data;
import com.abid_mujtaba.bitcoin.tracker.exceptions.DataException;
import com.abid_mujtaba.bitcoin.tracker.services.FetchPriceService;
import static com.abid_mujtaba.bitcoin.tracker.Resources.Logd;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.util.List;


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

            case R.id.clear_data:

                if ( Data.clear() )         // Returns true if the deletion is successful
                {
                    Toast.makeText(this, "Data cleared.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this, "Failed to clear data.", Toast.LENGTH_SHORT).show();
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void graph()            // Method for reading the data and drawing the graph from it.
    {
        try
        {
            GraphView graphView = new LineGraphView(this, "BitCoin Prices");            // This is the view that is added to the layout to display the graph
            graphView.setScalable(true);                                                // Allows the graph to be both scalable and scrollable
            graphView.setScrollable(true);

            List<String> lines = Data.read();                                   // Read in the lines in the data file.

            int length = lines.size();
            String line;
            String[] components;
            long time;
            float buy_price, sell_price;

            GraphViewData[] data_buy = new GraphViewData[length];               // Array used to populate the graph series
            GraphViewData[] data_sell = new GraphViewData[length];

            for (int ii = 0; ii < length; ii++)
            {
                line = lines.get(ii);
                components = line.split(" ");                           // split line in to components delimited by space

                time = Long.parseLong(components[0]);                   // Read in the data in the correct format (type)
                buy_price = Float.parseFloat(components[1]);
                sell_price = Float.parseFloat(components[2]);

                data_buy[ii] = new GraphViewData(time, buy_price);
                data_sell[ii] = new GraphViewData(time, sell_price);
            }

            GraphViewSeries.GraphViewSeriesStyle buy_style = new GraphViewSeries.GraphViewSeriesStyle(Color.rgb(250, 50, 0), 2);            // Style to be used by graph
            GraphViewSeries buy_series = new GraphViewSeries("Buy", buy_style, data_buy);

            GraphViewSeries.GraphViewSeriesStyle sell_style = new GraphViewSeries.GraphViewSeriesStyle(Color.rgb(50, 250, 0), 2);
            GraphViewSeries sell_series = new GraphViewSeries("SEll", sell_style, data_sell);

            graphView.addSeries(buy_series);
            graphView.addSeries(sell_series);

            LinearLayout layout = (LinearLayout) findViewById(R.id.container);
            layout.addView(graphView);
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
}
