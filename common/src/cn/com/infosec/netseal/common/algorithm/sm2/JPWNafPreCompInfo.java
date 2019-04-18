package cn.com.infosec.netseal.common.algorithm.sm2;


public class JPWNafPreCompInfo implements JPPreCompInfo
{
    /**
     * Array holding the precomputed <code>ECPoint</code>s used for the Window
     * NAF multiplication in <code>
     * {@link org.bouncycastle.math.ec.multiplier.WNafMultiplier.multiply()
     * WNafMultiplier.multiply()}</code>.
     */
    private JPECPoint[] preComp = null;

    /**
     * Holds an <code>ECPoint</code> representing twice(this). Used for the
     * Window NAF multiplication in <code>
     * {@link org.bouncycastle.math.ec.multiplier.WNafMultiplier.multiply()
     * WNafMultiplier.multiply()}</code>.
     */
    private JPECPoint twiceP = null;

    protected JPECPoint[] getPreComp()
    {
        return preComp;
    }

    protected void setPreComp(JPECPoint[] preComp)
    {
        this.preComp = preComp;
    }

    protected JPECPoint getTwiceP()
    {
        return twiceP;
    }

    protected void setTwiceP(JPECPoint twiceThis)
    {
        this.twiceP = twiceThis;
    }

}
