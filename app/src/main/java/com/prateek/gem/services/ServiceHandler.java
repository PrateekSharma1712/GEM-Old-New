package com.prateek.gem.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;

import com.prateek.gem.AppConstants;

import android.util.Log;

public class ServiceHandler {

	static InputStream is = null;
	static String response = null;

	public ServiceHandler() {

	}

	/*
	 * Making service call
	 * @url - url to make request
	 * @method - http request method
	 * */
	public String makeServiceCall(String url, int method) {
		return this.makeServiceCall(url, method, null);
	}

	/*
	 * Making service call
	 * @url - url to make request
	 * @method - http request method
	 * @params - http request params
	 * */
	public String makeServiceCall(String url, int method,
			List<NameValuePair> params) {
		System.out.print("outside try");
		System.out.println(new Date().getTime());
		try {
			// http client
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpEntity httpEntity = null;
			HttpResponse httpResponse = null;
			
			// Checking http request method type
			if (method == AppConstants.REQUEST_METHOD_POST) {
				HttpPost httpPost = new HttpPost(url);
				// adding post params
				if (params != null) {
					System.out.print("before sending entity");
					System.out.println(new Date().getTime());
					httpPost.setEntity(new UrlEncodedFormEntity(params));
				}



				httpResponse = httpClient.execute(httpPost);

			} else if (method == AppConstants.REQUEST_METHOD_GET) {
				// appending params to url
				if (params != null) {
					String paramString = URLEncodedUtils
							.format(params, "utf-8");
					url += "?" + paramString;
					
				}
				
				HttpGet httpGet = new HttpGet(url);

				httpResponse = httpClient.execute(httpGet);
				

			}
			httpEntity = httpResponse.getEntity();
			System.out.print("before gettign content");
			System.out.println(new Date().getTime());
			is = httpEntity.getContent();
			System.out.print("after sending entity");
			System.out.println(new Date().getTime());
		} catch(NoHttpResponseException e){
			System.out.println("No http response......");
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			
			is.close();
			System.out.print("before converting is to respnse");
			System.out.println(new Date().getTime());
			response = sb.toString();
			System.out.print("after converting is to respnse");
			System.out.println(new Date().getTime());
		} catch (Exception e) {
			Log.e("Buffer Error", "Error: " + e.toString());
		}
		System.out.print("returning converting is to respnse");
		System.out.println(new Date().getTime());
		System.out.println("Final response"+response);
		return response;

	}
	
	
}