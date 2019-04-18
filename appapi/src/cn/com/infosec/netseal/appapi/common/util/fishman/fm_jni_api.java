package cn.com.infosec.netseal.appapi.common.util.fishman;

public class fm_jni_api {
  public static final int FM_DEV_TYPE_PCI_1_2X = 288;
  public static final int FM_DEV_TYPE_PCI_2_0X = 512;
  public static final int FM_DEV_TYPE_PCIE_1_0X = 57600;
  public static final int FM_DEV_TYPE_PCIE_2_0X = 57856;
  public static final int FM_DEV_TYPE_SJK0809 = 1;
  public static final int FM_DEV_TYPE_SJK0810 = 2;
  public static final int FM_DEV_TYPE_CURRENT = 57600;
  public static final int FM_PUBKEY_LEN = 516;
  public static final int FM_PRIKEY_LEN = 1412;
  public static final int FM_ECCPUBKEY_LEN = 68;
  public static final int FM_ECCPRIKEY_LEN = 36;
  public static final int FM_ECCCIPHER_LEN = 260;
  public static final int FM_ECCCIPHER_C_LEN = 160;
  public static final int FM_ECCSIGN_LEN = 64;
  public static final int FM_OPEN_EXCLUSIVE = 1;
  public static final int FM_OPEN_MULTITHREAD = 2;
  public static final int FM_OPEN_MULTIPROCESS = 4;
  public static final int CPC_BK_STORE_HOST = 1;
  public static final int FM_HKEY_TO_HOST = 33554431;
  public static final int FM_HKEY_FROM_HOST = 50331647;
  public static final int FM_ALG_SSF33 = 0;
  public static final int FM_ALG_SCB2_S = 1;
  public static final int FM_ALG_SCB2_G = 2;
  public static final int FM_ALG_SM1 = 2;
  public static final int FM_ALG_SM6 = 1;
  public static final int FM_ALG_3DES = 3;
  public static final int FM_ALG_AES = 4;
  public static final int FM_ALG_DES = 5;
  public static final int FM_ALG_RC2 = 6;
  public static final int FM_ALG_RC4 = 7;
  public static final int FM_ALG_SM4 = 8;
  public static final int FM_ALG_RSA1024 = 0;
  public static final int FM_ALG_RSA2048 = 1;
  public static final int FM_ALG_RSA4096 = 2;
  public static final int FM_ALG_SM2_1 = 3;
  public static final int FM_ALG_SM2_2 = 4;
  public static final int FM_ALG_SM2_3 = 5;
  public static final int FM_ALG_MD2 = 0;
  public static final int FM_ALG_MD4 = 1;
  public static final int FM_ALG_MD5 = 2;
  public static final int FM_ALG_SHA1 = 3;
  public static final int FM_ALG_SHA256 = 4;
  public static final int FM_ALG_SHA384 = 5;
  public static final int FM_ALG_SHA512 = 6;
  public static final int FM_ALG_SM3 = 7;
  public static final int FM_ALGMODE_ECB = 0;
  public static final int FM_ALGMODE_CBC = 1;

  static
  {
    Runtime.getRuntime().loadLibrary("fmjniapi");
  }

  public static native int FM_CPC_JNI_OpenDevice(byte[] paramArrayOfByte, int paramInt1, int paramInt2);

  public static native int FM_CPC_JNI_CloseDevice();

  public static native int FM_CPC_JNI_OpenDeviceForSM3(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int[] paramArrayOfInt);

  public static native int FM_CPC_JNI_CloseDeviceForSM3(int paramInt);

  public static native int FM_CPC_JNI_GetDeviceInfo(byte[] paramArrayOfByte);

  public static native int FM_CPC_JNI_GenRandom(int paramInt, byte[] paramArrayOfByte);

  public static native int FM_CPC_JNI_GetInfo(int paramInt1, int paramInt2, byte[] paramArrayOfByte1, int[] paramArrayOfInt, byte[] paramArrayOfByte2);

  public static native int FM_CPC_JNI_GetErrInfo(int paramInt1, int paramInt2, int[] paramArrayOfInt, byte[] paramArrayOfByte);

  public static native int FM_CPC_JNI_GenRSAKeypair(int paramInt, int[] paramArrayOfInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);

  public static native int FM_CPC_JNI_DelRSAKeypair(int paramInt);

  public static native int FM_CPC_JNI_ImportRSAKeypair(int[] paramArrayOfInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);

  public static native int FM_CPC_JNI_ExportRSAKeypair(int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);

  public static native int FM_CPC_JNI_RSAEncrypt(int paramInt1, byte[] paramArrayOfByte1, int paramInt2, byte[] paramArrayOfByte2, int[] paramArrayOfInt, byte[] paramArrayOfByte3);

  public static native int FM_CPC_JNI_RSADecrypt(int paramInt1, byte[] paramArrayOfByte1, int paramInt2, byte[] paramArrayOfByte2, int[] paramArrayOfInt, byte[] paramArrayOfByte3);

  public static native int FM_CPC_JNI_GenKey(int paramInt1, int paramInt2, int[] paramArrayOfInt, byte[] paramArrayOfByte);

  public static native int FM_CPC_JNI_DelKey(int paramInt);

  public static native int FM_CPC_JNI_ImportKey(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int[] paramArrayOfInt);

  public static native int FM_CPC_JNI_ExportKey(int paramInt, byte[] paramArrayOfByte, int[] paramArrayOfInt);

  public static native int FM_CPC_JNI_EncryptInit(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte1, int paramInt4, byte[] paramArrayOfByte2, int paramInt5);

  public static native int FM_CPC_JNI_EncryptUpdate(int paramInt1, byte[] paramArrayOfByte1, int paramInt2, byte[] paramArrayOfByte2, int[] paramArrayOfInt);

  public static native int FM_CPC_JNI_EncryptFinal(int paramInt);

  public static native int FM_CPC_JNI_Encrypt(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte1, int paramInt4, byte[] paramArrayOfByte2, int[] paramArrayOfInt, byte[] paramArrayOfByte3, int paramInt5, byte[] paramArrayOfByte4, int paramInt6);

  public static native int FM_CPC_JNI_DecryptInit(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte1, int paramInt4, byte[] paramArrayOfByte2, int paramInt5);

  public static native int FM_CPC_JNI_DecryptUpdate(int paramInt1, byte[] paramArrayOfByte1, int paramInt2, byte[] paramArrayOfByte2, int[] paramArrayOfInt);

  public static native int FM_CPC_JNI_DecryptFinal(int paramInt);

  public static native int FM_CPC_JNI_Decrypt(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte1, int paramInt4, byte[] paramArrayOfByte2, int[] paramArrayOfInt, byte[] paramArrayOfByte3, int paramInt5, byte[] paramArrayOfByte4, int paramInt6);

  public static native int FM_CPC_JNI_HashInit(int paramInt1, byte[] paramArrayOfByte, int paramInt2);

  public static native int FM_CPC_JNI_HashUpdate(int paramInt1, byte[] paramArrayOfByte, int paramInt2);

  public static native int FM_CPC_JNI_HashFinal(int paramInt, byte[] paramArrayOfByte, int[] paramArrayOfInt);

  public static native int FM_CPC_JNI_USER_GetInfo(byte[] paramArrayOfByte);

  public static native int FM_CPC_JNI_USER_BackupMng(int paramInt, byte[] paramArrayOfByte, int[] paramArrayOfInt1, int[] paramArrayOfInt2);

  public static native int FM_CPC_JNI_USER_UserMng(int paramInt1, byte[] paramArrayOfByte, int paramInt2);

  public static native int FM_CPC_JNI_USER_ChangePin(int paramInt1, byte[] paramArrayOfByte1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int[] paramArrayOfInt);

  public static native int FM_CPC_JNI_USER_Login(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2);

  public static native int FM_CPC_JNI_USER_Logout(int[] paramArrayOfInt);

  public static native int FM_CPC_JNI_FILE_Init();

  public static native int FM_CPC_JNI_FILE_CreateDir(byte[] paramArrayOfByte, int paramInt);

  public static native int FM_CPC_JNI_FILE_DeleteDir(byte[] paramArrayOfByte);

  public static native int FM_CPC_JNI_FILE_CreateFile(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2);

  public static native int FM_CPC_JNI_FILE_ReadFile(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, byte[] paramArrayOfByte3);

  public static native int FM_CPC_JNI_FILE_WriteFile(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, byte[] paramArrayOfByte3);

  public static native int FM_CPC_JNI_FILE_DeleteFile(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);

  public static native int FM_CPC_JNI_FILE_EnmuDir(byte[] paramArrayOfByte1, int[] paramArrayOfInt1, byte[] paramArrayOfByte2, int[] paramArrayOfInt2);

  public static native int FM_CPC_JNI_FILE_EnmuFile(byte[] paramArrayOfByte1, int[] paramArrayOfInt1, byte[] paramArrayOfByte2, int[] paramArrayOfInt2);

  public static native int FM_CPC_JNI_Cmd(int paramInt1, int[] paramArrayOfInt1, byte[] paramArrayOfByte1, int paramInt2, byte[] paramArrayOfByte2, int[] paramArrayOfInt2);

  public static native int FM_CPC_JNI_GenMasterKey();

  public static native int FM_CPC_JNI_Master_Encrypt(byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2, int[] paramArrayOfInt);

  public static native int FM_CPC_JNI_Master_Decrypt(byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2, int[] paramArrayOfInt);

  public static native int FM_CPC_JNI_LCD(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2);

  public static native int FM_CPC_JNI_GenECCKeypair(int paramInt, int[] paramArrayOfInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);

  public static native int FM_CPC_JNI_DelECCKeypair(int paramInt);

  public static native int FM_CPC_JNI_ImportECCKeypair(int[] paramArrayOfInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);

  public static native int FM_CPC_JNI_ExportECCKeypair(int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);

  public static native int FM_CPC_JNI_ECCEncrypt(int paramInt1, int paramInt2, byte[] paramArrayOfByte1, int paramInt3, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3);

  public static native int FM_CPC_JNI_ECCDecrypt(int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int[] paramArrayOfInt);

  public static native int FM_CPC_JNI_ECCSign(int paramInt1, byte[] paramArrayOfByte1, int paramInt2, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3);

  public static native int FM_CPC_JNI_ECCVerify(int paramInt1, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt2, byte[] paramArrayOfByte3);

  public static native int FM_CPC_JNI_SM3Init(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt);

  public static native int FM_CPC_JNI_SM3Update(byte[] paramArrayOfByte, int paramInt);

  public static native int FM_CPC_JNI_SM3Final(byte[] paramArrayOfByte, int[] paramArrayOfInt);

  public static native int FM_CPC_JNI_SM3InitForSM3(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2);

  public static native int FM_CPC_JNI_SM3UpdateForSM3(byte[] paramArrayOfByte, int paramInt1, int paramInt2);

  public static native int FM_CPC_JNI_SM3FinalForSM3(byte[] paramArrayOfByte, int[] paramArrayOfInt, int paramInt);

  public static native int FM_CPC_JNI_GetIniItemChar(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4);

  public static native int FM_CPC_JNI_GetIniItemInt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int[] paramArrayOfInt);

  public static native int FM_CPC_JNI_SetIniItemChar(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4);

  public static native int FM_CPC_JNI_SetIniItemInt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt);

  public static native int FM_CPC_JNI_SetLine(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2, byte[] paramArrayOfByte3, int paramInt3, byte[] paramArrayOfByte4, int paramInt4);

  public static native int FM_CPC_JNI_GetLine(byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2, int[] paramArrayOfInt);

  public static native int FM_CPC_JNI_OpenDeviceForSym(byte[] paramArrayOfByte, int paramInt1, int paramInt2);

  public static native int FM_CPC_JNI_CloseDeviceForSym();

  public static native int FM_CPC_JNI_ImportKeyForSym(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int[] paramArrayOfInt);

  public static native int FM_CPC_JNI_ExportKeyForSym(int paramInt, byte[] paramArrayOfByte, int[] paramArrayOfInt);

  public static native int FM_CPC_JNI_EncryptForSym(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte1, int paramInt4, byte[] paramArrayOfByte2, int[] paramArrayOfInt, byte[] paramArrayOfByte3, int paramInt5, byte[] paramArrayOfByte4, int paramInt6);

  public static native int FM_CPC_JNI_DecryptForSym(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte1, int paramInt4, byte[] paramArrayOfByte2, int[] paramArrayOfInt, byte[] paramArrayOfByte3, int paramInt5, byte[] paramArrayOfByte4, int paramInt6);

  public static native int FM_CPC_GenerateAgreementDataAndKeyWithECC(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte1, int paramInt4, byte[] paramArrayOfByte2, int paramInt5, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4, byte[] paramArrayOfByte5, byte[] paramArrayOfByte6, byte[] paramArrayOfByte7);

  public static native int FM_CPC_GenerateKeyWithECC(int paramInt1, byte[] paramArrayOfByte1, int paramInt2, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt3, byte[] paramArrayOfByte4);

  public static native int FM_CPC_GenerateAgreementDataWithECC(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte1, int paramInt4, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int[] paramArrayOfInt);
}