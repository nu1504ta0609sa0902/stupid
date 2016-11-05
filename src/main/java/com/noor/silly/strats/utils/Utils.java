package com.noor.silly.strats.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class Utils {

	static Map<String, List<String>> mapOfDataToFileKey = new HashMap<String, List<String>>();

	public static final Logger log = LoggerFactory.getLogger(Utils.class);

	public static List<String> getRandomDataWithXPercentOfWins(int winsEveryX, int numberOfData, int maxWinCount) {
		List<String> listOfDataItems = new ArrayList<String>();

		// int maxWinCount = (int)((1.0 / winsEveryX) * numberOfData);
		// maxWinCount = maxWinCount + someRandomValueBetween(1,3, maxWinCount,
		// numberOfData);
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



	public static String getRandomWLValue(int winsEveryX) {
		int randomNumber = new Random().nextInt(9000) + 1000;
		int reminder = randomNumber % winsEveryX;

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

	/**
	 * Read a list of items from file
	 * @return
	 * @param monthCount
	 * @param numberOfDaysPerMonth
	 */
	public static List<String> getDataFromFile(int monthCount, int numberOfDaysPerMonth, String fileKey) {
		int from = 0;
		int to = numberOfDaysPerMonth;
		if(monthCount > 1){
			from = (monthCount-1) * numberOfDaysPerMonth;
			to = monthCount * numberOfDaysPerMonth;
		}

		List<String> data = mapOfDataToFileKey.get(fileKey);

		if(data == null) {
			data = new ArrayList<String>();

			String root = new File("").getAbsolutePath();
			String location = root + File.separator + "src/test/resources" + "/data/dataWL" + fileKey + ".txt";
			location = location.replace("SillyStrats1", "sillystratsm1");
			//Read file content
			try {
				FileReader fr = new FileReader(new File(location));
				BufferedReader br = new BufferedReader(fr);

				String line = "";
				while ((line = br.readLine()) != null) {
					if (line.equals("W") || line.equals("L"))
						data.add(line);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			mapOfDataToFileKey.put(fileKey, data);

			System.out.println("Items count in file : " + data.size());
		}

		//Get data between a range
		List<String> su = new ArrayList<String>();
		try {
			su = data.subList(from, to);
		}catch(IndexOutOfBoundsException e){
			if(from < data.size() - 1)
			su = data.subList(from, data.size()-1);
		}

		return su;
	}



	public static double getPercentToXDecimal(double value, int numberOfDecimalPlace){
		String vv = String.format("%." + numberOfDecimalPlace + "f", value);
		return Double.parseDouble(vv);
	}
}
