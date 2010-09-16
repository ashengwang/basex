package org.basex.query.expr;

import org.basex.query.QueryContext;
import org.basex.query.QueryException;
import org.basex.query.item.Empty;
import org.basex.query.item.Item;
import org.basex.query.item.Nod;
import org.basex.query.iter.Iter;
import org.basex.query.iter.NodIter;
import org.basex.query.iter.NodeIter;
import org.basex.util.InputInfo;

/**
 * Intersect expression.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-10, ISC License
 * @author Christian Gruen
 */
public final class InterSect extends Set {
  /**
   * Constructor.
   * @param ii input info
   * @param l expression list
   */
  public InterSect(final InputInfo ii, final Expr[] l) {
    super(ii, l);
  }

  @Override
  public Expr comp(final QueryContext ctx) throws QueryException {
    super.comp(ctx);
    return oneEmpty() ? optPre(Empty.SEQ, ctx) : this;
  }

  @Override
  protected NodIter eval(final Iter[] iter) throws QueryException {
    NodIter ni = new NodIter();

    Item it;
    while((it = iter[0].next()) != null) ni.add(checkNode(it));
    final boolean db = ni.dbnodes();

    for(int e = 1; e != expr.length && ni.size() != 0; ++e) {
      final NodIter nt = new NodIter().random();
      final Iter ir = iter[e];
      while((it = ir.next()) != null) {
        final Nod n = checkNode(it);
        final int i = ni.indexOf(n, db);
        if(i != -1) nt.add(n);
      }
      ni = nt;
    }
    return ni;
  }

  @Override
  protected NodeIter iter(final Iter[] iter) {
    return new SetIter(iter) {
      @Override
      public Nod next() throws QueryException {
        if(item == null) item = new Nod[iter.length];

        for(int i = 0; i != iter.length; ++i) if(!next(i)) return null;

        for(int i = 1; i != item.length;) {
          final int d = item[0].diff(item[i]);
          if(d > 0) {
            if(!next(i)) return null;
          } else if(d < 0) {
            if(!next(0)) return null;
            i = 1;
          } else {
            ++i;
          }
        }
        return item[0];
      }
    };
  }
}
