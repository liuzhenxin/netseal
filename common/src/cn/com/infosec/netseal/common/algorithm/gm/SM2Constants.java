package cn.com.infosec.netseal.common.algorithm.gm;


import java.math.BigInteger;

import cn.com.infosec.math.ec.ECConstants;
import cn.com.infosec.math.ec.ECFieldElement;


public interface SM2Constants {

	static ECFieldElement ONE = new ECFieldElement.Fp(SM2GMParameter.gmp, ECConstants.ONE);
	static ECFieldElement TWO = new ECFieldElement.Fp(SM2GMParameter.gmp, ECConstants.TWO);
	static ECFieldElement FOUR = new ECFieldElement.Fp(SM2GMParameter.gmp, BigInteger.valueOf(4));
	static ECFieldElement THREE = new ECFieldElement.Fp(SM2GMParameter.gmp,	BigInteger.valueOf(3));
	static ECFieldElement EIGHT = new ECFieldElement.Fp(SM2GMParameter.gmp,	BigInteger.valueOf(8));
	static ECFieldElement A = new ECFieldElement.Fp(SM2GMParameter.gmp, SM2GMParameter.gma);
	static JPECPoint jg = new JPECPoint.Fp(SM2GMParameter.gmg);
}
