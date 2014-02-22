package com.abid_mujtaba.bitcoin.tracker.data;

import android.os.Environment;

import com.abid_mujtaba.bitcoin.tracker.exceptions.DataException;

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
}
