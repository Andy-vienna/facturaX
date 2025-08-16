package org.andy.code.misc;

import java.math.BigDecimal;

public class BD {
	
	private BD() {} // Verhindert Instanziierung

	public static final BigDecimal M_ONE       = new BigDecimal("-1").setScale(2);
    public static final BigDecimal ZERO        = BigDecimal.ZERO.setScale(2);
    public static final BigDecimal DOT_TWO     = new BigDecimal("0.2").setScale(2);
    public static final BigDecimal ONE         = BigDecimal.ONE.setScale(2);
    public static final BigDecimal TWO         = BigDecimal.TWO.setScale(2);
    public static final BigDecimal THREE       = new BigDecimal("3").setScale(2);
    public static final BigDecimal FOUR        = new BigDecimal("4").setScale(2);
    public static final BigDecimal FIVE        = new BigDecimal("5").setScale(2);
    public static final BigDecimal TEN         = BigDecimal.TEN.setScale(2);
    public static final BigDecimal TWENTY      = new BigDecimal("20").setScale(2);
    public static final BigDecimal HUNDRED     = new BigDecimal("100").setScale(2);
    public static final BigDecimal THOUSAND    = new BigDecimal("1000").setScale(2);

}
