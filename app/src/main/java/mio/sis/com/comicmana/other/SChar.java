package mio.sis.com.comicmana.other;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/1/11.
 */

public class SChar {
    /*
        char 轉正整數，失敗回傳 -1
        失敗表示 chars 中含有非數字字元
     */
    static public int GetNumber(char[] chars) {
        int result = 0;
        for (int i = 0; i < chars.length; ++i) {
            if (chars[i] < '0' || chars[i] > '9') return -1;
            result = result * 10 + (chars[i] - '0');
        }
        return result;
    }
    /*
        chars 轉正整數數列
        Example：
            123ABAS32ASD42 轉換結果為 123, 32, 42
            若是沒有數字存在的字串，則回傳沒有元素的 list

        使用 state machine 結構
        state = 0 目前沒有任何東西
                1 上一個讀的是數字
     */
    static public ArrayList<Integer> ParseIntList(char[] chars) {
        ArrayList<Integer> result = new ArrayList<>();
        int state = 0, cur = 0;
        for (int i = 0; i < chars.length; ++i) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                switch (state) {
                    case 0:
                        cur = chars[i] - '0';
                        state = 1;
                        break;
                    case 1:
                        cur = cur * 10 + chars[i] - '0';
                        break;
                }
            } else {
                switch (state) {
                    case 0:
                        break;
                    case 1:
                        result.add(cur);
                        state = 0;
                        break;
                }
            }
        }
        if (state == 1) {
            result.add(cur);
        }
        return result;
    }
    static public boolean StringInListIgnoreCase(String string, String[] list) {
        for(String listString : list) {
            if(string.compareToIgnoreCase(listString)==0) return true;
        }
        return false;
    }
    static public boolean StringInListIgnoreCase(String string, ArrayList<String> list) {
        for(String listString : list) {
            if(string.compareToIgnoreCase(listString)==0) return true;
        }
        return false;
    }
}
