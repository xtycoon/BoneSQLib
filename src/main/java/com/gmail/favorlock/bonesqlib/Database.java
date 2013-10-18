package com.gmail.favorlock.bonesqlib;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * @author Evan Lindsay
 * @date 8/23/13
 * @time 1:34 AM
 */
public abstract class Database {
	/**
	 * Logger to log errors to.
	 */
	protected Logger log;
	/**
	 * Plugin prefix to display during errors.
	 */
	protected final String PREFIX;
	/**
	 * Database prefix to display after the plugin prefix.
	 */
	protected final String DATABASE_PREFIX;
	/**
	 * Whether the Database is connected or not.
	 */
	protected boolean connected;
	/**
	 * The Database connection pool.
	 */
	protected BoneCP connectionPool;
	/**
	 * The Database connection pool configuration.
	 */
	protected BoneCPConfig config;

	/**
	 * Holder for the last update count by a query.
	 */
	public int lastUpdate;

	/**
	 * Constructor used in child class super().
	 *
	 * @param log the Logger used by the plugin.
	 * @param prefix the prefix of the plugin.
	 * @param dp the prefix of the database.
	 */
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

	/**
	 * Writes information to the console.
	 *
	 * @param message the {@link java.lang.String}.
	 * of content to write to the console.
	 */
	protected final String prefix(String message) {
		return this.PREFIX + this.DATABASE_PREFIX + message;
	}

	/**
	 * Writes information to the console.
	 *
	 * @param toWrite the {@link java.lang.String}.
	 * of content to write to the console.
	 */
	public final void writeInfo(String toWrite) {
		if (toWrite != null) {
			this.log.info(prefix(toWrite));
		}
	}

	/**
	 * Writes either errors or warnings to the console.
	 *
	 * @param toWrite the {@link java.lang.String}.
	 * written to the console.
	 * @param severe whether console output should appear as an error or warning.
	 */
	public final void writeError(String toWrite, boolean severe) {
		if (toWrite != null) {
			if (severe) {
				this.log.severe(prefix(toWrite));
			} else {
				this.log.warning(prefix(toWrite));
			}
		}
	}

	/**
	 * Used to check whether the class for the SQL engine is installed.
	 */
	protected abstract boolean initialize();

	/**
	 * Used to initialize the connection pool using the specified configuration.
	 * @param config the configuration used by the connection pool.
	 */
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

	/**
	 * Opens a connection pool with the database.
	 */
	public abstract boolean open();

	/**
	 * Closes the connection pool with the database.
	 */
	public final boolean close() {
		this.connected = false;
		if (connectionPool != null) {
			connectionPool.close();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Specifies whether the connection pool is connected or not.
	 */
	public final boolean isConnected() {
		return this.connected;
	}

	/**
	 * Gets the connection pool variable.
	 */
	public final BoneCP getConnectionPool() {
		return this.connectionPool;
	}

	/**
	 * Initializes the connection pool configuration.
	 */
	public final void initConfig() {
		this.config = new BoneCPConfig();
	}

	/**
	 * Gets the connection pool configuration variable.
	 */
	public final BoneCPConfig getConfig() {
		return this.config;
	}

	/**
	 * Gets a connection from the connection pool.
	 */
	public final Connection getConnection() {
		try {
			return this.getConnectionPool().getConnection();
		} catch (SQLException e) {
			writeError("Failed to get a connection to the database", true);
			return null;
		}
	}

	/**
	 * Gets the last update count from the last execution.
	 *
	 * @return the last update count.
	 */
	public final int getLastUpdateCount() {
		return this.lastUpdate;
	}

	public boolean isTable(String table) {
		Connection connection;
		Statement statement;

		try {
			connection = getConnection();
			statement = connection.createStatement();
		} catch (SQLException e) {
			return false;
		}

		try {
			statement.executeQuery("SELECT * FROM " + table);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	/**
	 * Sends a query to the SQL database.
	 *
	 * @param query the SQL query to send to the database.
	 * @return the table of results from the query.
	 */
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
