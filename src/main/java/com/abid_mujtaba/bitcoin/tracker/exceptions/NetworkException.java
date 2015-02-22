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
            Log.append( this );         // We send the exception itself for appending the log.
        }
        catch (DataException e) { Loge("Error while logging exception.", e); }
    }

    public void log()
    {
        Loge(mMessage, mThrowable);
    }
}
