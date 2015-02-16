package com.example.ncrdigitalreciept;

public interface OnRequestExecuted {
	void OnRequestExecuted(String res, int requestTypeId, Object customObjectForResponse, int httpErrorCode);

}
