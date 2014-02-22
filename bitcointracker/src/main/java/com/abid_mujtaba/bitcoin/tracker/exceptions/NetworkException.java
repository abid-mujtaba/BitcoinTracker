package com.abid_mujtaba.bitcoin.tracker.exceptions;


/**
 * Custom exception used to encapsulate all possible exceptions raised while connecting to the
 * backend and retrieving data over the internet.
 */

public class NetworkException extends Exception
{
    public NetworkException(String msg, Throwable t)
    {
        super(msg, t);
    }
}
