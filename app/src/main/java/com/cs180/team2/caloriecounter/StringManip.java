package com.cs180.team2.caloriecounter;

/**
 * Created by David on 11/13/2016.
 */

import java.util.ArrayList;
import java.lang.Math;

public class StringManip {
    public StringManip() {
    }

    public static double similarity(String str1, String str2) {
        // str1 = string to compare to
        // str2 = the possibly misspelled srtring
        ArrayList<ArrayList<Integer>> d = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i <= str1.length(); i++) {
            d.add(new ArrayList<Integer>());
            for (int j = 0; j <= str2.length(); j++) {
                d.get(i).add(0);
            }
        }

        for (int i = 0; i <= str1.length(); i++) {
            d.get(i).set(0, i);
        }
        for (int j = 0; j <= str2.length(); j++) {
            d.get(0).set(j, j);
        }

        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                int cost = 0;

                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    cost = 0;
                }
                else {
                    cost = 1;
                }

                d.get(i).set(j,
                        Math.min(
                                Math.min(d.get(i - 1).get(j) + 1,
                                        d.get(i).get(j - 1) + 1
                                ),
                                d.get(i - 1).get(j - 1) + cost
                        )
                );

                if (i > 1 && j > 1 && str1.charAt(i - 1) == str2.charAt(j - 2) && str1.charAt(i - 2) == str2.charAt(j - 1)) {
                    d.get(i).set(j,
                            Math.min(d.get(i).get(j),
                                    d.get(i - 2).get(j - 2) + cost
                            )
                    );
                }
            }
        }

        int diff = d.get(str1.length()).get(str2.length());
        return 100.0 * ((double)(str1.length() - diff) / (double)str1.length());
    }
}
