package com.noor.silly.strats.strategy;

import com.noor.silly.strats.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tayyibah on 05/11/2016.
 *
 * Aim is to create set of Buy/Sell orders and try and do it for real
 *
 * Or check what would happen if you follow it with real data.
 *
 *
 */
public class BuySellOrder {

    public static final Logger log = LoggerFactory.getLogger(BuySellOrder.class);

    public static void main(String [] args){
        int numberOfInstruments = 5;
        int daysPerMonth = 21;

        List<List<String>> allBuySellOrder = new ArrayList<List<String>>();

        for(int c = 0; c < numberOfInstruments; c++) {
            List<String> buySellOrders = Utils.generateRandomBuySellOrders(daysPerMonth);
            allBuySellOrder.add(buySellOrders);
        }

        int totalBuy = 0;
        int totalSell = 0;

        for(int c = 0; c < allBuySellOrder.size(); c++) {
            List<String> buySellOrders = allBuySellOrder.get(c);
            int buyOrders = printReport(buySellOrders);
            totalBuy = totalBuy + buyOrders;
            totalSell = totalSell + (buySellOrders.size() - buyOrders);
        }

        log.info("\n---------------------------------");
        log.info("Avg Buy Orders : " + (totalBuy/numberOfInstruments));
        log.info("Avg Sell Orders : " + (totalSell/numberOfInstruments));
        log.info("---------------------------------");


        //printAvgAtSpecifiedPosition(1, allBuySellOrder);

        printAvgAtAllPositions(allBuySellOrder);
    }

    private static void printAvgAtAllPositions(List<List<String>> allBuySellOrder){

        List<String> buySellRecommendedRandom = new ArrayList<String>();

        for(int c = 1; c <= allBuySellOrder.get(0).size(); c++) {
            String recommended = printAvgAtSpecifiedPosition(c, allBuySellOrder);
            buySellRecommendedRandom.add(recommended);
        }

        printRandomRecommendation(buySellRecommendedRandom);

    }

    private static void printRandomRecommendation(List<String> buySellRecommendedRandom) {

        log.info("\n---------------------------------");
        log.info("Recommended Overall : " + buySellRecommendedRandom);
        int buyCount = 0;
        String buyOrSellOrder = "Buy";

        for(int c = 0; c < buySellRecommendedRandom.size(); c++) {
            String buyOrSell = buySellRecommendedRandom.get(c);
            if(buyOrSell.equals("Buy")){
                buyCount++;
            }
        }

        int sellCount = 0;
        log.info("Buy Orders : " + buyCount);
        log.info("Sell Orders : " + (sellCount = (buySellRecommendedRandom.size() - buyCount)));

        log.info("---------------------------------");
    }

    private static String printAvgAtSpecifiedPosition(int i, List<List<String>> allBuySellOrder) {
        int buyCount = 0;
        String buyOrSellOrder = "Buy";
        log.info("\n---------------------------------");

        for(int c = 0; c < allBuySellOrder.size(); c++) {
            List<String> buySellOrders = allBuySellOrder.get(c);
            String buyOrSell = buySellOrders.get(i-1);
            log.info(buyOrSell);
            if(buyOrSell.equals("Buy")){
                buyCount++;
            }
        }

        int sellCount = 0;
        log.info("Number Of Buy Sell At Position : " + i);
        log.info("Buy Orders : " + buyCount);
        log.info("Sell Orders : " + (sellCount = (allBuySellOrder.size() - buyCount)));

        if(buyCount > sellCount){
            log.info("Recommended : Buy");
        }else{
            log.info("Recommended : Sell");
            buyOrSellOrder = "Sell";
        }
        log.info("---------------------------------");

        return buyOrSellOrder;
    }

    private static int printReport(List<String> buySellOrders) {
        int numberOfBuyOrders = 0;

        for(String x: buySellOrders){
            if(x.equals("Buy")){
                numberOfBuyOrders++;
            }
        }

        log.info("---------------------------------");
        log.info("Data : " + buySellOrders);
        log.info("Buy Order Count : " + numberOfBuyOrders);
        log.info("Sell Order Count : " + ( buySellOrders.size() - numberOfBuyOrders) );
        log.info("---------------------------------");

        return numberOfBuyOrders;
    }
}
