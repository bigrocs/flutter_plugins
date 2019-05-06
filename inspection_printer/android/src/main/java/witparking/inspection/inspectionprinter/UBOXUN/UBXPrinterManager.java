//【仔细研读注解，APP调用层和实现层才能匹配】
//【请考虑多APP对同一模块的调用，使用单例务必注意数据清空，包括static，fini()保证释放单例占用资源】
package witparking.inspection.inspectionprinter.UBOXUN;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.os.Handler;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 模块：    热敏打印机（POS厂商实现层）
 * 说明：
 *           实现打印功能，目前主流热敏行式打印机均支持ESC/POS指令，
 *           拓展功能，某些机型不支持，返回对应的值。
 * 版本号:   v2.0.2
 * 更新时间: 2016/01/20
 * 更新记录：
 *           v2.0.0 2015/12/17 kay 在1.0基础上简化流程，更新为SDK2.0可移植包
 *           v2.0.1 2015/12/22 kay 完善注解
 *           v2.0.2 2015/01/20 kay 增加setPrtWidth()方法，修正部分返回void为int型
 * 特别说明：
 *           1）实现层所有异常，于实现层全部捕获处理，可转为错误return值传递给APP调用层。
 *           2）APP调用层只使用酷银当前定义的接口和成员，不使用POS厂商定义的接口和成员。
 *           3）POS厂商须提供对应的demo来论证实现层是否符合要求。
 */

public class UBXPrinterManager {



    /**
     * 字体定义
     * 如有3种字体以上，按最常用的字体，分别为默认，和字体1，和字体2
     * FONT_FAMILY_DEFAULT ：默认字体
     * FONT_FAMILY_1 ：其他字体（如无，可以不实现）
     * FONT_FAMILY_2 ：其他字体（如无，可以不实现）
     */
    public static final int FONT_FAMILY_DEFAULT = 0x1000;
    public static final int FONT_FAMILY_1 = 0x2000;
    public static final int FONT_FAMILY_2 = 0x4000;

    /**
     * 字大小定义
     * 字大小，不必严格按照整倍数对应，按最常用的大小，分别为普通，小号，大号
     * FONT_SIZE_NOMAL ：普通  单位为2x
     * FONT_SIZE_SMALL ：小号  单位为1x（如SMALL为字号为x, 普通即约2倍大小，大小为约4倍大小）
     * FONT_SIZE_BIG   ：大号  单位为4x
     */
    public static final int FONT_SIZE_NORMAL = 0x0100;
    public static final int FONT_SIZE_SMALL = 0x0200;
    public static final int FONT_SIZE_BIG = 0x0400;

    /**
     * 字样式定义
     * FONT_STYLE_BASIC : 基本
     * FONT_STYLE_BOLD : 加粗
     * FONT_STYLE_ITALIC : 斜体
     * 注：加粗和斜体可以异或存在
     */
    public static final int FONT_STYLE_BASIC = 0x0010;
    public static final int FONT_STYLE_BOLD = 0x0020;
    public static final int FONT_STYLE_ITALIC = 0x0040;

    /**
     * 字缩放定义
     * FONT_SCALE_NORMAL : 未缩放
     * FONT_SCALE_DOUBLEHIGHT : 双倍高
     * FONT_SCALE_DOUBLEWIDTH : 双倍宽
     * 注：双倍高和双倍宽可以异或存在，有些打印机实现为字大小再按倍数显示
     */
    public static final int FONT_SCALE_NORMAL = 0x0001;
    public static final int FONT_SCALE_DOUBLEHIGHT = 0x0002;
    public static final int FONT_SCALE_DOUBLEWIDTH = 0x0004;

    /**
     * 内容对齐定义
     * CONTENT_ALIGN_LEFT : 左对齐
     * CONTENT_ALIGN_CENTER : 居中对齐
     * CONTENT_ALIGN_RIGHT : 右对齐
     */
    public static final int CONTENT_ALIGN_LEFT = 0xA001;
    public static final int CONTENT_ALIGN_CENTER = 0xA010;
    public static final int CONTENT_ALIGN_RIGHT = 0xA100;

    /**
     * 切纸定义
     * 部分低端打印机不支持，可不做实现
     * PAPER_CUT_HALF : 半切
     * PAPER_CUT_ALL : 全切
     */
    public static final int PAPER_CUT_HALF = 0xC101;
    public static final int PAPER_CUT_ALL = 0xC111;

    protected Handler mHandler = null; //保存APP调用层传入的handler对象，避免直接使用调用层对象。
    protected Context mContext = null; //保存APP调用层传入的context对象，避免直接使用调用层对象。

    //----------酷银定义常量和变量区【POS厂商请勿改动】-----结束-----


    //----------POS厂商定义常量和变量区【POS厂商在实现层内部使用】-----开始-----
    //
    //
    private android.device.PrinterManager mPrinter;
    private int printerIndex = 0;
    private int currentYPoint = 0;

    private static final int BARCODE_LINE_WIDTH = 2;
    private static final int BARCODE_HEIGTH = 90;
    private static final int QRCODE_LINE_WIDTH = 8;
    private static final int QRCODE_HEIGTH = 120;
    private static final int MAX_PAGEWIDTH = 384;
    private static final int DEF_FONT_SIZE_SMALL = 12;
    private static final int DEF_FONT_SIZE = 2 * DEF_FONT_SIZE_SMALL;
    private static final int DEF_FONT_SIZE_BIG = 4 * DEF_FONT_SIZE_SMALL;
    private static final int DEF_FONT_SCALE_NORMAL = 0x0001;
    private static final int DEF_FONT_SCALE_DOUBLEHIGHT = 0x0002;
    private static final int DEF_FONT_SCALE_DOUBLEWIDTH = 0x0004;
    //----------POS厂商定义常量和变量区【POS厂商在实现层内部使用】-----结束-----


    //----------酷银定义方法【POS厂商请勿改动】-----开始-----

    /**
     * 此接口为同步接口，获取打印机打印权限。
     * 特别注意：打印机资源不同于其他外设，一般打印机有打印队列，初始化不清空其他APP的打印队列。
     * 打印机继续工作打印其他APP的未完成的打印任务，本次获取打印机的打印任务，接在打印队列之后。
     * @param mHandler: APP调用层传入的消息handler
     * @param mContext：APP调用层传入的上下文context
     * @param prtIndex: 打印机标示符，0为默认打印机（必须支持），1为第一辅助打印机，2为第二辅助打印机
     * @return jsonObject格式如下：
     * {
     *      "success": xxx         //"1"：成功， "0"：失败
     *      "mode": xxx            //"0": 直接打印模式，"1": 预排版模式，"2": XML高级排版模式待扩展
     *                             //如支持多种模式，建议实现"0"为主要模式。
     * }
     */
    public JSONObject init(Handler handler,Context context,int prtIndex){

        //TODO POS厂商按上述描述去实现

        JSONObject jsonObject = new JSONObject();

        this.mHandler = handler; //赋值保存
        this.mContext = context; //赋值保存
        this.printerIndex = prtIndex;
        mPrinter = new android.device.PrinterManager();
        int ret = mPrinter.open();
        ret = mPrinter.setupPage(-1, -1);
        // TODO POS厂商按上述描述去实现
        try {
            jsonObject.put("success", ret == 0 ? 1: 0);
            jsonObject.put("mode", 1);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 此接口为同步接口。
     * 和init()配套，结束打印流程，释放APP调用层handler和context。
     */
    public void fini(){

        //TODO POS厂商按上述描述去实现
        if (mPrinter != null) {
            mPrinter.clearPage();
            mPrinter.close();
            mPrinter = null;
        }
        this.mHandler = null; //释放资源
        this.mContext = null; //释放资源

    }

    //====================================特殊功能==================================//

    /**
     * 打印机进纸功能（独立控制进纸）
     * @param lines：单次调用进纸长度，单位行。可进行对应换算。
     * @return ret 0：成功
     *            -1：失败
     */
    public int feedPaper(int lines){

        //TODO POS厂商按上述描述去实现
        int ret = -1;
        if(mPrinter != null && lines > 0) {
            mPrinter.paperFeed(3*lines);//bits mm
            currentYPoint += 3*lines;
            ret = 0;
        }
        return ret;
    }

    /**
     * 打印机切纸功能（可包含feedPaper功能，即切纸前可以默认进纸一定长度）
     * @param cutMode： 参考切纸定义
     * @return ret 0：成功
     *            -1：失败
     *          -999：不支持该功能（可以不支持）
     */
    public int cutPaper(int cutMode){

        //TODO POS厂商按上述描述去实现

        int ret = -999;
        return ret;
    }

    /**
     * 通过打印机RJ11口，钱箱打开功能
     * @return ret 0：成功
     *            -1：失败
     *          -999：不支持该功能 （可以不支持，尤其手持POS）
     */
    public int openCashBox(){

        //TODO POS厂商按上述描述去实现

        int ret = -999;
        return ret;
    }

    //====================================打印设置和状态==================================//

    /**
     * 获取当前init()驱动的打印机状态
     * 注：如同时存在多个问题，返回绝对值最小的数值。
     * @return ret 0：正常
     *            -1：缺纸
     *            -2：未合盖
     *            -3：卡纸
     *          -100：其他故障
     *          -999：不支持该功能（可以不支持）
     */
    public int getStatus(){

        //TODO POS厂商按上述描述去实现

        int ret = -999;
        return ret;
    }

    /**
     * 设置打印机的宽度，默认58mm（不调用此接口）
     * 影响到DashLine,以及MultiTxt的排版
     * 对于外置的打印机一般需要根据实际配置打印机需要调用，打印一体POS机，请返回-999。
     * @param width 打印宽度，一般为58或80两个档位。
     * @return ret 0：成功
     *            -1：失败
     *          -999：不支持该功能（可以不支持，固定58mm）
     */
    public int setPrtWidth(int width) {
        int ret = -999;
        return ret;
    }

    /**
     * 设置进纸速度
     * @param speed： 速度数值，分为5个等级（数值越大越快），默认为3。可进行对应换算。
     * @return ret 0：成功
     *            -1：失败
     *          -999：不支持该功能（可以不支持）
     */
    public int setPrtSpeed(int speed){
        int result = -1;
        // TODO 打印机厂商按此描述实现功能
        if (mPrinter != null) {// level value is 50 to 80,default 62
            switch (speed) {
                case 1:
                    speed = 56;
                    break;
                case 2:
                    speed = 62;
                    break;
                case 3:
                    speed = 68;
                    break;
                case 4:
                    speed = 74;
                    break;
                case 5:
                    speed = 80;
                    break;
                default:
                    speed = 68;
                    break;
            }
            mPrinter.setSpeedLevel(speed);
            result = 0;
        }
        return result;
    }

    /**
     * 设置打印灰度
     * @param level： 灰度级别，分为5个等级（数值越大越黑），默认3。可进行对应换算。
     * @return ret 0：成功
     *            -1：失败
     *          -999：不支持该功能（可以不支持）
     */
    public int setGrayLevel(int level){
        int result = -1;
        // TODO 打印机厂商按此描述实现功能
        if (mPrinter != null) {// level value is 0 to 30, default 15.
            switch (level) {
                case 1:
                    level = 5;
                    break;
                case 2:
                    level = 10;
                    break;
                case 3:
                    level = 15;
                    break;
                case 4:
                    level = 20;
                    break;
                case 5:
                    level = 30;
                    break;
                default:
                    level = 15;
                    break;
            }
            mPrinter.setGrayLevel(level);
            result = 0;
        }
        return result;
    }

    //====================================直接打印模式==================================//
    /**
     * 立即打印一行虚线，打印完成后自动回车换行
     * @return ret 0：成功
     *            -1：失败
     */
    public int prtDashLine(){

        //TODO POS厂商按上述描述去实现

        int ret = -1;
        return ret;
    }

    /**
     * 打印机回车换行，用于定位打印位置到下一行（打印空白行）
     * @return ret 0：成功
     *            -1：失败
     */
    public int prtBlankLine(){

        //TODO POS厂商按上述描述去实现

        int ret = -1;
        return ret;
    }

    /**
     * 立即打印文本内容（单一对齐模式），打印完成后自动回车换行（注：超过打印宽度自动换行）
     * @param text：待打印的文本
     * @param fontProperty: 参考字体属性定义
     * @param alignment：参考内容对齐定义
     * @return ret 0：成功
     *            -1：失败
     */
    public int prtText(String text, int fontProperty, int alignment){

        //TODO POS厂商按上述描述去实现

        int ret = -1;
        return ret;
    }

    /**
     * 立即打印文本内容（左右对齐模式），打印完成后自动回车换行（注：超过打印宽度自动换行）
     * @param textLeftAlign： 左边对齐的文本
     * @param textRightAlign：右边对齐的文本
     * @param fontProperty: 参考字体属性定义
     * @return ret 0：成功
     *            -1：失败
     *          -999: 不支持该功能（可以不支持）
     */
    public int prtMultiText(String textLeftAlign, String textRightAlign, int fontProperty){

        //TODO POS厂商按上述描述去实现

        int ret = -999;
        return ret;
    }

    /**
     * 立即打印条形码（默认高度），打印完成后自动回车换行
     * @param barCode：条形码信息（字符串）
     * @param alignment：参考内容对齐定义
     * @return ret 0：成功
     *            -1：失败
     *          -999: 不支持该功能（可以不支持）
     */
    public int prtBarCode(String barCode, int alignment){

        //TODO POS厂商按上述描述去实现

        int ret = -999;
        return ret;
    }

    /**
     * 立即打印二维码（默认正方形大小），打印完成后自动回车换行
     * @param qrCode：二维码信息（字符串）
     * @param alignment：参考内容对齐定义
     * @return ret 0：成功
     *            -1：失败
     *          -999: 不支持该功能（可以不支持）
     */
    public int prtQRCode(String qrCode, int alignment){

        //TODO POS厂商按上述描述去实现

        int ret = -999;
        return ret;
    }

    /**
     * 立即打印位图，打印完成后自动回车换行
     * @param bitmap：位图信息
     * @param alignment：参考内容对齐定义
     * @return ret 0：成功
     *            -1：失败
     */
    public int prtBitmap(Bitmap bitmap, int alignment){

        //TODO POS厂商按上述描述去实现

        int ret = -1;
        return ret;
    }

    //====================================预排版模式(相对坐标)==================================//

    /**
     * 加入打印一行虚线后，自动回车换行
     * @return ret 0：成功
     *            -1：失败
     */
    public int appendDashLine(){
        int result = -1;
        // TODO 打印机厂商按此描述实现功能
        if (mPrinter != null) {
            currentYPoint += mPrinter.drawTextEx("-------------------------------------------------", 0,
                    currentYPoint, -1, -1, "arial", DEF_FONT_SIZE, 0, 0, 0);
            Log.d("koolPOS", " appendDashLine  curHeigth ==" + currentYPoint);
            result = 0;
        }
        return result;
    }

    /**
     * 加入打印一行空行后，自动回车换行
     * @return ret 0：成功
     *            -1：失败
     */
    public int appendBlankLine(){

        //TODO POS厂商按上述描述去实现
        int ret = 0;
        if (mPrinter != null) {
            currentYPoint += mPrinter
                    .drawTextEx(
                            "                                                                                                            ",
                            0, currentYPoint, -1, -1, "arial", DEF_FONT_SIZE, 0, 0, 0);
            Log.d("koolPOS", " appendBlankLine  curHeigth ==" + currentYPoint);
        }
        return ret;
    }

    /**
     * 加入打印文本内容（单一对齐模式），自动回车换行（注：超过打印宽度自动换行）
     * @param text：待打印的文本
     * @param fontProperty: 参考字体属性定义
     * @param alignment：参考内容对齐定义
     * @return ret 0：成功
     *            -1：失败
     */
    public int appendText(String text, int fontProperty, int alignment){
        int result = -1;
        // TODO 打印机厂商按此描述实现功能
        int fontSize = 26;
        int fontStyle = 0;
        int fontScale = 0;
        int nowrap = 0;// 自动回车换行
        if (mPrinter != null && text != null) {
            boolean fontBold = false;
            boolean fontItalic = true;
            int xPoint = 0;

            if ((fontProperty & FONT_SCALE_NORMAL) == FONT_SCALE_NORMAL) {
                fontScale = DEF_FONT_SCALE_NORMAL;
            } else if ((fontProperty & FONT_SCALE_DOUBLEHIGHT) == FONT_SCALE_DOUBLEHIGHT) {
                fontScale = DEF_FONT_SCALE_DOUBLEHIGHT;
            } else if ((fontProperty & FONT_SCALE_DOUBLEWIDTH) == FONT_SCALE_DOUBLEWIDTH) {
                fontScale = DEF_FONT_SCALE_DOUBLEWIDTH;
            }

            if ((fontProperty & FONT_STYLE_BASIC) == FONT_STYLE_BASIC) {
                fontStyle |= 0;
            }
            if ((fontProperty & FONT_STYLE_BOLD) == FONT_STYLE_BOLD) {
                fontBold  =true;
                fontStyle |= 0x0001;
            }
            if ((fontProperty & FONT_STYLE_ITALIC) == FONT_STYLE_ITALIC) {
                fontItalic = true;
                fontStyle |= 0x0002;
            }
            if(alignment == CONTENT_ALIGN_LEFT) {
                currentYPoint += mPrinter.drawTextEx(text, xPoint, currentYPoint, MAX_PAGEWIDTH, -1, "arial", fontSize, 0,
                        fontStyle, nowrap);
            } else if(alignment == CONTENT_ALIGN_RIGHT) {
                Paint mPaint = getPaint(fontSize, fontBold, fontItalic);
                float totalWidth = mPaint.measureText(text);
                int fontHeight = getFontHight(mPaint);
                if(totalWidth <= MAX_PAGEWIDTH) {
                    xPoint = (int)(MAX_PAGEWIDTH - totalWidth);
                    currentYPoint += mPrinter.drawTextEx(text, xPoint, currentYPoint, MAX_PAGEWIDTH, -1, "arial", fontSize, 0,
                            fontStyle, nowrap);
                } else {
                    try{
                        char[] textChar = text.toCharArray();
                        //float scalar = mPaint.measureText(" ");
                        float curLinePyWidth = 0;
                        int length = textChar.length;
                        int i = 0;
                        int offset = 0;
                        int charCount = 0;
                        while(i < length) {
                            curLinePyWidth = curLinePyWidth + mPaint.measureText(textChar, i, 1);
                            charCount++;
                            switch(textChar[i]) {

                                case 0x0a:{//\n 坐标Y加一个字体高度
                                    xPoint = 0;
                                    //String curLineText = new String(textChar, offset, charCount);
                                    offset = offset + charCount;
                                    /*xPoint = (int)(MAX_PAGEWIDTH - curLinePyWidth);
                                    currentYPoint += mPrinter.drawTextEx(curLineText, xPoint, currentYPoint, MAX_PAGEWIDTH, -1, "arial", fontSize, 0,
                                            fontStyle, nowrap);*/
                                    currentYPoint =currentYPoint + fontHeight;
                                    curLinePyWidth = 0;
                                    charCount = 0;
                                }
                                break;
                                case 0x0d:{ //\r 坐标Y不变
                                    xPoint = 0;
                                    String curLineText = new String(textChar, offset, charCount);
                                    offset = offset + charCount;
                                    xPoint = (int)(MAX_PAGEWIDTH - curLinePyWidth);
                                    /*currentYPoint += mPrinter.drawTextEx(curLineText, xPoint, currentYPoint, MAX_PAGEWIDTH, -1, "arial", fontSize, 0,
                                            fontStyle, nowrap);*/
                                    mPrinter.drawTextEx(curLineText, xPoint, currentYPoint, MAX_PAGEWIDTH, -1, "arial", fontSize, 0,
                                            fontStyle, nowrap);
                                    curLinePyWidth = 0;
                                    charCount = 0;
                                }
                                break;
                                /*case 0x09:{//\t

                                }
                                break;
                                case 0x08:{//\b

                                }
                                break;*/
                                default: {
                                    float scalar = 0;
                                    if(i + 1 < length)
                                        scalar =mPaint.measureText(textChar, i + 1, 1);
                                    if(curLinePyWidth + scalar >= MAX_PAGEWIDTH) {
                                        xPoint = 0;
                                        String curLineText = new String(textChar, offset, charCount);
                                        offset = offset + charCount;
                                        xPoint = (int)(MAX_PAGEWIDTH - curLinePyWidth);
                                        currentYPoint += mPrinter.drawTextEx(curLineText, xPoint, currentYPoint, MAX_PAGEWIDTH, -1, "arial", fontSize, 0,
                                                fontStyle, nowrap);
                                        curLinePyWidth = 0;
                                        charCount = 0;
                                    } else {
                                        if(i + 1 == length) {
                                            String curLineText = new String(textChar, offset, charCount);
                                            xPoint = (int)(MAX_PAGEWIDTH - curLinePyWidth);
                                            currentYPoint += mPrinter.drawTextEx(curLineText, xPoint, currentYPoint, MAX_PAGEWIDTH, -1, "arial", fontSize, 0,
                                                    fontStyle, nowrap);
                                        }
                                    }
                                }
                                break;
                            }
                            i++;
                        }
                    } catch(IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            } else if(alignment == CONTENT_ALIGN_CENTER) {
                Paint mPaint = getPaint(fontSize, fontBold, fontItalic);
                float totalWidth = mPaint.measureText(text);
                int fontHeight = getFontHight(mPaint);
                if(totalWidth <= MAX_PAGEWIDTH) {
                    xPoint = (int)(MAX_PAGEWIDTH - totalWidth) /2;
                    currentYPoint += mPrinter.drawTextEx(text, xPoint, currentYPoint, MAX_PAGEWIDTH, -1, "arial", fontSize, 0,
                            fontStyle, nowrap);
                } else {
                    try{
                        char[] textChar = text.toCharArray();
                        //float scalar = mPaint.measureText(" ");
                        float curLinePyWidth = 0;
                        int length = textChar.length;
                        int i = 0;
                        int offset = 0;
                        int charCount = 0;

                        while(i < length) {
                            curLinePyWidth = curLinePyWidth + mPaint.measureText(textChar, i, 1);
                            charCount++;
                            switch(textChar[i]) {

                                case 0x0a:{//\n
                                    xPoint = 0;
                                    //String curLineText = new String(textChar, offset, charCount);
                                    offset = offset + charCount;
                                    /*xPoint = (int)(MAX_PAGEWIDTH - curLinePyWidth) /2;
                                    currentYPoint += mPrinter.drawTextEx(curLineText, xPoint, currentYPoint, MAX_PAGEWIDTH, -1, "arial", fontSize, 0,
                                            fontStyle, nowrap);*/
                                    currentYPoint =currentYPoint + fontHeight;
                                    curLinePyWidth = 0;
                                    charCount = 0;
                                    Log.d("koolPOS", curLinePyWidth + " appendText  curHeigth =0x0a=" + currentYPoint);
                                }
                                break;
                                case 0x0d:{ //\r
                                    xPoint = 0;
                                    String curLineText = new String(textChar, offset, charCount);
                                    offset = offset + charCount;
                                    xPoint = (int)(MAX_PAGEWIDTH - curLinePyWidth) /2;
                                    /*currentYPoint += mPrinter.drawTextEx(curLineText, xPoint, currentYPoint, MAX_PAGEWIDTH, -1, "arial", fontSize, 0,
                                            fontStyle, nowrap);*/
                                    mPrinter.drawTextEx(curLineText, xPoint, currentYPoint, MAX_PAGEWIDTH, -1, "arial", fontSize, 0,
                                            fontStyle, nowrap);
                                    curLinePyWidth = 0;
                                    charCount = 0;
                                    Log.d("koolPOS", " appendText  curHeigth =0x0d=" + currentYPoint);
                                }
                                break;
                                /*case 0x09:{//\t

                                }
                                break;
                                case 0x08:{//\b

                                }
                                break;*/
                                default: {
                                    float scalar = 0;
                                    if(i + 1 < length)
                                        scalar =mPaint.measureText(textChar, i + 1, 1);
                                    if(curLinePyWidth + scalar >= MAX_PAGEWIDTH) {
                                        xPoint = 0;
                                        String curLineText = new String(textChar, offset, charCount);
                                        offset = offset + charCount;
                                        xPoint = (int)(MAX_PAGEWIDTH - curLinePyWidth) /2;
                                        currentYPoint += mPrinter.drawTextEx(curLineText, xPoint, currentYPoint, MAX_PAGEWIDTH, -1, "arial", fontSize, 0,
                                                fontStyle, nowrap);
                                        curLinePyWidth = 0;
                                        charCount = 0;
                                    } else {
                                        if(i + 1 == length) {
                                            String curLineText = new String(textChar, offset, charCount);
                                            xPoint = (int)(MAX_PAGEWIDTH - curLinePyWidth) /2;
                                            currentYPoint += mPrinter.drawTextEx(curLineText, xPoint, currentYPoint, MAX_PAGEWIDTH, -1, "arial", fontSize, 0,
                                                    fontStyle, nowrap);
                                        }
                                    }
                                }
                                break;
                            }
                            i++;
                        }
                    } catch(IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.d("koolPOS", fontSize + " appendText  curHeigth ==" + currentYPoint);
            result = 0;
        }
        return result;
    }

    /**
     * 加入打印文本内容（左右对齐模式），自动回车换行（注：超过打印宽度自动换行）
     * @param textLeftAlign： 左边对齐的文本
     * @param textRightAlign：右边对齐的文本
     * @param fontProperty: 参考字体属性定义
     * @return ret 0：成功
     *            -1：失败
     *          -999: 不支持该功能（可以不支持）
     */
    public int appendMultiText(String textLeftAlign, String textRightAlign, int fontProperty){
        int result = -1;
        // TODO 打印机厂商按此描述实现功能
        int fontSize = DEF_FONT_SIZE;
        int fontStyle = 0;
        int nowrap = 0;// 自动回车换行
        int xPoint = 0;
        if (mPrinter != null) {
            boolean fontBold = false;
            boolean fontItalic = false;
            if ((fontProperty & FONT_SIZE_NORMAL) != 0) {
                fontSize = DEF_FONT_SIZE;
            } else if ((fontProperty & FONT_SIZE_SMALL) != 0) {
                fontSize = DEF_FONT_SIZE_SMALL;
            } else if ((fontProperty & FONT_SIZE_BIG) != 0) {
                fontSize = DEF_FONT_SIZE_BIG;
            }

            if ((fontProperty & FONT_STYLE_BASIC) != 0) {
                fontStyle |= 0;
            }
            if ((fontProperty & FONT_STYLE_BOLD) != 0) {
                fontStyle |= 0x0001;
                fontBold = true;
            }
            if ((fontProperty & FONT_STYLE_ITALIC) != 0) {
                fontStyle |= 0x0002;
                fontItalic = true;
            }
            if (textLeftAlign != null) {
                xPoint = 0;
                currentYPoint += mPrinter.drawTextEx(textLeftAlign, xPoint, currentYPoint, MAX_PAGEWIDTH, -1, "arial",
                        fontSize, 0, fontStyle, nowrap);
            }
            Log
                    .d("koolPOS", " appendMultiText  textLeftAlign curHeigth ==" + currentYPoint);
            if (textRightAlign != null) {
                Paint mPaint = getPaint(fontSize, fontBold, fontItalic);
                float totalWidth = mPaint.measureText(textRightAlign);
                int fontHeight = getFontHight(mPaint);
                if(totalWidth <= MAX_PAGEWIDTH) {
                    xPoint = (int)(MAX_PAGEWIDTH - totalWidth);
                    currentYPoint += mPrinter.drawTextEx(textRightAlign, xPoint, currentYPoint, MAX_PAGEWIDTH, -1, "arial", fontSize, 0,
                            fontStyle, nowrap);
                } else {
                    try{
                        char[] textChar = textRightAlign.toCharArray();
                        float curLinePyWidth = 0;
                        int length = textChar.length;
                        int i = 0;
                        int offset = 0;
                        int charCount = 0;
                        while(i < length) {
                            curLinePyWidth = curLinePyWidth + mPaint.measureText(textChar, i, 1);
                            charCount++;
                            switch(textChar[i]) {
                                case 0x0a:{//\n 坐标Y加一个字体高度
                                    xPoint = 0;
                                    offset = offset + charCount;
                                    currentYPoint =currentYPoint + fontHeight;
                                    curLinePyWidth = 0;
                                    charCount = 0;
                                }
                                break;
                                case 0x0d:{ //\r 坐标Y不变
                                    xPoint = 0;
                                    String curLineText = new String(textChar, offset, charCount);
                                    offset = offset + charCount;
                                    xPoint = (int)(MAX_PAGEWIDTH - curLinePyWidth);
                                    mPrinter.drawTextEx(curLineText, xPoint, currentYPoint, MAX_PAGEWIDTH, -1, "arial", fontSize, 0,
                                            fontStyle, nowrap);
                                    curLinePyWidth = 0;
                                    charCount = 0;
                                }
                                break;
                                /*case 0x09:{//\t

                                }
                                break;
                                case 0x08:{//\b

                                }
                                break;*/
                                default: {
                                    float scalar = 0;
                                    if(i + 1 < length)
                                        scalar =mPaint.measureText(textChar, i + 1, 1);
                                    if(curLinePyWidth + scalar >= MAX_PAGEWIDTH) {
                                        xPoint = 0;
                                        //if charCount < 0 || offset < 0 || offset + charCount > data.length
                                        String curLineText = new String(textChar, offset, charCount);
                                        offset = offset + charCount;
                                        xPoint = (int)(MAX_PAGEWIDTH - curLinePyWidth);
                                        currentYPoint += mPrinter.drawTextEx(curLineText, xPoint, currentYPoint, MAX_PAGEWIDTH, -1, "arial", fontSize, 0,
                                                fontStyle, nowrap);
                                        curLinePyWidth = 0;
                                        charCount = 0;
                                    } else {
                                        if(i + 1 == length) {
                                            String curLineText = new String(textChar, offset, charCount);
                                            xPoint = (int)(MAX_PAGEWIDTH - curLinePyWidth);
                                            currentYPoint += mPrinter.drawTextEx(curLineText, xPoint, currentYPoint, MAX_PAGEWIDTH, -1, "arial", fontSize, 0,
                                                    fontStyle, nowrap);
                                        }
                                    }
                                }
                                break;
                            }
                            i++;
                        }
                    } catch(IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.d("koolPOS", " appendMultiText  textRightAlign curHeigth =="
                    + currentYPoint);
            result = 0;
        }
        return result;
    }

    /**
     * 加入打印条形码（默认高度），自动回车换行
     * @param barCode：条形码信息（字符串）
     * @param alignment：参考内容对齐定义
     * @return ret 0：成功
     *            -1：失败
     *          -999: 不支持该功能（可以不支持）
     */
    public int appendBarCode(String barCode, int alignment){
        int ret = -1;
        // TODO 打印机厂商按此描述实现功能
        if (mPrinter != null && barCode != null) {
            int xPoint = 0;
            if(alignment == CONTENT_ALIGN_LEFT) {
                currentYPoint += mPrinter.drawBarcode(barCode, xPoint, currentYPoint, 20, BARCODE_LINE_WIDTH, BARCODE_HEIGTH, 0);// code128
            } else if(alignment == CONTENT_ALIGN_RIGHT) {
                currentYPoint += mPrinter.drawBarcode(barCode, xPoint, currentYPoint, 20, BARCODE_LINE_WIDTH, BARCODE_HEIGTH, 0);// code128
            } else if(alignment == CONTENT_ALIGN_CENTER) {
                currentYPoint += mPrinter.drawBarcode(barCode, xPoint, currentYPoint, 20, BARCODE_LINE_WIDTH, BARCODE_HEIGTH, 0);// code128
            }
            Log.d("koolPOS", " appendBarCode curHeigth= " + currentYPoint);
            ret = 0;
        }
        return ret;
    }

    /**
     * 加入打印二维码（默认正方形大小），自动回车换行
     * @param qrCode：二维码信息（字符串）
     * @param alignment：参考内容对齐定义
     * @return ret 0：成功
     *            -1：失败
     *          -999: 不支持该功能
     */
    public int appendQRCode(String qrCode, int alignment){
        int result = -1;
        // TODO 打印机厂商按此描述实现功能
        if (mPrinter != null && qrCode != null) {
            int xPoint = 0;
            if(alignment == CONTENT_ALIGN_LEFT) {
                currentYPoint += mPrinter.drawBarcode(qrCode, xPoint, currentYPoint, 58, QRCODE_LINE_WIDTH, QRCODE_HEIGTH, 0);
            } else if(alignment == CONTENT_ALIGN_CENTER) {
                currentYPoint += mPrinter.drawBarcode(qrCode, xPoint, currentYPoint, 58, QRCODE_LINE_WIDTH, QRCODE_HEIGTH, 0);
            } else if(alignment == CONTENT_ALIGN_RIGHT) {
                currentYPoint += mPrinter.drawBarcode(qrCode, xPoint, currentYPoint, 58, QRCODE_LINE_WIDTH, QRCODE_HEIGTH, 0);
            }
            Log.d("koolPOS", " appendQRCode curHeigth= " + currentYPoint);
            result = 0;
        }
        return result;
    }

    /**
     * 加入打印位图，自动回车换行
     * @param bitmap：位图信息
     * @param alignment：参考内容对齐定义
     * @return ret 0：成功
     *            -1：失败
     */
    public int appendBitmap(Bitmap bitmap, int alignment){
        int result = -1;
        // TODO 打印机厂商按此描述实现功能
        if (mPrinter != null && bitmap != null) {
            int xPoint = 0;
            int width = bitmap.getWidth();
            int heigth = bitmap.getHeight();
            if(alignment == CONTENT_ALIGN_LEFT) {
                result = mPrinter.drawBitmap(bitmap, xPoint, currentYPoint);
            } else if(alignment == CONTENT_ALIGN_RIGHT) {
                if(width >= MAX_PAGEWIDTH) {
                    xPoint = 0;
                } else {
                    xPoint = (MAX_PAGEWIDTH - width);
                }
                result = mPrinter.drawBitmap(bitmap, xPoint, currentYPoint);
            } else if(alignment == CONTENT_ALIGN_CENTER) {
                if(width >= MAX_PAGEWIDTH) {
                    xPoint = 0;
                } else {
                    xPoint = (MAX_PAGEWIDTH - width) /2;
                }
                result = mPrinter.drawBitmap(bitmap, xPoint, currentYPoint);
            }
            if (result == 0)
                currentYPoint += heigth;
            Log.d("koolPOS", " appendBitmap curHeigth= " + currentYPoint + "==xPoint= " + xPoint);
        }
        return result;
    }

    /**
     * 开始打印当前预排版缓冲页面内容
     * @return ret 0：成功
     *            -1：失败
     *          -100: 其他故障
     */
    public int prtLayout(){
        int result = -1;
        // TODO 打印机厂商按此描述实现功能
        if (mPrinter != null && currentYPoint > 0) {
            result = mPrinter.printPage(0);
            currentYPoint = 0;
            mPrinter.clearPage();
            mPrinter.setupPage(-1, -1);
        }
        return result;
    }

    //----------酷银定义方法【POS厂商请勿改动】-----结束-----

    //----------POS厂商定义内部类或方法【POS厂商在实现层内部使用】-----开始-----
    private volatile static UBXPrinterManager singleton;
    private UBXPrinterManager() {
    }

    public static UBXPrinterManager getInstance() {
        if (singleton == null) {
            synchronized (UBXPrinterManager.class) {
                if (singleton == null) {
                    singleton = new UBXPrinterManager();
                }
            }
        }
        return singleton;
    }
    private int getFontHight(Paint paint) {
        // FontMetrics对象
        FontMetrics fontMetrics = paint.getFontMetrics();
        int fontHeight = (int)Math.ceil(fontMetrics.descent - fontMetrics.ascent);
        Log.d("koolPOS", "fontHeight  is:" + fontHeight);
        return  fontHeight;
    }
    private Paint getPaint(int size, boolean bold, boolean  fontItalic) {
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.reset();
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(size);
        mPaint.setFakeBoldText(bold);
        if(fontItalic)
            mPaint.setTextSkewX(-0.5f);
        return mPaint;
    }
    //----------POS厂商定义内部类或方法【POS厂商在实现层内部使用】-----结束-----

}
