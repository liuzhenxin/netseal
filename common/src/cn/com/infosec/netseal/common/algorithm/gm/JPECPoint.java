package cn.com.infosec.netseal.common.algorithm.gm;


import java.math.BigInteger;

import cn.com.infosec.asn1.x9.X9IntegerConverter;
import cn.com.infosec.math.ec.ECCurve;
import cn.com.infosec.math.ec.ECFieldElement;
import cn.com.infosec.math.ec.ECPoint;


/**
 * base class for points on elliptic curves.
 */
public abstract class JPECPoint {
	ECCurve curve;
	ECFieldElement x;
	ECFieldElement y;
	ECFieldElement z;

	protected boolean withCompression;
	protected JPECPoint infinity = null;

//	protected JPECMultiplier multiplier = null;
//
//	protected JPPreCompInfo preCompInfo = null;

	private static X9IntegerConverter converter = new X9IntegerConverter();

	protected JPECPoint(ECCurve curve, ECFieldElement x, ECFieldElement y,
			ECFieldElement z) {
		this.curve = curve;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public JPECPoint(ECPoint point) {
		if (point.isInfinity()) {
			this.curve = null;
			this.x = null;
			this.y = null;
			this.z = null;
		} else {
			this.curve = point.getCurve();
			this.x = point.getX();
			this.y = point.getY();
			this.z = new ECFieldElement.Fp(SM2GMParameter.gmp, BigInteger.ONE);
		}
	}

	public ECCurve getCurve() {
		return curve;
	}

	public ECFieldElement getX() {
		return x;
	}

	public ECFieldElement getY() {
		return y;
	}

	public ECFieldElement getZ() {
		return z;
	}

	public boolean isInfinity() {
		return x == null && y == null && z == null;
	}

	public JPECPoint getInfinity() {
		return new JPECPoint.Fp(SM2GMParameter.gmec256.getInfinity());
	}

	public boolean isCompressed() {
		return withCompression;
	}

	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}

		if (!(other instanceof JPECPoint)) {
			return false;
		}

		JPECPoint o = (JPECPoint) other;

		if (this.isInfinity()) {
			return o.isInfinity();
		}

		return x.equals(o.x) && y.equals(o.y);
	}

	public int hashCode() {
		if (this.isInfinity()) {
			return 0;
		}

		return x.hashCode() ^ y.hashCode();
	}

//	/**
//	 * Sets the <code>PreCompInfo</code>. Used by <code>ECMultiplier</code>s to
//	 * save the precomputation for this <code>ECPoint</code> to store the
//	 * precomputation result for use by subsequent multiplication.
//	 * 
//	 * @param preCompInfo
//	 *            The values precomputed by the <code>ECMultiplier</code>.
//	 */
//	void setPreCompInfo(JPPreCompInfo preCompInfo) {
//		this.preCompInfo = preCompInfo;
//	}

	public abstract byte[] getEncoded();

	public abstract JPECPoint add(JPECPoint b);

	public abstract JPECPoint subtract(JPECPoint b);

	public abstract JPECPoint negate();

	public abstract JPECPoint twice();

//	/**
//	 * Sets the default <code>ECMultiplier</code>, unless already set.
//	 */
//	synchronized void assertECMultiplier() {
//		if (this.multiplier == null) {
//			this.multiplier = new JPWNafMultiplier();
//		}
//	}

	/**
	 * convert to ECPoint
	 * @return an ECPoint
	 */
	ECPoint toECPoint() {
		BigInteger ax = null, ay = null;
		BigInteger zi = z.toBigInteger().modInverse(SM2GMParameter.gmp);// zi=1/z
		BigInteger zi2 = zi.multiply(zi);
		ax = x.toBigInteger().multiply(zi2).mod(SM2GMParameter.gmp);// x=x/z^2
		ay = y.toBigInteger().multiply(zi).multiply(zi2).mod(SM2GMParameter.gmp); // y=y/z^3

		return SM2GMParameter.gmec256.createPoint(ax, ay, false);
	}

//	/**
//	 * Multiplies this <code>JPECPoint</code> by the given number.
//	 * 
//	 * @param k
//	 *            The multiplicator.
//	 * @return <code>k * this</code>.
//	 */
//	public JPECPoint multiply(BigInteger k) {
//		if (k.signum() < 0) {
//			throw new IllegalArgumentException(
//					"The multiplicator cannot be negative");
//		}
//
//		if (this.isInfinity()) {
//			return this;
//		}
//
//		if (k.signum() == 0) {
//			return infinity;
//		}
//		assertECMultiplier();
//		return this.multiplier.multiply(this, k, preCompInfo);
//	}

	/**
	 * Elliptic curve points over Fp
	 */
	public static class Fp extends JPECPoint {

		/**
		 * Create a point which encodes with point compression.
		 * 
		 * @param curve
		 *            the curve to use
		 * @param x
		 *            jacobi x co-ordinate
		 * @param y
		 *            jacobi y co-ordinate
		 * @param z
		 *            jacobi z co-ordinate
		 */

		public Fp(ECCurve curve, ECFieldElement x, ECFieldElement y,
				ECFieldElement z) {
			this(curve, x, y, z, false);
		}

		public Fp(ECCurve curve, ECFieldElement x, ECFieldElement y) {
			this(curve, x, y, SM2Constants.ONE, false);
		}

		public Fp(ECPoint point) {
			super(point);
		}

		/**
		 * Create a point that encodes with or without point compresion.
		 * 
		 * @param curve
		 *            the curve to use
		 * @param x
		 *            jacobi x co-ordinate
		 * @param y
		 *            jacobi y co-ordinate
		 * @param z
		 *            jacobi z co-ordinate
		 * @param withCompression
		 *            if true encode with point compression
		 */
		public Fp(ECCurve curve, ECFieldElement x, ECFieldElement y,
				ECFieldElement z, boolean withCompression) {
			super(curve, x, y, z);

			if ((x != null && y == null) || (x == null && y != null)) {
				throw new IllegalArgumentException(
						"Exactly one of the field elements is null");
			}

			this.withCompression = withCompression;
		}

		/**
		 * return the field element encoded with point compression. (S 4.3.6)
		 */
		public byte[] getEncoded() {
			if (this.isInfinity()) {
				return new byte[1];
			}
			ECPoint ecp = toECPoint();
			int qLength = converter.getByteLength(ecp.getX());// x);

			if (withCompression) {
				byte PC;

				if (ecp.getY().toBigInteger().testBit(0)) {
					PC = 0x03;
				} else {
					PC = 0x02;
				}

				byte[] X = converter.integerToBytes(ecp.getX().toBigInteger(),
						qLength);
				byte[] PO = new byte[X.length + 1];

				PO[0] = PC;
				System.arraycopy(X, 0, PO, 1, X.length);

				return PO;
			} else {
				byte[] X = converter.integerToBytes(ecp.getX().toBigInteger(),
						qLength);
				byte[] Y = converter.integerToBytes(ecp.getY().toBigInteger(),
						qLength);
				byte[] PO = new byte[X.length + Y.length + 1];

				PO[0] = 0x04;
				System.arraycopy(X, 0, PO, 1, X.length);
				System.arraycopy(Y, 0, PO, X.length + 1, Y.length);

				return PO;
			}
		}

		public JPECPoint add(JPECPoint b) {
			if (this.isInfinity()) {
				return b;
			}

			if (b.isInfinity()) {
				return this;
			}

			// Check if b = this or b = -this
			if (this.x.equals(b.x)) {
				if (this.y.equals(b.y)) {
					// this = b, i.e. this must be doubled
					return this.twice();
				}

				// this = -b, i.e. the result is the point at infinity
				return infinity;
			}

			//2017/11/8 by wangzb
			ECFieldElement T1=this.z.square();
			ECFieldElement T2=T1.multiply(this.z);
			T1=T1.multiply(b.getX());
			T2=T2.multiply(b.getY());
			T1=T1.subtract(this.x);
			T2=T2.subtract(this.getY());
			if(ECFieldElement.ZERO.equals(T1.toBigInteger())){
				if(ECFieldElement.ZERO.equals(T2.toBigInteger())){
					return	b.twice();
				}else{
					return  this.getInfinity();
				}
			}
			//2017/11/8 end

			/*
			 * lambda1=x1*z2^2 lambda2=x2*z1^2 lambda3=lambda1-lambda2
			 * lambda4=y1*z2^3 lambda5=y2*z1^3 lambda6=lambda4-lambda5
			 * lambda7=lambda1+lambda2 lambda8=lambda4+lambda5
			 * x3=lambda6^2-lambda7*lambda3^2 lambda9=lambda7*lambda3^2-2*x3
			 * y3=(lambda9*lambda6-lambda8*lambda3^3)/2 z3=z1*z2*lambda3
			 */
			ECFieldElement z1s = this.z.square();
			ECFieldElement z2s = b.getZ().square();
			ECFieldElement lbd1 = this.x.multiply(z2s);// x1*z2^2
			ECFieldElement lbd2 = b.getX().multiply(z1s);// x2*z1^2
			ECFieldElement lbd3 = lbd1.subtract(lbd2); // lambda1-lambda2
			ECFieldElement lbd4 = this.y.multiply(z2s).multiply(b.getZ());// y1*z2^3
			ECFieldElement lbd5 = b.getY().multiply(z1s).multiply(this.z);// y2*z1^3
			ECFieldElement lbd6 = lbd4.subtract(lbd5);
			ECFieldElement lbd7 = lbd1.add(lbd2);
			ECFieldElement lbd8 = lbd4.add(lbd5);
			ECFieldElement x3 = lbd6.square().subtract(
					lbd7.multiply(lbd3.square()));
			ECFieldElement lbd9 = lbd7.multiply(lbd3.square()).subtract(x3)
					.subtract(x3);
			ECFieldElement y3 = lbd9.multiply(lbd6)
					.subtract(lbd8.multiply(lbd3.square()).multiply(lbd3))
					.divide(SM2Constants.TWO);
			ECFieldElement z3 = this.z.multiply(b.getZ()).multiply(lbd3);

			return new Fp(curve, x3, y3, z3);
		}

		public JPECPoint twice() {
			if (this.isInfinity()) {
				// Twice identity element (point at infinity) is identity
				return this;
			}

			/*
			 * lambda1=3*x1^2+a*z1^4 lambda2=4x1y1^2 lambda3=8y1^4
			 * x3=lambda1^2-2lambda2 y3=lambda1(lambda2-x3)-lambda3 z3=2y1z1
			 */

			ECFieldElement lbd1 = SM2Constants.THREE.multiply(this.x.square())
					.add(this.z.square().square().multiply(SM2Constants.A));
			ECFieldElement lbd2 = SM2Constants.FOUR.multiply(this.x).multiply(
					this.y.square());
			ECFieldElement lbd3 = SM2Constants.EIGHT.multiply(this.y.square()
					.square());
			ECFieldElement x3 = lbd1.square().subtract(
					SM2Constants.TWO.multiply(lbd2));
			ECFieldElement y3 = lbd2.subtract(x3).multiply(lbd1).subtract(lbd3);
			ECFieldElement z3 = SM2Constants.TWO.multiply(this.y).multiply(
					this.z);

			return new Fp(curve, x3, y3, z3);
		}

		public JPECPoint subtract(JPECPoint b) {
			if (b.isInfinity()) {
				return this;
			}

			// Add -b
			return add(b.negate());
		}

		public JPECPoint negate() {
			return new JPECPoint.Fp(curve, x, y.negate(), z);
		}

//		/**
//		 * Sets the default <code>ECMultiplier</code>, unless already set.
//		 */
//		synchronized void assertECMultiplier() {
//			if (this.multiplier == null) {
//				this.multiplier = new JPWNafMultiplier();
//			}
//		}
	}
}
