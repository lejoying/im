package cn.buaa.myweixin.apiutils;


public class MCNowUser {
	private static Account account;

	public static void setNowUser(String phone, String head, String nickName,
			String mainBusiness,String status,String accessKey) {
		MCNowUser.account = new Account();
		MCNowUser.account.setStatus(status);
		MCNowUser.account.setPhone(phone);
		MCNowUser.account.setNickName(nickName);
		MCNowUser.account.setMainBusiness(mainBusiness);
		MCNowUser.account.setHead(head);
		MCNowUser.account.setAccessKey(accessKey);
	}

	public static void setNowUser(Account account){
		MCNowUser.account = account;
	}
	
	public static Account getNowUser() {
		return account;
	}
}
