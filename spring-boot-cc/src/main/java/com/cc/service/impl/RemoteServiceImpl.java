/**
 * 
 */
package com.cc.service.impl;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import com.cc.service.IRemoteService;

/**
 * @author Caleb.Cheng
 *
 */
@Component
public class RemoteServiceImpl implements IRemoteService{

	@Override
	public String call(String url) {
		String responseText = null;
        CloseableHttpClient httpclient = null;
        try {
            try {
                HttpRequestBase request = null;
                StringBuilder requestUri = new StringBuilder(url);
                request = new HttpGet(requestUri.toString());
                BasicCookieStore cookieStore = new BasicCookieStore();
                httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
                RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(3000).setConnectTimeout(3000).build();
                request.setConfig(requestConfig);
                CloseableHttpResponse response = httpclient.execute(request);
                try {
					HttpEntity entity = response.getEntity();
					responseText = (null == entity ?  "" : EntityUtils.toString(entity));
					EntityUtils.consume(entity);
					StatusLine statusLine = response.getStatusLine();
					if(null == statusLine) {
						throw new RuntimeException("http error!");
					} else {
					    if(HttpStatus.SC_OK == statusLine.getStatusCode()) {
					        return responseText;
					    } else {
					    	throw new RuntimeException("http error!");
					    }
					}
                } finally {
                    if(null != response) response.close();
                }

            } finally {
                if(null != httpclient) httpclient.close();
            }
        } catch(Throwable cause) {
            throw new RuntimeException(cause);
        }
	}
	
}
