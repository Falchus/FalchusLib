package com.falchus.lib.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Utility class to manage MySQL connection.
 */
@RequiredArgsConstructor
public class MySQL {

	@NonNull private final String host;
	private final int port;
	@NonNull private final String database;
	@NonNull private final String username;
	private final String password;
	
	private Connection connection;
	
	/**
	 * Establishes a new connection if none is currently open.
	 */
	public void connect() {
		if (isConnected()) return;
		try {
			String url = "jdbc:mysql://" + host + ":" + port + "/" + database 
					+ "?useSSL=false&autoReconnect=true&characterEncoding=UTF-8&useUnicode=true";

            if (password == null) {
                connection = DriverManager.getConnection(url, username, "");
            } else {
                connection = DriverManager.getConnection(url, username, password);
            }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks whether the connection is active and not closed.
	 * 
	 * @return true if a valid connection is open
	 */
	public boolean isConnected() {
		try {
			return connection != null && !connection.isClosed();
		} catch (SQLException e) {
			return false;
		}
	}
	
	/**
	 * Closes the current connection if it exists.
	 */
	public void disconnect() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException ignored) {}
			connection = null;
		}
	}
	
	/**
	 * Returns the active connection, reconnecting if necessary.
	 * 
	 * @return an open {@link Connection} instance (may be newly created)
	 */
    public Connection getConnection() {
        if (!isConnected()) {
            connect();
        }
        return connection;
    }
}
