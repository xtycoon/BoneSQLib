package com.gmail.favorlock.bonesqlib;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import sun.jdbc.odbc.ee.ConnectionPool;

import java.sql.*;
import java.util.logging.Logger;

/**
 * @author Evan Lindsay
 * @date 8/23/13
 * @time 1:34 AM
 */
public abstract class Database {

	protected Logger log;
	protected final String PREFIX;
	protected final String DATABASE_PREFIX;
	protected boolean connected;
	protected BoneCP connectionPool;
	protected BoneCPConfig config;

	public int lastUpdate;

	public Database(Logger log, String prefix, String dp) {
		if (log == null) {
			// TODO DatabaseException
		}
		if (prefix == null || prefix.length() == 0) {
			// TODO DatabaseException
		}

		this.log = log;
		this.PREFIX = prefix;
		this.DATABASE_PREFIX = dp;
		this.connected = false;
	}

	protected final String prefix(String message) {
		return this.PREFIX + this.DATABASE_PREFIX + message;
	}

	public final void writeInfo(String toWrite) {
		if (toWrite != null) {
			this.log.info(prefix(toWrite));
		}
	}

	public final void writeError(String toWrite, boolean severe) {
		if (toWrite != null) {
			if (severe) {
				this.log.severe(prefix(toWrite));
			} else {
				this.log.warning(prefix(toWrite));
			}
		}
	}

	protected abstract boolean initialize();

	protected boolean initCP(BoneCPConfig config) throws SQLException {
		connectionPool = new BoneCP(config);
		Connection connection = this.getConnection();

		if (connection == null) {
			return false;
		} else {
			connected = true;
			connection.close();
			return true;
		}
	}

	public abstract boolean open();

	public final boolean close() {
		this.connected = false;
		if (connectionPool != null) {
			connectionPool.close();
			return true;
		} else {
			return false;
		}
	}

	public final boolean isConnected() {
		return this.connected;
	}

	public final BoneCP getConnectionPool() {
		return this.connectionPool;
	}

	public final void initConfig() {
		this.config = new BoneCPConfig();
	}

	public final BoneCPConfig getConfig() {
		return this.config;
	}

	public final Connection getConnection() throws SQLException {
		return this.getConnectionPool().getConnection();
	}

	public final int getLastUpdateCount() {
		return this.lastUpdate;
	}

	public final ResultSet query(String query) throws SQLException {
		Connection connection = this.getConnection();
		Statement statement = connection.createStatement();
		ResultSet rs;
		if (statement.execute(query)) {
			rs = statement.getResultSet();
			connection.close();
			return rs;
		} else {
			int uc = statement.getUpdateCount();
			this.lastUpdate = uc;
			rs = connection.createStatement().executeQuery("SELECT " + uc);
			connection.close();
			return rs;
		}
	}
}
