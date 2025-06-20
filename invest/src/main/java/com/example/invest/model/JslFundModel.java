package com.example.invest.model;

public class JslFundModel {
    public JslFundModel() {}
    public static JslFundModel tran(YunFundModel model){
        JslFundModel model1 = new JslFundModel();
        model1.setFund_id(model.getFundCode());
        model1.setFund_nm(model.getFundName());
        model1.setPrice(String.valueOf(model.getCurrentPrice()));
        model1.setPrice_dt(model.getUpStringTime());
        model1.setIncrease_rt(String.valueOf(model.getIncreaseRt()));
        model1.setVolume(String.valueOf(model.getAmount()));
        model1.setLast_time(model.getIntoTime());
        model1.setAmount(model.getType());
        model1.setAmount_incr(String.valueOf(model.getIncrShare()));
        model1.setAmount_increase_rt(String.valueOf(model.getDiscount()));
        model1.setNav_dt(model.getUpStringTime());
        model1.setDiscount_rt(String.valueOf(model.getDiscount()));
        model1.setEstimate_value(String.valueOf(model.getEstimateLimit()));
        return model1;
    }
    private String fund_id;
    private String fund_nm;
    private String asset_ratio;
    private String price;
    private String price_dt;
    private String increase_rt;
    private String volume;
    private String stock_volume;
    private String last_time;
    private int amount;
    private String amount_incr;
    private String amount_increase_rt;
    private String fund_nav;
    private String nav_dt;
    private String estimate_value;
    private String est_val_dt;
    private String last_est_time;
    private String discount_rt;
    private String index_id;
    private String index_nm;
    private String index_increase_rt;
    private String idx_price_dt;
    private String apply_fee;
    private String apply_status;
    private String apply_fee_tips;
    private String redeem_fee;
    private String redeem_status;
    private String redeem_fee_tips;
    private String min_amt;
    private String notes;
    private String issuer_nm;
    private String urls;
    private int owned;
    private int holded;
    private String apply_redeem_status;
    private String amount_incr_tips;
    private String turnover_rt;
    public void setFund_id(String fund_id) { this.fund_id = fund_id; }
    public String getFund_id() { return fund_id; }
    public void setFund_nm(String fund_nm) { this.fund_nm = fund_nm; }
    public String getFund_nm() { return fund_nm; }
    public void setAsset_ratio(String asset_ratio) { this.asset_ratio = asset_ratio; }
    public String getAsset_ratio() { return asset_ratio; }
    public void setPrice(String price) { this.price = price; }
    public String getPrice() { return price; }
    public void setPrice_dt(String price_dt) { this.price_dt = price_dt; }
    public String getPrice_dt() { return price_dt; }
    public void setIncrease_rt(String increase_rt) { this.increase_rt = increase_rt; }
    public String getIncrease_rt() { return increase_rt; }
    public void setVolume(String volume) { this.volume = volume; }
    public String getVolume() { return volume; }
    public void setStock_volume(String stock_volume) { this.stock_volume = stock_volume; }
    public String getStock_volume() { return stock_volume; }
    public void setLast_time(String last_time) { this.last_time = last_time; }
    public String getLast_time() { return last_time; }
    public void setAmount(int amount) { this.amount = amount; }
    public int getAmount() { return amount; }
    public void setAmount_incr(String amount_incr) { this.amount_incr = amount_incr; }
    public String getAmount_incr() { return amount_incr; }
    public void setAmount_increase_rt(String amount_increase_rt) { this.amount_increase_rt = amount_increase_rt; }
    public String getAmount_increase_rt() { return amount_increase_rt; }
    public void setFund_nav(String fund_nav) { this.fund_nav = fund_nav; }
    public String getFund_nav() { return fund_nav; }
    public void setNav_dt(String nav_dt) { this.nav_dt = nav_dt; }
    public String getNav_dt() { return nav_dt; }
    public void setEstimate_value(String estimate_value) { this.estimate_value = estimate_value; }
    public String getEstimate_value() { return estimate_value; }
    public void setEst_val_dt(String est_val_dt) { this.est_val_dt = est_val_dt; }
    public String getEst_val_dt() { return est_val_dt; }
    public void setLast_est_time(String last_est_time) { this.last_est_time = last_est_time; }
    public String getLast_est_time() { return last_est_time; }
    public void setDiscount_rt(String discount_rt) { this.discount_rt = discount_rt; }
    public String getDiscount_rt() { return discount_rt; }
    public void setIndex_id(String index_id) { this.index_id = index_id; }
    public String getIndex_id() { return index_id; }
    public void setIndex_nm(String index_nm) { this.index_nm = index_nm; }
    public String getIndex_nm() { return index_nm; }
    public void setIndex_increase_rt(String index_increase_rt) { this.index_increase_rt = index_increase_rt; }
    public String getIndex_increase_rt() { return index_increase_rt; }
    public void setIdx_price_dt(String idx_price_dt) { this.idx_price_dt = idx_price_dt; }
    public String getIdx_price_dt() { return idx_price_dt; }
    public void setApply_fee(String apply_fee) { this.apply_fee = apply_fee; }
    public String getApply_fee() { return apply_fee; }
    public void setApply_status(String apply_status) { this.apply_status = apply_status; }
    public String getApply_status() { return apply_status; }
    public void setApply_fee_tips(String apply_fee_tips) { this.apply_fee_tips = apply_fee_tips; }
    public String getApply_fee_tips() { return apply_fee_tips; }
    public void setRedeem_fee(String redeem_fee) { this.redeem_fee = redeem_fee; }
    public String getRedeem_fee() { return redeem_fee; }
    public void setRedeem_status(String redeem_status) { this.redeem_status = redeem_status; }
    public String getRedeem_status() { return redeem_status; }
    public void setRedeem_fee_tips(String redeem_fee_tips) { this.redeem_fee_tips = redeem_fee_tips; }
    public String getRedeem_fee_tips() { return redeem_fee_tips; }
    public void setMin_amt(String min_amt) { this.min_amt = min_amt; }
    public String getMin_amt() { return min_amt; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getNotes() { return notes; }
    public void setIssuer_nm(String issuer_nm) { this.issuer_nm = issuer_nm; }
    public String getIssuer_nm() { return issuer_nm; }
    public void setUrls(String urls) { this.urls = urls; }
    public String getUrls() { return urls; }
    public void setOwned(int owned) { this.owned = owned; }
    public int getOwned() { return owned; }
    public void setHolded(int holded) { this.holded = holded; }
    public int getHolded() { return holded; }
    public void setApply_redeem_status(String apply_redeem_status) { this.apply_redeem_status = apply_redeem_status; }
    public String getApply_redeem_status() { return apply_redeem_status; }
    public void setAmount_incr_tips(String amount_incr_tips) { this.amount_incr_tips = amount_incr_tips; }
    public String getAmount_incr_tips() { return amount_incr_tips; }
    public void setTurnover_rt(String turnover_rt) { this.turnover_rt = turnover_rt; }
    public String getTurnover_rt() { return turnover_rt; }
} 