package com.actuate.aces.idapi.control;

import com.actuate.schemas.*;
import org.apache.axis.client.Call;
import org.apache.axis.message.SOAPHeaderElement;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class AcxControl {

	public static final String NAMESPACE = "http://schemas.actuate.com/actuate" + VersionInfo.ACTUATE_MAJOR_VERSION;

	// control variables
	private String username = "Administrator";
	private String password = "";
	private String actuateServerURL = "http://localhost:8000/";
	private byte[] extendedCredentials = null;

	// proxy operation
	public ActuateSoapBindingStub proxy;
	public com.actuate.schemas.internal.ActuateSoapBindingStub proxyInternal;
	public ActuateAPIEx actuateAPI;
	public ActuateAPIInternalEx actuateAPIInternal;

	public AcxControl() throws MalformedURLException, ServiceException {
		actuateAPI = new ActuateAPILocatorEx();
		actuateAPIInternal = new ActuateAPIInternalLocatorEx();
		setActuateServerURL(actuateServerURL);
	}

	public AcxControl(String serverURL) throws MalformedURLException, ServiceException {
		actuateAPI = new ActuateAPILocatorEx();
		actuateAPIInternal = new ActuateAPIInternalLocatorEx();
		setActuateServerURL(serverURL);
	}

	public Call createCall() throws ServiceException {
		Call call = (Call) actuateAPI.createCall();
		call.setTargetEndpointAddress(this.actuateServerURL);
		return call;
	}

	public Call createCallInternal() throws ServiceException {
		Call call = (Call) actuateAPIInternal.createCall();
		call.setTargetEndpointAddress(this.actuateServerURL);
		return call;
	}

	public boolean login(String username, String password, String targetVolume) {
		if (targetVolume == null || targetVolume.equals(""))
			targetVolume = getDefaultVolume();
		setTargetVolume(targetVolume);
		return login(username, password);
	}

	public boolean login(String username, String password) {
		setUsername(username);
		setPassword(password);
		return login();
	}

	public boolean login() {

		Login login = new Login();
		login.setPassword(password);
		login.setUser(username);
		login.setCredentials(extendedCredentials);

		try {
			setConnectionHandle(null);
			setAuthenticationId(null);
			LoginResponse loginResponse = proxy.login(login);
			setAuthenticationId(loginResponse.getAuthId());
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean systemLogin(String systemPassword) {

		SystemLogin systemLogin = new SystemLogin();
		systemLogin.setSystemPassword(systemPassword);

		try {
			setConnectionHandle(null);
			setAuthenticationId(null);
			SystemLoginResponse systemLoginResponse = proxy.systemLogin(systemLogin);
			setAuthenticationId(systemLoginResponse.getAuthId());
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}

		return true;

	}

	public void reset() {
		if (proxy != null) {
			String authenticationId = null;
			if (actuateAPI != null)
				authenticationId = actuateAPI.getAuthId();

			actuateAPI = new ActuateAPILocatorEx();
			try {
				proxy = (ActuateSoapBindingStub) actuateAPI.getActuateSoapPort(new URL(actuateServerURL));
			} catch (ServiceException e) {
				e.printStackTrace();
				return;
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return;
			}

			if (authenticationId != null)
				actuateAPI.setAuthId(authenticationId);
		}

		if (proxyInternal != null) {
			String authenticationId = null;
			if (actuateAPIInternal != null)
				authenticationId = actuateAPIInternal.getAuthId();

			actuateAPIInternal = new ActuateAPIInternalLocatorEx();
			try {
				proxyInternal = (com.actuate.schemas.internal.ActuateSoapBindingStub) actuateAPIInternal.getActuateSoapPort(new URL(actuateServerURL));
			} catch (ServiceException e) {
				e.printStackTrace();
				return;
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return;
			}

			if (authenticationId != null)
				actuateAPIInternal.setAuthId(authenticationId);
		}
	}

	public void setActuateServerURL(String serverURL) throws MalformedURLException, ServiceException {
		if ((proxy == null) || !serverURL.equals(actuateServerURL))
			proxy = (ActuateSoapBindingStub) actuateAPI.getActuateSoapPort(new URL(serverURL));

		if ((proxyInternal == null) || !serverURL.equals(actuateServerURL))
			proxyInternal = (com.actuate.schemas.internal.ActuateSoapBindingStub) actuateAPIInternal.getActuateSoapPort(new URL(serverURL));

		actuateServerURL = serverURL;
	}

	public String getActuateServerURL() {
		return actuateServerURL;
	}

	public void setExtendedCredentials(byte[] extendedCredentials) {
		this.extendedCredentials = extendedCredentials;
	}

	public byte[] getExtendedCredentials() {
		return extendedCredentials;
	}

	public void setAuthenticationId(String authenticationId) {
		actuateAPI.setAuthId(authenticationId);
		actuateAPIInternal.setAuthId(authenticationId);
	}

	public String getAuthenticationId() {
		String authId = actuateAPI.getAuthId();
		if (authId != null)
			return authId;
		else
			return actuateAPIInternal.getAuthId();
	}

	public void setPassword(String password) {
		//TODO: detect if encrypted, and if not, encrypt it.
		if (password == null)
			this.password = "";
		else
			this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setUsername(String username) {
		if (username == null)
			this.username = "";
		else
			this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setConnectionHandle(String connectionHandle) {
		actuateAPI.setConnectionHandle(connectionHandle);
		actuateAPIInternal.setConnectionHandle(connectionHandle);
	}

	public String getConnectionHandle() {
		String connHandle = actuateAPI.getConnectionHandle();
		if (connHandle != null)
			return connHandle;
		else
			return actuateAPIInternal.getConnectionHandle();
	}

	public void setTargetVolume(String volume) {
		actuateAPI.setTargetVolume(volume);
		actuateAPIInternal.setTargetVolume(volume);
	}

	public String getTargetVolume() {
		String volume = actuateAPI.getTargetVolume();
		if (volume != null)
			return volume;
		else
			return actuateAPIInternal.getTargetVolume();
	}

	public void setSOAPHeader(String name, String value) {
		proxy.setHeader(new SOAPHeaderElement(new QName(NAMESPACE, name), value));
	}

	public void removeSOAPHeader(String name) {
		SOAPHeaderElement[] headers = proxy.getHeaders();
		ArrayList<SOAPHeaderElement> newHeaders = new ArrayList<SOAPHeaderElement>();

		for (SOAPHeaderElement header : headers) {
			if (!header.getName().equals(name))
				newHeaders.add(header);
		}

		proxy.clearHeaders();

		for (SOAPHeaderElement newHeader : newHeaders) {
			proxy.setHeader(newHeader);
		}
	}

	public void setSOAPHeaderInternal(String name, String value) {
		proxyInternal.setHeader(new SOAPHeaderElement(new QName(NAMESPACE, name), value));
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void removeSOAPHeaderInternal(String name) {
		SOAPHeaderElement[] headers = proxyInternal.getHeaders();
		ArrayList<SOAPHeaderElement> newHeaders = new ArrayList<SOAPHeaderElement>();

		for (SOAPHeaderElement header : headers) {
			if (!header.getName().equals(name))
				newHeaders.add(header);
		}

		proxyInternal.clearHeaders();

		for (SOAPHeaderElement newHeader : newHeaders) {
			proxyInternal.setHeader(newHeader);
		}
	}

	private String getDefaultVolume() {
		GetSystemVolumeNames getSystemVolumeNames = new GetSystemVolumeNames();
		getSystemVolumeNames.setOnlineOnly(true);

		GetSystemVolumeNamesResponse getSystemVolumeNamesResponse;
		try {
			getSystemVolumeNamesResponse = proxy.getSystemVolumeNames(getSystemVolumeNames);
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}

		return getSystemVolumeNamesResponse.getSystemDefaultVolume();
	}

}
