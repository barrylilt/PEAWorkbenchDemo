package com.saama.workbench.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.saama.workbench.bean.HierarchySelectBean;
import com.saama.workbench.model.WbCustomer;
import com.saama.workbench.model.WbCustomerClosure;
import com.saama.workbench.model.WbCustomerTaxonomy;
import com.saama.workbench.model.WbProduct;
import com.saama.workbench.model.WbProductClosure;
import com.saama.workbench.model.WbProductTaxonomy;
import com.saama.workbench.util.AppConstants;

@Repository
public class HierarchyDao {

	private static final Logger logger = Logger.getLogger(HierarchyDao.class);

	@Autowired
	SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	// public Map executeSelectQuery(Map<String, Object> hmInput) throws
	// Exception {
	// String query = hmInput.getOrDefault(AppConstants.QUERY_SQL,
	// "").toString();
	// if (query == null) {
	// return null;
	// }
	//
	// Map<String, Object> nameParams = (Map<String, Object>)
	// hmInput.getOrDefault(AppConstants.NAME_PARAMS, new HashMap<>());
	//
	// Session session = getSessionFactory().getCurrentSession();
	// SQLQuery sqlQry = session.createSQLQuery(query);
	//
	// for (Map.Entry<String, Object>)
	//
	//
	// return null;
	// }

	public Map getHierarchyDataNodes(Map<String, Object> hmInput)
			throws Exception {
		Map<String, Object> hmOutput = new HashMap<>();

		String query = "select  	vw.Ancestor as ParentID, vw.PCode AS ParentProductCode, vw.PName AS ParentProductName, vw.Descendent as ChildID, vw.ChildPCode AS ChildProductCode, vw.ChildPName AS ChildProductName, vw.Length AS Distance, vw.Level AS Level, vw.ChildImg AS ChildImg from ( "
				+ "	select	pcl.Ancestor, p.PCode, p.PName, pcl.Descendent, pc.PCode AS ChildPCode, pc.PName as ChildPName, pcl.Length, t.Level, pc.PImg as ChildImg "
				+ "	from	LND.WB_PRODUCT_Closure pcl "
				+ "			join LND.WB_PRODUCT pc on pc.ProductID_PK = pcl.Descendent "
				+ "			join LND.WB_PRODUCT p on p.ProductID_PK = pcl.Ancestor "
				+ "			JOIN LND.WB_PRODUCT_TAXONOMY t ON t.child_id =  pc.ProductId_Pk"	
				+ "	where	pcl.Ancestor = :"
				+ AppConstants.ROOT_ID
				+ " and pcl.Descendent = :"
				+ AppConstants.ROOT_ID
				+ " "
				+ "	union "
				+ "	select	pcc.Ancestor, p.PCode, p.PName, pcl.Descendent, pc.PCode AS ChildPCode, pc.PName as ChildPName, pcc.Length, t.Level, pc.PImg as ChildImg "
				+ "	from	LND.WB_PRODUCT_Closure pcl "
				+ "			join LND.WB_PRODUCT pc on pc.ProductID_PK = pcl.Descendent "
				+ "			join LND.WB_PRODUCT_Closure pcc on pcc.Descendent = pcl.Descendent and pcc.Length = 1 "
				+ "			join LND.WB_PRODUCT p on p.ProductID_PK = pcc.Ancestor "
				+ "			JOIN LND.WB_PRODUCT_TAXONOMY t ON t.child_id =  pc.ProductId_Pk"
				+ "	where	pcl.Ancestor = :"
				+ AppConstants.ROOT_ID
				+ " and pcl.Descendent <> :"
				+ AppConstants.ROOT_ID
				+ " "
				+ ") vw " + "order by cast(vw.Ancestor as integer), vw.Length";
		

		if (AppConstants.CUSTOMER.equalsIgnoreCase(hmInput.get(AppConstants.TYPE).toString())) {

			query = "select			vw.Ancestor as ParentID, vw.CCode AS ParentCustomerCode, vw.CName AS ParentCustomerName, vw.Descendent as ChildID, vw.ChildPCode AS ChildCustomerCode, vw.ChildPName AS ChildCustomerName, vw.Length AS Distance, vw.Level AS Level, vw.CImg AS CImg "
					+ "from ( "
					+ "		select	pcl.Ancestor, p.CCode, p.CName, pcl.Descendent, pc.CCode AS ChildPCode, pc.CName as ChildPName, pcl.Length, t.Level, pc.CImg "
					+ "		from	LND.WB_CUSTOMERS_Closure pcl "
					+ "				join LND.WB_CUSTOMERS pc on pc.CustomerID_PK = pcl.Descendent "
					+ "				join LND.WB_CUSTOMERS p on p.CustomerID_PK = pcl.Ancestor "
					+ "				JOIN LND.WB_CUSTOMERS_TAXONOMY t ON t.child_id = pc.CustomerID_PK " 
					+ "		where	pcl.Ancestor = :"
					+ AppConstants.ROOT_ID
					+ " and pcl.Descendent = :"
					+ AppConstants.ROOT_ID
					+ " "
					+ "		union "
					+ "		select	pcc.Ancestor, p.CCode, p.CName, pcl.Descendent, pc.CCode AS ChildPCode, pc.CName as ChildPName, pcc.Length, t.Level, pc.CImg "
					+ "		from	LND.WB_CUSTOMERS_Closure pcl "
					+ "				join LND.WB_CUSTOMERS pc on pc.CustomerID_PK = pcl.Descendent "
					+ "				join LND.WB_CUSTOMERS_Closure pcc on pcc.Descendent = pcl.Descendent and pcc.Length = 1 "
					+ "				join LND.WB_CUSTOMERS p on p.CustomerID_PK = pcc.Ancestor "
					+ "				JOIN LND.WB_CUSTOMERS_TAXONOMY t ON t.child_id = pc.CustomerID_PK "
					+ "     where		pcl.Ancestor = :"
					+ AppConstants.ROOT_ID
					+ " and pcl.Descendent <> :"
					+ AppConstants.ROOT_ID
					+ " "
					+ ") vw "
					+ "order by	cast(vw.Ancestor as integer), vw.Length";
		}

		SQLQuery qry = getSessionFactory().getCurrentSession().createSQLQuery(
				query);
		qry.setInteger(AppConstants.ROOT_ID,
				Integer.parseInt(hmInput.get(AppConstants.ROOT_ID).toString()));

		List list = qry.list();

		if (list.size() > 0) {
			hmOutput.put(AppConstants.RESPONSE, list);
		}

		return hmOutput;
	}

	public Map getLevelProductSelectData(Map<String, Object> hmInput)
			throws Exception {
		Map<String, Object> hmOutput = new HashMap<>();
		List<HierarchySelectBean> dataList = new ArrayList<>();
		List<Object[]> list = new ArrayList<>();
		HierarchySelectBean node = null;

		Session session = getSessionFactory().openSession();
		try {
			String query = "SELECT	PLevelNumber LevelNumber, PLevelName LevelName, ProductId_PK ProductId, PId, PCode, PName "
					+ "FROM	LND.WB_PRODUCT_TAXONOMY a  "
					+ "		JOIN LND.WB_PRODUCT b on a.Child_id = b.ProductID_PK "
					+ "		JOIN LND.WB_PRODUCT_MASTER c on a.level = c.PLevelNumber "
					+ "ORDER BY PName;";

			SQLQuery qry = session.createSQLQuery(query);
			list = qry.list();

			if (list.size() > 0) {
				for (Object[] obj : list) {
					node = new HierarchySelectBean();
					node.setLevelNumber(obj[0] != null ? Integer.parseInt(obj[0].toString()) : -1);
					node.setLevelName(obj[1] != null ? obj[1].toString() : AppConstants.BLANK);
					node.setChildId(obj[2] != null ? Integer.parseInt(obj[2].toString()) : -1);
					node.setCId((obj[3] != null ? Integer.parseInt(obj[3].toString()) : -1));
					node.setChildCode(obj[4] != null ? obj[4].toString() : AppConstants.BLANK);
					node.setChildName(obj[5] != null ? obj[5].toString() : AppConstants.BLANK);

					dataList.add(node);
				}
			}

			hmOutput.put(AppConstants.RESPONSE, dataList);
		} finally {
			if (session != null)
				session.close();

			return hmOutput;
		}
	}

	public Map getLevelCustomerSelectData(Map<String, Object> hmInput)
			throws Exception {
		Map<String, Object> hmOutput = new HashMap<>();
		List<HierarchySelectBean> dataList = new ArrayList<>();
		List<Object[]> list = new ArrayList<>();
		HierarchySelectBean node = null;

		Session session = getSessionFactory().openSession();
		try {
			String query = "select	CLevelNumber LevelNumber, CLevelName LevelName, CustomerId_PK CustomerId, CId, CCode CustomerCode, CName CustomerName "
					+ "from	LND.WB_CUSTOMERS_TAXONOMY a "
					+ "		JOIN LND.WB_CUSTOMERS b on a.Child_id = b.CustomerID_PK "
					+ "		JOIN LND.WB_CUSTOMERS_MASTER c on a.level = c.CLevelNumber "
					+ "order by CName";

			SQLQuery qry = session.createSQLQuery(query);
			list = qry.list();

			if (list.size() > 0) {
				for (Object[] obj : list) {
					node = new HierarchySelectBean();
					node.setLevelNumber(obj[0] != null ? Integer.parseInt(obj[0].toString()) : -1);
					node.setLevelName(obj[1] != null ? obj[1].toString() : AppConstants.BLANK);
					node.setChildId(obj[2] != null ? Integer.parseInt(obj[2].toString()) : -1);
					node.setCId((obj[3] != null ? Integer.parseInt(obj[3].toString()) : -1));
					node.setChildCode(obj[4] != null ? obj[4].toString() : AppConstants.BLANK);
					node.setChildName(obj[5] != null ? obj[5].toString() : AppConstants.BLANK);

					dataList.add(node);
				}
			}

			hmOutput.put(AppConstants.RESPONSE, dataList);
		} finally {
			if (session != null)
				session.close();

			return hmOutput;
		}
	}

	public Map insertProduct(Map<String, Object> hmInput) throws Exception {
		Map<String, Object> hmOutput = new HashMap<>();

		Session session = null;

		try {
			session = getSessionFactory().openSession();
			Criteria crit = session.createCriteria(WbProduct.class);
			crit.add(Restrictions.eq("code", hmInput.getOrDefault(AppConstants.CODE, AppConstants.BLANK).toString()));
			crit.add(Restrictions.eq("name",hmInput.getOrDefault(AppConstants.NAME, AppConstants.BLANK).toString()));

			List list = crit.list();
			if (list != null && list.size() > 0) {
				throw new Exception("Product " + hmInput.getOrDefault(AppConstants.NAME, AppConstants.BLANK) + " is already present");
			}

			WbProduct product = new WbProduct();
			product.setCode(hmInput.getOrDefault(AppConstants.CODE, AppConstants.BLANK).toString());
			product.setName(hmInput.getOrDefault(AppConstants.NAME, AppConstants.BLANK).toString());
			product.setDateCreated(new Date(System.currentTimeMillis()));
			product.setLastUpdated(new Date(System.currentTimeMillis()));
			
			if (hmInput.get(AppConstants.NODE_IMAGE) != null) {
				product.setImg((byte[]) hmInput.get(AppConstants.NODE_IMAGE));
			}

			session.save(product);

			hmOutput.put(AppConstants.OBJECT, product);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return hmOutput;
	}
	
	public Map insertCustomer(Map<String, Object> hmInput) throws Exception {
		Map<String, Object> hmOutput = new HashMap<>();

		Session session = null;

		try {
			session = getSessionFactory().openSession();
			Criteria crit = session.createCriteria(WbCustomer.class);
			crit.add(Restrictions.eq("code", hmInput.getOrDefault(AppConstants.CODE, AppConstants.BLANK).toString()));
			crit.add(Restrictions.eq("name", hmInput.getOrDefault(AppConstants.NAME, AppConstants.BLANK).toString()));

			List list = crit.list();
			if (list != null && list.size() > 0) {
				throw new Exception("Customer " + hmInput.getOrDefault(AppConstants.NAME, AppConstants.BLANK) + " is already present");
			}

			WbCustomer customer = new WbCustomer();
			customer.setCode(hmInput.getOrDefault(AppConstants.CODE, AppConstants.BLANK).toString());
			customer.setName(hmInput.getOrDefault(AppConstants.NAME, AppConstants.BLANK).toString());
			customer.setDateCreated(new Date(System.currentTimeMillis()));
			customer.setLastUpdated(new Date(System.currentTimeMillis()));
			
			if (hmInput.get(AppConstants.NODE_IMAGE) != null) {
				customer.setImg((byte[]) hmInput.get(AppConstants.NODE_IMAGE));
			}

			session.save(customer);

			hmOutput.put(AppConstants.OBJECT, customer);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return hmOutput;
	}

	public Map getProductTaxonomy(Map<String, Object> hmInput) throws Exception {
		Map<String, Object> hmOutput = new HashMap<String, Object>();

		Session session = null;
		try {
			session = getSessionFactory().openSession();
			Criteria crit = session.createCriteria(WbProductTaxonomy.class);
			crit.add(Restrictions.eq("childId", Long.parseLong(hmInput.getOrDefault(AppConstants.PARENT_ID, "0").toString())));
			List<WbProductTaxonomy> list = crit.list();

			if (list != null && list.size() > 0) {
				WbProductTaxonomy ProdTaxonomy = list.get(0);
				hmOutput.put(AppConstants.OBJECT, ProdTaxonomy);
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return hmOutput;
	}
	
	public Map getCusomerTaxonomy(Map<String, Object> hmInput) throws Exception {
		Map<String, Object> hmOutput = new HashMap<String, Object>();

		Session session = null;
		try {
			session = getSessionFactory().openSession();
			Criteria crit = session.createCriteria(WbCustomerTaxonomy.class);
			crit.add(Restrictions.eq("childId", Long.parseLong(hmInput.getOrDefault(AppConstants.PARENT_ID, "0").toString())));
			List<WbCustomerTaxonomy> list = crit.list();

			if (list != null && list.size() > 0) {
				WbCustomerTaxonomy custTaxonomy = list.get(0);
				hmOutput.put(AppConstants.OBJECT, custTaxonomy);
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return hmOutput;
	}

	public Map insertProductTaxonomy(Map<String, Object> hmInput)
			throws Exception {
		Map<String, Object> hmOutput = new HashMap<String, Object>();

		Session session = null;
		try {
			session = getSessionFactory().openSession();

			WbProductTaxonomy prodTx = new WbProductTaxonomy();
			prodTx.setChildId(Long.parseLong(hmInput.get(AppConstants.CHILD_ID).toString()));
			prodTx.setParentId(Long.parseLong(hmInput.get(AppConstants.PARENT_ID).toString()));
			prodTx.setLevel(Long.parseLong(hmInput.get(AppConstants.LEVEL).toString()) + 1);

			session.save(prodTx);

			hmOutput.put(AppConstants.OBJECT, prodTx);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return hmOutput;
	}
	
	public Map insertCustomerTaxonomy(Map<String, Object> hmInput) throws Exception {
		Map<String, Object> hmOutput = new HashMap<String, Object>();

		Session session = null;
		try {
			session = getSessionFactory().openSession();

			WbCustomerTaxonomy custTx = new WbCustomerTaxonomy();
			custTx.setChildId(Long.parseLong(hmInput.get(AppConstants.CHILD_ID).toString()));
			custTx.setParentId(Long.parseLong(hmInput.get(AppConstants.PARENT_ID).toString()));
			custTx.setLevel(Long.parseLong(hmInput.get(AppConstants.LEVEL).toString()) + 1);

			session.save(custTx);

			hmOutput.put(AppConstants.OBJECT, custTx);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return hmOutput;
	}

	public Map insertProductClosure(Map<String, Object> hmInput)
			throws Exception {
		Map<String, Object> hmOutput = new HashMap<String, Object>();

		Session session = null;
		try {
			session = getSessionFactory().openSession();
			Criteria crit = session.createCriteria(WbProductClosure.class);
			crit.add(Restrictions.eq("descendent",hmInput.getOrDefault(AppConstants.PARENT_ID, AppConstants.BLANK).toString()));
			List<WbProductClosure> list = crit.list();

			// insert self closure
			WbProductClosure _new = new WbProductClosure();
			_new.setAncestor(hmInput.getOrDefault(AppConstants.CHILD_ID, "0").toString());
			_new.setDescendent(hmInput.getOrDefault(AppConstants.CHILD_ID, "0").toString());
			_new.setLength(0L);
			session.save(_new);

			// insert all parent closure
			if (list != null && list.size() > 0) {
				for (WbProductClosure parentClosure : list) {
					_new = new WbProductClosure(parentClosure);
					_new.setDescendent(hmInput.getOrDefault(AppConstants.CHILD_ID, "0").toString());
					_new.setLength(_new.getLength() + 1);

					session.save(_new);
				}

			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return hmOutput;
	}
	
	public Map insertCustomerClosure(Map<String, Object> hmInput)
			throws Exception {
		Map<String, Object> hmOutput = new HashMap<String, Object>();

		Session session = null;
		try {
			session = getSessionFactory().openSession();
			Criteria crit = session.createCriteria(WbCustomerClosure.class);
			crit.add(Restrictions.eq("descendent", hmInput.getOrDefault(AppConstants.PARENT_ID, AppConstants.BLANK).toString()));
			List<WbCustomerClosure> list = crit.list();

			// insert self closure
			WbCustomerClosure _new = new WbCustomerClosure();
			_new.setAncestor(hmInput.getOrDefault(AppConstants.CHILD_ID, "0").toString());
			_new.setDescendent(hmInput.getOrDefault(AppConstants.CHILD_ID, "0").toString());
			_new.setLength(0L);
			session.save(_new);

			// insert all parent closure
			if (list != null && list.size() > 0) {
				for (WbCustomerClosure parentClosure : list) {
					_new = new WbCustomerClosure(parentClosure);
					_new.setDescendent(hmInput.getOrDefault(AppConstants.CHILD_ID, "0").toString());
					_new.setLength(_new.getLength() + 1);

					session.save(_new);
				}

			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return hmOutput;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map addIntoProductHierarchy(Map<String, Object> hmInput)
			throws Exception {
		Map<String, Object> hmOutput = new HashMap<>();

		hmOutput = insertProduct(hmInput);
		WbProduct childProduct = (WbProduct) hmOutput.get(AppConstants.OBJECT);
		hmInput.put(AppConstants.CHILD_ID, childProduct.getProductIdPK());

		hmOutput = getProductTaxonomy(hmInput);
		WbProductTaxonomy prodTaxonomy = (WbProductTaxonomy) hmOutput.get(AppConstants.OBJECT);
		hmInput.put(AppConstants.LEVEL, prodTaxonomy.getLevel());

		hmOutput = insertProductTaxonomy(hmInput);

		hmOutput = insertProductClosure(hmInput);

		hmOutput.put(AppConstants.SUCCESS, true);
		hmOutput.put(AppConstants.MESSAGE, "Product is added successfully");

		return hmOutput;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map addIntoCustomerHierarchy(Map<String, Object> hmInput)
			throws Exception {
		Map<String, Object> hmOutput = new HashMap<>();

		hmOutput = insertCustomer(hmInput);
		WbCustomer childCustomer = (WbCustomer) hmOutput.get(AppConstants.OBJECT);
		hmInput.put(AppConstants.CHILD_ID, childCustomer.getCustomerIdPK());

		hmOutput = getCusomerTaxonomy(hmInput);
		WbCustomerTaxonomy custTaxonomy = (WbCustomerTaxonomy) hmOutput.get(AppConstants.OBJECT);
		hmInput.put(AppConstants.LEVEL, custTaxonomy.getLevel());

		hmOutput = insertCustomerTaxonomy(hmInput);

		hmOutput = insertCustomerClosure(hmInput);

		hmOutput.put(AppConstants.SUCCESS, true);
		hmOutput.put(AppConstants.MESSAGE, "Product is added successfully");

		return hmOutput;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map deleteProductHierarchy(Map<String, Object> hmInput)
			throws Exception {
		Map<String, Object> hmOutput = new HashMap<>();
		Long productId = Long.parseLong(hmInput.get(AppConstants.PARENT_ID).toString());

		String delProdTaxonomy = "DELETE FROM WbProductTaxonomy WHERE childId in (SELECT descendent FROM WbProductClosure WHERE ancestor = :" + AppConstants.PRODUCTID + " )";
		String delProdClosure = "DELETE FROM WbProductClosure where ancestor = :" + AppConstants.PRODUCTID + " OR descendent = :" + AppConstants.PRODUCTID;
		String delProd = "DELETE FROM WbProduct where productIdPK = :" + AppConstants.PRODUCTID;
		
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(delProdTaxonomy);
		query.setLong(AppConstants.PRODUCTID, productId);
		query.executeUpdate();
		
		query = session.createQuery(delProdClosure);
		query.setLong(AppConstants.PRODUCTID, productId);
		query.executeUpdate();
		
		query = session.createQuery(delProd);
		query.setLong(AppConstants.PRODUCTID, productId);
		query.executeUpdate();
		
		hmOutput.put(AppConstants.SUCCESS, true);
		hmOutput.put(AppConstants.MESSAGE, "Product is added successfully");

		return hmOutput;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map deleteCustomerHierarchy(Map<String, Object> hmInput)
			throws Exception {
		Map<String, Object> hmOutput = new HashMap<>();
		Long customerId = Long.parseLong(hmInput.get(AppConstants.PARENT_ID).toString());

		String delCustTaxonomy = "DELETE FROM WbCustomerTaxonomy WHERE childId in (SELECT descendent FROM WbCustomerClosure WHERE ancestor = :" + AppConstants.CUSTOMERID + " )";
		String delCustClosure = "DELETE FROM WbCustomerClosure where ancestor = :" + AppConstants.CUSTOMERID + " OR descendent = :" + AppConstants.CUSTOMERID;
		String delCust = "DELETE FROM WbCustomer where customerIdPK = :" + AppConstants.CUSTOMERID;
		
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(delCustTaxonomy);
		query.setLong(AppConstants.CUSTOMERID, customerId);
		query.executeUpdate();
		
		query = session.createQuery(delCustClosure);
		query.setLong(AppConstants.CUSTOMERID, customerId);
		query.executeUpdate();
		
		query = session.createQuery(delCust);
		query.setLong(AppConstants.CUSTOMERID, customerId);
		query.executeUpdate();
		
		hmOutput.put(AppConstants.SUCCESS, true);
		hmOutput.put(AppConstants.MESSAGE, "Product is added successfully");

		return hmOutput;
	}
	
	public Map editProductHierarchy(Map<String, Object> hmInput) throws Exception {
		
		Map<String, Object> hmOutput = new HashMap<>();
		
		Session session = getSessionFactory().getCurrentSession();
		WbProduct wbProduct = (WbProduct) session.load(WbProduct.class, Long.parseLong(hmInput.get(AppConstants.NODEID).toString()));
		
		wbProduct.setName(hmInput.get(AppConstants.NODENAME).toString());
		
		if (hmInput.get(AppConstants.NODE_IMAGE) != null) {
			wbProduct.setImg((byte[]) hmInput.get(AppConstants.NODE_IMAGE));
		}
		
		session.update(wbProduct);
		
		
//		StringBuilder sql = new StringBuilder("UPDATE WbProduct SET name = :" + AppConstants.PRODUCTNAME + ", lastUpdated = getDate() where productIdPK = :" + AppConstants.PRODUCTID);
////		Session session = getSessionFactory().getCurrentSession();
//		Query qry = session.createQuery(sql.toString());
//		
//		qry.setString(AppConstants.PRODUCTNAME, hmInput.get(AppConstants.NODENAME).toString());
//		qry.setLong(AppConstants.PRODUCTID, Long.parseLong(hmInput.get(AppConstants.NODEID).toString()));
//		
//		
//		qry.executeUpdate();
		
		hmOutput.put(AppConstants.SUCCESS, true);
		hmOutput.put(AppConstants.MESSAGE, "Product is edited successfully");
		
		return hmOutput;
	}
	
	public Map editCustomerHierarchy(Map<String, Object> hmInput) throws Exception {
		
		Map<String, Object> hmOutput = new HashMap<>();
		
		Session session = getSessionFactory().getCurrentSession();
		WbCustomer wbCustomer = (WbCustomer) session.load(WbCustomer.class, Long.parseLong(hmInput.get(AppConstants.NODEID).toString()));
		
		wbCustomer.setName(hmInput.get(AppConstants.NODENAME).toString());
		
		if (hmInput.get(AppConstants.NODE_IMAGE) != null) {
			wbCustomer.setImg((byte[]) hmInput.get(AppConstants.NODE_IMAGE));
		}
		
		session.update(wbCustomer);
		
		
		
//		StringBuilder sql = new StringBuilder("UPDATE WbCustomer SET name = :" + AppConstants.CUSTOMERNAME+ ", lastUpdated = getDate() where customerIdPK = :" + AppConstants.CUSTOMERID);
////		Session session = getSessionFactory().getCurrentSession();
//		Query qry = session.createQuery(sql.toString());
//		
//		qry.setString(AppConstants.CUSTOMERNAME, hmInput.get(AppConstants.NODENAME).toString());
//		qry.setLong(AppConstants.CUSTOMERID, Long.parseLong(hmInput.get(AppConstants.NODEID).toString()));
//		
//		qry.executeUpdate();
		
		hmOutput.put(AppConstants.SUCCESS, true);
		hmOutput.put(AppConstants.MESSAGE, "Product is edited successfully");
		
		return hmOutput;
	}
}
