package com.example.expensestracker;

import java.text.NumberFormat;
import java.util.Locale;

public class Numberutills {

        public static String formatNumber(int number) {
            return NumberFormat.getNumberInstance(Locale.US).format(number);
        }
        public static String formatNumber(double number) {
            return NumberFormat.getNumberInstance(Locale.US).format(number);
        }
}
