/*
 *  Copyright 2014 Abid Hasan Mujtaba
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.abid_mujtaba.bitcoin.tracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.abid_mujtaba.bitcoin.tracker.data.Data;
import com.abid_mujtaba.bitcoin.tracker.exceptions.NetworkException;
import com.abid_mujtaba.bitcoin.tracker.network.exceptions.ClientException;
import com.abid_mujtaba.bitcoin.tracker.services.FetchPriceService;

import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.abid_mujtaba.bitcoin.tracker.Resources.Loge;


public class MainActivity extends Activity
{
    private LinearLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mLayout = (LinearLayout) findViewById(R.id.container);

        graph(1, 86400);        // The default value is sampling factor of 1 and sampling window of 1 day.
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
            case R.id.sampling_interval:

                change_sampling_interval();
                break;

            case R.id.sampling_window:

                change_sampling_window();
                break;

            case R.id.current_price:

                new FetchCurrentPriceTask().execute();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Source: http://android-graphview.org/
     */
    private void graph(int factor, long window)            // Method for reading the data and drawing the graph from it.
    {
        LineGraphView graphView = new LineGraphView(this, "BitCoin Prices");            // This is the view that is added to the layout to display the graph
        graphView.setScalable(true);                                                // Allows the graph to be both scalable and scrollable
        graphView.setScrollable(true);
        graphView.setDrawDataPoints(true);
        graphView.setDataPointsRadius(5f);
        graphView.setCustomLabelFormatter(labelFormatter);

        new FetchAndGraphDataTask(graphView).execute();
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
    private final String[] intervals = {"5 min", "10 min", "15 min", "30 min", "1 hour", "3 hours", "6 hours", "12 hours", "1 day", "1 week", "1 month"};
    private final int[] factors = {1, 2, 3, 6, 12, 36, 72, 144, 288, 2016, 8640};            // We define the multiplicative factor between the interval of time and the smallest interval defined: 5 min

    private void change_sampling_interval()          // Method called when the user clicks the "Change Interval" button on the ActionBar menu
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Sampling Interval");
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

                graph(factors[index], windows[mChosenWindowIndex]);          // Redraw graph with the new factor
            }
        }
    };


    private int mChosenWindowIndex = 4;         // The default value of the window interval is 1 day.
    private AlertDialog windowDialog;
    private final String[] window_strings = {"6 hours", "12 hours", "1 day", "2 days", "1 week", "1 month", "ALL"};
    private final long[] windows = {6 * 3600, 12 * 3600, 86400, 2 * 86400, 7 * 86400, 30 * 86400, Long.MAX_VALUE};

    private void change_sampling_window()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Sampling window");
        builder.setSingleChoiceItems(window_strings, mChosenWindowIndex, windowListener);

        windowDialog = builder.create();
        windowDialog.show();
    }

    private DialogInterface.OnClickListener windowListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialogInterface, int index)
        {
            windowDialog.dismiss();

            if (index != mChosenWindowIndex)
            {
                mLayout.removeAllViews();

                mChosenWindowIndex = index;

                graph(factors[mChosenIntervalIndex], windows[index]);
            }
        }
    };


    private class FetchCurrentPriceTask extends AsyncTask<Void, Void, Void>
    {
        NetworkException mException;
        private String buy_price, sell_price;

        private ProgressDialog mProgressDialog;


        @Override
        protected void onPreExecute()
        {
            mProgressDialog = new ProgressDialog(MainActivity.this);            // Show ProgressDialog while fetching price from the backend
            mProgressDialog.setMessage("Fetching price ...");
            mProgressDialog.show();
        }


        @Override
        protected Void doInBackground(Void... voids)
        {
            try
            {
                String data = FetchPriceService.get_btc_price();

                String[] components = data.split(" ");
                buy_price = components[1];
                sell_price = components[2];
            }
            catch (NetworkException e) { mException = e; }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid)
        {
            if (mException != null)
            {
                Toast.makeText(MainActivity.this, "Failed to fetch current bitcoin price.", Toast.LENGTH_SHORT).show();
                return;
            }

            mProgressDialog.dismiss();      // Dismiss Progress Dialog before displaying the price using an AlertDialog

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle("Bitcoin Price")
                   .setMessage( String.format("Buy:  $%s\n\nSell:  $%s", buy_price, sell_price) );

            builder.setPositiveButton("OK", null);

            builder.show();
        }
    }


    private class FetchAndGraphDataTask extends AsyncTask<Void, Void, Void>
    {
        private ClientException mException;
        private LineGraphView mGraphView;

        private JSONObject jResponse;

        public FetchAndGraphDataTask(LineGraphView graphView)
        {
            mGraphView = graphView;
        }


        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                jResponse = Data.fetch();
            }
            catch (ClientException e) { mException = e; }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid)
        {
            if (mException != null)
            {
                Loge("Error while fetching JSON data from backend.", mException);
                Toast.makeText(MainActivity.this, "Failed to fetch Data.", Toast.LENGTH_SHORT).show();

                return;
            }

            try
            {
                JSONArray jArray = jResponse.getJSONArray("data");

                int length = jArray.length();
                JSONObject jData;
                long time;
                double buy_price, sell_price;

                List<GraphViewData> data_buy = new ArrayList<GraphViewData>();              // We create lists of GraphViewData for buy and sell data
                List<GraphViewData> data_sell = new ArrayList<GraphViewData>();

                for (int ii = 0; ii < length; ii++)
                {
                    jData = jArray.getJSONObject(ii);

                    time = jData.getLong("t");
                    buy_price = (Double) jData.get("b");
                    sell_price = (Double) jData.get("s");

                    data_buy.add( new GraphViewData(time, buy_price) );
                    data_sell.add( new GraphViewData(time, sell_price) );
                }

                // Calculate the ViewPort starting point and duration to show 6 hours worth of data.
                long last_time = jArray.getJSONObject(length - 1).getLong("t");         // Get last time price was fetched, from the data.
                long start = last_time - (6 * 3600);         // We will limit the view port to the last 6 hours

                mGraphView.setViewPort(start, 6 * 3600);


                GraphViewData[] array_buy = new GraphViewData[data_buy.size()];         // Create GraphViewData arrays to populate and then create GraphViewSeries
                GraphViewData[] array_sell = new GraphViewData[data_sell.size()];

                data_buy.toArray(array_buy);            // Populate the array
                data_sell.toArray(array_sell);

                GraphViewSeries.GraphViewSeriesStyle buy_style = new GraphViewSeries.GraphViewSeriesStyle(Color.rgb(250, 50, 0), 2);            // Style to be used by graph
                GraphViewSeries buy_series = new GraphViewSeries("Buy", buy_style, array_buy);

                GraphViewSeries.GraphViewSeriesStyle sell_style = new GraphViewSeries.GraphViewSeriesStyle(Color.rgb(50, 250, 0), 2);
                GraphViewSeries sell_series = new GraphViewSeries("SEll", sell_style, array_sell);

                mGraphView.addSeries(buy_series);
                mGraphView.addSeries(sell_series);

                mLayout.addView(mGraphView);
            }
            catch (JSONException e)
            {
                Loge("JSON Error occurred while parsing JSON response.", e);
                Toast.makeText(MainActivity.this, "Failed to fetch Data.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}