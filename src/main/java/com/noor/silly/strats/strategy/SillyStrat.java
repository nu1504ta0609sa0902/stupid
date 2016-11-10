package com.noor.silly.strats.strategy;

import com.noor.silly.strats.domain.Result;
import com.noor.silly.strats.utils.FileUtils;
import com.noor.silly.strats.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SillyStrat {

	public static final Logger log = LoggerFactory.getLogger(SillyStrat.class);

	public static void main(String[] args) {

		log.warn("\n\n--------------New Entry-------------");
		log.warn("Time : " + Calendar.getInstance().getTime());

		//-----------------------------------
		// READ first READ first
		//-----------------------------------

		/**
		 * Configurations for data generation
		 *
		 * Option to override simulation with
		 * real data from file
		 *
		 * See below
		 */

		//Controls configuration either SIMULATED OR FILE DATA
		boolean readDataFromFile = false;

		int groupSize = 3;		//This has no relation to consecutiveWins
		double winsEveryXNumber = 3;
		double startWithXToRisk = 160;
		double doubleOrTriple = 2;
		double halfIt = 2;

		//If we want to stop as soon as win X number of times in a row
		boolean stopOnceTargetWinsReached = true;
		int stopAtConsecutiveWins = 3;

		int numberOfInstruments = 1;	//Gbp/usd, eur/usd etc
		int monthsToSimulate = 3;
		int daysPerMonth = 21;

		//Generate win range between x and y
		int winRangeFrom = 4;
		int winRangeTo = 10;

		//-----------------------------------
		//Overide with file configuration
		//-----------------------------------

		//If we read from file, specify the file, file= real data
		String currency1 = "dji";
		String currency2 = "";
		String time = "08";	//one of 07, 08, 12 or 14=2pm
		readDataFromFile = true;
		if(readDataFromFile){
			groupSize = 3;	//This has no relation to consecutiveWins
			startWithXToRisk = 320;
			doubleOrTriple = 2; 	//Wins recorded in file is based on SL=20, W=37
			halfIt = 2;		//Loose half it or more

			numberOfInstruments = 1;
			monthsToSimulate = 22;
			daysPerMonth = 21;		//This needs to be 2 X if fileKey contains 2 currency

			stopOnceTargetWinsReached = true;
			stopAtConsecutiveWins = 3;
			//stopAtConsecutiveWins = (groupSize/2)+2;

			//Which file to read
			time = getValidTime(time, currency1);
			currency1 = time + currency1.toLowerCase();
			if(currency2 != null && !currency2.trim().equals(""))
				currency1 = currency1 + time + currency2.toLowerCase();

			log.warn("GENERATE WITH REAL DATA FROM FILE");
			log.warn("Reading Data File : dataWL"+ currency1 + ".txt");

			if(currency1.length() > 5)
				daysPerMonth = daysPerMonth * 2;

		}else{
			log.warn("GENERATE WITH SIMULATED DATA");
		}

		log.warn("Group size : " + groupSize);
		log.warn("Stop at consecutive wins : " + stopAtConsecutiveWins);

		/**
		 * File Configuraiton finished
		 */
		//-----------------------------------
		//-----------------------------------

		// Group size, X to risk at beginning of each group, if set to true
		// it will base it on SL and startWithXToRisk amount
		//boolean calculateStartXAgainstSL = true;
		//if (calculateStartXAgainstSL) {
//			int sl = 25;
//			startWithXToRisk = Utils.getInitialRiskAmount(groupSize, sl, calculateStartXAgainstSL);
//		}
		int toAdd = (int) (startWithXToRisk / 2);

		//----------------------------------------------------
		// Calculation total place holders for all instruments
		//----------------------------------------------------
		int consecutiveWins = 0;
		double allWins = 0;
		double allRisks = 0;
		int allNumberOfWinsCount = 0;
		int allNumberOfLooseCount = 0;
		List<List<String>> allData = new ArrayList<List<String>>();
		List<Result> listOfResults = new ArrayList<Result>();

		//For each instrument generate either simulated WL or read from file
		for (int nic = 0; nic < numberOfInstruments; nic++) {

			int numberOfMonthsCounter = monthsToSimulate;

			// Win rate calculation
			int numberOfDaysPerMonth = daysPerMonth;
			int monthCount = 0;
			//Do for each months
			do {
				monthCount++;
				String key = Utils.generateRandomKey("Key");

				//Generate simulated data or load from file
				List<String> dataWL = null;
				if(readDataFromFile){
					dataWL = FileUtils.getDataFromFile(monthCount, numberOfDaysPerMonth, currency1, "dataWL");
				}else{
					int maxWinCount = Utils.someRandomValueBetween(winRangeFrom, winRangeTo);
					dataWL = Utils.getRandomDataWithXPercentOfWins(winsEveryXNumber, numberOfDaysPerMonth, maxWinCount);
				}
				allData.add(dataWL);

				//Place holders: Reset before processing a set of data
				double currentValueOfXToRisk = startWithXToRisk;
				double initialRiskAmount = 0;
				double acRiskAmount = startWithXToRisk;
				double acWinsAmount = 0;
				double sumOfDifference = 0;

				int entryNumber = 1;
				int winCount = 0;
				int looseCount = 0;

				log.info(key);
				log.info("-----");

				//For each of the data set calculate
				for (String x : dataWL) {
					double preAmount = currentValueOfXToRisk;
					// Calculate doubling or halving
					if (x.equals("W")) {
						// Double it
						currentValueOfXToRisk = currentValueOfXToRisk * doubleOrTriple;
						currentValueOfXToRisk = Utils.getPercentToXDecimal(currentValueOfXToRisk, 2);
						log.info(x + ", " + preAmount + " = " + currentValueOfXToRisk);
						winCount++;
						consecutiveWins++;
					} else {
						// Half it
						currentValueOfXToRisk = currentValueOfXToRisk / halfIt;
						currentValueOfXToRisk = Utils.getPercentToXDecimal(currentValueOfXToRisk, 2);
						log.info(x + ", " + preAmount + " = " + currentValueOfXToRisk);
						looseCount++;
						if(consecutiveWins < stopAtConsecutiveWins)
							consecutiveWins = 0;
					}

					// End of the group sum up results, reset and keep track
					if (entryNumber != 1 && entryNumber % groupSize == 0) {
						double diff = currentValueOfXToRisk - initialRiskAmount;
						if (initialRiskAmount == 0) {
							diff = diff - startWithXToRisk;
						}
						diff = Utils.getPercentToXDecimal(diff, 2);

						sumOfDifference = sumOfDifference + diff;
						acWinsAmount = acWinsAmount + currentValueOfXToRisk;
						acRiskAmount = acRiskAmount + initialRiskAmount;
						log.info("Difference : " + diff);
						log.info("Win so far : " + Utils.getPercentToXDecimal(acWinsAmount - acRiskAmount, 2));

						//If we have more than half of what we started with add to the new initial start with value
						if (currentValueOfXToRisk >= startWithXToRisk / 2) {
							currentValueOfXToRisk = startWithXToRisk + toAdd;
						} else {
							currentValueOfXToRisk = startWithXToRisk;
						}
						initialRiskAmount = currentValueOfXToRisk;

						log.info("----------------------");

						//If we want to stop as soon as we win X number of times in a row
						if(stopOnceTargetWinsReached && consecutiveWins>=stopAtConsecutiveWins){
							consecutiveWins = 0;
							break;
						}

					}

					entryNumber++;

				} // Finished looping a single data set

				printResults(key, dataWL, startWithXToRisk, winCount+looseCount, winCount, acWinsAmount, acRiskAmount, sumOfDifference, "Data set summary");
				consecutiveWins = 0;
				numberOfMonthsCounter--;

				//Keep track of winnings
				allWins = allWins + acWinsAmount;
				allRisks = allRisks + acRiskAmount;
				allNumberOfWinsCount = allNumberOfWinsCount + winCount;
				allNumberOfLooseCount = allNumberOfLooseCount + looseCount;

				//Store results for reporting
				Result result = getResults(startWithXToRisk, winCount, looseCount, allWins, allRisks, acWinsAmount, acRiskAmount, dataWL, key);

				listOfResults.add(result);

			} while (numberOfMonthsCounter > 0); // Finished all data sets

		} // Finished FOR Loop

		printReportToScreen(allData, allWins, allRisks, daysPerMonth, monthsToSimulate, allNumberOfWinsCount, "Sum Of ALL",
				numberOfInstruments - 1, allNumberOfLooseCount + allNumberOfWinsCount, listOfResults);

		log.warn("--------------Entry Completed-------------");
	}

	private static String getValidTime(String time, String currency1) {

		if(currency1.equals("wins") || currency1.equals("loose")){
			time = "";
		}else{
			if(time.equals("07") || time.equals("08") || time.equals("12") || time.equals("14")){
				return time;
			}else{
				return "08";
			}
		}
		return time;
	}

	private static void printReportToScreen(List<List<String>> allData, double allWins, double allRisks, int daysPerMonth, int monthsToSimulate, int allNumberOfWinsCount,
											String message, int numberOfInstruments, int totalCount, List<Result> listOfResults) {
		printAllWinsResults(allData, allWins, allRisks, daysPerMonth, monthsToSimulate, allNumberOfWinsCount, message,
				numberOfInstruments, totalCount);

		printDataSet(listOfResults, allData);

		printAllWinsResults(allData, allWins, allRisks, daysPerMonth, monthsToSimulate, allNumberOfWinsCount, message,
				numberOfInstruments, totalCount);
	}

	private static Result getResults(double startWithXToRisk, int winCount, int looseCount, double allWins, double allRisks, double acWinsAmount, double acRiskAmount, List<String> dataWL, String key) {
		Result result = new Result(startWithXToRisk);
		result.setWinCount(winCount);
		result.setLooseCount(looseCount);
		result.setTotalAmountWin(allWins);
		result.setTotalAmountLoose(allRisks);
		result.setWinThisDataSet(acWinsAmount);
		result.setLooseThisDataset(acRiskAmount);
		result.addDataSet(dataWL);
		result.setKey(key);
		return result;
	}

	private static void printDataSet(List<Result> listOfResults, List<List<String>> allData) {
		log.warn("\nData Sets : ");
		int totalWC = 0;
		if (allData != null) {
			for (List<String> rowOfData : allData) {
				int wc = 0;
				for(String w: rowOfData){
					if(w.equals("W")){
						wc++;
					}
				}
				//log.warn("" + rowOfData + ", W=" + wc);
				totalWC = totalWC + wc;
				wc = 0;
			}

		}

		System.out.println();
		for(Result aResult: listOfResults){
			System.out.println(aResult);
		}

		int elements = allData.get(0).size() * allData.size();
		double percent = Utils.getPercentToXDecimal((totalWC * 1.0) / elements, 2) * 100;
		log.warn("\nNumber of data sets : " + allData.size() + "");
		log.warn("Total number of W counts : " + (totalWC));
		log.warn("Total number of elements : " + (elements));
		log.warn("Actual win % : " + percent + "%\n");
	}

	private static void printAllWinsResults(List<List<String>> allData, double allWins, double allRisks,
											int numberOfDaysPerMonth, int months, int allNumberOfWinsCount, String message, int nic, int totalCount) {
		log.warn("\n-------" + message + "------- : ");
		double totalNDays = totalCount;
		double percent = Utils.getPercentToXDecimal( allNumberOfWinsCount / totalNDays, 2)*100;

		double elements = getNumberOfTotalElements(allData);
		double percentPlayed = Utils.getPercentToXDecimal( totalNDays / elements, 2)*100;
		log.warn("ALL win  : " + Utils.getPercentToXDecimal(allWins,2));
		log.warn("ALL risk : " + Utils.getPercentToXDecimal(allRisks,2));
		log.warn("Difference : " + Utils.getPercentToXDecimal(allWins - allRisks, 2));
		log.warn("ALL win % : " + (allNumberOfWinsCount) + "/" + (totalNDays) + " = " + percent + "%");
		log.warn("Percentage played : " + percentPlayed + "%, Out of : " + elements + " = " + totalNDays );
		log.warn("-------" + message + "------- : ");

	}

	private static int getNumberOfTotalElements(List<List<String>> allData) {
		int noe = 0;
		for(List<String> lor: allData){
			for(String loi: lor){
				noe++;
			}
		}
		return  noe;
	}

	public static void printResults(String key, List<String> dataWL, double startWithXToRisk, int numberOfItems,
									int winCount, double acWinsAmount, double acRiskAmount, double sumOfDiffernce, String message) {

		log.warn("\n-------" + message + "------- : ");
		log.warn("Data : " + dataWL);
		log.warn("Key : " + key);
		log.warn("Win count : " + winCount);
		log.warn("Loss count : " + (numberOfItems - winCount));
		if(numberOfItems > 0)
			log.warn("W Percentage : " + ((winCount * 100 / numberOfItems)) + "%");
		log.warn("Starting Risk : " + startWithXToRisk);
		log.warn("Account W Amount : " + Utils.getPercentToXDecimal(acWinsAmount, 2));
		log.warn("Risk Amount : " + acRiskAmount);
		if (sumOfDiffernce < 0) {
			log.warn("Loss : " + Utils.getPercentToXDecimal(sumOfDiffernce, 2));
		} else {
			log.warn("Wins : " + Utils.getPercentToXDecimal(sumOfDiffernce, 2));
		}
		log.warn("-------" + message + "------- : ");
	}

}
