package com.bogoslovov.kaloyan.simplecurrencyconvertor;

/**
 * Created by kaloqn on 5/14/17.
 */

public class Utils {

    public static int getObjectNumber(String[]currencies, String currency){
        int number = 0;
        for (int i=0; i<currencies.length; i++){
            if (currencies[i].equals(currency)){
                return i;
            }
        }
        return number;
    }

}
