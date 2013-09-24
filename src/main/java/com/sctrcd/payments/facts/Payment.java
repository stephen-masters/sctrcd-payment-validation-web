package com.sctrcd.payments.facts;

import java.math.BigDecimal;

public class Payment {

    private String sellCurrency;
    private String buyCurrency;
    private BigDecimal sellAmount;
    private BigDecimal buyAmount;
    private Leg fixedLeg;
    private BigDecimal rate;
    private String iban;
    private String bic;

    public Payment() {
    }

    public Payment(String sellCurrency, String buyCurrency) {
        this.sellCurrency = sellCurrency;
        this.buyCurrency = buyCurrency;
    }

    public Payment(String sellCurrency, String buyCurrency,
            BigDecimal sellAmount, BigDecimal buyAmount, Leg fixedLeg) {
        this.sellCurrency = sellCurrency;
        this.buyCurrency = buyCurrency;
        this.sellAmount = sellAmount;
        this.buyAmount = buyAmount;
        this.fixedLeg = fixedLeg;
    }
    
    public Payment(String sellCurrency, String buyCurrency,
            BigDecimal sellAmount, BigDecimal buyAmount, Leg fixedLeg, BigDecimal rate) {
        this(sellCurrency, buyCurrency, sellAmount, buyAmount, fixedLeg);
        this.rate = rate;
    }

    public String getSellCurrency() {
        return sellCurrency;
    }

    public void setSellCurrency(String sellCurrency) {
        this.sellCurrency = sellCurrency;
    }

    public String getBuyCurrency() {
        return buyCurrency;
    }

    public void setBuyCurrency(String buyCurrency) {
        this.buyCurrency = buyCurrency;
    }

    public BigDecimal getSellAmount() {
        return sellAmount;
    }

    public void setSellAmount(BigDecimal sellAmount) {
        this.sellAmount = sellAmount;
    }

    public BigDecimal getBuyAmount() {
        return buyAmount;
    }

    public void setBuyAmount(BigDecimal buyAmount) {
        this.buyAmount = buyAmount;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    /**
     * The fixed leg indicates which leg of the transaction the customer
     * requested up front. For instance, if a customer wishes to buy 1000 Euro,
     * the fixed leg is BUY, which indicates that the final sellAmount was
     * derived from buyAmount/rate.
     * 
     * @return
     */
    public Leg getFixedLeg() {
        return fixedLeg;
    }

    public void setFixedLeg(Leg fixedLeg) {
        this.fixedLeg = fixedLeg;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public enum Leg {
        SELL, BUY;
    }

}
