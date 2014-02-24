package com.abid_mujtaba.bitcoin.tracker.exceptions;

import android.os.Environment;

import com.abid_mujtaba.bitcoin.tracker.Resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Implements logging to local file for exceptions
 */

public class Log
{
    private static final String FOLDER = "bitcoin";
    private static final String FILENAME = "error_log.txt";


    private static File log_file()     // Method for getting File object handle on the local data file
    {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + FOLDER);
        dir.mkdirs();           // Make the directory along with all necessary parent directories if they don't already exist

        return new File(dir, FILENAME);
    }


    public static boolean clear()      // Method for clearing the data by deleting the file
    {
        File file = log_file();

        boolean success = file.delete();

        if (success) { log_file(); }           // We call log_file again to create an empty file once the original file has been deleted.

        return success;
    }


    public static void append( Throwable t ) throws DataException     // Append line to data file.
    {
        // First we convert the stacktrace to string:
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        String log = Resources.time() + " :\n\n" + sw.toString() + "\n";

        File file = log_file();

        try
        {
            FileOutputStream fos = new FileOutputStream(file, true);        // We pass in true so that the text is appended
            PrintWriter pw = new PrintWriter(fos);
            pw.println( log );
            pw.flush();
            pw.close();
            fos.close();
        }
        catch (FileNotFoundException e) { throw new DataException("Error while writing to file.", e); }
        catch (IOException e) { throw new DataException("Error while writing to file.", e); }
    }
}