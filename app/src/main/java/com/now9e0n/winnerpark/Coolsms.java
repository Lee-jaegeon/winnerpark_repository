package com.now9e0n.winnerpark;

import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Properties;

/*
 * Coolsms Class
 * RestApi JAVA 
 * v1.1 
 * POST?GET REQUEST
*/
public class Coolsms extends Https {

	final String URL = "https://api.coolsms.co.kr";
	private String smsUrl = URL + "/sms/1.5/";
	private String senderIdUrl = URL + "/senderid/1.1/";
	private String apiKey;
	private String apiSecret;
	private Https https = new Https();
	Properties properties = System.getProperties();

	// Set apiKey, apiSecret
	public Coolsms(String api_key, String api_secret) {
		this.apiKey = api_key;
		this.apiSecret = api_secret;
	}
	
	/*
	 * Send messages
	 * @param set : HashMap<String, String>
	*/
	public JSONObject send(HashMap<String, String> params) {
		JSONObject response = new JSONObject();
		try {
			// 기본정보 입력
			setBasicInfo(params);
			params.put("os_platform", properties.getProperty("os_name"));
			params.put("dev_lang", "JAVA " + properties.getProperty("java.version"));
			params.put("sdk_version", "JAVA SDK 1.1");

			// Send message 
			response = https.postRequest(smsUrl + "send", params);
		} catch (Exception e) {
			response.put("status", false);
			response.put("message", e.toString());
		}
		return response;
	}
	
	/*
	 * Sent messages
	 * @param set : HashMap<String, String>
	*/
	public JSONObject sent(HashMap<String, String> params) {
		JSONObject response = new JSONObject();
		try {
			// 기본정보 입력
			setBasicInfo(params);
			
			response = https.request(smsUrl + "sent", params); // GET 방식 전송
		} catch (Exception e) {
			response.put("status", false);
			response.put("message", e.toString());
		}
		return response;
	}

	/*
	 * Reserve message cancel 
	 * @param set : HashMap<String, String>
	 */
	public JSONObject cancel(HashMap<String, String> params) {
		JSONObject response = new JSONObject();
		try {
			// 기본정보 입력
			setBasicInfo(params);

			// Cancel reserve message 
			response = https.postRequest(smsUrl + "cancel", params);

			// Cancel 은 response 가 empty 면 성공
			if (response.get("message") == "response is empty") {
				response.put("status", true);
				response.put("message", null);
			}
		} catch (Exception e) {
			response.put("status", false);
			response.put("message", e.toString());
		}
		return response;
	}

	// Balance info
	public JSONObject balance() {
		JSONObject response = new JSONObject();
		try {
			// 기본정보 입력
			HashMap<String, String> params = new HashMap<String, String>();
			setBasicInfo(params);

			// GET 방식 전송
			response = https.request(smsUrl + "balance", params); // GET 방식 전송
		} catch (Exception e) {
			response.put("status", false);
			response.put("message", e.toString());
		}
		return response;
	}

	/*
	 * Register sender number
	 * @param set : HashMap<String, String>
	*/
	public JSONObject register(HashMap<String, String> params) {
		JSONObject response = new JSONObject();
		try {
			// 기본정보 입력
			setBasicInfo(params);
			
			// Register sender number request
			response = https.postRequest(senderIdUrl + "register", params);
		} catch (Exception e) {
			response.put("status", false);
			response.put("message", e.toString());
		}
		return response;
	}

	/*
	 * Verify sender number
	 * @param set : HashMap<String, String>
	*/
	public JSONObject verify(HashMap<String, String> params) {
		JSONObject response = new JSONObject();
		try {
			// 기본정보 입력
			setBasicInfo(params);
			
			// Register verify sender number 
			response = https.postRequest(senderIdUrl + "verify", params);
			if (response.get("message") == "response is empty") {
				response.put("status", true);
				response.put("message", null);
			}
		} catch (Exception e) {
			response.put("status", false);
			response.put("message", e.toString());
		}
		return response;
	}

	/*
	 * Delete sender number
	 * @param set : HashMap<String, String>
	*/
	public JSONObject delete(HashMap<String, String> params) {
		JSONObject response = new JSONObject();
		try {
			// 기본정보 입력
			setBasicInfo(params);
			
			// Register delete sender number 
			response = https.postRequest(senderIdUrl + "delete", params);
			if (response.get("message") == "response is empty") {
				response.put("status", true);
				response.put("message", null);
			}
		} catch (Exception e) {
			response.put("status", false);
			response.put("message", e.toString());
		}
		return response;
	}

	/*
	 * Set default sender number
	 * @param set : HashMap<String, String>
	*/
	public JSONObject setDefault(HashMap<String, String> params) {
		JSONObject response = new JSONObject();
		try {
			// 기본정보 입력
			setBasicInfo(params);
			
			// Register set default sender number 
			response = https.postRequest(senderIdUrl + "set_default", params);
			if (response.get("message") == "response is empty") {
				response.put("status", true);
				response.put("message", null);
			}
		} catch (Exception e) {
			response.put("status", false);
			response.put("message", e.toString());
		}
		return response;
	}

	/*
	 * Get sender number list
	 * @param set : HashMap<String, String>
	*/
	public JSONObject list() {
		JSONObject response = new JSONObject();
		try {
			// 기본정보 입력
			HashMap<String, String> params = new HashMap<String, String>();
			setBasicInfo(params);
			
			// Register sender number request
			response = https.request(senderIdUrl + "list", params);
		} catch (Exception e) {
			response.put("status", false);
			response.put("message", e.toString());
		}
		return response;
	}

	/*
	 * Get default sender number
	 * @param set : HashMap<String, String>
	*/
	public JSONObject getDefault() {
		JSONObject response = new JSONObject();
		try {
			// 기본정보 입력
			HashMap<String, String> params = new HashMap<String, String>();
			setBasicInfo(params);
			
			// Get default sender number 
			response = https.request(senderIdUrl + "get_default", params);
		} catch (Exception e) {
			response.put("status", false);
			response.put("message", e.toString());
		}
		return response;
	}

	/*
	 * Set api_key and api_secret.
	 * @param set : HashMap<String, String>
	*/
	private void setBasicInfo(HashMap<String, String> params) {
		params.put("api_secret", this.apiSecret);
		params.put("api_key", this.apiKey);
	}
}
