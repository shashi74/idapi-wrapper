package com.actuate.aces.idapi.control;

import com.actuate.schemas.internal.ActuateAPI;
import org.apache.axis.client.Call;

public interface ActuateAPIInternalEx extends ActuateAPI {

	public void setAuthId(String authId);

	public void setLocale(String locale);

	public void setTargetVolume(String targetVolume);

	public void setConnectionHandle(String connectionHandle);

	public void setDelayFlush(Boolean delayFlush);

	public void setFileType(String fileType);

	public String getAuthId();

	public String getLocale();

	public String getTargetVolume();

	public String getConnectionHandle();

	public Boolean getDelayFlush();

	public String getFileType();

	public Call getCall();

}
