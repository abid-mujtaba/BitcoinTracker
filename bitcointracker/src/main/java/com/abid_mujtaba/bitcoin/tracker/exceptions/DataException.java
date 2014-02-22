package com.abid_mujtaba.bitcoin.tracker.exceptions;

import static com.abid_mujtaba.bitcoin.tracker.Resources.Loge;

/**
 * Custom exception to handle errors while reading data from file.
 */

public class DataException extends Exception
{
    private String mMessage;
    private Throwable mThrowable;

    public DataException(String msg, Throwable t)
    {
        super(msg, t);

        mMessage = msg;
        mThrowable = t;
    }

    public void log()       // Method for logging the exception
    {
        Loge(mMessage, mThrowable);
    }
}
