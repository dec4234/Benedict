package net.dec4234.database.framework;


import net.dec4234.javadestinyapi.utils.framework.OAuthManager;

public class BenedictOAuth extends OAuthManager {

	private DatabaseConfig config = new DatabaseConfig();

	@Override
	public String getAccessToken() {
		return config.getString(MongoKey.ACCESS_TOKEN.getKey());
	}

	@Override
	public String getRefreshToken() {
		return config.getString(MongoKey.REFRESH_TOKEN.getKey());
	}

	@Override
	public String getAPIToken() {
		return config.getString(MongoKey.API_TOKEN.getKey());
	}

	@Override
	public void setAccessToken(String s) {
		config.setString(MongoKey.ACCESS_TOKEN.getKey(), s);
	}

	@Override
	public void setRefreshToken(String s) {
		config.setString(MongoKey.REFRESH_TOKEN.getKey(), s);
	}

	@Override
	public void setAPIToken(String s) {
		config.setString(MongoKey.API_TOKEN.getKey(), s);
	}
}
