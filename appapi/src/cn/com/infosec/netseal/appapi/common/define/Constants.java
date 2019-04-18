package cn.com.infosec.netseal.appapi.common.define;

public class Constants {

	public static final String VERISON = "NetSeal 1.0.004.2 build201901111531"; // 系统版本号统一出处
	public static final String UTF_8 = "UTF-8"; // 系统编码
	public static final String API = "API"; // API连接池标识符
	public static final String SPLIT_1 = ","; // 分割符1
	public static final String SPLIT_2 = ";"; // 分割符2
	public static final String SPLIT_DIR = "/"; // 目录分割符
	public static final String SPOT = ".";

	public static final String PHOTO_SUFFIX = ".png"; // 图片后缀
	public static final String PDF_SUFFIX = ".pdf"; // pdf后缀
	public static final String CERT_SUFFIX = ".cer"; // 证书后缀
	public static final String CERT_CHAIN_SUFFIX = ".p7b"; // 证书链后缀
	public static final String SEAL_SUFFIX = ".seal"; // 印章后缀
	public static final String STAMP_SUFFIX = ".stamp"; // 签章后缀
	public static final String TMP_SUFFIX = ".tmp"; // 临时后缀
	public static final String PFX_SUFFIX = ".pfx"; // 密钥后缀
	public static final String JKS_SUFFIX = ".jks"; // 密钥后缀
	public static final String PRI_SUFFIX = ".pri"; // 密钥后缀
	public static final String KEY_SUFFIX = ".key"; // 密钥后缀
	public static final String CRL_SUFFIX = ".crl"; // crl文件后缀
	public static final String CSR_SUFFIX = ".csr"; // p10文件后缀
	public static final String ZIP_SUFFIX = ".zip"; // 密钥备份后缀
	public static final String LICENSE_SUFFIX = ".li"; // license文件后缀
	public static final String LOG_SUFFIX = ".log"; // log文件后缀
	public static final String ASN1_SUFFIX = ".asn1"; // asn1文件后缀
	public static final String ENV_SUFFIX = ".env"; // 数字信封文件后缀
	public static final String PEM_SUFFIX = ".pem"; // 私钥文件后缀
	public static final String HA_LOG_SUFFIX = "-log"; // 私钥文件后缀
	public static final String HA_DEBUG_LOG_SUFFIX = "-debug"; // 私钥文件后缀
	public static final String TXT_SUFFIX = ".txt"; // txt文件后缀
	public static final String OFD_SUFFIX = ".ofd"; // ofd文件后缀

	public static final long PHOTO_SIZE = 20; // 印模图片大小 KB
	public static final int LENGTH_EIGHT = 8; // 限制长度
	public static final int LENGTH_TWENTY = 20; // 限制长度
	public static final int LENGTH_THIRTY = 30; // 限制长度
	public static final int LENGTH_FIFTY = 50; // 限制长度
	public static final int LENGTH_HUNDRED = 100; // 限制长度
	public static final int LENGTH_TWO_HUNDRED = 200; // 限制长度
	public static final int LENGTH_THREE_HUNDRED = 300; // 限制长度
	public static final int LENGTH_THOUSAND = 1000;// 限制长度
	public static final long LENGTH_20KB_B64 = 27308L; // 20K base64 后的长度
	public static final long LENGTH_3MB_B64 = 4194304L; // 3MB base64 后的长度
	public static final long LENGTH_5MB = 5 * 1024 * 1024L; // 5MB

	public static final String SCHEDULER_NTP = "SCHEDULER_NTP"; // 计划任务 NTP
	public static final String SCHEDULER_CRL = "SCHEDULER_CRL"; // 计划任务 CRL
	public static final String SCHEDULER_JMX = "SCHEDULER_JMX"; // 计划任务 JMX

	public static final String PARAM_TYPE_STRING_NOT_NULL = "PARAM_TYPE_STRING_NOT_NULL";
	public static final String PARAM_TYPE_STRING_NULLABLE = "PARAM_TYPE_STRING_NULLABLE";
	public static final String PARAM_TYPE_INT = "PARAM_TYPE_INT";
	public static final String PARAM_TYPE_LONG = "PARAM_TYPE_LONG";
	public static final String PARAM_TYPE_DATE = "PARAM_TYPE_DATE";
	public static final String PARAM_TYPE_FLOAT = "PARAM_TYPE_FLOAT";
	public static final String PARAM_TYPE_BOOLEAN = "PARAM_TYPE_BOOLEAN";

	public static final int QFZ_ALIGN_LEFT = 0; // 左骑缝
	public static final int QFZ_ALIGN_RIGHT = 1; // 右骑缝

	public static final String APPLICATION_CONTEXT_PATH = "cn/com/infosec/netseal/appserver/config/applicationContext.xml";
	public static final String WEBLICATION_CONTEXT_PATH = "cn/com/infosec/netseal/webserver/config/applicationContext.xml";

	// public static final String ROOT_PATH = "E:/eclipseprj/source2016/NetSeal_v4/";
	public static final String ROOT_PATH = "/opt/infosec/NetSeal/";
	public static final String SEAL_PATH = ROOT_PATH + "seal/";
	public static final String CERT_PATH = ROOT_PATH + "cert/";
	public static final String PHOTO_PATH = ROOT_PATH + "photo/";
	public static final String REPORT_PATH = ROOT_PATH + "report/";
	public static final String STAMP_PATH = ROOT_PATH + "stamp/";

	public static final String CONF_PATH = ROOT_PATH + "conf/";
	public static final String CRL_PATH = ROOT_PATH + "crl/";
	public static final String CRL_FROM_PATH = CRL_PATH + "from/";
	public static final String CRL_TO_PATH = CRL_PATH + "to/";
	public static final String LOG_PATH = ROOT_PATH + "log/";
	public static final String SSL_PATH = ROOT_PATH + "ssl/";
	public static final String LICENSE_PATH = ROOT_PATH + "license/";
	public static final String NATIVELIB_PATH = ROOT_PATH + "appserver/nativelib/";
	public static final String APPSERVER_PATH = ROOT_PATH + "appserver/";
	public static final String RPM_CHECK_PATH = ROOT_PATH + "rpm_check/";

	public static final String KEY_PATH = ROOT_PATH + "key/";
	public static final String BACKUP_PATH = ROOT_PATH + "backup/";
	public static final String TMP_PATH = ROOT_PATH + "tmp/";
	public static final String PDF_TEMPLATE_PATH = ROOT_PATH + "pdf_template/";
	public static final String PDF_PATH = ROOT_PATH + "pdf/";
	public static final String PDF_STAMP_PATH = ROOT_PATH + "pdf_stamp/";
	public static final String OFD_PATH = ROOT_PATH + "ofd/";
	public static final String OFD_STAMP_PATH = ROOT_PATH + "ofd_stamp/";

	public static final String BACKUP_KEY = "NetSealKey.zip";
	public static final String BACKUP_PDF = "StampPdf.zip";
	public static final String BACKUP_OFD = "StampOfd.zip";
	public static final String BACKUP_TMP = "tmp.zip";

	public static final String PUB_KEY = "netseal.pub";
	public static final String PRI_KEY = "netseal.pri";
	public static final String PRI_CARD_KEY = "netseal.key";
	public static final String PEM_KEY = "netseal.pem";

	public static final String PFX = "netseal.pfx";
	public static final String JKS = "netseal.jks";

	public static final String CSR = "netseal.csr";
	public static final String CER = "netseal.cer";
	public static final String KEY_PWD = "68683556";

	public static final String LICENSE = "license.li";
	public static final String LICENSE_APP = "licenseApp.li";
	public static final String LICENSE_GUID = "licenseGuid.li";

	public static final String P10_LABEL = "p10";
	public static final String P10_DN = "cn=p10_";

	public static final int RSA_KEY_SIZE = 1024;
	public static final int SM2_KEY_SIZE = 16;
	public static final String KEY_ALG = "SM4";

	public static final String SOFT_MODE = "SOFT_MODE";
	public static final String HARD_MODE = "HARD_MODE";

	public static final String DEFAULT_STRING = "";
	public static final int DEFAULT_INT = 0;
	public static final String SYS_USER_DEFAULT_PWD = "123456";
	public static final String DEFAULT_UNKNOWN_STRING = "N/A";
	public static final int DEFAULT_UNKNOWN_INT = -1;

	public static final int USAGE_SIGNATURE = 1;
	public static final int USAGE_ENCRYPT = 2;
	public static final int USAGE_SIGN_ENC = 3;

	public static final String NETCERT_CA_RSA = "rsa_ca";// 申请rsa证书的ca
	public static final String NETCERT_CA_SM2 = "sm2_ca";// 申请sm2证书的ca

	public static final String COUNTER_KEY = "COUNTER_KEY";
	public static final String SOCKET_NUM = "SOCKET_NUM";
	public static final String CERT_NUM = "CERT_NUM";
	public static final String DEAL_S_NUM = "DEAL_S_NUM"; // 成功交易
	public static final String DEAL_F_NUM = "DEAL_F_NUM"; // 失败交易

	public static final String LDAP_CONTEXT_FACTORY = "LDAP_CONTEXT_FACTORY";
	public static final String LDAP_PROVIDER_URL = "LDAP_PROVIDER_URL";
	public static final String LDAP_SECURITY_AUTHENTICATION = "LDAP_SECURITY_AUTHENTICATION";
	public static final String LDAP_BASE_DN = "LDAP_BASE_DN";
	public static final String LDAP_ACCOUNT = "LDAP_ACCOUNT";
	public static final String LDAP_PASSWORD = "LDAP_PASSWORD";
	public static final String LDAP_SEARCH_FILTER = "LDAP_SEARCH_FILTER";
	public static final String LDAP_NONE = "none";

	public static final String ACCESS = "ACCESS";
	public static final String MANAGE = "MANAGE";
	public static final String DEBUG = "DEBUG";
	public static final String SYSTEM = "SYSTEM";
	public static final String ERROR = "ERROR";
	public static final String SYSLOG = "SYSLOG";

	// 通讯报文字段标识
	public static final String SEAL_ID = "SEAL_ID";
	public static final String SEAL_STATUS = "SEAL_STATUS";
	public static final String SEAL_NAME = "SEAL_NAME";
	public static final String SEAL_TYPE = "SEAL_TYPE";
	public static final String SEAL_USED_COUNT = "SEAL_USED_COUNT";
	public static final String SEAL_USED_LIMIT = "SEAL_USED_LIMIT";
	public static final String SEAL_NOTBEFOR = "SEAL_NOTBEFOR";
	public static final String SEAL_NOTAFTER = "SEAL_NOTAFTER";
	public static final String SEAL_GENERATE_TIME = "SEAL_GENERATE_TIME";
	public static final String SEAL_DOWNLOAD_TIME = "SEAL_DOWNLOAD_TIME";
	public static final String SEAL_IS_AUTH_CERT_DOWNLOAD = "SEAL_IS_AUTH_CERT_DOWNLOAD";
	public static final String SEAL_IS_DOWNLOAD = "SEAL_IS_DOWNLOAD";
	public static final String SEAL_DATA = "SEAL_DATA";
	public static final String CRYPTO_SERVER_KEYID = "CRYPTO_SERVER_KEYID";// 制章密钥id
	public static final String SOCKET_NUM_LIMIT = "SOCKET_NUM_LIMIT";// 连接数限制
	public static final String CERT_NUM_LIMIT = "CERT_NUM_LIMIT";// 证书数限制

	public static final String CERT_DN_SERVER = "CERT_DN_SERVER";// 服务器证书DN
	public static final String CERT_DN_CLIENT = "CERT_DN_CLIENT";// 客户端证书DN
	public static final String CERT_DN = "CERT_DN";
	public static final String CERT_ISSUE_DN = "CERT_ISSUE_DN";
	public static final String CERT_DATA = "CERT_DATA";
	public static final String SIGN_DATA = "SIGN_DATA";

	public static final String TEMPLATE_ID = "TEMPLATE_ID";
	public static final String TEMPLATE_NAME = "TEMPLATE_NAME";

	public static final String COMPANY_ID = "COMPANY_ID";
	public static final String COMPANY_NAME = "COMPANY_NAME";

	public static final String PHOTO_NAME = "PHOTO_NAME";
	public static final String PHOTO_HIGH = "PHOTO_HIGH";
	public static final String PHOTO_WIDTH = "PHOTO_WIDTH";
	public static final String PHOTO_DATA = "PHOTO_DATA";
	public static final String PHOTO_DATA_GEN = "PHOTO_DATA_GEN";

	public static final String PRINTER_ID = "PRINTER_ID";
	public static final String PRINTER_NAME = "PRINTER_NAME";
	public static final String PRINTER_NUM = "PRINTER_NUM";
	public static final String PRINTER_LIMIT = "PRINTER_LIMIT";
	public static final String PRINTER_PWD = "PRINTER_PWD";

	public static final String PDF_DATA = "PDF_DATA";
	public static final String PDF_DATA_STAMP = "PDF_DATA_STAMP";
	public static final String PDF_PAGENUM = "PDF_PAGENUM";// 页号
	public static final String PDF_X = "PDF_X";// 左下角x坐标值
	public static final String PDF_Y = "PDF_Y";// 左下角y坐标值
	public static final String PDF_QFZ = "PDF_QFZ";// QFZ骑缝章
	public static final String PDF_KEYWORDS = "PDF_KEYWORDS";// 关键字
	public static final String PDF_BIZNUM = "PDF_BIZNUM";// 业务编号
	public static final String PDF_TEMPLATE_NAME = "PDF_TEMPLATE_NAME";// pdf模板名称
	public static final String PDF_TEMPLATE_FIELD = "PDF_TEMPLATE_FIELD";// pdf模板文本域值
	public static final String PDF_CHECK_SEAL_DATE = "PDF_CHECK_SEAL_DATE";// 验签章时是否验印章有效期
	public static final String PDF_CHECK_CERT_DATE = "PDF_CHECK_CERT_DATE";// 验签章时是否验证书有效期

	public static final String OFD_DATA = "OFD_DATA";
	public static final String OFD_DATA_STAMP = "OFD_DATA_STAMP";
	public static final String OFD_PAGENUM = "OFD_PAGENUM";// 页号
	public static final String OFD_X = "OFD_X";// 左下角x坐标值
	public static final String OFD_Y = "OFD_Y";// 左下角y坐标值
	public static final String OFD_QFZ = "OFD_QFZ";// QFZ骑缝章
	public static final String OFD_KEYWORDS = "OFD_KEYWORDS";// 关键字
	public static final String OFD_BIZNUM = "OFD_BIZNUM";// 业务编号
	public static final String OFD_CHECK_SEAL_DATE = "OFD_CHECK_SEAL_DATE";// 验签章时是否验印章有效期
	public static final String OFD_CHECK_CERT_DATE = "OFD_CHECK_CERT_DATE";// 验签章时是否验证书有效期

	public static final String PDF_BARCODE_X = "PDF_BARCODE_X";// 左下角x坐标值
	public static final String PDF_BARCODE_Y = "PDF_BARCODE_Y";// 左下角y坐标值
	public static final String PDF_BARCODE_WIDTH = "PDF_BARCODE_WIDTH";// 宽
	public static final String PDF_BARCODE_CONTENT = "PDF_BARCODE_CONTENT";// 二维码内容
	public static final String PDF_BARCODE_PAGENUM = "PDF_BARCODE_PAGENUM";// 页号

	public static final String USER_NAME = "USER_NAME";
	public static final String RESULT = "RESULT";

	public static final String PRO_KEY = "PRO_KEY";
	public static final String PRO_GET_CURRENT_TIME = "PRO_GET_CURRENT_TIME";

	public static final String OP_LOG_ACCOUNT = "OP_LOG_ACCOUNT";
	public static final String OP_LOG_TYPE = "OP_LOG_TYPE";
	public static final String OP_LOG_TIME = "OP_LOG_TIME";
	public static final String OP_LOG_RETURN_CODE = "OP_LOG_RETURN_CODE";
	public static final String OP_LOG_ERR_MSG = "OP_LOG_ERR_MSG";

	public static final float GEN_SEAL_DPI = 96f; // 每英寸多少点
	public static final String GEN_SEAL_HEAD_ID = "ES";
	public static final int GEN_SEAL_HEAD_VERSION = 1;
	public static final String GEN_SEAL_HEAD_VID = "Infosec_NetSeal_1.0";

	// 算法标识
	public static final String GM_OID = "1234567812345678";
	public static final String EC = "EC";
	public static final String SM2 = "SM2";
	public static final String SM3 = "SM3";
	public static final String SM4 = "SM4";
	public static final String SM3_SM2 = "SM3withSM2";

	public static final String RSA = "RSA";
	public static final String SHA1 = "SHA1";
	public static final String SHA256 = "SHA256";
	public static final String SHA1_RSA = "SHA1withRSA";
	public static final String SHA256_RSA = "SHA256withRSA";
	public static final String AES = "AES";

	public static final String JMX_URL = "service:jmx:rmi:///jndi/rmi://127.0.0.1:8999/jmxrmi";
	public static final String JMX_INFOSEC_OBJ_NAME = "cn.com.infosec:type=NetSeal";
	public static final String JMX_DRUID_OBJ_NAME = "com.alibaba.druid:type=DruidDataSource,id=NetSeal";
	public static final String JMX_MEMORY_OBJ_NAME = "java.lang:type=Memory";

	public static final String TABLE_SEAL_SYS_USER = "SEAL_SYS_USER";
	public static final String TABLE_SEAL_COMPANY = "SEAL_COMPANY";
	public static final String TABLE_SEAL_USER = "SEAL_USER";
	public static final String TABLE_SEAL_ROLE = "SEAL_ROLE";
	public static final String TABLE_SEAL_MENU = "SEAL_MENU";
	public static final String TABLE_SEAL_KEY = "SEAL_KEY";
	public static final String TABLE_SEAL_TEMPLATE = "SEAL_TEMPLATE";
	public static final String TABLE_SEAL_REQUEST = "SEAL_REQUEST";
	public static final String TABLE_SEAL_AUDIT = "SEAL_AUDIT";
	public static final String TABLE_SEAL_SEAL = "SEAL_SEAL";
	public static final String TABLE_SEAL_PRINTER = "SEAL_PRINTER";
	public static final String TABLE_SEAL_CERT_CHAIN = "SEAL_CERT_CHAIN";
	public static final String TABLE_SEAL_ACCESS_LOG = "SEAL_ACCESS_LOG";
	public static final String TABLE_SEAL_MANAGE_LOG = "SEAL_MANAGE_LOG";
	public static final String TABLE_SEAL_OPERATE_LOG = "SEAL_OPERATE_LOG";
	public static final String TABLE_SEAL_CRL = "SEAL_CRL";
	public static final String TABLE_SEAL_CERT = "SEAL_CERT";
	public static final String TABLE_SEAL_PHOTO_DATA = "SEAL_PHOTO_DATA";
	public static final String TABLE_SEAL_SEAL_DATA = "SEAL_SEAL_DATA";
	public static final String TABLE_SEAL_CERT_DATA = "SEAL_CERT_DATA";
	public static final String TABLE_SEAL_KEY_DATA = "SEAL_KEY_DATA";
	public static final String TABLE_SEAL_ID_DELETE = "SEAL_ID_DELETE";
	public static final String TABLE_SEAL_CSR_DATA = "SEAL_CSR_DATA";
	public static final String TABLE_SEAL_PDF_TEMPLATE = "SEAL_PDF_TEMPLATE";
	public static final String TABLE_SEAL_PDF_TEMPLATE_DATA = "SEAL_PDF_TEMPLATE_DATA";
	public static final String TABLE_SEAL_USER_CERT_REQUEST = "SEAL_USER_CERT_REQUEST";

	// WebServer日志操作类型
	public static final String LOG_OPTYPE_LOGCONFIG = "logConfig";
	public static final String LOG_OPTYPE_ADDCOMPANY = "addCompany";
	public static final String LOG_OPTYPE_EDITCOMPANY = "editCompany";
	public static final String LOG_OPTYPE_DELCOMPANY = "delCompany";
	public static final String LOG_OPTYPE_SYSUSERLOGIN = "sysUserLogin";
	public static final String LOG_OPTYPE_SYSUSERLOGOUT = "sysUserLogout";
	public static final String LOG_OPTYPE_ADDSYSUSER = "addSysUser";
	public static final String LOG_OPTYPE_EDITSYSUSER = "editSysUser";
	public static final String LOG_OPTYPE_DELSYSUSER = "delSysUser";
	public static final String LOG_OPTYPE_RESETSYSUSERPWD = "resetSysUserPwd";
	public static final String LOG_OPTYPE_UPDATESYSUSERPWD = "updateSysUserPwd";
	public static final String LOG_OPTYPE_ADDUSER = "addUser";
	public static final String LOG_OPTYPE_EDITUSER = "editUser";
	public static final String LOG_OPTYPE_DELUSER = "delUser";
	public static final String LOG_OPTYPE_EDITROLE = "ditRole";
	public static final String LOG_OPTYPE_ADDTEMPLATE = "addTemplate";
	public static final String LOG_OPTYPE_EDITTEMPLATE = "editTemplate";
	public static final String LOG_OPTYPE_DELTEMPLATE = "delTemplate";
	public static final String LOG_OPTYPE_UPDATETEMPLATESTATUS = "updateTemplateStatus";
	public static final String LOG_OPTYPE_REQUESTSEAL = "requestSeal";
	public static final String LOG_OPTYPE_AUDITSEAL = "auditSeal";
	public static final String LOG_OPTYPE_DELREQUEST = "delRequest";
	public static final String LOG_OPTYPE_DELSEAL = "delseal";
	public static final String LOG_OPTYPE_UPDATESEALSTATUS = "updateSealStatus";
	public static final String LOG_OPTYPE_EDITSEAL = "editSeal";
	public static final String LOG_OPTYPE_SERVERCERTREQUEST = "serverCertRequest";
	public static final String LOG_OPTYPE_SERVERSMCERTREQUEST = "serverSMCertRequest";
	public static final String LOG_OPTYPE_SERVERCERTIMPORT = "serverCertImport";
	public static final String LOG_OPTYPE_SERVERSMCARDCERTREQUEST = "serverSMCardCertRequest";
	public static final String LOG_OPTYPE_SERVERSMCARDCERTIMPORT = "serverSMCardCertImport";
	public static final String LOG_OPTYPE_SERVERCERTPFXIMPORT = "serverCertPfxImport";
	public static final String LOG_OPTYPE_SERVERSMCERTIMPORT = "serverSMCertImport";
	public static final String LOG_OPTYPE_SERVERSMCARDENVIMPORT = "serversmcardenvimport";
	public static final String LOG_OPTYPE_DELKEY = "delJksKey";
	public static final String LOG_OPTYPE_BACKUPSKEY = "backupsKey";
	public static final String LOG_OPTYPE_RECOVERYKEY = "recoveryKey";
	public static final String LOG_OPTYPE_ADDCERTCHAIN = "addCertChain";
	public static final String LOG_OPTYPE_DELCERTCHAIN = "delCertChain";
	public static final String LOG_OPTYPE_ADDPDFTEMPLATE = "addPdfTemplate";
	public static final String LOG_OPTYPE_DELPDFTEMPLATE = "delPdfTemplate";
	public static final String LOG_OPTYPE_CRLMANAGESAVE = "crlManageSave";
	public static final String LOG_OPTYPE_CRLMANAGEOPER = "crlManageOper";
	public static final String LOG_OPTYPE_DBCONFIGSAVE = "dbConfigSave";
	public static final String LOG_OPTYPE_TSACONFIGSAVE = "tsaConfigSave";
	public static final String LOG_OPTYPE_SAVELICENSE = "saveLicense";
	public static final String LOG_OPTYPE_CHECKREPORT = "checkReport";
	public static final String LOG_OPTYPE_HACONFIGSAVE = "haConfigSave";
	public static final String LOG_OPTYPE_HASERVICEUPDATE = "haServiceUpdate";
	public static final String LOG_OPTYPE_HOSTSCONFIGSAVE = "hostsConfigSave";
	public static final String LOG_OPTYPE_DOWNSERVERCERT = "downServerCert";
	public static final String LOG_OPTYPE_DOWNLOGFILE = "downLogFile";
	public static final String LOG_OPTYPE_DOWNSEAL = "downSeal";
	public static final String LOG_OPTYPE_CARDUSERLOGOUT = "cardUserLogout";
	public static final String LOG_OPTYPE_CARDUSERLOGIN = "cardUserLogin";
	public static final String LOG_OPTYPE_DOWNHAFILE = "downHaFile";
	public static final String LOG_OPTYPE_DOWNLICENSE = "downLicense";
	public static final String LOG_OPTYPE_DELLICENSE = "delLicense";
	public static final String LOG_OPTYPE_DOWNCHECKREPORT = "downCheckReport";
	public static final String LOG_OPTYPE_SYNCNTP = "syncNtp";
	public static final String LOG_OPTYPE_DOWNPDFTEMPLATE = "downPdfTemplate";
	public static final String LOG_OPTYPE_SAVESYSCONFIG = "saveSysConfig";
	public static final String LOG_OPTYPE_DELCERT = "delCert";
	public static final String LOG_OPTYPE_GENSTAMP = "genStamp";
	public static final String LOG_OPTYPE_SYSUSERCLEAR = "sysUserClear";

}
