package com.saama.workbench.util;

import java.text.SimpleDateFormat;



public interface AppConstants {
	public static final String DATE_RANGE_SEPARATOR = " - ";
	public static final String BLANK = "";
	public static final String DOT = ".";
	public static final String COLON = ":";
	public static final String SYM_POUND = "&pound;";
	public static final String SYM_INR = "&#8377;";
	public static final String SYM_POUND_SIGN = "�";
	public static final String SYM_PERC = "%";
	public static final String SYM_DASH = "-";
	public static final String SYM_PIPE_REGX = "\\|";
	public static final String[] SHORT_MONTHS= {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	public static final String[] MONTHS= {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

	public static final String EXL_STYLE_RED_PERC = "#,##0.00%;[Red](#,##0.00%)";
	public static final String EXL_STYLE_RED_CURR = "�#,##0.00_);[Red](�#,##0.00)";
	public static final String EXL_STYLE_RED_NUM = "#,##0;[Red](#,##0)";
	public static final String EXL_STYLE_DMY = "dd/mm/yyyy";
	
	public static final String US = "US";
	public static final String CA = "CA";
	public static final String UK = "UK";
	public static final String SIMPLE_JNDI_PATH = "path.simple-jndi";
	public static final String KETTLE_HOME_PATH = "path.kettle-home";
	
	public static final String DEPLOYED_ENV = PropertiesUtil.getProperty("deployed.env");
	public static final String HARMONIZER_DATABASE = PropertiesUtil.getProperty(DEPLOYED_ENV + AppConstants.DOT + "jdbc.harmonizer.database"); 
	
	public static final String CACHE_LEVEL_PRODUCT_DATA = "cache.level.product.data";
	public static final String CACHE_LEVEL_CUSTOMER_DATA = "cache.level.customer.data";
	
	public static final String SESSION_CACHE = "session.cache";
	public static final String COMMON_SETTINGS_SESSION_TIMEOUT = "COMMON.settings.session.timeout";
	public static final String COMMON_SETTINGS_WITHOUT_LOGIN = "COMMON.settings.without.login";
	public static final String COMMON_SETTINGS_EXPORT_DRIVE = "COMMON.settings.export.drive";
	public static final String COMMON_SETTINGS_LASTEST_SVN_VERSION = "COMMON.settings.latest.svn.version";
	public static final String COMMON_SETTINGS_SPLITTER_DATE_FORMAT = "COMMON.settings.splitter.date.format";
	public static final String COMMON_SETTINGS_UPLOAD_FILEPATH = "COMMON.settings.upload.filepath";
	
	public static final String OPSO_REPORT_TEMPLATE_FILENAME = "opso.report.template.filename";
	public static final String COLUMN = "Column";
	public static final String CONDITION = "Condition";
	public static final String COLUMNS = "COLUMNS";
	public static final String TABMENU = "TABMENU";
	public static final String EXCEPTION = "EXCEPTION";
	public static final String PROMOTION = "PROMOTION";
	public static final String PROMOTION_ID = "PromotionID";
	public static final String PROMO_ID = "PromoID";
	public static final String PROMO_ALIGN_DATA = "PromoAlignData";
	public static final String WEEKLYPROMOTION = "WEEKLYPROMOTION";
	public static final String COLUMN_MAPPING = "column.mapping";
	public static final String AUDIT_COLUMN = "audit.column";
	public static final String AUDIT_COLUMN_UPDATE = "audit.column.update";
	public static final String AUDIT_COLUMN_ADD = "audit.column.add";
	public static final String ADD_COLUMN = "add.column"; 
	public static final String ADD_COLUMN_IDX = ADD_COLUMN + DOT + "idx";
	public static final String ADD_COLUMN_AUTOINCR = ADD_COLUMN + DOT + "autoIncr";
	public static final String ADD_COLUMN_AUDIT_DATA = ADD_COLUMN + DOT + "audit.data";
	public static final String QUERY_SQL = "query.sql";
	public static final String WHERE_CLAUSE = "where.clause";
	public static final String SORT_MANUAL = "sort.manual";
	public static final String WHERE_CLAUSE_SQL_KEY = "<where_clause>";
	public static final String DATA = "data";
	public static final String RESPONSE = "response";
	public static final String CUSTOMERHIERACHY = "customer";
	public static final String PRODUCTHIERACHY = "product";
	public static final String DISPLAY_START = "iDisplayStart";
	public static final String DISPLAY_LENGTH = "iDisplayLength";
	public static final String SORT_COL_0 = "iSortCol_0";
	public static final String SORT_DIR_0 = "sSortDir_0";
	public static final String BSEARCHABLE_ = "bSearchable_";
	public static final String SORT_COL_SQL_KEY = "<sort_col>";
	public static final String SORT_DIR_SQL_KEY = "<sort_dir>";
	public static final String SEARCH_STR_SQL_KEY = "<search_str>";
	public static final String SEARCH_CONDITION_SQL_KEY = "<search_condition>";
	public static final String COLUMNS_SQL_KEY = "<columns>";
	
	public static final String SEARCH_PARAM = "sSearch";
	public static final String NG_SEARCH_PARAM = "search[value]";
	public static final String TRUE = "TRUE";
	public static final String ON = "ON";
	public static final String ASC = "ASC";
	public static final String DATE = "DATE";
	public static final String INTEGER = "INTEGER";
	public static final String D = "D";
	public static final String T = "T";
	public static final String S = "S";
	public static final String N = "N";
	public static final String Y = "Y";
	public static final String COL = "COL";
	public static final String DIRECTORY_PATH = PropertiesUtil.getProperty(COMMON_SETTINGS_UPLOAD_FILEPATH);
	public static final String ESRA="ESRA";
	public static final String LOGINURI = "/login";
	public static final String USERNAME = "USERNAME";
	public static final String USERPHOTO = "userPhoto";
	public static final String LDAPGROUP = "LDAPGROUP";
	public static final String LAST_REQUEST_TIME = "lastRequestTime";
	public static final String CUSTOMER_HIERARCHY = "CustomerHierarchy";
	public static final String PRODUCT_HIERARCHY = "ProductHierarchy";
	public static final String FORMAT_XLS = "xls";
	public static final String FORMAT_XLSX = "xlsx";
	public static final String FORMAT_CSV ="csv";
	public static final String PATH_EXPORT = PropertiesUtil.getPropertyOrDefault(AppConstants.COMMON_SETTINGS_EXPORT_DRIVE, "C") + ":\\";
	//Datatable 
//	public static final String COLON = ":";
	public static final String PIPE = "|";
//	public static final String DOT = ".";
//	public static final String N = "N";
//	public static final String COL = "COL";
	
	public static final String TABLENAME = "tableName";
	
//	public static final String QUERY_SQL = "query.sql";
	public static final String TABLE = "table";
	public static final String QUERY_UPDATE = "query.update";
	public static final String COLUMN_MAPING = "column.mapping";
	public static final String COLUMN_EDITABLE = "column.editable";
	public static final String COLUMN_CURRENCY = "column.currency";
	public static final String COLUMN_PERCENTAGE = "column.percentage";
	public static final String COLUMN_TRIM = "column.trim";
	public static final String DELETABLE = "deletable";
	public static final String COLUMN_HIDDEN = "column.hidden";
	public static final String KEY_COLUMN_IDX = "key.column.idx";
//	public static final String COLUMNS = "columns";
//	public static final String SESSION_CACHE = "session.cache";
	public static final String DATE_FORMAT_UI = "dateFormatUI";
	public static final String DATE_FORMAT = "date.format";
	public static final String TIME_FORMAT = "time.format";
	
	
	public static final String DASHBOARD_JOBRUNSTATISTICS = "Dashboard.jobRunStatistics";
	
//	public static final String DISPLAY_START = "iDisplayStart";
//	public static final String DISPLAY_LENGTH = "iDisplayLength";
//	public static final String SORT_COL_0 = "iSortCol_0";
//	public static final String SORT_DIR_0 = "sSortDir_0";
//	public static final String BSEARCHABLE_ = "bSearchable_";
//	public static final String SORT_COL_SQL_KEY = "<sort_col>";
//	public static final String SORT_DIR_SQL_KEY = "<sort_dir>";
//	public static final String SEARCH_STR_SQL_KEY = "<search_str>";
//	public static final String SEARCH_CONDITION_SQL_KEY = "<search_condition>";
//	public static final String COLUMNS_SQL_KEY = "<columns>";
//	
//	public static final String SEARCH_PARAM = "sSearch";
//	public static final String TRUE = "TRUE";
//	public static final String DATA = "data";
//	public static final String ON = "ON";
//	public static final String ASC = "ASC";
//	public static final String DATE = "DATE";
//	public static final String INTEGER = "INTEGER";
//	public static final String D = "D";
//	public static final String T = "T";
//	public static final String S = "S";
	public static final String YMDHMS = "yyyy-MM-dd hh:mm:ss";
	public static final String YMD = "yyyy-MM-dd";
	public static final String DMY = "dd/MM/yyyy";
	public static final String DMONY = "dd MMM yyyy";
//	public static final SimpleDateFormat SDF_YMDHMS = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//	public static final SimpleDateFormat SDF_YMD = new SimpleDateFormat("yyyy-MM-dd");
	public static final String KEY = "key";
	public static final String RESULT = "Result";
	public static final String MESSAGE = "Message";
	public static final String SUCCESS = "SUCCESS";
	public static final String FAILURE = "FAILURE";
	public static final String EXCLUDEACTIONS = "EXCLUDEACTIONS";
	public static final String EXCELLIST = "EXCELLIST";
	public static final String FILENAME = "fileName";
//	public static final String USERNAME = "USERNAME";
	public static final String SESSION_COOKIE_ENABLE = "session.cookie.enable";
//	public static final String LAST_REQUEST_TIME = "last.request.time";
	public static final String START = "start";
	public static final String LENGTH = "length";
	public static final String DRAW = "draw";
	public static final String COLTYPE = "colType";
	public static final String COLDBNAME = "colDBName";
	public static final String ISNGREQUEST = "isNgRequest";
	public static final String VIEWEXCEPTIONS = "ViewExceptions";
	public static final String VIEWPROMOTEDPRODUCTS = "ViewPromotedProducts";
	public static final String VIEWWEEKLYPROMOTION = "ViewWeeklyPromotion";
	public static final String DATAAVAILABILITY = "DataAvailability";
	public static final String VIEWDATASETS = "ViewDatasets";
	public static final String PromoMechanic = "PromoMechanic";
	public static final String MAPPING_DATA = "MappingData";
	public static final String MAPPING_DATA_KANTAR = "MappingDataKantar";
	public static final String CUST_MAPPING_DATA = "CustMappingData";
	public static final String MANAGE_BUSINESS_RULES = "ManageBusinessRules";
	public static final String separator = "|";
	public static final String DBCOLUMN = "dbColumn";
	public static final String DISPLAYCOLUMN = "displayColumn";
	public static final String SEARCH_SQL_CLAUSE = "<search_sql_clause>";
	public static final String OPERATOR = "OPERATOR";
	public static final String VALUE = "VALUE";
	public static final String HIDDEN_COLUMNS = "hiddenColumns";
	public static final String DISPLAY_COLUMNS = "displayColumns";
	public static final String VIEW = "view";
	public static final String DISPLAY_COL_LIST = "displayColList";
	public static final String VALUE_TYPE = "valueType";
	public static final String SS = "SS";
	public static final String MS = "MS";
	public static final String MSARR = "MSARR";
	public static final String ACTIONS_COLUMNS_POSITION = "action.column.position";
	public static final String SQL_REPLACE_REQ_PARAM = "sql.replace.req.param";
	public static final String FIRST = "FIRST";
	public static final String LAST = "LAST";
	public static final String COLUMN_IDX_MAP = "columnIdxMap";
	public static final String BETWEEN = "between";
	public static final String DATA_LIST_ARRAY = "data.list.array";
	public static final String DERIVED_DATA_LIST_ARRAY = "derived.data.list.array";
	public static final String LIKE = "like";
	public static final String COL_WIDTH = "colWidth";
	public static final String COL_WIDTH_MAP = "colWidthMap";
	public static final String SEL_ACCOUNTS = "selAccounts";
	public static final String COL_DECIMALS = "colDecimals";
	public static final String COL_TYPE_MAP = "colTypeMap";
	public static final String SESSION_ATTR_CUST_HIERARCHY = "session.attr.cust.hierarchy";
	public static final String SESSION_ATTR_PROD_HIERARCHY = "session.attr.prod.hierarchy";
	public static final String EDITDATA = "editData";
	public static final String COL_DATATYPE = "colDatatype";
	public static final String CURRENCY = "currency";
	public static final String PERCENTAGE = "percentage";
	public static final String JOB_URL = AppConstants.DEPLOYED_ENV + ".JOB_URL";
	public static final String JOB_XML = "JOB_XML";
	public static final String _INTERVAL = "_INTERVAL";
	
	public static final String JOB_CREATE_DATASET_NAME = "job.create.dataset.name";
	public static final String STEP_CREATE_DATASET_NAME = "step.create.dataset.name";
	public static final String OPSO = "OPSO";
	public static final String EYNTK = "EYNTK";
	public static final String TYPE = "TYPE";
	public static final String CSVLIKEOR = "CSVLIKEOR";
	public static final String OPSO_REPORT_PROMO = "OPSOReportPromo";
	public static final String OPSO_REPORT_PRODUCT = "OPSOReportProduct";
	public static final String OPSO_REPORT_FILE_PATH = AppConstants.DEPLOYED_ENV + ".opso.report.file.path";
	public static final String GROUPNAME = "PEAHarmonizerAdmin";
	public static final String MAPDATA = "mapData";
	public static final String EXT_CUSTID = "extCustId";
	public static final String INT_CUSTID = "intCustId";
	public static final String INT_CUSTOMER = "intCustomer";
	public static final String EXT_PRODID = "extProdId";
	public static final String INT_PRODUCT = "intProduct";
	public static final String BRAND = "brand";
	public static final String FORM = "form";
	public static final String SUBFORM = "subForm";
	public static final String SOURCEID = "sourceId";
	public static final String FLTR_SOURCE = "fltrSource";
	public static final String WEIGHTAGE = "Weightage";
	public static final String MAPPED = "Mapped";
	public static final String UNMAPPED = "UnMapped";
	
	public static final String LDAP_DOMAIN_NAME = AppConstants.DEPLOYED_ENV + ".ldap.domain.name";
	public static final String LDAP_PROVIDER_URL = AppConstants.DEPLOYED_ENV + ".ldap.provider.url";
	public static final String LDAP_USER_BASE = AppConstants.DEPLOYED_ENV + ".ldap.user.base";
	public static final String KANTAR = "Kantar";
	public static final String NIELSEN = "Niesen";
	
	public static final String SAML_ENABLED = "COMMON.settings.saml.enabled";
	public static final String LANGUAGE = "LANGUAGE";
	public static final String BUSINESSEXCEPTION = "businessexception";
	public static final String MAPPINGEXCEPTION = "mappingexception";
	public static final String TECHNICALDQ = "technicaldq";
	
	public static final String Dashboard_dataqualityreport_buisnessexception="select x.month, x.year, x.ExceptionFlag, Count(x.ExceptionFlag) count from ( select case when a.ExceptionFlag = 'Y' then 'Exception' else 'Clean' end ExceptionFlag, DATEPART(MM,b.PromotionStartDate) month, DATEPART(yyyy,b.PromotionStartDate) year from PRS.PROMOTION_FACT a join PRS.PROMO_DIM b on a.promotionId = b.promotionId and PromotionStartDate <= GETDATE()) x group by x.month, x.year, x.ExceptionFlag order by x.year desc,x.month asc";
	public static final String Dashboard_dataqualityreport_mappingexception="select max(ProdCnt) UnMappedProdCnt, max(CustCnt) UnMappedCustCnt, max(mapProdCnt) MappedProdCnt, max(mapCustCnt) MappedCustCnt   from (   	  	select count(*) ProdCnt, 0 CustCnt, 0 mapProdCnt, 0 mapCustCnt   	  	from CNF.EXT_PRODUCT_MASTER     	  	where ExtProdId not in ( select distinct ExtProdId from LNK.PROD_MAP )   	union  	  	  	select 0 ProdCnt, 0 CustCnt, count(*) mapProdCnt, 0 mapCustCnt   	  	from CNF.EXT_PRODUCT_MASTER     	  	where ExtProdId in ( select distinct ExtProdId from LNK.PROD_MAP )   	union     	  	select 0 ProdCnt, count(*)  CustCnt, 0 mapProdCnt, 0 mapCustCnt   	  	from CNF.EXT_CUSTOMER_MASTER  	  	  	where ExtCustId not in ( select distinct ExtCustId from LNK.CUST_MAP )  	  	  	union     	  	select 0 ProdCnt, 0 CustCnt, 0 mapProdCnt, count(*)   	mapCustCnt   	from CNF.EXT_CUSTOMER_MASTER  	  	  	where ExtCustId in ( select distinct ExtCustId from LNK.CUST_MAP )     ) r  ";
 	public static final String Dashboard_dataqualityreport_techicalreject="select *, Count(*) from (select ltrim(rtrim(ProcessName)) ProcessName, month(loadDate) Mon, year(loadDate) Year from AUX.DQ_REJECTS where status in ('Pending')) x group by ProcessName, Mon, year order by year, Mon, ProcessName";

	public static final String DataQualityReport = "dataQualityReport";

	public static final String DB_AUTH_ENABLED = "COMMON.settings.DBAuth.enabled";
	public static final String STARTDATE = "startDate";
	public static final String ENDDATE = "endDate";
//	public static final String promotion_alignment ="select   cast(Datediff(s,'1970-01-01' , som.week) AS bigint)*1000 as week,sum(som.metricvalue)   ,cd.lowestlevelvalue CustomerName,pd.lowestlevelvalue ProductName  from cnf.sell_out_base sob left join cnf.sell_out_measures som on som.selloutid=sob.selloutid left join cnf.ext_customer_master ecm on sob.retailer=ecm.customername left join lnk.cust_map cm on ecm.extcustid=cm.extcustid left join lnk.cust_dim cd on cd.custid=cm.custid   left join cnf.ext_product_master pcm on sob.product=pcm.productname left join lnk.prod_map pm on pcm.extprodid=pm.extprodid left join lnk.prod_dim pd on pd.prodid=pm.prodid WHERE	(som.week BETWEEN :startdate AND :enddate)     group by som.week, cd.lowestlevelvalue, pd.lowestlevelvalue having  cd.lowestlevelvalue ='<customerName>' and pd.lowestlevelvalue ='<productName>'";
	public static final String promotion_alignment = "SELECT Cast(Datediff(s, '1970-01-01', som.week) AS BIGINT) * 1000 AS week, Sum(som.metricvalue), cd.lowestlevelvalue CustomerName, pd.lowestlevelvalue ProductName FROM cnf.sell_out_base sob LEFT JOIN cnf.sell_out_measures som ON som.selloutid = sob.selloutid LEFT JOIN cnf.ext_customer_master ecm ON sob.retailer = ecm.customername LEFT JOIN lnk.cust_map cm ON ecm.extcustid = cm.extcustid LEFT JOIN lnk.cust_dim cd ON cd.custid = cm.custid LEFT JOIN cnf.ext_product_master pcm ON sob.product = pcm.productname LEFT JOIN lnk.prod_map pm ON pcm.extprodid = pm.extprodid LEFT JOIN lnk.prod_dim pd ON pd.prodid = pm.prodid WHERE ( som.week BETWEEN :startdate AND :enddate ) GROUP BY som.week, cd.lowestlevelvalue, pd.lowestlevelvalue, pd.ProductCode, pd.ProductName HAVING cd.lowestlevelvalue = '<customerName>' and pd.ProductCode + ' - ' + pd.ProductName = '<productName>' ORDER BY som.week";
    //public static final String promotion_alignment_data = "select Promotion,Status,planningAccountName,ShipmentStartDate,ShipmentEndDate from PRS.PROMO_DIM where ( InStoreStartDate between :startdate and :enddate) and (InStoreEndDate between :startdate and :enddate) and planningAccountName=:planningAccountName";
//    public static final String promotion_alignment_data = "select  PromoID,Promotion,Retailer,cast(Datediff(s,'1970-01-01' , PromotionStartDate) AS bigint)*1000 as PromotionStartDate,cast(Datediff(s,'1970-01-01' , PromotionEndDate) AS bigint)*1000 as PromotionEndDate from PRS.PROMO_DIM where PromotionId in (select distinct PromotionId from PRS.PROMOTION_FACT a join PRS.PRODUCT_DIM b on a.ProductId = b.ProdId and b.lowestlevelvalue = '<productName>' join PRS.CUSTOMER_DIM c on a.RetailerID = c.CustId and c.lowestlevelvalue ='<customerName>' ) and  ( PromotionStartDate between :startdate and :enddate) and (PromotionEndDate between :startdate and :enddate)";
//    public static final String promotion_alignment_data = "SELECT pd.promoid, pd.promotion, pd.retailer, Cast(Datediff(s, '1970-01-01', CASE when d.AlignDate is not null then d.AlignDate ELSE pd.promotionstartdate END) AS BIGINT) * 1000 AS PromotionStartDate, Cast(Datediff(s, '1970-01-01', CASE when d.AlignDate is not null then DATEADD(DAY, DATEDIFF(DAY, pd.promotionstartdate, pd.promotionenddate), d.AlignDate) ELSE pd.promotionenddate END) AS BIGINT) * 1000 AS PromotionEndDate FROM PRS.promo_dim pd JOIN prs.promotion_fact a ON pd.promotionid = a.promotionid AND ( promotionstartdate BETWEEN :startdate AND :enddate ) AND ( promotionenddate BETWEEN :startdate AND :enddate ) JOIN prs.product_dim b ON a.productid = b.prodid AND b.lowestlevelvalue = '<productName>' JOIN prs.customer_dim c ON a.retailerid = c.custid AND c.lowestlevelvalue = '<customerName>' LEFT JOIN LND.PROMOTION_OVERRIDE d on d.CustomerId = c.custId and d.ProductId = b.ProdId and d.PromoId = pd.PromoId";
    public static final String promotion_alignment_data = "SELECT pd.promoid, pd.promotion, pd.retailer, Cast(Datediff(s, '1970-01-01', CASE WHEN d.aligndate IS NOT NULL THEN d.aligndate ELSE pd.promotionstartdate END) AS BIGINT) * 1000 AS PromotionStartDate, Cast(Datediff(s, '1970-01-01', CASE WHEN d.aligndate IS NOT NULL THEN Dateadd(day, Datediff(day, pd.promotionstartdate, pd.promotionenddate), d.aligndate) ELSE pd.promotionenddate END) AS BIGINT) * 1000 AS PromotionEndDate, a1.BaseUnitVolumeSO, a1.IncrUnitVolumeSO, pd.status FROM PRS.promo_dim pd JOIN prs.PROMOTION_FACT_GROUP_NEW a1 ON pd.PromotionId = a1.PromotionId JOIN prs.PROMOTION_FACT a ON pd.promotionid = a.promotionid AND ( promotionstartdate BETWEEN :startdate AND :enddate ) AND ( promotionenddate BETWEEN :startdate AND :enddate ) JOIN LNK.PROD_DIM b ON a.productid = b.prodid AND b.ProductCode + ' - ' + b.ProductName = '<productName>' JOIN prs.customer_dim c ON a.retailerid = c.custid AND c.lowestlevelvalue = '<customerName>' LEFT JOIN lnd.promotion_override d ON d.customerid = c.custid AND d.productid = b.prodid AND d.promoid = pd.promoid ";
	public static final String PRODUCTNAME = "productName";
	public static final String CUSTOMERNAME = "customerName";
	public static final int previousmonth = -1;
	public static final String PRODUCTID = "ProductId";
	public static final String CUSTOMERID = "CustomerId";
	public static final String NODEID = "NodeId";
	public static final String NODENAME = "NodeName";
	public static final String MAX_PRODUCTS = "MaxProducts";
	public static final String SUGGESTED_PRODUCTS = "suggestedProducts";
	public static final String SUGGESTED_CUSTOMERS = "suggestedCustomers";
	public static final String FALSE = "false";
	public static final String COLUMNS_TO_UPDATE = "columnsToUpdate";
	public static final String ALIGN_CHANGE = "AlignChange";
	public static final String METRIC_CHANGE = "MetricChange";
	public static final String ADHOC_CHANGE = "AdhocChange";
	public static final String SELL_IN_CHANGE = "SellInChange";
	public static final String SELL_OUT_CHANGE = "SellOutChange";
	public static final String EPOS_CHANGE = "EPOSChange";
	public static final String CB = "CB";
	public static final String CREATED_DATE = "createdDate";
	public static final String CREATED_BY = "createdBy";
	public static final String UPDATED_DATE = "updatedDate";
	public static final String UPDATED_BY = "updatedBy";
	public static final String STATUS = "status";
	public static final String DQ_REJECTS = "DQ_REJECTS";
	public static final String EDITED_STATUS = "editedStatus";
	public static final String TRIM_LENGTH = "trim.length";
	public static final String REPROCESS_PROMOTIONS = "ReprocessPromotions";
	public static final String EDIT_COL_TYPE = "editColType";
	public static final String EDIT_COL_DISABLED = "editColDisabled";
	public static final String EDITABLE = "editable";

	public static final String NAME_PARAMS = "name.params";
	public static final String ROOT_ID = "rootId";
	public static final String CUSTOMER = "customer";
	public static final String PRODUCT = "product";
	public static final String USER_OBJECT = "UserObject";
	public static final String CURRENCY_FORMAT = "CurrencyFormat";
	public static final String LANG_FORMAT = "LangFormat";
	public static final String CODE = "code";
	public static final String NAME = "name";
	public static final String OBJECT = "object";
	public static final String PARENT_ID = "parentId";
	public static final String CHILD_ID = "ChildId";
	public static final String LEVEL = "Level";
	public static final String FORCED = "forced";
	public static final String LOGOUT_URL = "logoutURL";
	public static final String DASHBOARD_NAME = AppConstants.DEPLOYED_ENV + ".dashboard.name";
	public static final String REPORT_NAME = AppConstants.DEPLOYED_ENV +  ".report.name";
	public static final String REPORT_USER_DOMAIN = AppConstants.DEPLOYED_ENV + ".report.user.domain";
	public static final String REPORT_SERVER = AppConstants.DEPLOYED_ENV + ".report.server";
	public static final String REPORT_TARGET_SITE = AppConstants.DEPLOYED_ENV + ".report.target.site";
	public static final String REPORT_CLIENT_IP = AppConstants.DEPLOYED_ENV + ".report.client.ip";
	public static final String REPORT_PUBLIC_IP = AppConstants.DEPLOYED_ENV + ".report.public.ip";
	public static final String REPORT_URL = "reportURL";
	public static final String NODE_IMAGE = "nodeImage";
	public static final String RULE_NAME = "RuleName";
	public static final String ENABLE = "Enable";
	public static final String DISABLE = "Disable";
	public static final String ACTION = "Action";
	public static final String DESCRIPTION = "Description";
	public static final String ISENABLED = "IsEnabled";
	public static final String ISDELETED = "IsDeleted";
	public static final String CONSTRAINT = "Constraint";
	public static final String ACTION_COLUMN = "ActionColumn";
	public static final String ACTION_VALUE = "ActionValue";
	public static final String RULE_ID = "RuleId";
	public static final String EXT_ITEM_OBJECT = "ExtItemObject";
	public static final String INT_ITEMS = "IntItems";
	public static final String EXT_ITEM_INDEX = "ExtItemIndex";
	public static final String MAX_ITEMS = "MaxItems";
	public static final String INT_PRODID = "intProdId";
	
	public static final String EXCHG_RATE_USD_INR = "64.24";
	public static final String COLUMN_HIGLIGHT_RULE = "highlight.rule.column";
	public static final String ACTUAL_DB_COL = "ActualDBColumn";
	public static final String RULE_DESC = "RuleDesc";
	public static final String RULE_COLUMN = "RuleColumn";
	public static final String BUSINESS_RULES_DETAIL = "BusinessRulesDetails";

}
