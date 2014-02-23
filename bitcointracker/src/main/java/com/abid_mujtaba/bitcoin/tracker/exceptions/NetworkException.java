package com.abid_mujtaba.bitcoin.tracker.exceptions;


import static com.abid_mujtaba.bitcoin.tracker.Resources.Loge;

/**
 * Custom exception used to encapsulate all possible exceptions raised while connecting to the
 * backend and retrieving data over the internet.
 */

public class NetworkException extends Exception
{
    private String mMessage;
    private Throwable mThrowable;

    public NetworkException(String msg, Throwable t)
    {
        super(msg, t);

        mMessage = msg;
        mThrowable = t;

        try
        {
            Log.append( t );
        }
        catch (DataException e) { Loge("Error while logging exception.", e); }
    }

    public void log()
    {
        Loge(mMessage, mThrowable);
    }
}
