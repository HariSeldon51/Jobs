/**
 * Jobs Plugin for Bukkit
 * Copyright (C) 2011 Zak Ford <zak.j.ford@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gamingmesh.jobs.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.PlayerInfo;
import com.gamingmesh.jobs.stuff.UUIDUtil;

public class JobsDAOMySQL extends JobsDAO {
    private String database;

    private JobsDAOMySQL(String hostname, String database, String username, String password, String prefix) {
	super("com.mysql.jdbc.Driver", "jdbc:mysql://" + hostname + "/" + database, username, password, prefix);
	this.database = database;
    }

    public static JobsDAOMySQL initialize(String hostname, String database, String username, String password, String prefix) {
	JobsDAOMySQL dao = new JobsDAOMySQL(hostname, database, username, password, prefix);
	try {
	    dao.setUp();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return dao;
    }

    @Override
    protected synchronized void setupConfig() throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null) {
	    Jobs.getPluginLogger().severe("Could not run database updates!  Could not connect to MySQL!");
	    return;
	}
	PreparedStatement prest = null;
	int rows = 0;
	try {
	    // Check for config table
	    prest = conn.prepareStatement("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ? AND table_name = ?;");
	    prest.setString(1, database);
	    prest.setString(2, getPrefix() + "config");
	    ResultSet res = prest.executeQuery();
	    if (res.next()) {
		rows = res.getInt(1);
	    }
	} finally {
	    if (prest != null) {
		try {
		    prest.close();
		} catch (SQLException e) {
		}
	    }
	}

	if (rows == 0) {
	    PreparedStatement insert = null;
	    try {
		executeSQL("CREATE TABLE `" + getPrefix() + "config` (`key` varchar(50) NOT NULL PRIMARY KEY, `value` varchar(100) NOT NULL);");

		insert = conn.prepareStatement("INSERT INTO `" + getPrefix() + "config` (`key`, `value`) VALUES (?, ?);");
		insert.setString(1, "version");
		insert.setString(2, "1");
		insert.execute();
	    } finally {
		if (insert != null) {
		    try {
			insert.close();
		    } catch (SQLException e) {
		    }
		}
	    }
	}
    }

    @Override
    protected synchronized void checkUpdate() throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null) {
	    Jobs.getPluginLogger().severe("Could not run database updates!  Could not connect to MySQL!");
	    return;
	}
	createDefaultJobsBase();
	createDefaultLogBase();
	createDefaultArchiveBase();
	createDefaultPointsBase();
	createDefaultExploreBase();
	createDefaultUsersBase();
    }

    @Override
    protected synchronized void checkUpdate2() throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null) {
	    Jobs.getPluginLogger().severe("Could not run database updates!  Could not connect to MySQL!");
	    return;
	}
	PreparedStatement prest = null;
	int rows = 0;
	try {
	    // Check for jobs table
	    prest = conn.prepareStatement("SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = ? AND table_name = ? AND column_name = ?;");
	    prest.setString(1, database);
	    prest.setString(2, getPrefix() + "jobs");
	    prest.setString(3, "username");
	    ResultSet res = prest.executeQuery();
	    if (res.next()) {
		rows = res.getInt(1);
	    }
	} finally {
	    if (prest != null) {
		try {
		    prest.close();
		} catch (SQLException e) {
		}
	    }
	}

	try {
	    if (rows == 0)
		executeSQL("ALTER TABLE `" + getPrefix() + "jobs` ADD COLUMN `username` varchar(20);");
	} finally {
	}
    }

    @Override
    protected synchronized void checkUpdate4() throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null) {
	    Jobs.getPluginLogger().severe("Could not run database updates!  Could not connect to MySQL!");
	    return;
	}
	PreparedStatement prest = null;
	int rows = 0;
	try {
	    // Check for jobs table
	    prest = conn.prepareStatement("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ? AND table_name = ?;");
	    prest.setString(1, database);
	    prest.setString(2, getPrefix() + "archive");
	    ResultSet res = prest.executeQuery();
	    if (res.next()) {
		rows = res.getInt(1);
	    }
	} finally {
	    if (prest != null) {
		try {
		    prest.close();
		} catch (SQLException e) {
		}
	    }
	}

	try {
	    if (rows == 0)
		executeSQL("CREATE TABLE `" + getPrefix()
		    + "archive` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `player_uuid` binary(16) NOT NULL, `username` varchar(20), `job` varchar(20), `experience` int, `level` int);");
	} finally {
	}
    }

    @Override
    protected synchronized void checkUpdate5() throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null) {
	    Jobs.getPluginLogger().severe("Could not run database updates!  Could not connect to MySQL!");
	    return;
	}
	PreparedStatement prest = null;
	int rows = 0;
	try {
	    // Check for jobs table
	    prest = conn.prepareStatement("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ? AND table_name = ?;");
	    prest.setString(1, database);
	    prest.setString(2, getPrefix() + "log");
	    ResultSet res = prest.executeQuery();
	    if (res.next()) {
		rows = res.getInt(1);
	    }
	} finally {
	    if (prest != null) {
		try {
		    prest.close();
		} catch (SQLException e) {
		}
	    }
	}

	try {
	    if (rows == 0)
		executeSQL("CREATE TABLE `" + getPrefix()
		    + "log` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `player_uuid` binary(16) NOT NULL, `username` varchar(20), `time` bigint, `action` varchar(20), `itemname` varchar(60), `count` int, `money` double, `exp` double);");
	} finally {
	}
    }

    @Override
    protected synchronized void checkUpdate6() throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null) {
	    Jobs.getPluginLogger().severe("Could not run database updates!  Could not connect to MySQL!");
	    return;
	}
	try {
	    executeSQL("ALTER TABLE `" + getPrefix() + "log` MODIFY `itemname` VARCHAR(60);");
	} catch (Exception e) {
	}
    }

    @Override
    protected synchronized void checkUpdate7() throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null) {
	    Jobs.getPluginLogger().severe("Could not run database updates!  Could not connect to MySQL!");
	    return;
	}

	boolean convertJobs = true;
	PreparedStatement tempPst = conn.prepareStatement("SELECT * FROM `" + getPrefix() + "jobs`;");
	ResultSet tempRes = tempPst.executeQuery();

	boolean noJobsdata = true;
	try {
	    while (tempRes.next()) {
		noJobsdata = false;
		tempRes.getByte("player_uuid");
		break;
	    }
	} catch (Exception e) {
	    convertJobs = false;
	} finally {
	    tempRes.close();
	    tempPst.close();
	}
	if (noJobsdata) {
	    dropDataBase("jobs");
	    createDefaultJobsBase();
	    convertJobs = false;
	}

	if (convertJobs) {
	    Jobs.getPluginLogger().info("Converting byte uuids to string.  This could take a long time!!!");
	    // Converting jobs players byte uuid into string
	    try {
		executeSQL("CREATE TABLE `" + getPrefix()
		    + "jobs_temp` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `player_uuid` varchar(36) NOT NULL,`username` varchar(20), `job` varchar(20), `experience` int, `level` int);");
	    } catch (Exception e) {
	    }

	    PreparedStatement pst1 = conn.prepareStatement("SELECT * FROM `" + getPrefix() + "jobs`;");
	    ResultSet rs = pst1.executeQuery();
	    PreparedStatement insert = null;

	    conn.setAutoCommit(false);

	    while (rs.next()) {

		byte[] uuidBytes = rs.getBytes("player_uuid");

		if (uuidBytes == null)
		    continue;

		String uuid = UUIDUtil.fromBytes(uuidBytes).toString();

		if (uuid != null) {
		    insert = conn.prepareStatement("INSERT INTO `" + getPrefix()
			+ "jobs_temp` (`player_uuid`, `username`, `job`, `experience`, `level`) VALUES (?, ?, ?, ?, ?);");
		    insert.setString(1, uuid);
		    insert.setString(2, rs.getString("username"));
		    insert.setString(3, rs.getString("job"));
		    insert.setInt(4, rs.getInt("experience"));
		    insert.setInt(5, rs.getInt("level"));
		    insert.addBatch();
		}
	    }

	    if (insert != null)
		insert.executeBatch();
	    conn.commit();
	    conn.setAutoCommit(true);

	    rs.close();
	    if (insert != null)
		insert.close();

	    executeSQL("DROP TABLE IF EXISTS `" + getPrefix() + "jobs`;");
	    executeSQL("ALTER TABLE `" + getPrefix() + "jobs_temp` RENAME TO `" + getPrefix() + "jobs`;");
	}

	boolean convertArchive = true;
	PreparedStatement tempArchivePst = conn.prepareStatement("SELECT * FROM `" + getPrefix() + "archive`;");
	ResultSet tempArchiveRes = tempArchivePst.executeQuery();

	boolean noArchivedata = true;
	try {
	    while (tempArchiveRes.next()) {
		noArchivedata = false;
		tempArchiveRes.getByte("player_uuid");
		break;
	    }
	} catch (Exception e) {
	    convertArchive = false;
	} finally {
	    tempArchiveRes.close();
	    tempArchivePst.close();
	}
	if (noArchivedata) {
	    dropDataBase("archive");
	    createDefaultArchiveBase();
	    convertArchive = false;
	}

	if (convertArchive) {
	    // Converting archive players byte uuid into string
	    try {
		executeSQL("CREATE TABLE `" + getPrefix()
		    + "archive_temp` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `player_uuid` varchar(36) NOT NULL, `username` varchar(20), `job` varchar(20), `experience` int, `level` int);");
	    } catch (Exception e) {
	    }

	    PreparedStatement pst11 = conn.prepareStatement("SELECT * FROM `" + getPrefix() + "archive`;");
	    ResultSet rs1 = pst11.executeQuery();
	    PreparedStatement insert1 = null;

	    conn.setAutoCommit(false);

	    while (rs1.next()) {
		String uuid = UUIDUtil.fromBytes(rs1.getBytes("player_uuid")).toString();
		if (uuid != null) {
		    insert1 = conn.prepareStatement("INSERT INTO `" + getPrefix()
			+ "archive_temp` (`player_uuid`, `username`, `job`, `experience`, `level`) VALUES (?, ?, ?, ?, ?);");
		    insert1.setString(1, uuid);
		    insert1.setString(2, rs1.getString("username"));
		    insert1.setString(3, rs1.getString("job"));
		    insert1.setInt(4, rs1.getInt("experience"));
		    insert1.setInt(5, rs1.getInt("level"));
		    insert1.addBatch();
		}
	    }
	    if (insert1 != null)
		insert1.executeBatch();
	    conn.commit();
	    conn.setAutoCommit(true);

	    rs1.close();
	    if (insert1 != null)
		insert1.close();

	    executeSQL("DROP TABLE IF EXISTS `" + getPrefix() + "archive`;");
	    try {
		executeSQL("ALTER TABLE `" + getPrefix() + "archive_temp` RENAME TO `" + getPrefix() + "archive`;");
	    } catch (Exception e) {
	    }
	}

	boolean convertLog = true;
	PreparedStatement tempLogPst = conn.prepareStatement("SELECT * FROM `" + getPrefix() + "log`;");
	ResultSet tempLogRes = tempLogPst.executeQuery();

	boolean nodata = true;
	try {
	    while (tempLogRes.next()) {
		nodata = false;
		tempLogRes.getByte("player_uuid");
		break;
	    }
	} catch (Exception e) {
	    convertLog = false;
	} finally {
	    tempLogRes.close();
	    tempLogPst.close();
	}
	if (nodata) {
	    dropDataBase("log");
	    createDefaultLogBase();
	    convertLog = false;
	}

	if (convertLog) {
	    Bukkit.getConsoleSender().sendMessage("Converting log database");
	    // Converting log players byte uuid into string
	    try {
		executeSQL("CREATE TABLE `" + getPrefix()
		    + "log_temp` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `player_uuid` varchar(36) NOT NULL, `username` varchar(20), `time` bigint, `action` varchar(20), `itemname` varchar(60), `count` int, `money` double, `exp` double);");
	    } catch (Exception e) {
	    }

	    PreparedStatement pst111 = conn.prepareStatement("SELECT * FROM `" + getPrefix() + "log`;");
	    ResultSet rs11 = pst111.executeQuery();
	    PreparedStatement insert11 = null;

	    conn.setAutoCommit(false);

	    while (rs11.next()) {
		String uuid = UUIDUtil.fromBytes(rs11.getBytes("player_uuid")).toString();
		if (uuid != null) {
		    insert11 = conn.prepareStatement("INSERT INTO `" + getPrefix()
			+ "log_temp` (`player_uuid`, `username`, `time`, `action`, `itemname`, `count`, `money`, `exp`) VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
		    insert11.setString(1, uuid);
		    insert11.setString(2, rs11.getString("username"));
		    insert11.setLong(3, rs11.getLong("time"));
		    insert11.setString(4, rs11.getString("action"));
		    insert11.setString(5, rs11.getString("itemname"));
		    insert11.setInt(6, rs11.getInt("count"));
		    insert11.setDouble(7, rs11.getDouble("money"));
		    insert11.setDouble(8, rs11.getDouble("exp"));
		    insert11.addBatch();
		}
	    }
	    if (insert11 != null)
		insert11.executeBatch();
	    conn.commit();
	    conn.setAutoCommit(true);

	    rs11.close();
	    if (insert11 != null)
		insert11.close();

	    executeSQL("DROP TABLE IF EXISTS `" + getPrefix() + "log`;");
	    try {
		executeSQL("ALTER TABLE `" + getPrefix() + "log_temp` RENAME TO `" + getPrefix() + "log`;");
	    } catch (Exception e) {
	    }
	}
    }

    @Override
    protected synchronized void checkUpdate8() throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null) {
	    Jobs.getPluginLogger().severe("Could not run database updates!  Could not connect to MySQL!");
	    return;
	}
	PreparedStatement prest = null;
	int rows = 0;
	try {
	    // Check for jobs table
	    prest = conn.prepareStatement("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ? AND table_name = ?;");
	    prest.setString(1, database);
	    prest.setString(2, getPrefix() + "explore");
	    ResultSet res = prest.executeQuery();
	    if (res.next()) {
		rows = res.getInt(1);
	    }
	} finally {
	    if (prest != null) {
		try {
		    prest.close();
		} catch (SQLException e) {
		}
	    }
	}

	try {
	    if (rows == 0)
		createDefaultExploreBase();
	} catch (Exception e) {
	} finally {
	}
    }

    @Override
    protected synchronized void checkUpdate9() throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null) {
	    Jobs.getPluginLogger().severe("Could not run database updates!  Could not connect to MySQL!");
	    return;
	}

	PreparedStatement tempPrest = null;
	int rows = 0;
	try {
	    // Check for jobs table
	    tempPrest = conn.prepareStatement("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ? AND table_name = ?;");
	    tempPrest.setString(1, database);
	    tempPrest.setString(2, getPrefix() + "users");
	    ResultSet res = tempPrest.executeQuery();
	    if (res.next()) {
		rows = res.getInt(1);
	    }
	} finally {
	    if (tempPrest != null) {
		try {
		    tempPrest.close();
		} catch (SQLException e) {
		}
	    }
	}

	if (rows == 0) {
	    HashMap<String, String> tempMap = new HashMap<String, String>();
	    PreparedStatement prest = null;
	    try {
		prest = conn.prepareStatement("SELECT DISTINCT(player_uuid),username FROM " + getPrefix() + "jobs;");
		ResultSet res = prest.executeQuery();
		while (res.next()) {
		    tempMap.put(res.getString("player_uuid"), res.getString("username"));
		}
	    } finally {
		if (prest != null) {
		    try {
			prest.close();
		    } catch (SQLException e) {
		    }
		}
	    }

	    try {
		prest = conn.prepareStatement("SELECT DISTINCT(player_uuid),username FROM " + getPrefix() + "archive;");
		ResultSet res = prest.executeQuery();
		while (res.next()) {
		    tempMap.put(res.getString("player_uuid"), res.getString("username"));
		}
	    } finally {
		if (prest != null) {
		    try {
			prest.close();
		    } catch (SQLException e) {
		    }
		}
	    }

	    try {
		prest = conn.prepareStatement("SELECT DISTINCT(player_uuid),username FROM " + getPrefix() + "log;");
		ResultSet res = prest.executeQuery();
		while (res.next()) {
		    tempMap.put(res.getString("player_uuid"), res.getString("username"));
		}
	    } finally {
		if (prest != null)
		    try {
			prest.close();
		    } catch (SQLException e) {
		    }
	    }

	    try {
		executeSQL("CREATE TABLE `" + getPrefix()
		    + "users` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `player_uuid` varchar(36) NOT NULL, `username` varchar(20));");
	    } catch (Exception e) {
	    }

	    try {
		prest = conn.prepareStatement("INSERT INTO `" + getPrefix() + "users` (`player_uuid`, `username`) VALUES (?, ?);");
		conn.setAutoCommit(false);
		for (Entry<String, String> users : tempMap.entrySet()) {
		    prest.setString(1, users.getKey());
		    prest.setString(2, users.getValue());
		    prest.addBatch();
		}
		prest.executeBatch();
		conn.commit();
		conn.setAutoCommit(true);
	    } finally {
		if (prest != null) {
		    try {
			prest.close();
		    } catch (SQLException e) {
		    }
		}
	    }

	    HashMap<String, PlayerInfo> tempPlayerMap = new HashMap<String, PlayerInfo>();

	    try {
		prest = conn.prepareStatement("SELECT * FROM " + getPrefix() + "users;");
		ResultSet res = prest.executeQuery();
		while (res.next()) {
		    tempPlayerMap.put(res.getString("player_uuid"), new PlayerInfo(res.getString("username"), res.getInt("id")));
		}
	    } finally {
		if (prest != null) {
		    try {
			prest.close();
		    } catch (SQLException e) {
		    }
		}
	    }

	    // Modifying jobs main table
	    try {
		executeSQL("ALTER TABLE `" + getPrefix() + "jobs` ADD COLUMN `userid` int;");
	    } catch (Exception e) {
	    }
	    try {
		prest = conn.prepareStatement("UPDATE `" + getPrefix() + "jobs` SET `userid` = ? WHERE `player_uuid` = ?;");
		conn.setAutoCommit(false);
		for (Entry<String, PlayerInfo> users : tempPlayerMap.entrySet()) {
		    prest.setInt(1, users.getValue().getID());
		    prest.setString(2, users.getKey());
		    prest.addBatch();
		}
		prest.executeBatch();
		conn.commit();
		conn.setAutoCommit(true);
	    } finally {
		if (prest != null) {
		    try {
			prest.close();
		    } catch (SQLException e) {
		    }
		}
	    }
	    try {
		executeSQL("ALTER TABLE `" + getPrefix() + "jobs` DROP COLUMN `player_uuid`, DROP COLUMN `username`;");
	    } catch (Exception e) {
	    }
	    // Modifying jobs archive table
	    try {
		executeSQL("ALTER TABLE `" + getPrefix() + "archive` ADD COLUMN `userid` int;");
	    } catch (Exception e) {
	    }
	    try {
		prest = conn.prepareStatement("UPDATE `" + getPrefix() + "archive` SET `userid` = ? WHERE `player_uuid` = ?;");
		conn.setAutoCommit(false);
		for (Entry<String, PlayerInfo> users : tempPlayerMap.entrySet()) {
		    prest.setInt(1, users.getValue().getID());
		    prest.setString(2, users.getKey());
		    prest.addBatch();
		}
		prest.executeBatch();
		conn.commit();
		conn.setAutoCommit(true);
	    } finally {
		if (prest != null) {
		    try {
			prest.close();
		    } catch (SQLException e) {
		    }
		}
	    }
	    try {
		executeSQL("ALTER TABLE `" + getPrefix() + "archive` DROP COLUMN `player_uuid`, DROP COLUMN `username`;");
	    } catch (Exception e) {
	    }
	    // Modifying jobs log table
	    try {
		executeSQL("ALTER TABLE `" + getPrefix() + "log` ADD COLUMN `userid` int;");
	    } catch (Exception e) {
	    }
	    try {
		prest = conn.prepareStatement("UPDATE `" + getPrefix() + "log` SET `userid` = ? WHERE `player_uuid` = ?;");
		conn.setAutoCommit(false);
		for (Entry<String, PlayerInfo> users : tempPlayerMap.entrySet()) {
		    prest.setInt(1, users.getValue().getID());
		    prest.setString(2, users.getKey());
		    prest.addBatch();
		}
		prest.executeBatch();
		conn.commit();
		conn.setAutoCommit(true);
	    } finally {
		if (prest != null) {
		    try {
			prest.close();
		    } catch (SQLException e) {
		    }
		}
	    }
	    try {
		executeSQL("ALTER TABLE `" + getPrefix() + "log` DROP COLUMN `player_uuid`, DROP COLUMN `username`;");
	    } catch (Exception e) {
	    }
	    // Create new points table
	    createDefaultPointsBase();
	}
    }

    private boolean createDefaultExploreBase() {
	try {
	    executeSQL("CREATE TABLE `" + getPrefix()
		+ "explore` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `worldname` varchar(64), `chunkX` int, `chunkZ` int, `playerName` varchar(32));");
	} catch (SQLException e) {
	    return false;
	}
	return true;
    }

    private boolean createDefaultPointsBase() {
	try {
	    executeSQL("CREATE TABLE `" + getPrefix()
		+ "points` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `userid` int, `totalpoints` double, `currentpoints` double);");
	} catch (SQLException e) {
	    return false;
	}
	return true;
    }

    private boolean createDefaultLogBase() {
	try {
	    executeSQL("CREATE TABLE `" + getPrefix()
		+ "log` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `userid` int, `time` bigint, `action` varchar(20), `itemname` varchar(60), `count` int, `money` double, `exp` double);");
	} catch (SQLException e) {
	    return false;
	}
	return true;
    }

    private boolean createDefaultArchiveBase() {
	try {
	    executeSQL("CREATE TABLE `" + getPrefix()
		+ "archive` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `userid` int, `job` varchar(20), `experience` int, `level` int);");
	} catch (SQLException e) {
	    return false;
	}
	return true;
    }

    private boolean createDefaultJobsBase() {
	try {
	    executeSQL("CREATE TABLE `" + getPrefix()
		+ "jobs` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `userid` int, `job` varchar(20), `experience` int, `level` int);");
	} catch (SQLException e) {
	    return false;
	}
	return true;
    }

    private boolean createDefaultUsersBase() {
	try {
	    executeSQL("CREATE TABLE `" + getPrefix()
		+ "users` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `player_uuid` varchar(36) NOT NULL, `username` varchar(20));");
	} catch (SQLException e) {
	    return false;
	}
	return true;
    }

    private boolean dropDataBase(String name) {
	try {
	    executeSQL("DROP TABLE IF EXISTS `" + getPrefix() + name + "`;");
	} catch (SQLException e) {
	    return false;
	}
	return true;
    }
}
