package org.basex.core;

import org.basex.util.Util;

/**
 * Management of executing read/write processes.
 * Supports multiple readers, limited by {@link Prop#PARALLEL},
 * and single writers (readers/writer lock).
 *
 * @author BaseX Team 2005-11, ISC License
 * @author Christian Gruen
 */
public final class Lock {
  /** Mutex object. */
  private final Object mutex = new Object();
  /** Database context. */
  private final Context ctx;

  /** Number of active readers. */
  private int readers;
  /** Writer flag. */
  private boolean writer;

  /**
   * Default constructor.
   * @param c context
   */
  public Lock(final Context c) {
    ctx = c;
  }

  /**
   * Modifications before executing a command.
   * @param w writing flag
   */
  public void register(final boolean w) {
    synchronized(mutex) {
      try {
        while(true) {
          if(!writer) {
            if(w) {
              if(readers == 0) {
                writer = true;
                break;
              }
            } else if(readers < Math.max(ctx.prop.num(Prop.PARALLEL), 1)) {
              ++readers;
              break;
            }
          }
          mutex.wait();
        }
      } catch(final InterruptedException ex) {
        Util.stack(ex);
      }
    }
  }

  /**
   * Modifications after executing a command.
   * @param w writing flag
   */
  public synchronized void unregister(final boolean w) {
    synchronized(mutex) {
      if(w) {
        writer = false;
      } else {
        --readers;
      }
      mutex.notifyAll();
    }
  }
}
