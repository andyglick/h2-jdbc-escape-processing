package org.zrgs.h2;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author glick
 */
public class JdbcTimestampEscapeSyntaxIT
{
  private static final transient Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static Connection connection;

  @BeforeClass
  public static void startH2TcpServerForTests() throws SQLException
  {
    String connectString = "jdbc:h2:tcp://127.0.0.1/mem:top;MODE=Oracle;MVCC=TRUE;DEFAULT_LOCK_TIMEOUT=60000;DB_CLOSE_ON_EXIT=FALSE;";

    connection = DriverManager.getConnection(connectString, "top", "top");
  }

  @AfterClass
  public static void shutdownH2TcpServerForTests() throws SQLException
  {
    connection.close();
  }

  @Test()
  public void testTSJbcEscapeSyntaxWithTSFails() throws SQLException
  {
    String connectString = "jdbc:h2:mem:top;MODE=Oracle;MVCC=TRUE;DEFAULT_LOCK_TIMEOUT=60000;DB_CLOSE_ON_EXIT=FALSE;";

    Connection connection = DriverManager.getConnection(connectString, "top", "top");

    Statement statement = connection.createStatement();

    ResultSet resultSet = statement.executeQuery("SELECT { TS '2015-03-16 12:12:12.12' }");

    while (resultSet.next())
    {
      LOG.info(resultSet.getTimestamp(1).toString());
    }
  }

  @Test()
  public void testTSJbcEscapeSyntaxWithTSBracesFails() throws SQLException
  {
    String connectString = "jdbc:h2:tcp://127.0.0.1/mem:top;MODE=Oracle;MVCC=TRUE;DEFAULT_LOCK_TIMEOUT=60000;DB_CLOSE_ON_EXIT=FALSE;IFEXISTS=TRUE";

    Connection connection = DriverManager.getConnection(connectString, "top", "top");

    Statement statement = connection.createStatement();

    ResultSet resultSet = statement.executeQuery("SELECT { TS '2015-03-16 12:12:12.12' }");

    while (resultSet.next())
    {
      LOG.info(resultSet.getTimestamp(1).toString());
    }
  }

  @Test
  public void testTSJbcEscapeSyntaxWithTimestampSucceeds() throws SQLException
  {
    String connectString = "jdbc:h2:tcp://127.0.0.1/mem:top;MODE=Oracle;MVCC=TRUE;DEFAULT_LOCK_TIMEOUT=60000;DB_CLOSE_ON_EXIT=FALSE;IFEXISTS=TRUE";

    Connection connection = DriverManager.getConnection(connectString, "top", "top");

    Statement statement = connection.createStatement();

    ResultSet resultSet = statement.executeQuery("SELECT TIMESTAMP '2015-03-16 12:12:12.12'");

    while (resultSet.next())
    {
      LOG.info(resultSet.getTimestamp(1).toString());
    }
  }
}
