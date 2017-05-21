package com.bogoslovov.kaloyan.simplecurrencyconvertor.xmlparser;

import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** temporary test solution for parsing the xml from ECB to be replaced with XmlPullParser
 * Created by kaloqn on 5/13/17.
 */

@Deprecated
public class XMLParser {

    public List<String> parse90Days(StringBuilder stringBuilder){

        List<String> list = new ArrayList<String>();

        //add date
        list.add(stringBuilder.substring(12,22));

        //add eur
        list.add("1");

        //get first value (USD)
        stringBuilder.delete(0,51);
        int endOfDollarRate = stringBuilder.indexOf("\"");

        list.add(stringBuilder.substring(0,endOfDollarRate));
        //System.out.print(" "+stringBuilder.substring(0,endOfDollarRate));
        stringBuilder.delete(0,endOfDollarRate+30);

        //All values between the first and the last;
        for (int i = 0; i<29;i++) {
            int indexOfNextQuote = stringBuilder.indexOf("\"");
            // System.out.print(" " + stringBuilder.substring(0, indexOfNextQuote));

            //add currency value to list
            list.add(stringBuilder.substring(0, indexOfNextQuote));

            stringBuilder.delete(0, indexOfNextQuote + 30);
        }
        //get final value (ZAR)
        int indexOfFinalQuote = stringBuilder.indexOf("\"");
        //System.out.print(" "+"final: " + stringBuilder.substring(0, indexOfFinalQuote));
        //add zar value to list
        list.add(stringBuilder.substring(0, indexOfFinalQuote));

        //System.out.println();

        return list;
    }

    public  void parseAndSaveDailyData(BufferedReader br, SharedPreferences sharedPreferences) throws IOException {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        String line = br.readLine();
        String date = line.substring(14, 24);
        editor.putString("EUR", "1");
        editor.putString("date",date);
        String remaining;
        String symbol;
        String rate;
        for (int i = 0; i < 31; i++) {
            line = br.readLine();
            remaining = line.substring(19);
            symbol = remaining.substring(0,3);
            rate = remaining.substring(11, remaining.length()-3);
            System.out.println("symbol: "+symbol+" rate: "+rate);
            editor.putString(symbol, rate);

        }
        System.out.println("data refreshed");
        editor.commit();
        br.close();
    }
}

