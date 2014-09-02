package com.intelliReader.model;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Author: Ting Chen
 * Date: 9/1/14
 * Time: 4:45 PM
 */
public class ModelUtil {
    public static int timeLapseInDays(Date prev, Date now)
    {
        assert now.after(prev) || prev.equals(now);
        long timeLapseInMiscSec = now.getTime() - prev.getTime();
        return (int) (timeLapseInMiscSec / (1000 * 3600 * 24));
    }

    /**
     *
     * @param score a raw score without considering time lapse info
     * @param prev the date when score is computed
     * @param now the current date
     * @return the score after factoring in the number of passing days using a exponential decay function
     */
    public static double exponentialDecayScore(double score, Date prev, Date now)
    {
        return score * Math.pow(0.5,timeLapseInDays(prev,now));
    }



}
