package com.abid_mujtaba.bitcoin.tracker.data;

import android.os.Environment;

import com.abid_mujtaba.bitcoin.tracker.exceptions.DataException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


/**
 * Class responsible for storing data locally
 */

public class Data
{
    private static final String FOLDER = "bitcoin";
    private static final String FILENAME = "data.txt";


    public static void clear()      // Method for clearing the data by deleting the file
    {}


    public static void append(String line) throws DataException     // Append line to data file.
    {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + FOLDER);
        dir.mkdirs();           // Make the directory along with all necessary parent directories if they don't already exist

        File file = new File(dir, FILENAME);

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
        return null;
    }
}
