package com.bbytes.daas.db.orientDb;


/**
 * This hold the context to resolve the correct data base at run time
 * 
 * @author Thanneer
 * 
 */
public class TenantRouter {

	private static final ThreadLocal<String> tenantDBNameHolder = new ThreadLocal<String>();

	/**
	 * Set the contextHolder with DataSourceContext
	 * 
	 * @param dataConnectionName
	 * @param agent
	 */
	public static void setTenantIdentifier(String tenantIdentifier) {

		
		if (tenantDBNameHolder.get() != null && !tenantIdentifier.equals(tenantDBNameHolder.get())) {
			clearContext();
		}

		tenantDBNameHolder.set(tenantIdentifier);
	}

	/**
	 * @return DataSourceContext from contextHolder
	 */
	public static String getTenantIdentifier() {
		return tenantDBNameHolder.get();
	}

	/**
	 * Remove DataSourceContext from contextHolder
	 */
	public static void clearContext() {
		tenantDBNameHolder.remove();
		OrientDbTemplate.THREAD_LOCAL_DB_INSTANCE.remove();
	}
}
