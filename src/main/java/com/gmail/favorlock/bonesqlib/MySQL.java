package com.gmail.favorlock.bonesqlib;

import com.gmail.favorlock.bonesqlib.delegates.HostnameDatabase;
import com.gmail.favorlock.bonesqlib.delegates.HostnameDatabaseImpl;
import com.jolbox.bonecp.BoneCPConfig;

import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author Evan Lindsay
 * @date 8/23/13
 * @time 2:09 AM
 */
public class MySQL extends Database {

	private HostnameDatabase delegate = new HostnameDatabaseImpl();

	public MySQL(Logger log, String prefix, String database,
	             String username, String password) {
		super(log, prefix, "[MySQL] ");
		setHostname("localhost");
		setPort(3306);
		setDatabase(database);
		setUsername(username);
		setPassword(password);
	}

	public MySQL(Logger log, String prefix, String hostname,
	             int port, String database, String username,
	             String password) {
		super(log, prefix, "[MySQL] ");
		setHostname(hostname);
		setPort(port);
		setDatabase(database);
		setUsername(username);
		setPassword(password);
	}

	public String getHostname() {
		return delegate.getHostname();
	}

	public void setHostname(String hostname) {
		delegate.setHostname(hostname);
	}

	public int getPort() {
		return delegate.getPort();
	}

	public void setPort(int port) {
		delegate.setPort(port);
	}

	public String getUsername() {
		return delegate.getUsername();
	}

	public void setUsername(String username) {
		delegate.setUsername(username);
	}

	public String getPassword() {
		return delegate.getPassword();
	}

	public void setPassword(String password) {
		delegate.setPassword(password);
	}

	public String getDatabase() {
		return delegate.getDatabase();
	}

	public void setDatabase(String database) {
		delegate.setDatabase(database);
	}

	@Override
	protected boolean initialize() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			return true;
		} catch (ClassNotFoundException e) {
			this.writeError("MySQL driver class missing: " + e.getMessage() + ".", true);
			return false;
		}
	}

	@Override
	public boolean open() {
		if (initialize()) {
			initConfig();
			BoneCPConfig config = getConfig();
			config.setJdbcUrl("jdbc:mysql://" + getHostname() + ":" + getPort() + "/" + getDatabase());
			config.setUsername(getUsername());
			config.setPassword(getPassword());
			config.setMinConnectionsPerPartition(5);
			config.setMaxConnectionsPerPartition(10);
			config.setPartitionCount(1);
			config.setDefaultAutoCommit(true);
			config.setDefaultReadOnly(false);

			Properties properties = new Properties();
			properties.setProperty("zeroDateTimeBehavior", "convertToNull");
			config.setDriverProperties(properties);

			try {
				initCP(config);
				return true;
			} catch (SQLException e) {
				this.writeError("Not connected to the database: " + e.getMessage() + ".", true);
				return false;
			}
		} else {
			return false;
		}
	}
}
