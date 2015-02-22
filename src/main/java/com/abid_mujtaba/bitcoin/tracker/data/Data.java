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

package com.abid_mujtaba.bitcoin.tracker.data;

import android.os.Environment;

import com.abid_mujtaba.bitcoin.tracker.exceptions.DataException;
import com.abid_mujtaba.bitcoin.tracker.network.Client;
import com.abid_mujtaba.bitcoin.tracker.network.exceptions.ClientException;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


/**
 * Class responsible for storing data locally
 */

public class Data
{
    private static final String FOLDER = "bitcoin";
    private static final String FILENAME = "data.txt";

    private static final String FETCH_URL = "https://marzipan.whatbox.ca:3983/bitcoin/api/since/%d/";


    private static File data_file()     // Method for getting File object handle on the local data file
    {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + FOLDER);
        dir.mkdirs();           // Make the directory along with all necessary parent directories if they don't already exist

        return new File(dir, FILENAME);
    }


    public static boolean clear()      // Method for clearing the data by deleting the file
    {
        File file = data_file();

        boolean success = file.delete();

        if (success) { data_file(); }           // We call data_file again to create an empty file once the original file has been deleted.

        return success;
    }


    public static void append(String line) throws DataException     // Append line to data file.
    {
        File file = data_file();

        try
        {
            FileOutputStream fos = new FileOutputStream(file, true);        // We pass in true so that the text is appended
            PrintWriter pw = new PrintWriter(fos);
            pw.println(line);
            pw.flush();
            pw.close();
            fos.close();
        }
        catch (FileNotFoundException e) { throw new DataException("Error while writing to file.", e); }
        catch (IOException e) { throw new DataException("Error while writing to file.", e); }
    }


    public static List<String> read() throws DataException          // Reads data file and returns a list of Strings corresponding to the lines in the file.
    {
        File file = data_file();
        ArrayList<String> lines = new ArrayList<String>();

        try
        {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String line;

            while ((line = br.readLine()) != null)
            {
                lines.add(line);
            }

            return lines;
        }
        catch (FileNotFoundException e) { throw new DataException("File not found.", e); }
        catch (IOException e) { throw new DataException("IO error while reading file.", e); }
    }


    public static JSONObject fetch() throws ClientException
    {
        long now = System.currentTimeMillis() / 1000;
        long threshold = now - (86400 * 4);             // Get unix time for four days ago

        String url = String.format(FETCH_URL, threshold);

        return Client.get_json(url);
    }
}
