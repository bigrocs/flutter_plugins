package android.src.main.java.witparking.inspection.inspectionprinter.ZKC;

import android.util.Log;

import com.smartdevicesdk.btprinter.StringUtility;

public class PrintCommand {
	private static final String TAG = "PrintStyle";

	/**
	 * 二进制字符串转byte
	 */
	private static byte decodeBinaryString(String byteStr) {
		int re, len;
		if (null == byteStr) {
			return 0;
		}
		len = byteStr.length();
		if (len != 4 && len != 8) {
			return 0;
		}
		if (len == 8) {// 8 bit处理
			if (byteStr.charAt(0) == '0') {// 正数
				re = Integer.parseInt(byteStr, 2);
			} else {// 负数
				re = Integer.parseInt(byteStr, 2) - 256;
			}
		} else {// 4 bit处理
			re = Integer.parseInt(byteStr, 2);
		}
		return (byte) re;
	}

	/**
	 * 设置字符右间距,其字符间间距为: n*0.125mm,当字符放大时，右间距随之放大相同的倍数
	 *
	 * @param n
	 * @return
	 */
	public static byte[] set_CharSpace_Right(int n) {
		if (n < 0 || n > 255) {
			return null;
		}
		CMD.ESC_margin_right[2] = (byte) n;

		return CMD.ESC_margin_right;
	}

	/**
	 * 选择字符打印模式
	 *
	 * @param font
	 *            0,24点阵字号; 1,16点阵字号
	 * @param blod
	 *            1,加粗
	 * @param doubleHeight
	 *            1，高度加倍
	 * @param doubleWidth
	 *            1，宽度加倍
	 * @param underLine
	 *            1、下划线
	 * @return
	 */
	public static byte[] set_FontStyle(int font, int blod, int doubleHeight,
									   int doubleWidth, int underLine) {
		if (font < 0 | font > 1 | blod < 0 | blod > 1 | doubleHeight < 0
				| doubleWidth > 1 | underLine < 0 | underLine > 1) {
			return null;
		}
		/*String str = font + "00" + blod + doubleHeight + doubleWidth + "0"
				+ underLine;*/
		String str = underLine+"0"+doubleWidth+doubleHeight+blod+"00" +font;
		byte bt = decodeBinaryString(str);
		CMD.ESC_font_style[2] = bt;
		Log.d(TAG, StringUtility.ByteArrayToString(CMD.ESC_font_style,
				CMD.ESC_font_style.length));
		return CMD.ESC_font_style;
	}

	/**
	 * 设置绝对打印位置
	 *
	 * @param n
	 * @return
	 */
	public static byte[] set_Absolute_Point(int n) {
		if (n < 0 || n > 65535) {
			return null;
		}
		CMD.ESC_absolute[2] = (byte) (n & 0xff);
		CMD.ESC_absolute[3] = (byte) (n >>> 8);
		return CMD.ESC_absolute;
	}

	/**
	 * 选择/取消下划线模式
	 *
	 * @param n
	 *            0，取消；1，1点宽；2，2点宽
	 * @return
	 */
	public static byte[] set_UnderLineMode(int n) {
		if (n < 0 | n > 2) {
			return null;
		}
		CMD.ESC_underline[2] = (byte) n;
		return CMD.ESC_underline;
	}

	/**
	 * 设置行间距
	 *
	 * @param n
	 * @return
	 */
	public static byte[] set_LineSpace(int n) {
		if (n < 0 | n > 255) {
			return null;
		}
		CMD.ESC_linespace[2] = (byte) n;
		return CMD.ESC_linespace;
	}

	/**
	 * 控制蜂鸣器提示。 <br/>
	 * 说明： n值为蜂鸣器鸣叫的次数； <br/>
	 * t值为蜂鸣器每次鸣叫的时间，时间为(t × 50)ms。
	 *
	 * @param n
	 * @param t
	 * @return
	 */
	public static byte[] set_Buzzer(int n, int t) {
		CMD.ESC_buzzer[2] = (byte) n;
		CMD.ESC_buzzer[3] = (byte) t;
		return CMD.ESC_buzzer;
	}

	/**
	 * 功能：控制蜂鸣器提示, 同时报警灯闪烁。<br/>
	 * 说明：·m值为蜂鸣器鸣叫的次数, 同时也是指示灯闪烁的次数；<br/>
	 * ·t值为蜂鸣器每次鸣叫的时间，时间为(t × 50)ms。<br/>
	 * ·n值为指示灯每次常亮的时间，时间为(t × 50)ms。<br/>
	 *
	 * @param m
	 * @param t
	 * @param n
	 * @return
	 */
	public static byte[] set_Buzzer_LED(int m, int t, int n) {
		CMD.ESC_buzzer_led[2] = (byte) m;
		CMD.ESC_buzzer_led[3] = (byte) t;
		CMD.ESC_buzzer_led[4] = (byte) n;
		return CMD.ESC_buzzer_led;
	}

	/**
	 * 功能：根据n取值选择或取消加粗模式。<br/>
	 * 说明：·0 ≤ n ≤ 255，但只有n的最低位有效；<br/>
	 * ·当最低位为0时，取消加粗模式；<br/>
	 * ·当最低位为1时，选择加粗模式；<br/>
	 * ·ESC ! 同样可以选择/取消加粗模式，最后接收的命令有效；<br/>
	 * ·n默认为0。
	 *
	 * @param n
	 * @return
	 */
	public static byte[] set_Blod(int n) {
		if (n < 0 | n > 255) {
			return null;
		}
		CMD.ESC_font_blod[2] = (byte) n;
		return CMD.ESC_font_blod;
	}

	/**
	 * 功能：根据n选择/取消双重打印模式。<br/>
	 * 说明：·0 ≤ n ≤ 255，但只有n的最低位有效；<br/>
	 * ·当最低位为0时，取消双重打印模式；<br/>
	 * ·当最低位为1时，选择双重打印模式；<br/>
	 * ·该命令与加粗打印效果相同;<br/>
	 * ·n默认为0。
	 *
	 * @param n
	 * @return
	 */
	public static byte[] set_Double(int n) {
		if (n < 0 | n > 255) {
			return null;
		}
		CMD.ESC_double_print[2] = (byte) n;
		return CMD.ESC_double_print;
	}

	/**
	 * 功能：打印缓冲区数据并走纸 n点行，0≤n≤255。<br/>
	 * 说明：·打印结束后，将当前打印位置置于行首；
	 *
	 * @param n
	 * @return
	 */
	public static byte[] set_PrintAndFeed(int n) {
		if (n < 0 | n > 255) {
			return null;
		}
		CMD.ESC_print_feed[2] = (byte) n;
		return CMD.ESC_print_feed;
	}

	/**
	 * 功能：根据n值选择字体，n值可取：0、1、48、49。<br/>
	 * 说明：·n值对应字体如下表：<br/>
	 * n 功能<br/>
	 * 0 选择24点阵字号<br/>
	 * 1 选择16点阵字号<br/>
	 *
	 * @param n
	 * @return
	 */
	public static byte[] set_FontSize(int n) {
		if (n < 0 | n > 1) {
			return null;
		}
		CMD.ESC_font_size[2] = (byte) n;
		return CMD.ESC_font_size;
	}

	/**
	 * 功能：根据n值选择字体，n值可取：0、1、48、49。<br/>
	 * 说明：·n值对应字体如下表： <br/>
	 * 说明<br/>
	 * 1B 4E 00 00 恢复出厂设置<br/>
	 * 1B 4E 02 m 设置串口波特率（取值范围1~8, 默认m=6, 波特率230400）<br/>
	 * m=1:波特率9600 m=2:波特率19200<br/>
	 * m=3:波特率38400 m=4:波特率57600<br/>
	 * m=5 波特率115200 m=6:波特率230400<br/>
	 * m=7 波特率460800 m=8:波特率921600<br/>
	 * <br/>
	 * 1B 4E 04 m 设置打印浓度级别（取值范围0~9, 默认m=0）<br/>
	 * m =1:打印浓度级别1<br/>
	 * m =2:打印浓度级别2<br/>
	 * m =3:打印浓度级别3<br/>
	 * m =4:打印浓度级别4<br/>
	 * …<br/>
	 * m =9:打印浓度级别9<br/>
	 * <br/>
	 * 1B 4E 05 m 设置代码页（默认m=15 CP_936, 简体中文）<br/>
	 * m 的值与 [ESC t n]指令中的n值意义相同.
	 *
	 * @param n
	 * @param m
	 * @return
	 */
	public static byte[] set_Setting_Parameter(int n, int m) {
		CMD.ESC_setting_parameter_save[2] = (byte) n;
		CMD.ESC_setting_parameter_save[3] = (byte) m;
		return CMD.ESC_setting_parameter_save;
	}

	/**
	 * 功能：设置字符倍宽打印<br/>
	 * 说明：·n值对应字体如下表：<br/>
	 * n 功能<br/>
	 * 1 字符不倍宽<br/>
	 * 2 字符宽度放大两倍<br/>
	 *
	 * @param n
	 * @return
	 */
	public static byte[] set_FontDoubleWidth(int n) {
		if (n < 1 | n > 2) {
			return null;
		}
		CMD.ESC_double_width[2] = (byte) n;
		return CMD.ESC_double_width;
	}

	/**
	 * 功能：设置字符倍宽倍高打印<br/>
	 * 说明：·n值对应字体如下表：<br/>
	 * n 功能<br/>
	 * 1 字符不倍宽不倍高<br/>
	 * 2 字符宽度高度都放大两倍<br/>
	 *
	 * @param n
	 * @return
	 */
	public static byte[] set_FontDouble(int n) {
		if (n < 1 | n > 2) {
			return null;
		}
		CMD.ESC_double_width_height[2] = (byte) n;
		return CMD.ESC_double_width_height;
	}

	/**
	 * 功能：设置横向相对位移,<br/>
	 * 说明：·该命令将打印位置设置到距当前位置( nL + nH×256)处， 0≤nL≤255；0≤nH≤255。 <br/>
	 * ·超出可打印区域的设置将被忽略；<br/>
	 * ·当打印位置向右移动时：nL+ nH×256 = N；<br/>
	 * ·打印起始位置从当前位置移动到N点位置处.<br/>
	 *
	 * @param n
	 * @return
	 */
	public static byte[] set_HorizontalPoint(int n) {
		if (n < 0 || n > 65535) {
			return null;
		}
		CMD.ESC_x_point[2] = (byte) (n & 0xff);
		CMD.ESC_x_point[3] = (byte) (n >>> 8);
		return CMD.ESC_x_point;
	}

	/**
	 * 功能：使所有的打印数据按某一指定对齐方式排列。<br/>
	 * 说明：·0≤n ≤2，48≤n ≤50, n默认为0。取值与对齐方式对应关系如下:<br/>
	 *
	 * n 对齐方式<br/>
	 * 0,48 左对齐<br/>
	 * 1,49 中间对齐<br/>
	 * 2,50 右对齐<br/>
	 *
	 * ·该命令只在行首有效；<br/>
	 * ·该命令在打印区域执行对齐； <br/>
	 *
	 * @param n
	 * @return
	 */
	public static byte[] set_Align(int n) {
		if (n < 0 | n > 2) {
			return null;
		}
		CMD.ESC_align[2] = (byte) n;
		return CMD.ESC_align;
	}

	/**
	 * 打印并向前走纸n字符行<br/>
	 * 功能：打印缓冲区里的数据并向前走纸n字符行， 0≤n≤255。<br/>
	 * 说明：·该命令将打印机的打印起始位置设置在行首；<br/>
	 * ·该命令不影响由ESC 2 或 ESC 3设置的行间距；<br/>
	 *
	 * @param n
	 * @return
	 */
	public static byte[] set_PrintAndFeedLine(int n) {
		if (n < 0 | n > 255) {
			return null;
		}
		CMD.ESC_print_feed_line[2] = (byte) n;
		return CMD.ESC_print_feed_line;
	}

	/**
	 * 设置代码页<br/>
	 * 【范围】 0 ≤ n ≤128<br/>
	 * 【描述】 从字符代码表中选择页n<br/>
	 * n 代码页<br/>
	 * 0 PC437 [美国，欧洲标准]<br/>
	 * 2 PC850 [多语言, 西欧语]<br/>
	 * 3 PC860 [葡萄牙语]<br/>
	 * 4 PC863 [加拿大-法语]<br/>
	 * 5 PC865 [北欧- 德语，日耳曼语]<br/>
	 * 6 PC1252 [West Europe]<br/>
	 * 7 PC737 [Greek]<br/>
	 * 8 PC862 [Hebrew]<br/>
	 * 11 CP775 [波罗的海语]<br/>
	 * 13 CP949 [韩文]<br/>
	 * 14 CP950 [繁体中文]<br/>
	 * 15 CP936 [简体中文]<br/>
	 * 16 PC1252<br/>
	 * 17 PC866 [Cyrillice*2]<br/>
	 * 18 PC852 [Latin2]<br/>
	 * 19 PC858 [西欧语]<br/>
	 * 21 CP866 [斯拉夫语/俄语]<br/>
	 * 22 CP855 [斯拉夫语 保加利亚]<br/>
	 * 23 CP857 [土耳其语] <br/>
	 * 24 CP864 [阿拉伯语]<br/>
	 * 34 CP1251[西里尔文 斯拉夫语 俄语]<br/>
	 * 35 CP1252[西欧(拉丁文I)]<br/>
	 * 36 CP1253[希腊文]<br/>
	 * 37 CP1254[土耳其文]<br/>
	 * 38 CP1255[希伯来文]<br/>
	 * 39 CP1256[阿拉伯文]<br/>
	 * 40 CP1257[波罗的海文]
	 *
	 * @param n
	 * @return
	 */
	public static byte[] set_CodePage(int n) {
		if (n < 0 | n > 40) {
			return null;
		}
		CMD.ESC_codepage[2] = (byte) n;
		return CMD.ESC_codepage;
	}

	/**
	 * 选择/取消倒置打印模式<br/>
	 * 功能：根据n值选择或取消倒置打印模式，0 ≤ n ≤ 255但n值只有最低位有效。<br/>
	 * 说明：·当n的最低位为0时，取消倒置打印模式；<br/>
	 * ·当n的最低位为1时，选择倒置打印模式；<br/>
	 * ·n默认值为0；
	 *
	 * @param n
	 * @return
	 */
	public static byte[] set_Handstand(int n) {
		if (n < 0 | n > 1) {
			return null;
		}
		CMD.ESC_handstand[2] = (byte) n;
		return CMD.ESC_handstand;
	}

	/**
	 * 设置QrCode二维码顶部空白高度<br/>
	 * 功能：设置QrCode二维码顶部空白高度<br/>
	 说明:  n的取值范围 0 <= n <= 255,  n默认取值24
	 * @param n
	 * @return
	 */
	public static byte[] set_QrCode_TopSpace(int n){
		if (n < 0 | n > 255) {
			return null;
		}
		CMD.US_qrcode_top_space[2]=(byte) n;
		return CMD.US_qrcode_top_space;
	}

	/*
	* 设置打印二维码的大小
	* */
	public static byte[] set_QrCode_ElementWidth( ){
		CMD.US_qrcode_element_width[2]=(byte) 8;
		return CMD.US_qrcode_element_width;
	}
}
