package cn.com.infosec.netseal.appapi.common.util.fishman;

public class FishManUserInfo {
	String userID;
	String userStatus;
	String userType;
	boolean isCurrentUser;
	String currentUserCn;

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public boolean isCurrentUser() {
		return isCurrentUser;
	}

	public void setIsCurrentUser(boolean isCurrentUser) {
		this.isCurrentUser = isCurrentUser;
	}

	public String getCurrentUserCn() {
		if(isCurrentUser)
			return "是";
		return "";
	}


}
