package cn.com.infosec.netsigninterface.resource;

/**
 * <p>
 * Title:NetSignRes
 * </p>
 * <p>
 * Description: ������ص���Դ˵��
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:infosec
 * </p>
 * 
 * @author huangyong
 * @version 1.0
 */

public class NetSignRes {

	public void NetSignRes() {
	}

	// �汾
	public static final String PRODUCT_VERSION = "NetSignAPI(JAVA) v1.8.100.1 Build201309121600";

	// ����CRL���ֵ��쳣
	public static final String INCORRECT_LDAP_HOST = "LDAP��������ַ����";

	public static final String INCORRECT_LDAP_PORT = "LDAP�˿ڴ���";

	public static final String CANNOT_CONNECTTO_LDAP = "�������ӵ�LDAP";

	public static final String CRL_PATH_ERROR = "����CRL�����г��ִ���,���鱣��CRL��Ŀ¼�Ƿ����";

	public static final String BASEDN_NOT_SET = "û����������CRL��BaseDN";

	public static final String SEARCHFILTER_NOT_SET = "û����������CRL�Ĺ�������";

	public static final String PROPERTYNAME_NOT_SET = "û����������CRL��������";

	public static final String PATH_NOT_SET = "û������CRL�Ĵ��Ŀ¼";

	public static final String PATH_NOT_FOUND = "����CRL�Ĵ��Ŀ¼���ô��󣬿�����Ŀ¼������";

	public static final String SEARCHCRL_EXCEPTION = "����CRL����";

	public static final String INTEVAL_NOT_SET = "û������CRL���غ͸���CRL�б��ʱ����";

	public static final String INVALID_INTERVAL = "CRL����ʱ�������ô���Ӧ��Ϊ���������ֵ���룩";

	public static final String CANNOT_START_DOWNTHREAD = "CRL�����߳�û������";

	public static final String CRLFILE_READONLY = "CRL�ļ�ֻ��,�޷�����";

	public static final String CANNOT_READ_CRL_DIRECTORY = "���ܶ�ȡָ����CRL��Ŀ¼";

	public static final String CANNOT_WRITE_CRL_DIRECTORY = "����д��ָ�����CRL��Ŀ¼";

	// ����ϵͳ���Գ��ֵ��쳣
	public static final String INPUTSTREAM_OF_PFX_IS_NULL = "��������keystore�� pfx ����������Ϊ��";

	public static final String PFX_PRIKEY_PASSWORD_IS_NULL = "��������keystore��˽Կ���������Ϊ��";

	public static final String SERVERKEYSTORES_IS_NULL = "������ϵͳ����ʱ,KeyStore�������鲻��Ϊ��";

	public static final String TRUSTCERTS_IS_NULL = "������ϵͳ����ʱ,���ε�֤���б���Ϊ��";

	public static final String INVALID_TRUSTCERTS_PATH = "����֤��·������";

	// ǩ������ǩ�����ֵ��쳣
	public static final String PLAINDATA_IS_NULL = "�������ݲ���Ϊ��";

	public static final String P7DATA_IS_NULL = "ǩ�����ݲ���Ϊ��";

	public static final String PLAINDATATOSIGNISNULL = "��ǩ������Ϊ��";

	public static final String NO_PLAIN_IN_P7DATA = "PKCS7���ݰ���û��ǩ��ԭ��";

	public static final String NETSIGN_VERIFY_ERROR = "��ǩ��δͨ��";

	public static final String DECRYPT_DN_IS_NULL = "���������ŷ��֤��DN����Ϊ��";

	public static final String P7ENVDATA_IS_NULL = "�����ŷ�����Ϊ��";

	public static final String SERVERKEYSTORE_FOR_DN_NOTFOUND = "DN��Ӧ��֤��û���ҵ�";

	public static final String DN_FOR_SIGN_IS_NULL = "���з�������ǩ����֤��DN����Ϊ��";

	public static final String CERTIFICATE_DATA_IS_NULL = "���������ŷ��֤�鲻��Ϊ��";

	public static final String PRIVATEKEY_NOT_FOUND = "������ServerKeystore���ҵ�˽Կ";

	public static final String CERTIFICATE_NOT_FOUND = "������ServerKeystore���ҵ�֤��";

	public static final String CERTIFICATE_CHAIN_NOT_FOUND = "������ServerKeystore���ҵ�֤����";

	// ֤�������Ϣ
	public static final String CERTIFICATE_ERROR = "֤�����";

	public static final String CERTIFICATE_REVOKED = "֤���ѱ�����";

	public static final String CERTNOTTRUSTERROR = "֤�鲻������";
}