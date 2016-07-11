package mtas.search;

import java.io.IOException;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.SimpleCollector;


/**
 * The Class MtasCollector.
 */
public class MtasCollector extends SimpleCollector {

  /* (non-Javadoc)
   * @see org.apache.lucene.search.Collector#needsScores()
   */
  @Override
  public boolean needsScores() {
    return false;
  }

  /* (non-Javadoc)
   * @see org.apache.lucene.search.SimpleCollector#doSetNextReader(org.apache.lucene.index.LeafReaderContext)
   */
  @Override
  protected void doSetNextReader(LeafReaderContext context) throws IOException {
       
  }
  
  /* (non-Javadoc)
   * @see org.apache.lucene.search.SimpleCollector#collect(int)
   */
  @Override
  public void collect(int doc) throws IOException {
    System.out.println("Mtas collector voor doc "+doc);
    
  }

  
  
}
