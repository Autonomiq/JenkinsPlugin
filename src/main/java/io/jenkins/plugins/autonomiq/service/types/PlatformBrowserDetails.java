package io.jenkins.plugins.autonomiq.service.types;

public class PlatformBrowserDetails {
    private String browser;
    private String browserVersion;
    private String platform;
	private String platformVersion;
    private String appiumVersion;
    private String deviceName;
    private String deviceOrientation;
    private String environmentType;
    private String sauceConnectProxy;

    public PlatformBrowserDetails(String browser, String browserVersion, String platform, String platformVersion,
    		String appiumVersion, String deviceName, String deviceOrientation,String environmentType,String sauceConnectProxy) {
        this.browser = browser;
        this.browserVersion = browserVersion;
        this.platform = platform;
        this.platformVersion = platformVersion;
        this.appiumVersion = appiumVersion;
        this.deviceName = deviceName;
        this.deviceOrientation = deviceOrientation;
        this.environmentType = environmentType;
        this.sauceConnectProxy=sauceConnectProxy;
    }

    public String getBrowser() {
        return browser;
    }
    public void setBrowserVersion(String browserVersion) {
        this.browserVersion = browserVersion;
    }

    public String getBrowserVersion() {
        return browserVersion;
    }
    
    public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
	public void setPlatformVersion(String platformVersion) {
		this.platformVersion = platformVersion;
	}
	
	public String getPlatformVersion() {
		return platformVersion;
	}

	public String getAppiumVersion() {
		return appiumVersion;
	}

	public void setAppiumVersion(String appiumVersion) {
		this.appiumVersion = appiumVersion;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceOrientation() {
		return deviceOrientation;
	}

	public void setDeviceOrientation(String deviceOrientation) {
		this.deviceOrientation = deviceOrientation;
	}
	
	 public String getEnvironmentType() {
			return environmentType;
	}

		public void setEnvironmentType(String environmentType) {
			this.environmentType = environmentType;
	}
	 public String getSauceConnectProxyType() {
				return sauceConnectProxy;
		}

		public void setSauceConnectProxyType(String sauceConnectProxy) {
				this.sauceConnectProxy = sauceConnectProxy;
		}
		
	
}
