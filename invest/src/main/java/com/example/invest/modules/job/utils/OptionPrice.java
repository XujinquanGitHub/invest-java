package com.example.invest.modules.job.utils;

import java.math.BigDecimal;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.special.Erf;
import org.apache.commons.math3.util.FastMath;

/**
 * @program: invest
 * @description: 期权价格计算
 * @author: 许金泉
 * @create: 2023-05-19 16:24
 **/
public class OptionPrice {

    /** &radic;(2) */
    private static final double SQRT2 = FastMath.sqrt(2.0);
    private final static String CALL = "call";
    private final static String PUT = "put";

    public static CalOptionModel cal(CalOptionModel model){
        double s = model.getCurrent(), x = model.getTarget(), t = model.getYearNum(), r = model.getLx(), sigma = model.getBd();
        OptionPrice mc = new OptionPrice(model.getLx(),model.getBd(), model.getLx(),model.getCurrent(),model.getTarget(), 20000,model.getYearNum());
        //B-S-M模型
        double bsm = priceBSM(s, x, t, r, sigma, CALL);
        BigDecimal multiply = new BigDecimal(100)
                .divide(new BigDecimal(model.getTarget()), 15, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal(bsm));
        model.setBsmValue(multiply);
        //蒙特卡罗模拟
        double[] simulationPrice = mc.monteCarloSimulation();
        BigDecimal bigDecimal = mc.euroOption(simulationPrice, CALL);
        BigDecimal mtValue = new BigDecimal(100)
                .divide(new BigDecimal(model.getTarget()), 15, BigDecimal.ROUND_HALF_UP)
                .multiply(bigDecimal);
        model.setMtValue(mtValue);

        return model;
    }

    /**
     * 获取看涨期权价格,持有成本b取0
     * 期货价格 S，执行价格X,有效天数T 即相隔天数，r无风险利率 ，sigma年度波动率
     */
    public static double priceBSM(double s, double x, double t, double r, double sigma, String callOrPut) {
        final double powER = Math.pow(Math.E, -r * t);
        double d1 = getD1(s, x, t, sigma, r);
        if(StringUtils.equals(CALL, callOrPut)) {
            return s * norm_s_dist(d1, true) - x * powER * norm_s_dist(getD2(d1, t, sigma), true);
        }
        //看跌
        return x*powER*norm_s_dist(-getD2(d1,t,sigma), true)-s*norm_s_dist(-d1, true);
    }

    /**
     * 取d1计算
     * @param s 期货价格
     * @param x 执行价格
     * @param t 有效天数
     * @param sigma 年化波动率
     * @return d1
     */
    private static double getD1(double s, double x, double t, double sigma, double r) {
        return (Math.log(s / x) + (r + 0.5 * Math.pow(sigma, 2)) * t) / (sigma * Math.sqrt(t));
    }

    /**
     * 获取d2
     * @param d1 ：计算的d1值
     * @param t：相隔天数
     * @param sigma ：年化波动率
     * @return ：d2
     */
    private static double getD2(double d1, double t, double sigma) {
        return d1 - sigma * (Math.sqrt(t));
    }

    protected double miu, sigma, r, s, x;
    Integer count;//模拟次数
    double term;//年化时间
    OptionPrice(double miu, double sigma, double r, double s,
            double x, Integer count, double term) {
        this.miu = miu;  //miu 代表 资产期望报酬率 ，风险 中性 世界 等于无风险利率
        this.sigma = sigma;  //sigma 代表 标的 资产价格波动率
        this.r = r;  //r 代表无风险利率
        this.s = s;  //s 代表标的 资产即期价格
        this.x = x;  //x 代表 标的资产到期 执行价格
        this.count = count;
        this.term = term;
    }

    /**
     * 蒙特卡洛价格模拟
     * @return
     */
    public double[] monteCarloSimulation() {
        double[] simulationPrice = new double[count];
        for (int j = 0; j < count; j++) {
            int i=0;
            double epsilon = norm_s_inv(Math.random());
            simulationPrice[j] = s * Math.exp((miu-0.5 * Math.pow(sigma, 2)) * (term) + sigma * epsilon* Math.sqrt(term));
        }
        return simulationPrice;
    }

    /**
     * 欧式期权收益的贴现
     * @param simulationPrice
     * @return
     */
    public BigDecimal euroOption(double[] simulationPrice, String callOrPut) {
        double aos = 0.0;
        for (int j = 0; j < count; j++) {
            double repay = StringUtils.equals(CALL, callOrPut)?simulationPrice[j] - x: x-simulationPrice[j];
            aos += Math.exp(-r * term) * Math.max(repay, 0);
        }
        aos = (aos / count);
        return new BigDecimal(aos);
    }

    /**
     * 正态分布计算：概率计算函数
     * @param mean 分布的算术平均值
     * @param standardDeviation 分布的标准偏差
     * @param x 需要计算其分布的数值
     * @param cumulative  确定函数形式的逻辑值。如果累积性为 TRUE, 则为 标准。返回累积分布函数;  FALSE, 则返回概率密度函数
     * @return
     */
    public static double norm_dist(double mean, double standardDeviation, double x, boolean cumulative) {
        if(cumulative) {
            return new NormalDistribution(mean, standardDeviation).cumulativeProbability(x);
        }
        return new NormalDistribution(mean, standardDeviation).density(x);
    }

    /**
     * 计算正态累积分布的反函数： 函数值计算
     * @param mean 分布的算术平均值
     * @param standardDeviation 分布的标准偏差
     * @param p 概率
     * @return
     */
    public static double norm_inv(double mean, double standardDeviation, double p) {
        return mean + standardDeviation * SQRT2 * Erf.erfInv(2 * p - 1);
    }

    /**
     * 标准正态分布
     * @param z
     * @param cumulative
     * @return
     */
    public static double norm_s_dist(double z, boolean cumulative) {
        return norm_dist(0, 1, z, cumulative);
    }

    /**
     * 标准正态分布
     * @param z
     * @return
     */
    public static double norm_s_inv(double z) {
        return norm_inv(0, 1, z);
    }
} 