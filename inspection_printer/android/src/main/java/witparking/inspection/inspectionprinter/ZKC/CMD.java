package android.src.main.java.witparking.inspection.inspectionprinter.ZKC;

public class CMD {
	private static byte ESC=0x1B;
	private static byte FS=0x1C;
	private static byte GS=0x1D;
	private static byte RS=0x1E;
	private static byte US=0x1F;

	private static byte SP=0x20;


	/**
	 * �����ַ������ӡ
	 */
	public static byte[] ESC_double_width_print=new byte[]{ESC,0x0E};

	/**
	 * ȡ���ַ������ӡ
	 */
	public static byte[] ESC_double_width_print_cancel=new byte[]{ESC,0x14};

	/**
	 * �����ַ��Ҽ��
	 */
	public static byte[] ESC_margin_right=new byte[]{ESC,SP,0};


	/**
	 * ѡ���ַ���ӡģʽ
	 */
	public static byte[] ESC_font_style=new byte[]{ESC,'!',0};

	/**
	 * ���þ��Դ�ӡλ��
	 */
	public static byte[] ESC_absolute=new byte[]{ESC,'$',0,0};

	/**
	 * ѡ��/ȡ���»���ģʽ
	 */
	public static byte[] ESC_underline=new byte[]{ESC,'-',0};

	/**
	 * ����Ĭ���м��
	 */
	public static byte[] ESC_linespace_default=new byte[]{ESC,0x32};

	/**
	 * �����м��
	 */
	public static byte[] ESC_linespace=new byte[]{ESC,0x33,0};

	/**
	 * ��ʼ����ӡ��
	 */
	public static byte[] ESC_init=new byte[]{ESC,0x40};

	/**
	 * ���Ʒ�������ʾ
	 */
	public static byte[] ESC_buzzer=new byte[]{ESC,'B',0,0};

	/**
	 * ���Ʒ�������ʾ��ָʾ����˸
	 */
	public static byte[] ESC_buzzer_led=new byte[]{ESC,'C',0,0,0};

	/**
	 * ѡ��/ȡ���Ӵ�ģʽ
	 */
	public static byte[] ESC_font_blod =new byte[]{ESC,'E',0};

	/**
	 * ѡ��/ȡ��˫�ش�ӡģʽ
	 */
	public static byte[] ESC_double_print=new byte[]{ESC,'G',0};

	/**
	 * ��ӡ����ֽn����
	 */
	public static byte[] ESC_print_feed=new byte[]{ESC,'J',0};

	/**
	 * ѡ���ֺ�
	 */
	public static byte[] ESC_font_size=new byte[]{ESC,'M',0};

	/**
	 * ���ô�ӡ�����������浽Flash��
	 */
	public static byte[] ESC_setting_parameter_save=new byte[]{ESC,'N',0,0};

	/**
	 * �����ַ�����
	 */
	public static byte[] ESC_double_width=new byte[]{ESC,'U',0};

	/**
	 * �����ַ�������
	 */
	public static byte[] ESC_double_width_height=new byte[]{ESC,'W',0};

	/**
	 * ������Ժ����ӡλ��
	 */
	public static byte[] ESC_x_point=new byte[]{ESC,0x5C,0,0};

	/**
	 * ѡ����뷽ʽ
	 */
	public static byte[] ESC_align=new byte[]{ESC,'a',0};

	/**
	 * ��ӡ����ǰ��ֽn�ַ���
	 */
	public static byte[] ESC_print_feed_line=new byte[]{ESC,'d',0};

	/**
	 * ��ӡ��ȫ��ֽ
	 */
	public static byte[] ESC_cut_all=new byte[]{ESC,'i'};

	/**
	 * ��ӡ������ֽ
	 */
	public static byte[] ESC_cut_half=new byte[]{ESC,'m'};

	/**
	 * ���ô���ҳ
	 */
	public static byte[] ESC_codepage=new byte[]{ESC,'t',0};

	/**
	 * ��ѯ��ӡ��״̬
	 */
	public static byte[] ESC_print_status=new byte[]{ESC,'v'};

	/**
	 * ��ѯ��ӡ���
	 */
	public static byte[] ESC_print_result=new byte[]{ESC,'w'};

	/**
	 * ѡ��/ȡ�����ô�ӡģʽ
	 */
	public static byte[] ESC_handstand=new byte[]{ESC,'{',0};




	/*************************************FSָ��********************************************/

	/**
	 * �����ַ�ģʽ
	 */
	public static byte[] FS_font_style=new byte[]{FS,'!',0};

	/**
	 * �����ַ��»���
	 */
	public static byte[] FS_underline =new byte[]{FS,'-',0};

	/**
	 * ѡ��/ȡ���ַ��Ŵ�������ӡ
	 */
	public static byte[] FS_font_double=new byte[]{FS,'W',0};




	/*************************************GSָ��********************************************/
	/**
	 * ѡ���ַ���С
	 */
	public static byte[] GS_font_size=new byte[]{GS,'!',0};

	/**
	 * ѡ��/ȡ�����״�ӡģʽ
	 */
	public static byte[] GS_highlight=new byte[]{GS,'B',0};

	/**
	 * ѡ��HRI�ַ��Ĵ�ӡλ��
	 */
	public static byte[] GS_hri_location=new byte[]{GS,'H',0};

	/**
	 * ������߾�
	 */
	public static byte[] GS_left_space=new byte[]{GS,'L',0,0};

	/**
	 * ���ô�ӡ������
	 */
	public static byte[] GS_width_area=new byte[]{GS,'W',0,0};

	/**
	 * ѡ������߶�
	 */
	public static byte[] GS_barcode_height=new byte[]{GS,'h',0};

	/**
	 * ѡ������ģ����
	 */
	public static byte[] GS_barcode_width=new byte[]{GS,'w',0};






	/*************************************RSָ��********************************************/
	/**
	 * ��������ģʽ
	 */
	public static byte[] RS_sleep=new byte[]{RS,0x01};

	/**
	 * �����Զ��������߳�ʱʱ��
	 */
	public static byte[] RS_autosleep_timeout=new byte[]{RS,0x02,0,0,0,0,0};

	/**
	 * ����/��ֹ��ӡ
	 */
	public static byte[] RS_alow_print=new byte[]{RS,0x03,0,0,0,0,0};

	/**
	 * �����Զ���ֹ��ӡ��ʱʱ��
	 */
	public static byte[] RS_autoalow_timeout=new byte[]{RS,0x04,0,0,0,0,0};

	/**
	 * ��ѯϵͳ��Դ��ѹ
	 */
	public static byte[] RS_voltage =new byte[]{RS,0x05};

	/**
	 * ��ѯ����汾
	 */
	public static byte[] RS_version=new byte[]{RS,0x20};

	/**
	 * ���봮�ڵ���ģʽ
	 */
	public static byte[] RS_serialport_debug=new byte[]{RS,(byte) 0xde};

	/**
	 * ��ӡ����λ
	 */
	public static byte[] RS_reset=new byte[]{RS,(byte) 0xdf,0x72,0x65,0x73,0x65,0x74};





	/*************************************USָ��********************************************/

	/**
	 * ��ӡ�Լ���Ϣ
	 */
	public static byte[] US_self_check=new byte[]{US,01};

	/**
	 * ����QrCode��ά����뷽ʽ
	 */
	public static byte[] US_qrcode_align=new byte[]{US,0x12,0};

	/**
	 * ����QrCode��ά�붥���հ׸߶�
	 */
	public static byte[] US_qrcode_top_space=new byte[]{US,0x13,0};

	/**
	 * ����QrCode��ά��ײ��հ׸߶�
	 */
	public static byte[] US_qrcode_bottom_space=new byte[]{US,0x14,0};

	/**
	 *����QrCode��ά����Сģ�鵥Ԫ���
	 */
	public static byte[] US_qrcode_element_width=new byte[]{US,0x15,0};




	/*************************************����ָ��********************************************/
	/**
	 * ����һ�Ʊ��λ�ÿ�ʼ��ӡ
	 */
	public static byte[] HT=new byte[]{0x09};

	/**
	 * ��ӡ������
	 */
	public static byte[] LF=new byte[]{0x0A};

	/**
	 * ��ֽ����һ���ڱ�/��϶��
	 */
	public static byte[] FF=new byte[]{0x0C};

	/**
	 * ��ӡ����������
	 */
	public static byte[] CR=new byte[]{0x0D};

	/**
	 * ��ֽ����һ���ڱ괦
	 */
	public static byte[] SO=new byte[]{0x0E};
}
