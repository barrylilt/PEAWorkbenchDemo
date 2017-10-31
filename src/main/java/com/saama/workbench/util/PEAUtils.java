package com.saama.workbench.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Security;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.w3c.dom.Document;

import com.saama.workbench.bean.NameCodeDistanceBean;
import com.saama.workbench.model.SblCustomerHierarchy;
import com.saama.workbench.model.SblProductHierarchy;
import com.saama.workbench.model.UserProfile;
import com.saama.workbench.service.IHarmonizerService;

public class PEAUtils {

	private static final Logger logger = Logger.getLogger(PEAUtils.class);
	private static DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
	private static DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
	private static Map<String, String> PROD_HIERARCHY_MAP = new HashMap<String, String>();
	private static Map<String, String> CUST_HIERARCHY_MAP = new HashMap<String, String>();
	
	private static Map<String, Object> CACHE_MAP = new HashMap<>();
	private static Map<String, String> KEY_LAST_DATE = new HashMap<>();
	private static SimpleDateFormat sdf_dmy = new SimpleDateFormat("DD-MM-YYYY");
	
	
	// Local cache
	public static void putIntoCacheMap (String key, Object value) {
		KEY_LAST_DATE.put(key, sdf_dmy.format(new Date()));
		CACHE_MAP.put(key, value);
	}
	
	public static Object getFromCacheMap (String key) {
		String keyLastDate = KEY_LAST_DATE.getOrDefault(key, "");
		if (!PEAUtils.isEmpty(keyLastDate)) {
			try {
				if (sdf_dmy.parse(keyLastDate).compareTo(sdf_dmy.parse(sdf_dmy.format(new Date()))) < 0) {
					return null;
				}
			}
			catch (Exception e) {
				logger.warn("Exception in getFromCacheMap - " + key, e);
			}
		}
		Object o = CACHE_MAP.get(key);
		if (o != null) {
			return o;
		}
		return null;
	}
	
	public static Object getFromCacheMap (String key, Object _default) {
		Object obj = getFromCacheMap(key);
		if (obj != null) {
			return obj;
		}
		return _default;
	}
	
	
	public static String getProdHierarchy() {
		try {
			if (PROD_HIERARCHY_MAP == null) {
				PROD_HIERARCHY_MAP = new HashMap<String, String>();
			}
			if (PROD_HIERARCHY_MAP.size() > 0) {
//				SimpleDateFormat sdf_dmy = new SimpleDateFormat("DD-MM-YYYY");
				String key = sdf_dmy.format(new Date());
				if (PROD_HIERARCHY_MAP.get(key) != null) {
					return PROD_HIERARCHY_MAP.get(key);
				}
			}
		}
		catch (Exception e) {
			logger.error("Exception while getProdHierarchy", e);
		}
		return null;
	}
	
	public static void setProdHierarchy(String entry) {
		try {
			PROD_HIERARCHY_MAP = new HashMap<String, String>();
//			SimpleDateFormat sdf_dmy = new SimpleDateFormat("DD-MM-YYYY");
			String key = sdf_dmy.format(new Date());
			PROD_HIERARCHY_MAP.put(key, entry);
		}
		catch (Exception e) {
			logger.error("Exception while setProdHierarchy", e);
		}
	}
	
	public static String getCustHierarchy() {
		try {
			if (CUST_HIERARCHY_MAP == null) {
				CUST_HIERARCHY_MAP = new HashMap<String, String>();
			}
			if (CUST_HIERARCHY_MAP.size() > 0) {
//				SimpleDateFormat sdf_dmy = new SimpleDateFormat("DD-MM-YYYY");
				String key = sdf_dmy.format(new Date());
				if (CUST_HIERARCHY_MAP.get(key) != null) {
					return CUST_HIERARCHY_MAP.get(key);
				}
			}
		}
		catch (Exception e) {
			logger.error("Exception while getCustHierarchy", e);
		}
		return null;
	}
	
	public static void setCustHierarchy(String entry) {
		try {
			CUST_HIERARCHY_MAP = new HashMap<String, String>();
//			SimpleDateFormat sdf_dmy = new SimpleDateFormat("DD-MM-YYYY");
			String key = sdf_dmy.format(new Date());
			CUST_HIERARCHY_MAP.put(key, entry);
		}
		catch (Exception e) {
			logger.error("Exception while setCustHierarchy", e);
		}
	}

	public static Object toPrecision(Object dec, int precision) {
		String plain = "";
		if (dec instanceof BigDecimal) {
			plain = ((BigDecimal) dec).movePointRight(precision)
					.toPlainString();
			if (plain.indexOf(".") != -1) {
				return new BigDecimal(plain.substring(0, plain.indexOf(".")))
						.movePointLeft(precision);
			} else {
				return (BigDecimal) dec;
			}
		}
		return null;

	}

	public static Object setProperScale(Object obj, RoundingMode rm, int scale) {
		if (obj instanceof BigDecimal) {
			if (obj != null) {
				BigDecimal bdObj = (BigDecimal) obj;
				rm = RoundingMode.HALF_UP;
				return bdObj.setScale(scale, (rm == null ? RoundingMode.FLOOR
						: rm));
			}
			return null;
		}
		return obj;
	}

	public static boolean isNumeric(String str) {
		if (str != null) {
			try {
				double d = Double.parseDouble(str);
			} catch (NumberFormatException nfe) {
				return false;
			}
			return true;
		}
		return false;
	}

	public static String escapeString(String str) {
		if (str == null) {
			return str;
		}
		str = str
				// .replaceAll("\\\\", "\\\\\\\\") //this is to escape \
				.replaceAll("\\$", "\\\\\\$").replaceAll("\\(", "\\\\\\(")
				.replaceAll("\\)", "\\\\\\)").replaceAll("\\[", "\\\\\\[")
				.replaceAll("\\]", "\\\\\\]").replaceAll("\\<", "\\\\\\<")
				.replaceAll("\\>", "\\\\\\>").replaceAll("\\{", "\\\\\\{")
				.replaceAll("\\}", "\\\\\\}").replaceAll("\\|", "\\\\\\|")
				.replaceAll("\\?", "\\\\\\?");
		return str;
	}

	public static Class<?> getFieldType(Class c, String str) {
		try {
			Field f = c.getDeclaredField(str);
			return f.getType();
		} catch (NoSuchFieldException | SecurityException e) {
			logger.error("Exception in method getFieldType: ", e);
		}
		return null;
	}

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();
		long factor = (long) Math.pow(10, places);
		value = value * factor;
		//double tmp = Math.floor(value);
		double tmp = Math.ceil(value);
		return tmp / factor;
	}

	public static byte[] hexToByteArray(String hex) {
		byte[] bts = new byte[hex.length() / 2];
		for (int i = 0; i < bts.length; i++) {
			bts[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2),
					16);
		}
		return bts;
	}

	public static int compareObjects(Object obj1, Object obj2, int sortDirection)
			throws Exception {

		if (obj1 instanceof Date && obj2 instanceof Date) {
			if ((obj1 == null || ((Date) obj1).toString().trim().equals(""))) {
				if (obj2 != null && !((Date) obj2).toString().trim().equals("")) {
					return -1 * sortDirection;
				} else {
					return 0;
				}
			}
			if (obj2 == null || ((Date) obj2).toString().trim().equals("")) {
				return 1 * sortDirection;
			}
			return ((Date) obj1).compareTo((Date) obj2) * sortDirection;
		}

		else if (obj1 instanceof Boolean && obj2 instanceof Boolean) {
			if ((obj1 == null || ((Boolean) obj1).toString().trim().equals(""))) {
				if (obj2 != null
						&& !((Boolean) obj2).toString().trim().equals("")) {
					return -1 * sortDirection;
				} else {
					return 0;
				}
			}
			if (obj2 == null || ((Boolean) obj2).toString().trim().equals("")) {
				return 1 * sortDirection;
			}
			return ((Boolean) obj1).compareTo((Boolean) obj2) * sortDirection;
		}
		else if (obj1 instanceof Integer && obj2 instanceof Integer) {
			if ((obj1 == null || ((Integer) obj1).toString().trim().equals(""))) {
				if (obj2 != null
						&& !((Boolean) obj2).toString().trim().equals("")) {
					return -1 * sortDirection;
				} else {
					return 0;
				}
			}
			if (obj2 == null || ((Integer) obj2).toString().trim().equals("")) {
				return 1 * sortDirection;
			}
			return ((Integer) obj1).compareTo((Integer) obj2) * sortDirection;
		}

		else if ((obj1 == null || ((String) obj1).trim().equals(""))) {
			if (obj2 != null && !((String) obj2).trim().equals("")) {
				return -1 * sortDirection;
			} else {
				return 0;
			}
		}

		else if (obj2 == null || ((String) obj2).trim().equals("")) {
			return 1 * sortDirection;
		}
		// return (dateFormatter.parse((String)
		// obj1)).compareTo(dateFormatter.parse((String) obj2)) * sortDirection;
		return ((String) obj1).toLowerCase().compareTo(
				((String) obj2).toLowerCase())
				* sortDirection;
	}

	public static boolean convertToBoolean(Object value) {
		List<String> TRUE = new ArrayList<String>();
		TRUE.add("TRUE");
		TRUE.add("YES");
		TRUE.add("ON");
		TRUE.add("Y");
		TRUE.add("1");
		if (value == null) {
			return false;
		} else if (value instanceof Boolean) {
			return (boolean) value;
		} else {
			return TRUE.contains(value.toString().toUpperCase());
		}
		// return false;
	}

	public static List<String> getPropertiesList(String stPropertyName,
			String stDelimitor) {
		List<String> lReturn = null;
		String stProperty = PropertiesUtil.getProperty(stPropertyName);
		if (stProperty != null && stDelimitor != null) {
			String[] stTemp = stProperty.split(stDelimitor);
			lReturn = Arrays.<String> asList(stTemp);
		}
		logger.info("lReturn = " + lReturn);
		return lReturn;
	}

	public static boolean isEmpty(String s) {
		if (s != null && s.trim().length() > 0)
			return false;
		return true;
	}
	
	public static File exportToFileOPSO(Map<String, Object> input) throws Exception {
		File f = null, of = null;
		
		List<String> lColumns = (List<String>) input.get(AppConstants.COLUMNS + AppConstants.OPSO_REPORT_PROMO);
		List<Object[]> reList = (List<Object[]>) input.get(AppConstants.DATA + AppConstants.OPSO_REPORT_PROMO);
		Map<String, String> colIdxMap = (Map<String, String>) input.get(AppConstants.COLUMN_IDX_MAP + AppConstants.OPSO_REPORT_PROMO);
		String exportDrive = PropertiesUtil.getPropertyOrDefault(AppConstants.COMMON_SETTINGS_EXPORT_DRIVE, "C");
		
		try {
			int r = 1;
			
			Resource resource = new ClassPathResource(PropertiesUtil.getProperty(AppConstants.OPSO_REPORT_TEMPLATE_FILENAME));
//			File file = resource.getFile();
			f = resource.getFile(); //new File(PropertiesUtil.getProperty(AppConstants.OPSO_REPORT_FILE_PATH));
			of = new File(exportDrive + ":\\test.xlsx");
			
			boolean skipColIdxMap = false;
//			SXSSFWorkbook hwb = new SXSSFWorkbook(new XSSFWorkbook(f));
			XSSFWorkbook hwb = new XSSFWorkbook(f);
			XSSFSheet sheet = (XSSFSheet) hwb.getSheet("Promo ID Data");
//			SXSSFRow rowhead = (SXSSFRow) sheet.createRow(r++);
			XSSFRow row;
//			f = new File(exportDrive + ":\\test.xls");
			
			
//			for (int iColIdx = 0; iColIdx < lColumns.size(); iColIdx++) {
//				rowhead.createCell(iColIdx).setCellValue(lColumns.get(iColIdx));
//			}
			
			XSSFDataFormat format = (XSSFDataFormat) hwb.createDataFormat();
			CellStyle styleDMY = hwb.createCellStyle();
			CellStyle styleRedPerc = hwb.createCellStyle();
			CellStyle styleRedNum = hwb.createCellStyle();
			
			styleDMY.setDataFormat(format.getFormat(AppConstants.EXL_STYLE_DMY));
			styleRedPerc.setDataFormat(format.getFormat(AppConstants.EXL_STYLE_RED_PERC));
			styleRedNum.setDataFormat(format.getFormat(AppConstants.EXL_STYLE_RED_NUM));
			
			CellStyle styleMY = hwb.createCellStyle();
			styleMY.setDataFormat(format.getFormat("mmm-yyyy"));
			
			// Pivot data - Promotion level
			System.out.println("Pivot data - Promotion level");
			r++;
			for (Object[] ce : reList) {
				row = (XSSFRow) sheet.getRow(r);
				if (row == null)
					row = (XSSFRow) sheet.createRow(r);
				for (int iColIdx = 0; iColIdx < lColumns.size(); iColIdx++) {
					String value = new String();
					int rowColIdx = iColIdx;
					if (!skipColIdxMap && colIdxMap != null && colIdxMap.get(lColumns.get(iColIdx)) != null) {
						rowColIdx = Integer.parseInt(colIdxMap.get(lColumns.get(iColIdx)));
					}
					if (ce[rowColIdx] != null) {
						value = ce[rowColIdx].toString();
					}
					Cell cell = row.getCell(iColIdx);
					if (cell == null) {
						cell = row.createCell(iColIdx);
					}
					
					
					
					if (getExceColIdex("CQ") == iColIdx) {
						String formula = "IF(ISNUMBER(SEARCH(\"exclude\",CONCATENATE(CH" + (r + 1) +",CI" + (r + 1) +",CJ" + (r + 1) +",CK" + (r + 1) +",CL" + (r + 1) +",CM" + (r + 1) +",CN" + (r + 1) +",CO" + (r + 1) +",CP" + (r + 1) +"))), \"exclude\", \"include\")";
						cell.setCellFormula(formula);
					}
					else if (getExceColIdex("CW") == iColIdx) {
//						CellReference cr = new CellReference("CQ" + r);
//						if (cr != null) {
//							Cell c = row.getCell(cr.getCol());
//							if (c != null) {
//								if ("include".equalsIgnoreCase(c.getStringCellValue())) {
//									cell.setCellValue(1);
//								}
//							}
//						}
						String formula = "IF(CQ" + (r + 1) +"=\"include\",CG" + (r + 1) +", \"\")";
						cell.setCellFormula(formula);
						
					}
					else if (getExceColIdex("CR") == iColIdx) {
						String reason = "";
						Map<String, CellReference> hmErrCells = new HashMap<String, CellReference>();
						
//						CellReference actualVolumeUpLift = new CellReference("CH" + r);
//						CellReference negGSV = new CellReference("CI" + r);
//						CellReference ttsGSV = new CellReference("CJ" + r);
//						CellReference pipefill = new CellReference("CK" + r);
						
						hmErrCells.put("-ve actual volume uplift", new CellReference("CH" + r));
						hmErrCells.put("-ve/0 GSV", new CellReference("CI" + r));
						hmErrCells.put("TTS GSV<5", new CellReference("CJ" + r));
						hmErrCells.put("pipefill", new CellReference("CK" + r));
						
						for (Entry<String, CellReference> en : hmErrCells.entrySet()) {
							CellReference cr = en.getValue();
							if (cr != null) {
								Cell c = row.getCell(cr.getCol());
								if (c != null) {
									if ("exclude".equalsIgnoreCase(c.getStringCellValue())) {
										if (PEAUtils.isEmpty(reason)) {
											reason = en.getKey();
										}
										else {
											reason = "several";
										}
									}
								}
							}
						}
						cell.setCellValue(reason);
					}
					else if (getExceColIdex("D") == iColIdx) {
						SimpleDateFormat fm =  new SimpleDateFormat("yyyy-MM-dd") ;
						cell.setCellStyle(styleMY);
						cell.setCellValue(fm.parse(value));
					}
					else if (!PEAUtils.isEmpty(value) && DateUtils.isDateString(value, "dd-mm-yyyy")) {
						SimpleDateFormat fm =  new SimpleDateFormat("yyyy-MM-dd") ;
						cell.setCellStyle(styleDMY);
						cell.setCellValue(fm.parse(value));
					}
					else if (!PEAUtils.isEmpty(value) && PEAUtils.isNumeric(value)) {
						cell.setCellType(Cell.CELL_TYPE_NUMERIC);
						cell.setCellValue(Double.parseDouble(value));
					}
					else if (!PEAUtils.isEmpty(value))
						cell.setCellValue(value);
				}
				r++;
			}
			
			Map<String, String> hmFormulaCells = new HashMap<String, String>();
			hmFormulaCells.put("CG0", "SUM(CG3:CG811)");
			hmFormulaCells.put("CW0", "SUM(CW3:CW811)");
			hmFormulaCells.put("CX0", "IFERROR(CW1/CG1, 0)");
			
			row = (XSSFRow) sheet.getRow(0);
			for (Entry<String, String> en : hmFormulaCells.entrySet()) {
				CellReference cr = new CellReference(en.getKey());
				if (cr != null) {
					Cell c = row.getCell(cr.getCol());
					if (c != null) {
						c.setCellFormula(en.getValue());
					}
				}
			}
			
			
			
			
			
			// Pivot data - Product level
			System.out.println("Pivot data - Product level");
			lColumns = (List<String>) input.get(AppConstants.COLUMNS + AppConstants.OPSO_REPORT_PRODUCT);
			reList = (List<Object[]>) input.get(AppConstants.DATA + AppConstants.OPSO_REPORT_PRODUCT);
			colIdxMap = (Map<String, String>) input.get(AppConstants.COLUMN_IDX_MAP + AppConstants.OPSO_REPORT_PRODUCT);
			
			List<Integer> percColumns = new ArrayList<Integer>();
			percColumns.add(getExceColIdex("BJ"));
			percColumns.add(getExceColIdex("BG"));
			percColumns.add(getExceColIdex("BD"));
			percColumns.add(getExceColIdex("BA"));
			percColumns.add(getExceColIdex("AX"));
			percColumns.add(getExceColIdex("AR"));
			percColumns.add(getExceColIdex("AK"));
			percColumns.add(getExceColIdex("AM"));
			percColumns.add(getExceColIdex("AN"));
			percColumns.add(getExceColIdex("AO"));
			percColumns.add(getExceColIdex("M"));
			percColumns.add(getExceColIdex("N"));
			percColumns.add(getExceColIdex("O"));
			
			
			
			sheet = (XSSFSheet) hwb.getSheet("Data for Pivots");
			r = 3;
			for (Object[] ce : reList) {
				row = (XSSFRow) sheet.getRow(r);
				if (row == null)
					row = (XSSFRow) sheet.createRow(r);
				for (int iColIdx = 0; iColIdx < lColumns.size(); iColIdx++) {
					String value = new String();
					int rowColIdx = iColIdx;
					if (!skipColIdxMap && colIdxMap != null && colIdxMap.get(lColumns.get(iColIdx)) != null) {
						rowColIdx = Integer.parseInt(colIdxMap.get(lColumns.get(iColIdx)));
					}
					if (ce[rowColIdx] != null) {
						value = ce[rowColIdx].toString();
					}
					Cell cell = row.getCell(iColIdx);
					if (cell == null) {
						cell = row.createCell(iColIdx);
					}
					
					if (getExceColIdex("BO") == iColIdx) {
						String formula = "IF(L" + (r + 1) +"=\"include\",BC" + (r + 1) +", \"\")";
						cell.setCellFormula(formula);
					}
					else if (getExceColIdex("BP") == iColIdx) {
						String formula = "IF(L" + (r + 1) +"=\"include\",BM" + (r + 1) +", \"\")";
						cell.setCellFormula(formula);
					}
					else if (getExceColIdex("BN") == iColIdx) {
						String formula = "IF(L" + (r + 1) +"=\"include\",BI" + (r + 1) +", \"\")";
						cell.setCellFormula(formula);
					}
					else if (getExceColIdex("BQ") == iColIdx) {
						String formula = "IF(L" + (r + 1) +"=\"include\",AZ" + (r + 1) +", \"\")";
						cell.setCellFormula(formula);
					}
					else if (getExceColIdex("AK") == iColIdx) {
						String formula = "IFERROR(IF(AH" + (r + 1) +"=0, \"\", AH" + (r + 1) +"/AG" + (r + 1) +"),0)";
						cell.setCellFormula(formula);
					}
					else if (getExceColIdex("AR") == iColIdx) {
						String formula = "IFERROR(AQ" + (r + 1) +"/AP" + (r + 1) +",0)"; 
						cell.setCellFormula(formula);
					}
					else if (getExceColIdex("AX") == iColIdx) {
						String formula = "IFERROR(AW" + (r + 1) +"/AV" + (r + 1) +",0)"; 
						cell.setCellFormula(formula);
					}
					else if (getExceColIdex("BA") == iColIdx) {
						String formula = "IFERROR(AZ" + (r + 1) +"/AY" + (r + 1) +",0)"; 
						cell.setCellFormula(formula);
					}
					else if (getExceColIdex("BD") == iColIdx) {
						String formula = "IFERROR(BC" + (r + 1) +"/BB" + (r + 1) +",0)"; 
						cell.setCellFormula(formula);
					}
					else if (getExceColIdex("BG") == iColIdx) {
						String formula = "IFERROR(BF" + (r + 1) +"/BE" + (r + 1) +",0)"; 
						cell.setCellFormula(formula);
					}
					else if (getExceColIdex("BJ") == iColIdx) {
						String formula = "IFERROR(BI" + (r + 1) +"/BH" + (r + 1) +",0)"; 
						cell.setCellFormula(formula);
					}
					else if (!PEAUtils.isEmpty(value) && DateUtils.isDateString(value, "dd-mm-yyyy")) {
						SimpleDateFormat fm =  new SimpleDateFormat("yyyy-MM-dd") ;
						cell.setCellStyle(styleDMY);
						cell.setCellValue(fm.parse(value));
						
					}
					else if (!PEAUtils.isEmpty(value) && PEAUtils.isNumeric(value)) {
						cell.setCellType(Cell.CELL_TYPE_NUMERIC);
						cell.setCellStyle(styleRedNum);
						cell.setCellValue(Double.parseDouble(value));
						
					}
					else if (!PEAUtils.isEmpty(value))
							cell.setCellValue(value);
					
					if (percColumns.contains(iColIdx)) {
						cell.setCellStyle(styleRedPerc);
					}
				}
				r++;
			}
			
			hmFormulaCells = new HashMap<String, String>();
			hmFormulaCells.put("AA0", "SUM(AA4:AA10000)");
			hmFormulaCells.put("AB0", "IFERROR(+BO2/AA1, 0)");
			hmFormulaCells.put("AC0", "SUM(AC4:AC10000)");
			hmFormulaCells.put("AD0", "SUM(AD4:AD10000)");
			hmFormulaCells.put("AE0", "SUM(AE4:AE10000)");
			hmFormulaCells.put("BC0", "SUM(BC4:BC10000)");
			hmFormulaCells.put("BI0", "SUM(BI4:BI10000)");
			hmFormulaCells.put("BO0", "IFERROR(BN2/BO2, 0)");
			
			row = (XSSFRow) sheet.getRow(0);
			for (Entry<String, String> en : hmFormulaCells.entrySet()) {
				CellReference cr = new CellReference(en.getKey());
				if (cr != null) {
					Cell c = row.getCell(cr.getCol());
					if (c != null) {
						c.setCellFormula(en.getValue());
					}
				}
			}
			
			hmFormulaCells = new HashMap<String, String>();
			hmFormulaCells.put("BM1", "SUM(BM4:BM10000)");
			hmFormulaCells.put("BN1", "SUM(BN4:BN10000)");
			hmFormulaCells.put("BO1", "SUM(BO4:BO10000)");
			hmFormulaCells.put("BP1", "SUM(BP4:BP10000)");
			hmFormulaCells.put("BQ1", "SUM(BQ4:BQ10000)");
			
			
			row = (XSSFRow) sheet.getRow(1);
			for (Entry<String, String> en : hmFormulaCells.entrySet()) {
				CellReference cr = new CellReference(en.getKey());
				if (cr != null) {
					Cell c = row.getCell(cr.getCol());
					if (c != null) {
						c.setCellFormula(en.getValue());
					}
				}
			}
			
			
			
			
			FileOutputStream fileOut = new FileOutputStream(of);
			hwb.write(fileOut);
			fileOut.close();

			of = Readxlsx.getPivot(of);
			
			return of;

		} catch (Exception e) {
			logger.error("Exception in exportToFileOPSO - " + e.getMessage());
			throw e;
		}
	}
	
	public static File exportToFile(Map<String, Object> input) throws Exception {
		File f = null;
		
		List<String> lColumns = (List<String>) input.get(AppConstants.COLUMNS);
		Map<String, Integer> hmColumnsCnt = new HashMap<String, Integer>();
		List<Object[]> reList = (List<Object[]>) input.get(AppConstants.DATA);
		List<String> alDisplayColList = (List<String>) input.get(AppConstants.DISPLAY_COL_LIST);
		List<String> alColIdxCurr = (List<String>) input.get(AppConstants.COLUMN_CURRENCY);
		List<String> alColIdxPerc = (List<String>) input.get(AppConstants.COLUMN_PERCENTAGE);
		
		Map<String, String> colIdxMap = (Map<String, String>) input.get(AppConstants.COLUMN_IDX_MAP);
		String exportDrive = PropertiesUtil.getPropertyOrDefault(AppConstants.COMMON_SETTINGS_EXPORT_DRIVE, "C");
		
		List<String> numStringColumns = Arrays.asList(new String[]{"CUEAN", "TUEAN", "PROMOID"});
		List<Integer> numStringColumnIdx = new ArrayList<Integer>();
		
		try {
			int r = 0;
			boolean skipColIdxMap = false;
			SXSSFWorkbook hwb = new SXSSFWorkbook();
			SXSSFSheet sheet = (SXSSFSheet) hwb.createSheet("data");
			SXSSFRow rowhead = (SXSSFRow) sheet.createRow(r++);
			SXSSFRow row;
			f = new File(exportDrive + ":\\test.xlsx");
			
			XSSFDataFormat format = (XSSFDataFormat) hwb.createDataFormat();
			CellStyle styleRedPerc = hwb.createCellStyle();
			CellStyle styleRedNum = hwb.createCellStyle();
			CellStyle styleRedCurr = hwb.createCellStyle();
			CellStyle styleDMY = hwb.createCellStyle();
			CellStyle styleDecimal = hwb.createCellStyle();
			styleRedPerc.setDataFormat(format.getFormat(AppConstants.EXL_STYLE_RED_PERC));
//			styleRedCurr.setDataFormat(format.getFormat("#,##0.00;[Red](#,##0.00)"));
			styleRedCurr.setDataFormat(format.getFormat(AppConstants.EXL_STYLE_RED_CURR));
			styleRedNum.setDataFormat(format.getFormat(AppConstants.EXL_STYLE_RED_NUM));
			styleDMY.setDataFormat(format.getFormat(AppConstants.EXL_STYLE_DMY));
			
			if (input.get(AppConstants.TABMENU) != null && AppConstants.DATAAVAILABILITY.equalsIgnoreCase(input.get(AppConstants.TABMENU).toString())) {
				rowhead.createCell(0).setCellValue(lColumns.get(0));
				
				Cell cell = rowhead.createCell(1);
				cell.setCellValue(input.get("prevMonth").toString());
				CellUtil.setAlignment(cell, hwb, CellStyle.ALIGN_CENTER);
				
				cell = rowhead.createCell(5);
				cell.setCellValue(input.get("curMonth").toString());
				CellUtil.setAlignment(cell, hwb, CellStyle.ALIGN_CENTER);
				
				rowhead = (SXSSFRow) sheet.createRow(r++);
				
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 4));
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 5, 8));
				sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));
				
				skipColIdxMap = true;
			}
			
			if (input.get(AppConstants.TABMENU) != null && AppConstants.MAPPING_DATA.equalsIgnoreCase(input.get(AppConstants.TABMENU).toString())) {
				
				
				if (input.get("mergedCol1") != null) {
					String[] mergedCol1 = input.get("mergedCol1").toString().split(",");
					
					Cell cell = rowhead.createCell(0);
					cell.setCellValue(input.get("ExternalProduct").toString());
					CellUtil.setAlignment(cell, hwb, CellStyle.ALIGN_CENTER);
					
					if (mergedCol1.length > 3)
						sheet.addMergedRegion(new CellRangeAddress(Integer.parseInt(mergedCol1[0]), 
							Integer.parseInt(mergedCol1[1]), 
							Integer.parseInt(mergedCol1[2]), 
							Integer.parseInt(mergedCol1[3])));
				}
				if (input.get("mergedCol2") != null) {
					String[] mergedCol2 = input.get("mergedCol2").toString().split(",");
					
					Cell cell = rowhead.createCell(Integer.parseInt(mergedCol2[2]));
					cell.setCellValue("Unilever Product");
					CellUtil.setAlignment(cell, hwb, CellStyle.ALIGN_CENTER);
					
					if (mergedCol2.length > 3)
						sheet.addMergedRegion(new CellRangeAddress(Integer.parseInt(mergedCol2[0]), 
							Integer.parseInt(mergedCol2[1]), 
							Integer.parseInt(mergedCol2[2]), 
							Integer.parseInt(mergedCol2[3])));
				}
				
				rowhead = (SXSSFRow) sheet.createRow(r++);
				
			}
			for (int iColIdx = 0; iColIdx < lColumns.size(); iColIdx++) {
				if (numStringColumns.contains(lColumns.get(iColIdx).toUpperCase())) {
					numStringColumnIdx.add(iColIdx);
				}
				rowhead.createCell(iColIdx).setCellValue(lColumns.get(iColIdx));
			}
			
			for (Object[] ce : reList) {
				row = (SXSSFRow) sheet.createRow(r);
				hmColumnsCnt = new HashMap<String, Integer>();
				for (int iColIdx = 0; iColIdx < lColumns.size(); iColIdx++) {
					String value = new String();
					int rowColIdx = iColIdx;
					
					hmColumnsCnt.put(lColumns.get(iColIdx), hmColumnsCnt.getOrDefault(lColumns.get(iColIdx), 0) + 1);
					
					if (!skipColIdxMap && colIdxMap != null && colIdxMap.get(lColumns.get(iColIdx)) != null) {
//						if (colIdxMap.get(lColumns.get(iColIdx)).split(",").length > 0)
//						if (hmColumnsCnt.getOrDefault(lColumns.get(iColIdx), 0) > 1) {
							 if (colIdxMap.get(lColumns.get(iColIdx)).split(",").length >= hmColumnsCnt.getOrDefault(lColumns.get(iColIdx), 0)) {
								 rowColIdx = Integer.parseInt(colIdxMap.get(lColumns.get(iColIdx)).split(",")[hmColumnsCnt.getOrDefault(lColumns.get(iColIdx), 0) - 1]);
							 }
//						}
//						else
//							rowColIdx = Integer.parseInt(colIdxMap.get(lColumns.get(iColIdx)));
					}
					if (ce[rowColIdx] != null) {
						value = ce[rowColIdx].toString();
					}
					Cell cell = row.createCell(iColIdx);
					if (!PEAUtils.isEmpty(value) && PEAUtils.isNumeric(value) && !numStringColumnIdx.contains(iColIdx)) {
						cell.setCellType(Cell.CELL_TYPE_NUMERIC);
						cell.setCellValue(Double.parseDouble(value));
//						if(alColIdxCurr!=null){
//							if (alColIdxCurr.contains(alDisplayColList.get(iColIdx).replace(AppConstants.COL, ""))) {
							if (alColIdxCurr != null && alColIdxCurr.contains(rowColIdx+"")) {
								cell.setCellStyle(styleRedCurr);
							}
//							else if (alColIdxPerc.contains(alDisplayColList.get(iColIdx).replace(AppConstants.COL, ""))) {
							else if (alColIdxPerc != null && alColIdxPerc.contains(rowColIdx+"")) {
								cell.setCellStyle(styleRedPerc);
							}
							else {
								cell.setCellStyle(styleRedNum);
							}
//						}
//						else {
//							cell.setCellStyle(styleRedNum);
//						}
					}
					else if (!PEAUtils.isEmpty(value) && DateUtils.isDateString(value, "dd-mm-yyyy")) {
						SimpleDateFormat fm =  new SimpleDateFormat("yyyy-MM-dd") ;
						cell.setCellStyle(styleDMY);
						//System.out.println(value);
						cell.setCellValue(fm.parse(value));
						
					}
					else {
						cell.setCellValue(value);
					}
					
				}
				r++;
			}
			if (input.get(AppConstants.TABMENU) != null && AppConstants.DATAAVAILABILITY.equalsIgnoreCase(input.get(AppConstants.TABMENU).toString())) {
				int m=r;
				row = (SXSSFRow) sheet.createRow(r++);
				Cell total = row.createCell(0);
				total.setCellValue("Total");
				Cell totalShipmentCell = row.createCell(1);
				totalShipmentCell.setCellStyle(styleRedNum);
				String cellsFromB = "b3:b"+m;
				
				totalShipmentCell.setCellFormula("SUM("+cellsFromB+")");
				Cell totalEpos = row.createCell(2);
				totalEpos.setCellStyle(styleRedNum);
				String cellsFromC = "c3:c"+m;
				totalEpos.setCellFormula("SUM("+cellsFromC+")");
				Cell ratio = row.createCell(3);
				ratio.setCellStyle(styleRedPerc);
				String ratiofrom_D3_to_Dn = "=(C"+r+"/B"+r+")";
				ratio.setCellFormula(ratiofrom_D3_to_Dn);				
				Cell curShipment = row.createCell(5);
				curShipment.setCellStyle(styleRedNum);
				String curShipmentFormula = "f3:f"+m;
				curShipment.setCellFormula("SUM("+curShipmentFormula+")");
				Cell curEpos = row.createCell(6);
				curEpos.setCellStyle(styleRedNum);
				String curEposFormula = "g3:g"+m;
				curEpos.setCellFormula("SUM("+curEposFormula+")");
				Cell ratioCur = row.createCell(7);
				ratioCur.setCellStyle(styleRedPerc);		
				String ratioForCur = "=IFERROR(g" + r + "/f" + r + ", \"\")";
				ratioCur.setCellFormula(ratioForCur);			
				Cell diff = row.createCell(8);
				diff.setCellStyle(styleRedPerc);
				
				String diffFormula = "=IFERROR(h"+r+"-d"+r+", \"\")";
				diff.setCellFormula(diffFormula);
			}
			
			
			
			
			
			FileOutputStream fileOut = new FileOutputStream(f);
			hwb.write(fileOut);
			fileOut.close();
			return f;

		} catch (Exception e) {
			logger.error("Exception in exportToFile - " + e.getMessage());
			throw e;
		}
	}
	
	public static int getExceColIdex(String excelColIdx) {
		String abcd = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		int idx = 0;
		switch (excelColIdx.length()) {
		case 1:
			idx = abcd.indexOf(excelColIdx);
			break;
		case 2:
			idx = abcd.indexOf(excelColIdx.charAt(1));
			int iidx = abcd.indexOf(excelColIdx.charAt(0)) + 1;
			idx += (iidx * 25) + iidx;
			break;
		}
		return idx;
	}
	
	public static File ExportProductHierarchy(List<SblProductHierarchy> aarList,String format) throws Exception {
		
		File f = null;
		try{
			String[] headers = new String[] { "ProductName", "ProductCode", "Level1Name", "Level1Code","Level2Name", "Level2Code","Level3Name", "Level3Code",
					"Level4Name", "Level4Code","Level5Name", "Level5Code","Level6Name", "Level6Code","Level7Name", "Level7Code","Level8Name", "Level8Code","Level9Name", "Level9Code","Level10Name", "Level10Code","Level11Name", "Level11Code","Level12Name", "Level12Code","Level13Name", "Level13Code"};
			if(format.equalsIgnoreCase(AppConstants.FORMAT_XLSX)){
			 f = new File(AppConstants.PATH_EXPORT+AppConstants.PRODUCT_HIERARCHY+"."+AppConstants.FORMAT_XLSX);
			 SXSSFWorkbook hwb = new SXSSFWorkbook();
		
			 SXSSFSheet hSSFSheet = hwb.createSheet("Product Hierarchy");		
			 SXSSFRow rowhead = hSSFSheet.createRow(0);
			
			for (int rn=0; rn<headers.length; rn++) {				
				rowhead.createCell(rn).setCellValue(headers[rn]);
				}

			
			SXSSFRow row;
			int i = 0;
			for (SblProductHierarchy objProduct : aarList) {
				
				row = hSSFSheet.createRow(i + 1);
				i++;
				row.createCell(0).setCellValue(objProduct.getProductName());
				row.createCell(1).setCellValue(objProduct.getProductCode());					
				row.createCell(2).setCellValue(objProduct.getLevel1Name());
				row.createCell(3).setCellValue(objProduct.getLevel1Code());
				row.createCell(4).setCellValue(objProduct.getLevel2Name());
				row.createCell(5).setCellValue(objProduct.getLevel2Code());
				row.createCell(6).setCellValue(objProduct.getLevel3Name());
				row.createCell(7).setCellValue(objProduct.getLevel3Code());
				row.createCell(8).setCellValue(objProduct.getLevel4Name());
				row.createCell(9).setCellValue(objProduct.getLevel4Code());
				row.createCell(10).setCellValue(objProduct.getLevel5Name());
				row.createCell(11).setCellValue(objProduct.getLevel5Code());
				row.createCell(12).setCellValue(objProduct.getLevel6Name());
				row.createCell(13).setCellValue(objProduct.getLevel6Code());
				row.createCell(14).setCellValue(objProduct.getLevel7Name());
				row.createCell(15).setCellValue(objProduct.getLevel7Code());
				row.createCell(16).setCellValue(objProduct.getLevel8Name());
				row.createCell(17).setCellValue(objProduct.getLevel8Code());
				row.createCell(18).setCellValue(objProduct.getLevel9Name());
				row.createCell(19).setCellValue(objProduct.getLevel9Code());
				row.createCell(20).setCellValue(objProduct.getLevel10Name());
				row.createCell(21).setCellValue(objProduct.getLevel10Code());
				row.createCell(22).setCellValue(objProduct.getLevel11Name());
				row.createCell(23).setCellValue(objProduct.getLevel11Code());
				row.createCell(24).setCellValue(objProduct.getLevel12Name());
				row.createCell(25).setCellValue(objProduct.getLevel12Code());
				row.createCell(26).setCellValue(objProduct.getLevel13Name());
				row.createCell(27).setCellValue(objProduct.getLevel13Code());
						
			}
			
			FileOutputStream fileOut = new FileOutputStream(f);
			hwb.write(fileOut);
			fileOut.close();
			return f;
			}else if(format.equalsIgnoreCase(AppConstants.FORMAT_CSV)){
				 f = new File(AppConstants.PATH_EXPORT+AppConstants.PRODUCT_HIERARCHY+"."+AppConstants.FORMAT_CSV);
				
				FileWriter writer = new FileWriter(f);
				
				for (int rn=0; rn<headers.length; rn++) {			
					
					writer.append(headers[rn]);
					writer.append(",");
					}
				 writer.append('\n');
				
				
				for (SblProductHierarchy objProduct : aarList) {					
					 writer.append(objProduct.getProductName());
					 writer.append(",");
					 writer.append(objProduct.getProductCode());
					 writer.append(",");
					 writer.append(objProduct.getLevel1Name());
					 writer.append(",");					
					 writer.append(objProduct.getLevel1Code());		
					
					 writer.append(",");
					 writer.append(objProduct.getLevel2Name());
					 writer.append(",");
					 writer.append(objProduct.getLevel2Code());
					 writer.append(",");
					 writer.append(objProduct.getLevel3Name());
					 writer.append(",");
					 writer.append(objProduct.getLevel13Code());
					 writer.append(",");
					 writer.append(objProduct.getLevel4Name());
					 writer.append(",");
					 writer.append(objProduct.getLevel4Code());
					 writer.append(",");
					 writer.append(objProduct.getLevel5Name());
					 writer.append(",");
					 writer.append(objProduct.getLevel5Code());
					 writer.append(",");
					 writer.append(objProduct.getLevel6Name());
					 writer.append(",");
					 writer.append(objProduct.getLevel6Code());
					 writer.append(",");
					 writer.append(objProduct.getLevel7Name());
					 writer.append(",");
					 writer.append(objProduct.getLevel7Code());
					 writer.append(",");
					 writer.append(objProduct.getLevel8Name());
					 writer.append(",");
					 writer.append(objProduct.getLevel8Code());
					 writer.append(",");
					 writer.append(objProduct.getLevel9Name());
					 writer.append(",");
					 writer.append(objProduct.getLevel9Code());
					 writer.append(",");
					 writer.append(objProduct.getLevel10Name());
					 writer.append(",");
					 writer.append(objProduct.getLevel10Code());
					 writer.append(",");
					 writer.append(objProduct.getLevel11Name());
					 writer.append(",");
					 writer.append(objProduct.getLevel11Code());
					 writer.append(",");
					 writer.append(objProduct.getLevel12Name());
					 writer.append(",");
					 writer.append(objProduct.getLevel12Code());
					 writer.append(",");
					 writer.append(objProduct.getLevel13Name());
					 writer.append(",");
					 writer.append(objProduct.getLevel13Code());
					
					 writer.append('\n');
					
				}
				 writer.flush();
				 writer.close();
				 return f;
			}
		
		}catch(Exception e){
			e.printStackTrace();
			
		}
		return f;
	}


	
	public static File ExportCustomerHierarchy(List<SblCustomerHierarchy> aarList,String format) throws Exception {
		
		File f = null;
		try{
			String[] headers = new String[] {"RetailerName","RetailerCode","Level1Name","Level1Code","Level2Name","Level2Code","Level3Name","Level3Code","Level4Name","Level4Code","Level5Name","Level5Code","Level6Name","Level6Code","Level7Name","Level7Code","Level8Name","Level8Code"};
			if(format.equalsIgnoreCase(AppConstants.FORMAT_XLSX)) {
				f = new File(AppConstants.PATH_EXPORT+AppConstants.CUSTOMER_HIERARCHY+"."+AppConstants.FORMAT_XLSX);
			
				SXSSFWorkbook hwb = new SXSSFWorkbook();
				SXSSFSheet hSSFSheet = hwb.createSheet("Customer Hierarchy");		
				SXSSFRow rowhead = hSSFSheet.createRow(0);

			for (int rn=0; rn<headers.length; rn++) {				
				rowhead.createCell(rn).setCellValue(headers[rn]);
				}
			
			SXSSFRow row;
			int i = 0;
			for (SblCustomerHierarchy objCustomer : aarList) {
				
				row = hSSFSheet.createRow(i + 1);
				i++;
				row.createCell(0).setCellValue(objCustomer.getRetailerName());
				row.createCell(1).setCellValue(objCustomer.getLevel1Name());
				row.createCell(2).setCellValue(objCustomer.getLevel1Code());
				row.createCell(3).setCellValue(objCustomer.getLevel2Name());
				row.createCell(4).setCellValue(objCustomer.getLevel2Code());
				row.createCell(5).setCellValue(objCustomer.getLevel3Name());
				row.createCell(6).setCellValue(objCustomer.getLevel3Code());
				row.createCell(7).setCellValue(objCustomer.getLevel4Name());
				row.createCell(8).setCellValue(objCustomer.getLevel4Code());
				row.createCell(9).setCellValue(objCustomer.getLevel5Name());
				row.createCell(10).setCellValue(objCustomer.getLevel5Code());
				row.createCell(11).setCellValue(objCustomer.getLevel6Name());
				row.createCell(12).setCellValue(objCustomer.getLevel6Code());
				row.createCell(13).setCellValue(objCustomer.getLevel7Name());
				row.createCell(14).setCellValue(objCustomer.getLevel7Code());
				row.createCell(15).setCellValue(objCustomer.getLevel8Name());
				row.createCell(16).setCellValue(objCustomer.getLevel8Code());
						
			}
			FileOutputStream fileOut = new FileOutputStream(f);
			hwb.write(fileOut);
			fileOut.close();
			return f;
			}else if(format.equalsIgnoreCase(AppConstants.FORMAT_CSV)){
				f = new File(AppConstants.PATH_EXPORT+AppConstants.CUSTOMER_HIERARCHY+"."+AppConstants.FORMAT_CSV);
				FileWriter writer = new FileWriter(f);
               for (int rn=0; rn<headers.length; rn++) {			
					writer.append(headers[rn]);
					writer.append(",");
					}
				 writer.append('\n');
				
				
				for (SblCustomerHierarchy objCustomer : aarList) {					
					 writer.append(objCustomer.getRetailerName());
					 writer.append(",");
					 writer.append(objCustomer.getLevel1Name());
					 writer.append(",");
					 writer.append(objCustomer.getLevel1Code());
					 writer.append(",");
					 writer.append(objCustomer.getLevel2Name());
					 writer.append(",");
					 writer.append(objCustomer.getLevel2Code());
					 writer.append(",");
					 writer.append(objCustomer.getLevel3Name());
					 writer.append(",");
					 writer.append(objCustomer.getLevel3Code());
					 writer.append(",");
					 writer.append(objCustomer.getLevel4Name());
					 writer.append(",");
					 writer.append(objCustomer.getLevel4Code());
					 writer.append(",");
					 writer.append(objCustomer.getLevel5Name());
					 writer.append(",");
					 writer.append(objCustomer.getLevel5Code());
					 writer.append(",");
					 writer.append(objCustomer.getLevel6Name());
					 writer.append(",");
					 writer.append(objCustomer.getLevel6Code());
					 writer.append(",");
					 writer.append(objCustomer.getLevel7Name());
					 writer.append(",");
					 writer.append(objCustomer.getLevel7Code());
					 writer.append(",");
					 writer.append(objCustomer.getLevel8Name());
					 writer.append(",");
					 writer.append(objCustomer.getLevel8Code());
					 writer.append(",");
					 writer.append('\n');
					 
					 
					 
				}
				 writer.flush();
				 writer.close();
				 return f;
			}
		
		}catch(Exception e){
			
			e.printStackTrace();
		}
		return f;
	}
	
	public static Date getUpdateDate() {
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		java.util.Date date = calendar.getTime();
		return date;
	}
	public static String getUpdateStringDate() {
		/*String calMon = "";
		int day, month, year;
		GregorianCalendar date = new GregorianCalendar();
		day = date.get(Calendar.DAY_OF_MONTH);
		month = date.get(Calendar.MONTH) + 1;
		year = date.get(Calendar.YEAR);

		calMon = "" + year + month;
		return "" + year + "-" + month + "-" + day;*/
		SimpleDateFormat dateFormatter = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		java.util.Date date = calendar.getTime();
		return dateFormatter.format(date);
	}
	
	public static List<Object[]> convertExcelToList(Map<String, Object> input) throws Exception {
		
		Workbook workbook = null;
		String fileName = input.get(AppConstants.FILENAME).toString();
		List<Object[]> excelList = new ArrayList<Object[]>();
		try {
			FileInputStream file = new FileInputStream(new File(fileName));
			if (fileName.endsWith(".xls")) {
				workbook = new HSSFWorkbook(file);
			} else if (fileName.endsWith(".xlsx")) {
				workbook = new XSSFWorkbook(file);
			}
			Sheet sheet = workbook.getSheetAt(0);
			
			for (int rowIdx = 1; rowIdx <= sheet.getPhysicalNumberOfRows(); rowIdx++) {
				Row row = sheet.getRow(rowIdx);
				if (row == null)
					continue;
				
				Object[] rowValObj = new Object[row.getLastCellNum()];
				
				for (int i = 0; i < row.getLastCellNum(); i++) {
					Cell cell = row.getCell(i);
					String cellValue = null;					
					if (cell == null) {
						cellValue = "";
					} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
						cellValue = new java.text.DecimalFormat("0").format(cell.getNumericCellValue());
					} else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
						cellValue = cell.getStringCellValue();
					}
					rowValObj[i] = cellValue;
				}
				excelList.add(rowValObj);
			}
		} catch(Exception e) {
			logger.error("Exception in convertExcelToList - " + e.getMessage());
			throw e;
		}
		return excelList;
	}
	
	public static String toFormattedNumber(Object obj, char seperator) {
		symbols.setGroupingSeparator(seperator);
		formatter.setDecimalFormatSymbols(symbols);
		String s = "";
		if (obj != null) {
			if (obj instanceof BigDecimal)
				s = formatter.format(((BigDecimal) obj).doubleValue());
			else 
				s = obj.toString();
		}
		//System.out.println(s);
		return s;
	}
	
	public static void runKJB(String jobName, String stepName, Map<String, String> ParamMap, IHarmonizerService harmonizerService) throws Exception {

		//Get job Id from table HARM_APP.ST_STEP
		String stJobId = harmonizerService.getJobIdByName(jobName);
		
		//Get job STATUS from table HARM_APP.ST_RUN
		String stJobStatus = harmonizerService.chkLastJobStatus(stJobId);
		logger.info("stJobId::" + stJobId);

		if (stJobStatus ==  null || !stJobStatus.equalsIgnoreCase("RUNNING")) {
			harmonizerService.saveStJobParams(stepName, ParamMap); //This saves params to HARM_APP.ST_STEP_PARAMETER TABLE
			String Status = runJob(jobName, stJobId, harmonizerService);
			logger.info("Final job Status:::" + Status);
			if (Status.contains("WAITING")) {
				throw new Exception("Could Not Process Your Request : Another Job is Running");
			} else if (Status.contains("Failure")) {
				throw new Exception("Could Not Process Your Request, Kindly try again.");
			}
		} else {
			logger.info("Another job running");
			throw new Exception("Could Not Process Your Request : Another Job is Running");
		}
	}
	
	public static String runJob(String stJobName, String stJobId,
			IHarmonizerService harmonizerService) throws Exception {
		if (stJobName == null || stJobId == null
				|| stJobName.trim().length() < 1 || stJobId.trim().length() < 1) {
			return "Job run Failure : Incorrect Input Params";
		}

		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
				logger.info("Warning: URL Host: " + urlHostName + " vs. "
						+ session.getPeerHost());
				return true;
			}
		};

		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(hv);
		int sleepTime = 0;
		int intervals = 0;

		try {

//			if (JOB_PROP_MAP.isEmpty()) {
//				init();
//			}
			// JOB_URL = http://localhost:8080/Harmonizer/services/UnileverService.UnileverServiceHttpsSoap12Endpoint
			String url = PropertiesUtil.getProperty(AppConstants.JOB_URL);
			String userId = "NO_USERID";
			String password = "NO_PASSWORD";
			String requestXML = PropertiesUtil.getProperty(AppConstants.JOB_XML);

			requestXML = requestXML.replaceAll("#!JOB_ID!#", stJobId);

			logger.info("URL: " + url);
			logger.info("Request XML: " + requestXML);

			// Create SAAJ SOAPMessage
			MessageFactory msgFactory = MessageFactory.newInstance();
			SOAPMessage soapMessage = msgFactory.createMessage();
			SOAPPart soapPart = soapMessage.getSOAPPart();
			SOAPBody soapBody = soapPart.getEnvelope().getBody();
			SOAPEnvelope soapEnv = soapPart.getEnvelope();

			SOAPHeader header = soapMessage.getSOAPHeader();
			SOAPElement security = header
					.addChildElement(
							"Security",
							"wsse",
							"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
			security.setAttribute("SOAP-ENV:mustUnderstand", "0");
			security.setAttribute(
					"xmlns:wsu",
					"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
			SOAPElement usernameToken = security.addChildElement(
					"UsernameToken", "wsse");
			SOAPElement username = usernameToken.addChildElement("Username",
					"wsse");
			username.addTextNode(userId);

			SOAPElement password1 = usernameToken.addChildElement("Password",
					"wsse");
			password1
					.setAttribute(
							"Type",
							"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText");
			password1.addTextNode(password);

			// Load the XML text into a DOM Document
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory
					.newInstance();
			builderFactory.setNamespaceAware(true);
			InputStream stream = new ByteArrayInputStream(requestXML.getBytes());
			Document doc = builderFactory.newDocumentBuilder().parse(stream);

			soapBody.addDocument(doc);
			stream.close();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			soapMessage.writeTo(baos);
			String soapRequest = new String(baos.toByteArray());

			logger.info("\nSOAP Request: " + soapRequest);

			baos.close();

			byte[] soapReqBytes = soapRequest.getBytes();

			// Set the socketfactory to the new JSSE2 provider
			Security.setProperty("ssl.SocketFactory.provider",
					"com.ibm.jsse2.SSLSocketFactoryImpl");

			// Create HttpURLConnection object
			URL oUrl = new URL(url);
			HttpURLConnection con = (HttpURLConnection) oUrl.openConnection();

			con.setUseCaches(false);

			con.setRequestMethod("POST");
			con.setRequestProperty("Content-type", "text/xml; charset=utf-8");

			con.setDoOutput(true);
			con.setDoInput(true);

			// POST SOAP request as XML message
			OutputStream reqStream = con.getOutputStream();
			reqStream.write(soapReqBytes);
			reqStream.flush();
			reqStream.close();

			InputStream resp = null;
			try {
				resp = con.getInputStream();
			} catch (IOException ex) {
				// try error stream
				resp = con.getErrorStream();
			}

			logger.info("\nHTTP response code: [" + con.getResponseCode()
					+ "] and HTTP response message: ["
					+ con.getResponseMessage() + "]");

			// Read SOAP response XML message
			InputStreamReader isr = new InputStreamReader(resp);
			BufferedReader in = new BufferedReader(isr);
			logger.info("\nSOAP Response: ");
			String stInputLine = "";
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				stInputLine = stInputLine + inputLine;
				logger.info(inputLine);
			}
			in.close();

			String stRunId = null;
			if (stInputLine != null && stInputLine.contains("runId")) {
				stRunId = stInputLine.substring(stInputLine.indexOf("runId"));
				stRunId = stRunId.substring(6, stRunId.indexOf("<"));
			}

			if (con.getResponseCode() != 200)
				return "Job run Failure : " + con.getResponseMessage();

			// INTERVAL = 15000_9600
			String stIntervals = PropertiesUtil.getProperty(stJobName + AppConstants._INTERVAL);
			
			// Get from Properties file - SLEEPTIME_Interval
			logger.info("Debug 0" + stIntervals);
			if (stIntervals != null && stIntervals.indexOf("_") > 1) {

				String[] stTemp = stIntervals.split("_");
				sleepTime = Integer.parseInt(stTemp[0]);
				intervals = Integer.parseInt(stTemp[1]);

				String stStatus = "";
				for (int i = intervals; i > 0; i--) {
					
					//Get job STATUS from table HARM_APP.ST_RUN
					stStatus = harmonizerService.getStatusByRunId(stRunId, stJobId);
					
					if (stStatus != null && stStatus.equalsIgnoreCase("SUCCESS")) {
						return "Job run Succcess : " + stStatus;
					} else if (stStatus != null && !stStatus.equalsIgnoreCase("RUNNING")) {
						return "Job run Failure : " + stStatus;
					}

					Thread.sleep(sleepTime);
				}
			}

		} catch (Exception e) {
			System.err.println("ERROR: Exception in Client: " + e.toString());
			e.printStackTrace();
		}
		return "Job run Failure : Timeout happened . Job not completed in "
				+ intervals + " intervals of " + sleepTime + " ms each";
	}

	public static String escapeLikeSpecialChar(String sSearchStr) {
		String[] specialChars = new String [] {"[", "]", "(", ")", "/"} ;
		
		for(String s : specialChars) {
			sSearchStr = sSearchStr.replace(s, ">" + s);
		}
		
		return sSearchStr;
	}
	
	public static Map getEditColType(String tableName) {
		Map<String, Map<String, Object>> hmColEditType = new HashMap<String, Map<String, Object>>();
		if (PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.COLUMN_EDITABLE) != null) {
			String[] colEditable = PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.COLUMN_EDITABLE).split("\\|");
			
			for(String ce : colEditable) {
				if (ce != null) {
					Map<String, Object> m = new HashMap<String, Object>();
					String [] ace = ce.split(AppConstants.COLON);
					if (ace.length > 1)
						m.put(AppConstants.EDIT_COL_TYPE, ace[1]);
					if (ace.length > 2)
						m.put(AppConstants.EDIT_COL_DISABLED, ace[2]);	
					
					hmColEditType.put(ace[0], m);
				}
			}
		}
		return hmColEditType;
	}

	public static Object convertToDatatype(Object oOld, String sColType) throws ParseException {
		Object oNew = oOld;
		switch (sColType) {
			case AppConstants.N:
				DecimalFormat df = new DecimalFormat();
				DecimalFormatSymbols symbols = new DecimalFormatSymbols();
//				symbols.setDecimalSeparator('');
//				symbols.setGroupingSeparator(' ');
				df.setDecimalFormatSymbols(symbols);
				Number s = df.parse(oOld.toString());
				oNew = s.doubleValue();
				break;
		}
		
		return oNew;
	}
	
	public static String getDateFormat(String tableName) {
		String defaultDateFormatBK = "dd/MM/yyyy";
		String defaultDateFormatUI = "DD MMM YYYY";
		String defaultTimeFormat = "hh:mm:ss";
		
		HttpServletRequest req = getServletRequest();
		HttpSession session = null; 
		if (req != null && (session = req.getSession()) != null && session.getAttribute(AppConstants.USER_OBJECT) != null) {
			UserProfile userProfile = (UserProfile)session.getAttribute(AppConstants.USER_OBJECT);
			
			if (userProfile != null && !isEmpty(userProfile.getDateFormat())) {
				defaultDateFormatBK = userProfile.getDateFormatBK();
				defaultDateFormatUI = userProfile.getDateFormatUI();
				
				session.setAttribute(AppConstants.DATE_FORMAT, defaultDateFormatBK);
				session.setAttribute(AppConstants.DATE_FORMAT_UI, defaultDateFormatUI);
			}
			if (tableName == null) {
				//return defaultDateFormatBK;
			}
			else if (PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.TIME_FORMAT) != null) {
				defaultDateFormatBK += " " + PropertiesUtil.getProperty(tableName + AppConstants.DOT + AppConstants.TIME_FORMAT);
			}
		} 
		else {
			defaultDateFormatBK = PropertiesUtil.getPropertyOrDefault(tableName + AppConstants.DOT + AppConstants.DATE_FORMAT, defaultDateFormatBK + " " + defaultTimeFormat);
		}
		
		return defaultDateFormatBK;
	}
	
	public static HttpServletRequest getServletRequest() {
		RequestAttributes attribs = RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = (HttpServletRequest) ((org.springframework.web.context.request.ServletRequestAttributes) attribs).getRequest();
		
		return request;
	}
	
	public static double calDistanceBtwnString(String str1, String str2) {
		String mtchStr = new String(str1), otherStr = new String(str2);
		int chrLen = 3;
		float value = 0;
		
//		if (str1.length() < str2.length()) {
//			mtchStr = new String(str2);
//			otherStr = new String(str1);
//		}
		otherStr = otherStr.toLowerCase();
		float len = mtchStr.length();
		
		for (int i=0; i<len && i<otherStr.length(); i++) {
			String tokn = new String(mtchStr.substring(i, (int) (((i + chrLen) < len) ? (i + chrLen) : len)));	
			if (otherStr.contains(tokn.toLowerCase())) {
				value++;
			}
		}
		return round(value / len * 100, 2);
	}
	
	public static String convertToBase64photo(Object photo) {
    	if (photo != null) {
			byte[] encoded = Base64.getEncoder().encode((byte[]) photo);
			if(encoded != null)
				return "data:image/jpeg;base64," + new String(encoded);
    	}
		return "";
	}
	
	public static List<NameCodeDistanceBean> getSuggestedItems (Map<String, Object> hmInput) {
		List<NameCodeDistanceBean> intProducts = new ArrayList<NameCodeDistanceBean>();
		List<NameCodeDistanceBean> alSuggestedIntProducts = new ArrayList<NameCodeDistanceBean>();
		List<Object[]> extProd = (List<Object[]>) hmInput.get(AppConstants.EXT_ITEM_OBJECT);
		List<Object[]> alIntProducts = (List<Object[]>) hmInput.get(AppConstants.INT_ITEMS);
		
		
		int maxProd = (int) hmInput.getOrDefault(AppConstants.MAX_ITEMS, -1);
		if (maxProd < 0) {
			maxProd = 3;
		}
		
		int iExtItmIdx = (int) hmInput.get(AppConstants.EXT_ITEM_INDEX);
		
		if (extProd != null && extProd.size() > 0) {
			for (Object[] obj : extProd) {
				if (obj[1] != null) {
					// Used ExtProductNameNonUnilever
					String extProdName = obj[iExtItmIdx].toString();
					
					for (Object[] obj1 : alIntProducts) {
						if (obj1!= null && obj1.length > 2 && obj1[2] != null) {
//							int distance = StringUtils.getLevenshteinDistance(extProdName, obj1[2].toString());
							double distance = PEAUtils.calDistanceBtwnString(extProdName, obj1[2].toString());
							intProducts.add(new NameCodeDistanceBean(distance, obj1[2].toString(), obj1[1].toString(), obj1[0].toString()));
						}
					}
				}
				
				intProducts.sort(new Comparator<NameCodeDistanceBean>() {
					@Override
					public int compare(NameCodeDistanceBean o1, NameCodeDistanceBean o2) {
						if (o1 == null || o2 == null)
							return 0;
						else if (o1.getDistance() > o2.getDistance()) {
							return -1;
						}
						else if (o1.getDistance() < o2.getDistance()) {
							return 1;
						}
						return 0;
					}
				});
				break;
			}
		}
		
		if (intProducts.size() > 0) {
			alSuggestedIntProducts = intProducts.subList(0, (intProducts.size() >= maxProd ? maxProd : intProducts.size()));
		}
		return alSuggestedIntProducts;
	}
	
	public static String escapeSql(String str) {
		if (str == null) return str;
		return StringEscapeUtils.escapeSql(str);
	}

	public static Object getDefaultIfNull(Object object, Object blank) {
		if (object == null) {
			if (blank == null)
				return null;
			return blank;
		}
		return object;
	}

}
