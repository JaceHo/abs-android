package info.futureme.abs.biz;


import java.io.IOException;

import info.futureme.abs.entity.AccessToken;
import info.futureme.abs.entity.Result;

/**
 * account manager interface used in framework to retrieve token, refresh token
 * and login, logout and so on.
 * @author Jeffrey
 * @version 1.0
 * @updated 16-2月-2016 15:16:58
 */
public interface IAccountManager {

	/**
	 * get refresh token when needed in framework
	 */
    String getRefreshToken();

	/**
	 * whether use is logged in or not
	 */
    boolean isLogin();

	/**
	 * logout user
	 */
    void logout();

	/**
	 * login and logout
	 */
    void reLogin();

	/**
	 * get accesstoken when need
	 */
    String getAccessToken();

	/**
	 * get access token, refresh token etc.
	 */
    AccessToken getToken();

    /*
    blocking call to refresh accesstoken
     */
    Result<AccessToken> refreshTokenSync() throws IOException;

	/**
	 * set token when retrieved token from network
	 * 
	 * @param result
	 */
    void setToken(AccessToken result);
}
