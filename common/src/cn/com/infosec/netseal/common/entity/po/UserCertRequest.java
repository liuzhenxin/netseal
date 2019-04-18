package cn.com.infosec.netseal.common.entity.po;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;

/**
 * 用户证书申请
 */
public class UserCertRequest extends Base {
	private Long userId;// 签章人ID
	private String certType;
	private String certTemplate;
	private String certDn;
	private String userUuid;
	private String certRefno;
	private String certAuthCode;
	private Integer status;
	private Integer validityLen;
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getCertType() {
		return certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}

	public String getCertTemplate() {
		return certTemplate;
	}

	public void setCertTemplate(String certTemplate) {
		this.certTemplate = certTemplate;
	}

	public String getCertDn() {
		return certDn;
	}

	public void setCertDn(String certDn) {
		this.certDn = certDn;
	}

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public String getCertRefno() {
		return certRefno;
	}

	public void setCertRefno(String certRefno) {
		this.certRefno = certRefno;
	}

	public String getCertAuthCode() {
		return certAuthCode;
	}

	public void setCertAuthCode(String certAuthCode) {
		this.certAuthCode = certAuthCode;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getValidityLen() {
		return validityLen;
	}

	public void setValidityLen(Integer validityLen) {
		this.validityLen = validityLen;
	}

	public String calMac()  {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			baos.write(calMac(getId()));
			baos.write(calMac(userId));
			baos.write(calMac(certType));
			baos.write(calMac(certTemplate));
			baos.write(calMac(certDn));
			baos.write(calMac(userUuid));
			baos.write(calMac(certRefno));
			baos.write(calMac(userId));
			baos.write(calMac(certAuthCode));
			baos.write(calMac(status));
			baos.write(calMac(validityLen));
			baos.write(calMac(getGenerateTime()));
			baos.write(calMac(getUpdateTime()));
		} catch (IOException e) {
			throw new NetSealRuntimeException(ErrCode.CALC_MAC_ERROR, e.getMessage());
		}
		return CryptoHandler.hashEnc64(baos.toByteArray());
	}	
}
