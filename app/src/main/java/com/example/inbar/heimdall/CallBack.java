package com.example.inbar.heimdall;

/**
 * Created by idan on 15/01/2018.
 */
public abstract class CallBack<TRet,TArg> {
    public abstract TRet call(TArg val);
}
