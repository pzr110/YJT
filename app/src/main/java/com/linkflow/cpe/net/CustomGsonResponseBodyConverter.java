package com.linkflow.cpe.net;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.linkflow.fitt360sdk.item.BaseBean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by Administrator on 2016/10/11 0011.
 */

public class CustomGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private final Gson mGson;
    private final TypeAdapter<T> adapter;

    public CustomGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        mGson = gson;
        this.adapter = adapter;
    }
    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        BaseBean re = mGson.fromJson(response, BaseBean.class);
        //自定义响应码中非1的情况，一律抛出ApiException异常。将该异常交给onError()去处理了。
        if (re.getCode() !=10000) {
            value.close();
            throw new ApiException(re.getStatus(), re.getMsg());
        }
            MediaType mediaType = value.contentType();
            Charset charset = mediaType != null ? mediaType.charset(UTF_8) : UTF_8;
            ByteArrayInputStream bis = new ByteArrayInputStream(response.getBytes());
            InputStreamReader reader = new InputStreamReader(bis, charset);
            JsonReader jsonReader = mGson.newJsonReader(reader);
            try {
                return adapter.read(jsonReader);
            } finally {
                value.close();
            }
        }
}
