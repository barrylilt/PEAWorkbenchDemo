package com.saama.workbench.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.saama.workbench.model.DatasetMeta;
import com.saama.workbench.util.AppConstants;

@Repository
public class ManageDatasetDao {
	@Autowired
	SessionFactory sessionFactory;
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public DatasetMeta createDataset(DatasetMeta datasetMetaData) {
		// TODO Auto-generated method stub
		boolean isDatasetCreated = false;
		try{
			Session session = getSessionFactory().getCurrentSession();
			//if(!datasetexists(datasetMetaData.getDatasetName())){
			   session.save(datasetMetaData);				
			//}
			
			//datasetMetaData.setIsDatasetExist(true);
			System.out.println("Dataset ending date " + datasetMetaData.getEndingDate());
			session.clear();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return datasetMetaData;
	}

  
	public List<DatasetMeta> retriveListDatasets() {
		// TODO Auto-generated method stub
	
		List<DatasetMeta> lstDatasetMeta = new ArrayList<DatasetMeta>();
		Session session = null;
		try{
//			Session session = getSessionFactory().getCurrentSession();
			session = getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(DatasetMeta.class);
			criteria.add(Restrictions.or(Restrictions.ne("isActive", "N"), Restrictions.isNull("isActive")));
			criteria.addOrder(Order.asc("datasetType"));		
			lstDatasetMeta = criteria.list();;
			
			session.clear();
		} catch(Exception e){
			e.printStackTrace();
		}
		finally {
			if (session != null) 
				session.close();
		}
		
		
		return lstDatasetMeta;
	}
	
	public Boolean datasetexists (String datasetName) {
		Session session = getSessionFactory().getCurrentSession();  
		Query query = session.createSQLQuery("select * from prs.DATASET_DIM t where t.DatasetName = :key");
	 
	        query.setString("key", datasetName);
	    return (query.uniqueResult() != null);
	}

	public boolean deleteDataset(Long datasetId) throws Exception {
		boolean isDatasetDeleted = false;
		try {
			
			Session session = getSessionFactory().getCurrentSession();
			Criteria crit = session.createCriteria(DatasetMeta.class);
			DatasetMeta datasetToDelete = (DatasetMeta) crit.add(Restrictions.eq("datasetId", datasetId)).uniqueResult();
			session.delete(datasetToDelete);
			
			isDatasetDeleted = true;
			
			
		} catch (Exception e) {
			isDatasetDeleted = false;
			throw e;
		}
		return isDatasetDeleted;
	}

	public boolean makeDatasetInActive(Long datasetId) throws Exception {

		boolean isDatasetDeleted = true;

		try {
			Session session = getSessionFactory().getCurrentSession();
			Criteria crit = session.createCriteria(DatasetMeta.class);
			DatasetMeta datasetToDelete = (DatasetMeta) crit.add(
					Restrictions.eq("datasetId", datasetId)).uniqueResult();

			datasetToDelete.setIsActive(AppConstants.N);
			session.saveOrUpdate(datasetToDelete);

		} catch (Exception e) {
			isDatasetDeleted = false;
			throw e;
		}

		return isDatasetDeleted;
	}

}
