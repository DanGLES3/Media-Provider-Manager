/*
 * Copyright 2023 Green Mushroom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// https://gist.github.com/seven332/eadc44f1b35f756e46c410a8487fcc1d

package me.gm.cleaner.plugin.ktx;

import java.math.BigInteger;
import java.text.Collator;
import java.util.Comparator;

public class NaturalSorter implements Comparator<String> {
    private static final Collator collator = Collator.getInstance();

    @Override
    public int compare(String o1, String o2) {
        int index1 = 0;
        int index2 = 0;
        while (true) {
            String data1 = nextSlice(o1, index1);
            String data2 = nextSlice(o2, index2);

            if (data1 == null && data2 == null) {
                return 0;
            }
            if (data1 == null) {
                return -1;
            }
            if (data2 == null) {
                return 1;
            }

            index1 += data1.length();
            index2 += data2.length();

            int result;
            if (isDigit(data1) && isDigit(data2)) {
                result = new BigInteger(data1).compareTo(new BigInteger(data2));
            } else {
                result = collator.compare(data1, data2);
            }

            if (result != 0) {
                return result;
            }
        }
    }

    private static boolean isDigit(String str) {
        // Just check the first char
        char ch = str.charAt(0);
        return ch >= '0' && ch <= '9';
    }

    static String nextSlice(String str, int index) {
        int length = str.length();
        if (index == length) {
            return null;
        }

        char ch = str.charAt(index);
        if (ch == '.' || ch == ' ') {
            return str.substring(index, index + 1);
        } else if (ch >= '0' && ch <= '9') {
            return str.substring(index, nextNumberBound(str, index + 1));
        } else {
            return str.substring(index, nextOtherBound(str, index + 1));
        }
    }

    private static int nextNumberBound(String str, int index) {
        for (int length = str.length(); index < length; index++) {
            char ch = str.charAt(index);
            if (ch < '0' || ch > '9') {
                break;
            }
        }
        return index;
    }

    private static int nextOtherBound(String str, int index) {
        for (int length = str.length(); index < length; index++) {
            char ch = str.charAt(index);
            if (ch == '.' || ch == ' ' || (ch >= '0' && ch <= '9')) {
                break;
            }
        }
        return index;
    }
}
