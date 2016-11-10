package com.noor.silly.strats.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class Utils {


	public static final Logger log = LoggerFactory.getLogger(Utils.class);

	public static List<String> getRandomDataWithXPercentOfWins(double winsEveryX, int numberOfData, int maxWinCount) {
		List<String> listOfDataItems = new ArrayList<String>();

		int numberOfWinsGenerated = 0;

		do {
			String wl = "L";
			if (numberOfWinsGenerated < maxWinCount) {
				wl = getRandomWLValue(winsEveryX);
				if (wl.equals("W")) {
					numberOfWinsGenerated++;
				}
			}

			listOfDataItems.add(wl);

		} while (listOfDataItems.size() < numberOfData);

		log.info("-----");
		log.info("Data : " + listOfDataItems);
		log.info("W=" + numberOfWinsGenerated);
		log.info("L=" + (numberOfData - numberOfWinsGenerated));
		log.info("-----");

		return listOfDataItems;
	}

	public static int someRandomValueBetween(int from, int to, double maxWinCount, double numberOfData) {
		double percent = (maxWinCount / numberOfData) * 100;
		int v = 0;
		Random r = new Random();
		int Low = from;
		int High = to;
		v = r.nextInt(High - Low) + Low;
		if (percent >= 40) {
			v = -1 * v;
		}

		// TODO Auto-generated method stub
		return v;
	}

	public static int someRandomValueBetween(int from, int to) {
		Random r = new Random();
		int v = r.nextInt((to - from)+1) + from;
		return v;
		//return getRandomNumberBetween(from, to);
	}

	public static int getRandomNumberBetween(int min, int max){
		// return (int) (Math.random()*max);  //incorrect always return zero
		int rn = (int) ((Math.random())*max);

		if(rn <= min){
			return min;
		}else{
			return rn;
		}
	}

	public static String getRandomWLValue(double winsEveryX) {
		int randomNumber = new Random().nextInt(9000) + 1000;
		double reminder = randomNumber % winsEveryX;

		if (reminder == 0) {
			return "W";
		} else {
			return "L";
		}
	}

	public static String generateRandomKey(String key) {
		int x = someRandomValueBetween(1000, 1000 * 1000);
		return key + x;
	}

	public static double getInitialRiskAmount(int groupSize, int sl, boolean calculate) {
		if (calculate) {
			double amount = 0;
			amount = (Math.pow(2, groupSize) * sl);
			return amount;
		}
		return 100;
	}


	public static double getPercentToXDecimal(double value, int numberOfDecimalPlace){
		String vv = String.format("%." + numberOfDecimalPlace + "f", value);
		return Double.parseDouble(vv);
	}

	public static List<String> generateRandomBuySellOrders(int daysPerMonth){

		List<String> listOfBuySellOrders = new ArrayList<String>();

		for(int c = 0; c < daysPerMonth; c++) {
			long time = System.nanoTime();
			try {
				Thread.sleep(11);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (time % 2 == 0) {
				listOfBuySellOrders.add("Buy");
			}else{
				listOfBuySellOrders.add("Sell");
			}
		}

		return listOfBuySellOrders;
	}
}
