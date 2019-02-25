package witparking.inspection.inspectionvoice;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.HashMap;
import java.util.Map;

public class Chepai {
  static String[] sheng = {"京", "津", "冀", "晋", "蒙", "辽", "吉", "黑", "沪", "苏", "浙", "皖", "闽", "赣", "鲁", "豫", "鄂", "湘", "桂", "琼", "渝", "川", "贵", "云", "藏", "陕", "甘", "青", "宁", "新", "粤"};
  static String[] shengLetter;

  static String[] shuzi = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
  static String[] shuziString = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "零"};
  static String[] shuziLetter;
  static Map<String, String> zimumap = new HashMap<String, String>();
  static Map<String, String> shuziMap = new HashMap<String, String>();
  static Map<String, String> shengMap = new HashMap<String, String>();

  static {
    getSetting();
  }

  private static String getNum8(String string){
    try {
      if (string == null || string.length() < 8) {
        return null;
      }
      String num = string.substring(0, 8);
      char[] c = num.toCharArray();

      //第一个省份纠正
      String firstPinyin = strings(converterToFirstSpell(String.valueOf(c[0])));
      String shengStr = null;
      for (int i = 0; i < shengLetter.length; i++) {
        if (shengLetter[i].indexOf(firstPinyin) != -1) {
          shengStr = sheng[i];
        }
      }
      if (shengStr == null) {
        String[] list = toPinyiNo(String.valueOf(c[0]));
        for (String string2 : list) {
          shengStr = shengMap.get(string2);
        }

      }
      if (shengStr == null) {
        System.out.println("省份未找到" + String.valueOf(c[0]));
        return null;
      }
      //第二个字母纠正
      String second = null;
      if (('a' <= c[1] && c[1] <= 'z') || ('A' <= c[1] && c[1] <= 'Z')) {
        second = String.valueOf(c[1]);
      } else {
        String[] secondPinyins = toPinyiNo(String.valueOf(c[1]));
        for (String string2 : secondPinyins) {
          if (second == null) {
            second = zimumap.get(string2);
          }
        }

      }
      if (second == null) {
        System.out.println("区域字母没找到");
        return null;
      }
      //剩余字母数字
      String letter2 = lastLetter(c[2]);
      String letter3 = lastLetter(c[3]);
      String letter4 = lastLetter(c[4]);
      String letter5 = lastLetter(c[5]);
      String letter6 = lastLetter(c[6]);
      String letter7 = lastLetter(c[7]);
      if (letter2 == null || letter3 == null || letter4 == null || letter5 == null || letter6 == null|| letter7 == null) {
        System.out.println("牌号未匹配");

        return null;
      }
      String result = shengStr + second + letter2 + letter3 + letter4 + letter5 + letter6+letter7;
      return result.toUpperCase();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }


  public static String getNum(String string) {
    try {
      if (string == null || string.length() < 7) {
        return null;
      }
      if(string.length()>=8){
        return  getNum8(string);
      }
      String num = string.substring(0, 7);
      char[] c = num.toCharArray();

      //第一个省份纠正
      String firstPinyin = strings(converterToFirstSpell(String.valueOf(c[0])));
      String shengStr = null;
      for (int i = 0; i < shengLetter.length; i++) {
        if (shengLetter[i].indexOf(firstPinyin) != -1) {
          shengStr = sheng[i];
        }
      }
      if (shengStr == null) {
        String[] list = toPinyiNo(String.valueOf(c[0]));
        for (String string2 : list) {
          shengStr = shengMap.get(string2);
        }

      }
      if (shengStr == null) {
        System.out.println("省份未找到" + String.valueOf(c[0]));
        return null;
      }
      //第二个字母纠正
      String second = null;
      if (('a' <= c[1] && c[1] <= 'z') || ('A' <= c[1] && c[1] <= 'Z')) {
        second = String.valueOf(c[1]);
      } else {
        String[] secondPinyins = toPinyiNo(String.valueOf(c[1]));
        for (String string2 : secondPinyins) {
          if (second == null) {
            second = zimumap.get(string2);
          }
        }

      }
      if (second == null) {
        System.out.println("区域字母没找到");
        return null;
      }
      //剩余字母数字
      String letter2 = lastLetter(c[2]);
      String letter3 = lastLetter(c[3]);
      String letter4 = lastLetter(c[4]);
      String letter5 = lastLetter(c[5]);
      String letter6 = lastLetter(c[6]);
      if (letter2 == null || letter3 == null || letter4 == null || letter5 == null || letter6 == null) {
        System.out.println("牌号未匹配");

        return null;
      }
      String result = shengStr + second + letter2 + letter3 + letter4 + letter5 + letter6;
      return result.toUpperCase();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static String lastLetter(char ch) {
    if (('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') || ('0' <= ch && ch <= '9')) {
      System.out.println("--" + ch);
      return String.valueOf(ch);
    }
    String letter = null;
    String pinyin0 = strings(converterToFirstSpell(String.valueOf(ch)));
    for (int i = 0; i < shuziLetter.length; i++) {
      if (shuziLetter[i].indexOf(pinyin0) != -1) {
        letter = shuzi[i];
      }
    }
    if (letter != null) {
      return letter;
    }
    String[] pinyin = toPinyiNo(String.valueOf(ch));
    for (int i = 0; i < pinyin.length; i++) {
      System.out.println("拼音：" + pinyin[i]);
      if (letter == null) {
        letter = shuziMap.get(pinyin[i]);

      }
      if (letter == null) {
        letter = zimumap.get(pinyin[i]);
      }
    }
    if (letter == null) {
      System.out.println("转换失败：" + ch);
    }
    return letter;
  }


  //初始化数据
  public static void getSetting() {
    shengLetter = new String[sheng.length];
    for (int i = 0; i < sheng.length; i++) {
      shengLetter[i] = strings(converterToFirstSpell(sheng[i]));
    }
    shuziLetter = new String[shuziString.length];
    for (int i = 0; i < shuziString.length; i++) {
      shuziLetter[i] = strings(converterToFirstSpell(shuziString[i]));
    }
    for (int i = 0; i < sheng.length; i++) {
      String[] sh = toPinyiNo(sheng[i]);
      for (String string : sh) {
        shengMap.put(string, sheng[i]);
      }

    }


//		zimumap.put("ai", "A");
    zimumap.put("ei", "A");
    zimumap.put("bi", "B");
    zimumap.put("sei", "C");
    zimumap.put("di", "D");
    zimumap.put("yi", "E");
//		map.put("", "F");
    zimumap.put("ji", "G");
//		map.put("", "H");
    zimumap.put("ai", "I");
    zimumap.put("zhei", "J");
    zimumap.put("kei", "K");
//		map.put("ai", "L");
//		map.put("ai", "M");
    zimumap.put("en", "N");
    zimumap.put("n", "N");
    zimumap.put("ou", "O");
    zimumap.put("pi", "P");
    zimumap.put("po", "P");
//		map.put("ai", "Q");
    zimumap.put("a", "R");
//		map.put("ai", "S");
    zimumap.put("ti", "T");
    zimumap.put("you", "U");
    zimumap.put("wei", "V");
//		map.put("", "W");
//		map.put("you", "X");
    zimumap.put("yai", "Y");
    zimumap.put("zei", "Z");

    //数字
    shuziMap.put("yao", "1");
    shuziMap.put("yi", "1");
    shuziMap.put("er", "2");
    shuziMap.put("san", "3");
    shuziMap.put("si", "4");
    shuziMap.put("wu", "5");
    shuziMap.put("liu", "6");
    shuziMap.put("qi", "7");
    shuziMap.put("ba", "8");
    shuziMap.put("jiu", "9");
    shuziMap.put("ling", "0");
  }

  public static String strings(String[] strs) {
    String str = "";
    for (String st : strs) {
      str = str + st;
    }
    return str;
  }

  public static String[] converterToFirstSpell(String chines) {
    StringBuffer pinyinName = new StringBuffer();
    char[] nameChar = chines.toCharArray();
    HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
    defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
//        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    for (int i = 0; i < nameChar.length; i++) {
      if (nameChar[i] > 128) {
        try {
          // 取得当前汉字的所有全拼
          String[] strs = PinyinHelper.toHanyuPinyinStringArray(
            nameChar[i], defaultFormat);
          return strs;
        } catch (BadHanyuPinyinOutputFormatCombination e) {
          e.printStackTrace();
        }
      } else {
        pinyinName.append(nameChar[i]);
      }
    }
    // return pinyinName.toString();
//        return parseTheChineseByObject(discountTheChinese(pinyinName.toString()));
    System.out.println(pinyinName.toString());
    return null;
  }

  //转换拼音没有声调
  public static String[] toPinyiNo(String chines) {
    StringBuffer pinyinName = new StringBuffer();
    char[] nameChar = chines.toCharArray();
    HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
    defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
    defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    for (int i = 0; i < nameChar.length; i++) {
      if (nameChar[i] > 128) {
        try {
          // 取得当前汉字的所有全拼
          String[] strs = PinyinHelper.toHanyuPinyinStringArray(
            nameChar[i], defaultFormat);
          return strs;
        } catch (BadHanyuPinyinOutputFormatCombination e) {
          e.printStackTrace();
        }
      } else {
        pinyinName.append(nameChar[i]);
      }
    }
    // return pinyinName.toString();
//        return parseTheChineseByObject(discountTheChinese(pinyinName.toString()));
    System.out.println(pinyinName.toString());
    return null;
  }

}
