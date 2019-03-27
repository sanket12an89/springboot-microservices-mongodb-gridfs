package com.shree.web.helper.pojo;

import java.util.Map;

import com.shree.web.constants.HTTPConstants;


public class GenericResponse<T> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	private int responsecode = HTTPConstants.HTTP_STATUS_200;

	private String status = HTTPConstants.HTTP_STATUS_MSG_SUUCESS;

	/** The custommessages. */
	private Map<String, String> custommessages;
	

	public int getResponsecode() {
		return responsecode;
	}

	public void setResponsecode(int responsecode) {
		this.responsecode = responsecode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/** The response. */
	private T response;


	public Map<String, String> getCustommessages() {
		return custommessages;
	}

	public void setCustommessages(Map<String, String> custommessages) {
		this.custommessages = custommessages;
	}

	public T getResponse() {
		return response;
	}

	public void setResponse(T response) {
		this.response = response;
	}
	
	
	
}
