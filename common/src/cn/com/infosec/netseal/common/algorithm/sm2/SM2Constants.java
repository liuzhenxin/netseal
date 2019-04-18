package cn.com.infosec.netseal.common.algorithm.sm2;

import java.math.BigInteger;

import cn.com.infosec.math.ec.ECConstants;
import cn.com.infosec.math.ec.ECFieldElement;

public interface SM2Constants {

	static ECFieldElement ONE = new ECFieldElement.Fp(SM2.gmp, ECConstants.ONE);
	static ECFieldElement TWO = new ECFieldElement.Fp(SM2.gmp, ECConstants.TWO);
	static ECFieldElement FOUR = new ECFieldElement.Fp(SM2.gmp,
			BigInteger.valueOf(4));
	static ECFieldElement THREE = new ECFieldElement.Fp(SM2.gmp,
			BigInteger.valueOf(3));
	static ECFieldElement EIGHT = new ECFieldElement.Fp(SM2.gmp,
			BigInteger.valueOf(8));
	static ECFieldElement A = new ECFieldElement.Fp(SM2.gmp, SM2.gma);
	static JPECPoint jg = new JPECPoint.Fp(SM2.gmg);
}
