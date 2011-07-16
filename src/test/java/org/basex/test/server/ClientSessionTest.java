package org.basex.test.server;

import static org.basex.core.Text.*;
import static org.junit.Assert.*;

import java.io.IOException;

import org.basex.BaseXServer;
import org.basex.server.ClientSession;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * This class tests the client/server API.
 *
 * @author BaseX Team 2005-11, BSD License
 * @author Christian Gruen
 */
public class ClientSessionTest extends SessionTest {
  /** Server reference. */
  private static BaseXServer server;

  /** Starts the server. */
  @BeforeClass
  public static void startServer() {
    server = new BaseXServer("-z");
  }

  /** Stops the server. */
  @AfterClass
  public static void stopServer() {
    server.stop();
  }

  /** Starts a session. */
  @Before
  public void startSession() {
    try {
      session = new ClientSession(server.context, ADMIN, ADMIN, out);
    } catch(final IOException ex) {
      fail(ex.toString());
    }
  }
}