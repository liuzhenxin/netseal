package cn.com.infosec.netseal.appapi.common.algorithm.sm2;

import java.math.BigInteger;

/**
 * Interface for classes encapsulating a point multiplication algorithm for
 * <code>ECPoint</code>s.
 */
interface JPECMultiplier {
	/**
	 * Multiplies the <code>ECPoint p</code> by <code>k</code>, i.e.
	 * <code>p</code> is added <code>k</code> times to itself.
	 * 
	 * @param p
	 *            The <code>ECPoint</code> to be multiplied.
	 * @param k
	 *            The factor by which <code>p</code> i multiplied.
	 * @return <code>p</code> multiplied by <code>k</code>.
	 */
	JPECPoint multiply(JPECPoint p, BigInteger k, JPPreCompInfo preCompInfo);
}
