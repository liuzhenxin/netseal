package cn.com.infosec.netseal.common.algorithm.gm;

import java.math.BigInteger;

import cn.com.infosec.math.ec.ECCurve;
import cn.com.infosec.math.ec.ECPoint;

public interface SM2GMParameter {
	final int SM2_SIZE = 32;

	final BigInteger gmp = new BigInteger(
			"FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFF",
			16);
	final BigInteger gma = new BigInteger(
			"FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFC",
			16);
	final BigInteger gmb = new BigInteger(
			"28E9FA9E9D9F5E344D5A9E4BCF6509A7F39789F515AB8F92DDBCBD414D940E93",
			16);

	final ECCurve gmec256 = new ECCurve.Fp(gmp, gma, gmb);
	final BigInteger gmgx = new BigInteger(
			"32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7",
			16);
	final BigInteger gmgy = new BigInteger(
			"BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0",
			16);

	final ECPoint gmg = gmec256.createPoint(gmgx, gmgy, false);

	final BigInteger gmn = new BigInteger(
			"FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFF7203DF6B21C6052B53BBF40939D54123",
			16);
}
