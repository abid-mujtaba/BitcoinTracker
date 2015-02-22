package com.abid_mujtaba.bitcoin.tracker.network.exceptions;

/**
 * General (chained) Exception raised whenever a Client method throws an exception.
 * This is the ONLY exception the UI end is required to handle when performing network I/O using "Client" methods.
 */


public class ClientException extends Exception
{
    public ClientException(String message, Throwable e)
    {
        super(message, e);
    }

    public ClientException(String message)
    {
        super(message);
    }
}
