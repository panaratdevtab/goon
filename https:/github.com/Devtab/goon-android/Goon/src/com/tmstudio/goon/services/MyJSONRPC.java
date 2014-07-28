package com.tmstudio.goon.services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.alexd.jsonrpc.JSONRPCClient;
import org.alexd.jsonrpc.JSONRPCException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class MyJSONRPC extends JSONRPCClient {
	/*
	 * HttpClient to issue the HTTP/POST request
	 */
	private HttpClient httpClient;
	/*
	 * Service URI
	 */
	private String serviceUri;

	// HTTP 1.0
	private static final ProtocolVersion PROTOCOL_VERSION = new ProtocolVersion(
			"HTTP", 1, 0);

	/**
	 * Construct a JsonRPCClient with the given service uri
	 * 
	 * @param uri
	 *            uri of the service
	 */
	public MyJSONRPC(String uri) {
		httpClient = new DefaultHttpClient();
		serviceUri = uri;
	}

	public static JSONRPCClient create(String uri) {
		return new MyJSONRPC(uri);
	}


	protected JSONObject doRequest(String method, JSONObject params)
			throws JSONRPCException {
		// Copy method arguments in a json array
		/*JSONArray jsonParams = new JSONArray();
		for (int i = 0; i < params.length; i++) {
			jsonParams.put(params[i]);
		}*/

		// Create the json request object
		JSONObject jsonRequest = new JSONObject();
		try {
			// id hard-coded at 1 for now
			jsonRequest.put("id", 1);
			jsonRequest.put("method", method);
			jsonRequest.put("params", params);
			jsonRequest.put("jsonrpc", "2.0");
		} catch (JSONException e1) {
			throw new JSONRPCException("Invalid JSON request", e1);
		}
		return doJSONRequest(jsonRequest);
	}

	protected JSONObject doJSONRequest(JSONObject jsonRequest)
			throws JSONRPCException {
		// Create HTTP/POST request with a JSON entity containing the request
		HttpPost request = new HttpPost(serviceUri);
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params,
				getConnectionTimeout());
		HttpConnectionParams.setSoTimeout(params, getSoTimeout());
		HttpProtocolParams.setVersion(params, PROTOCOL_VERSION);
		request.setParams(params);
		
		Log.d("Goon json request : ", jsonRequest.toString());

		HttpEntity entity;
		try {
			entity = new JSONEntity(jsonRequest);
		} catch (UnsupportedEncodingException e1) {
			throw new JSONRPCException("Unsupported encoding", e1);
		}
		request.setEntity(entity);

		try {
			// Execute the request and try to decode the JSON Response
			long t = System.currentTimeMillis();
			HttpResponse response = httpClient.execute(request);
			t = System.currentTimeMillis() - t;
			Log.d("json-rpc", "Request time :" + t);
			String responseString = EntityUtils.toString(response.getEntity());
			responseString = responseString.trim();
			Log.d("Goon json respond : ", responseString.toString());
			JSONObject jsonResponse = new JSONObject(responseString);
			// Check for remote errors
			if (jsonResponse.has("error")) { 
				Object jsonError = jsonResponse.get("error");
				if (!jsonError.equals(null))
					throw new JSONRPCException(jsonResponse.get("error"));
				return jsonResponse; // JSON-RPC 1.0
			} else {
				return jsonResponse; // JSON-RPC 2.0
			}
		}
		// Underlying errors are wrapped into a JSONRPCException instance
		catch (ClientProtocolException e) {
			throw new JSONRPCException("HTTP error", e);
		} catch (IOException e) {
			throw new JSONRPCException("IO error", e);
		} catch (JSONException e) {
			throw new JSONRPCException("Invalid JSON response", e);
		}
	}

	class JSONEntity extends StringEntity {
		public JSONEntity(JSONObject jsonObject)
				throws UnsupportedEncodingException {
			super(jsonObject.toString());
		}

		@Override
		public Header getContentType() {
			return new BasicHeader(HTTP.CONTENT_TYPE, "application/json");
		}
	}
}
