package com.mars.azure;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Azure Functions with HTTP Trigger.
 */
public class MSSQLSource {
	
	protected ComboPooledDataSource cpds;
	protected Properties connectionProperties, properties;
	public MSSQLSource() throws IOException {
		getProperties();
		initDatasource();
	}

    protected static final Logger log;
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-16s] %5$s %n");
        log = Logger.getLogger(MSSQLSource.class.getName());
    }
	
	private void initDatasource() {
		cpds = new ComboPooledDataSource();
		cpds.setJdbcUrl(connectionProperties.getProperty("url"));
        cpds.setUser(connectionProperties.getProperty("user"));
        cpds.setPassword(connectionProperties.getProperty("password"));
 
        // Optional Settings
        cpds.setInitialPoolSize(1);
        cpds.setMinPoolSize(1);
        cpds.setAcquireIncrement(1);
        cpds.setMaxPoolSize(5);
        cpds.setMaxStatements(5);
        
        //todo connection testing
	}

	private void getProperties() throws IOException {
		properties = new Properties();
		connectionProperties = new Properties();
		InputStream is = MSSQLSource.class.getClassLoader().getResourceAsStream("application.properties"); 
		connectionProperties.load(is);
		is.close();
		is = MSSQLSource.class.getClassLoader().getResourceAsStream("business.properties");
		properties.load(is);
		is.close();
	}
}
