package com.windy.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zj.plugin.loader.ExecuteDetailVo;
import com.zj.plugin.loader.Feature;
import com.zj.plugin.loader.FeatureDefine;
import com.zj.plugin.loader.ParamValueType;
import com.zj.plugin.loader.ParameterDefine;
import com.zj.plugin.loader.ResponseDetailVo;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TemplateDemo implements Feature {

    private final OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS).build();

    public ExecuteDetailVo getWindyVersion(String host) {
        ExecuteDetailVo executeDetailVo = new ExecuteDetailVo();
        //1 设置请求信息，在模版执行之后可在执行记录中查看
        executeDetailVo.addRequestInfo("Request Host", host);
        executeDetailVo.addRequestInfo("Request Time", System.currentTimeMillis());

        //2 业务逻辑处理，返回json格式
        //2 设置处理之后的响应数据
        ResponseDetailVo responseDetailVo = new ResponseDetailVo();
        responseDetailVo.setResponseStatus(true);

        try {
            Request request = new Request.Builder().url(host + "/v1/devops/system/version").get().build();
            Response response = okHttpClient.newCall(request).execute();
            String responseString = response.body().string();
            if (response.isSuccessful()) {
                responseDetailVo.setResponseStatus(true);
                //如果是正确返回只支持json object格式，其他类型处理可能会有问题
                responseDetailVo.setResponseBody(JSON.parse(responseString));
            }else{
                responseDetailVo.setResponseStatus(false);
                responseDetailVo.setErrorMessage("request windy version error: " + responseString);
            }

        }catch (Exception e){
            responseDetailVo.setResponseStatus(false);
            responseDetailVo.setErrorMessage(e.toString());
        }

        executeDetailVo.setResponseDetailVo(responseDetailVo);
        return executeDetailVo;
    }


    public static void main(String[] args) {
        ExecuteDetailVo executeDetailVo = new TemplateDemo().getWindyVersion("http://xxxxxx:9768");
        System.out.println(JSON.toJSONString(executeDetailVo));
    }
    @Override
    public List<FeatureDefine> scanFeatureDefines() {
        FeatureDefine featureDefine = new FeatureDefine();
        featureDefine.setSource("com.windy.demo.TemplateDemo");
        featureDefine.setMethod("getWindyVersion");
        featureDefine.setName("Get Windy Version");
        featureDefine.setDescription("Get Windy console version");
        ParameterDefine parameterDefine = new ParameterDefine();
        parameterDefine.setParamKey("host");
        parameterDefine.setDescription("Windy server host address");
        parameterDefine.setType(ParamValueType.String.name());
        featureDefine.setParams(Collections.singletonList(parameterDefine));
        return Collections.singletonList(featureDefine);
    }
}
