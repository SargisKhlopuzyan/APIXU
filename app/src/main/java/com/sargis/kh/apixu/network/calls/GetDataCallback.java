package com.sargis.kh.apixu.network.calls;

import okhttp3.ResponseBody;

public interface GetDataCallback<T> {

    void onSuccess(T dataResponse);

    void onError(int errorCode, ResponseBody errorResponse);

    void onFailure(Throwable failure);

}