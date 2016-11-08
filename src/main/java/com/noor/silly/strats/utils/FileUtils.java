package com.noor.silly.strats.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tayyibah on 06/11/2016.
 */
public class FileUtils {

    public static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    static Map<String, List<String>> mapOfDataToFileKey = new HashMap<String, List<String>>();
    public static final String resourceRoot = File.separator + "src" + File.separator +"test" + File.separator +"resources";

    /**
     * Read a list of items from file
     * @return
     * @param monthCount
     * @param numberOfDaysPerMonth
     */
    public static List<String> getDataFromFile(int monthCount, int numberOfDaysPerMonth, String fileKey, String fileBeginsWith) {
        int from = 0;
        int to = numberOfDaysPerMonth;
        if(monthCount > 1){
            from = (monthCount-1) * numberOfDaysPerMonth;
            to = monthCount * numberOfDaysPerMonth;
        }

        List<String> data = mapOfDataToFileKey.get(fileKey);

        if(data == null) {
            data = loadDataFromFile(fileBeginsWith, fileKey);
        }

        //Get data between a range
        List<String> su = new ArrayList<String>();
        try {
            su = data.subList(from, to);
        }catch(IndexOutOfBoundsException e){
            if(from < data.size() - 1)
                su = data.subList(from, data.size()-1);
        }

        printSummary(su);

        return su;
    }

    private static void printSummary(List<String> su) {
        int numberOfWinsGenerated = 0;
        for(String x: su){
            if(x.equals("W")){
                numberOfWinsGenerated++;
            }
        }

        log.info("-----");
        log.info("Data : " + su);
        log.info("W=" + numberOfWinsGenerated);
        log.info("L=" + (su.size() - numberOfWinsGenerated));
        log.info("-----");
    }

    private static List<String> loadDataFromFile(String fileBeginsWith, String fileKey) {

        List<String> data = new ArrayList<String>();

        String root = new File("").getAbsolutePath();
        String location = root + resourceRoot + "/data/"+ fileBeginsWith + fileKey + ".txt";
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

        //System.out.println("Items count in file : " + data.size());
        return data;
    }

    public static List<String> mixAndMatchDataFromFile(String fileKey1, String fileKey2) {

        List<String> file1Data = mapOfDataToFileKey.get(fileKey1);
        List<String> file2Data = mapOfDataToFileKey.get(fileKey1);

        //load data
        if(file1Data==null){
            file1Data = loadDataFromFile("dataWL", fileKey1);
            log.info("File1 : " + file1Data);
        }
        if(file2Data==null){
            file2Data = loadDataFromFile("dataWL", fileKey2);
            log.info("File2 : " + file2Data);
        }

        //Now mix and match data
        int length = file1Data.size();
        //if(length > file2Data.size()){
        //    length = file2Data.size();
        //}

        List<String> mixedData = new ArrayList<String>();
        for(int c = 0; c < length; c++){
            try {
                String fv1 = file1Data.get(c);
                String fv2 = file2Data.get(c);
                mixedData.add(fv1);
                mixedData.add(fv2);
            }catch (Exception e){}
        }

        return mixedData;
    }

    private static void writeToFile(List<String> data, String fileName) {

        String root = new File("").getAbsolutePath();
        String location = root + resourceRoot + File.separator + "data" + File.separator + fileName;

        //Write to file
        File file = new File(location);
        if (!file.exists()) {
            try {
                // if file doesnt exists, then create it
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Generate content
        String separator = "\n";
        StringBuilder sb = new StringBuilder();
        for(String x: data){
            sb.append(x);
            sb.append(separator);
        }

        try {
            // if file doesnt exists, then create it
            file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(sb.toString());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void main(String [] args){
        List<String> fileNames = getFileNames();

        for(String data: fileNames) {
            String[] split = data.split(",");
            String currency1 = split[0];
            String currency2 = split[1];
            List<String> strings = FileUtils.mixAndMatchDataFromFile(currency1, currency2);
            System.out.println(strings);
            writeToFile(strings, "dataWL" + currency1 + currency2 + ".txt");
        }
    }

    private static List<String> getFileNames() {
        List<String> files = new ArrayList<String>();
        //Data from 6.57
        files.add("07gbp,07eur");
        files.add("07gbp,07dji");
        files.add("07eur,07gbp");
        files.add("07eur,07dji");
        files.add("07dji,07gbp");
        files.add("07dji,07eur");
        //Data from 7.57
        files.add("08gbp,08eur");
        files.add("08gbp,08dji");
        files.add("08eur,08gbp");
        files.add("08eur,08dji");
        files.add("08dji,08gbp");
        files.add("08dji,08eur");
        //Data from 11.57
        files.add("12gbp,12eur");
        files.add("12gbp,12dji");
        files.add("12eur,12gbp");
        files.add("12eur,12dji");
        files.add("12dji,12gbp");
        files.add("12dji,12eur");
        //Data from 13.57
        files.add("14gbp,14eur");
        files.add("14gbp,14dji");
        files.add("14eur,14gbp");
        files.add("14eur,14dji");
        files.add("14dji,14gbp");
        files.add("14dji,14eur");

        return files;
    }

}
