package mtas.search.spans;

import java.io.IOException;
import java.util.HashSet;
import org.apache.lucene.search.spans.SpanCollector;
import org.apache.lucene.search.spans.Spans;

import mtas.search.spans.MtasSpanFullyAlignedWithQuery.MtasSpanFullyAlignedWithQuerySpans;
import mtas.search.spans.util.MtasSpans;

/**
 * The Class MtasSpanFullyAlignedWithSpans.
 */
public class MtasSpanFullyAlignedWithSpans extends Spans implements MtasSpans {

  /** The spans 1. */
  private MtasSpanFullyAlignedWithQuerySpans spans1;
  
  /** The spans 2. */
  private MtasSpanFullyAlignedWithQuerySpans spans2;

  /** The last spans 2 start position. */
  private int lastSpans2StartPosition;
  
  /** The last spans 2 end position. */
  private int lastSpans2EndPosition;
  
  /** The previous spans 2 start position. */
  private int previousSpans2StartPosition;
  
  /** The previous spans 2 end positions. */
  private HashSet<Integer> previousSpans2EndPositions;

  /** The called next start position. */
  private boolean calledNextStartPosition;
  
  /** The no more positions. */
  private boolean noMorePositions;

  /** The doc id. */
  private int docId;

  /**
   * Instantiates a new mtas span fully aligned with spans.
   *
   * @param mtasSpanFullyAlignedWithQuery the mtas span fully aligned with query
   * @param spans1 the spans 1
   * @param spans2 the spans 2
   */
  public MtasSpanFullyAlignedWithSpans(
      MtasSpanFullyAlignedWithQuery mtasSpanFullyAlignedWithQuery,
      MtasSpanFullyAlignedWithQuerySpans spans1,
      MtasSpanFullyAlignedWithQuerySpans spans2) {
    super();
    docId = -1;
    this.spans1 = spans1;
    this.spans2 = spans2;
    previousSpans2EndPositions = new HashSet<Integer>();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.lucene.search.spans.Spans#nextStartPosition()
   */
  @Override
  public int nextStartPosition() throws IOException {
    // no document
    if (docId == -1 || docId == NO_MORE_DOCS) {
      throw new IOException("no document");
      // finished
    } else if (noMorePositions) {
      return NO_MORE_POSITIONS;
      // littleSpans already at start match, because of check for matching
      // document
    } else if (!calledNextStartPosition) {
      calledNextStartPosition = true;
      return spans1.spans.startPosition();
      // compute next match
    } else {
      if (goToNextStartPosition()) {
        // match found
        return spans1.spans.startPosition();
      } else {
        // no more matches: document finished
        return NO_MORE_POSITIONS;
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.lucene.search.spans.Spans#startPosition()
   */
  @Override
  public int startPosition() {
    return calledNextStartPosition
        ? (noMorePositions ? NO_MORE_POSITIONS : spans1.spans.startPosition())
        : -1;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.lucene.search.spans.Spans#endPosition()
   */
  @Override
  public int endPosition() {
    return calledNextStartPosition
        ? (noMorePositions ? NO_MORE_POSITIONS : spans1.spans.endPosition())
        : -1;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.lucene.search.spans.Spans#width()
   */
  @Override
  public int width() {
    return calledNextStartPosition ? (noMorePositions ? 0
        : spans1.spans.endPosition() - spans1.spans.startPosition()) : 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.apache.lucene.search.spans.Spans#collect(org.apache.lucene.search.spans
   * .SpanCollector)
   */
  @Override
  public void collect(SpanCollector collector) throws IOException {
    spans1.spans.collect(collector);
    spans2.spans.collect(collector);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.lucene.search.spans.Spans#positionsCost()
   */
  @Override
  public float positionsCost() {
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.lucene.search.DocIdSetIterator#docID()
   */
  @Override
  public int docID() {
    return docId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.lucene.search.DocIdSetIterator#nextDoc()
   */
  @Override
  public int nextDoc() throws IOException {
    reset();
    while (!goToNextDoc())
      ;
    return docId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.lucene.search.DocIdSetIterator#advance(int)
   */
  @Override
  public int advance(int target) throws IOException {
    reset();
    if (docId == NO_MORE_DOCS) {
      return docId;
    } else if (target < docId) {
      // should not happen
      docId = NO_MORE_DOCS;
      return docId;
    } else {
      // advance 1
      int spans1DocId = spans1.spans.docID();
      if (spans1DocId < target) {
        spans1DocId = spans1.spans.advance(target);
        if (spans1DocId == NO_MORE_DOCS) {
          docId = NO_MORE_DOCS;
          return docId;
        }
        target = Math.max(target, spans1DocId);
      }
      int spans2DocId = spans2.spans.docID();
      // advance 2
      if (spans2DocId < target) {
        spans2DocId = spans2.spans.advance(target);
        if (spans2DocId == NO_MORE_DOCS) {
          docId = NO_MORE_DOCS;
          return docId;
        }
      }
      // check equal docId, otherwise next
      if (spans1DocId == spans2DocId) {
        docId = spans1DocId;
        // check match
        if (goToNextStartPosition()) {
          return docId;
        } else {
          return nextDoc();
        }
      } else {
        return nextDoc();
      }
    }
  }

  /**
   * Go to next doc.
   *
   * @return true, if successful
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private boolean goToNextDoc() throws IOException {
    if (docId == NO_MORE_DOCS) {
      return true;
    } else {
      int spans1DocId = spans1.spans.nextDoc();
      int spans2DocId = spans2.spans.docID();
      docId = Math.max(spans1DocId, spans2DocId);
      while (spans1DocId != spans2DocId && docId != NO_MORE_DOCS) {
        if (spans1DocId < spans2DocId) {
          spans1DocId = spans1.spans.advance(spans2DocId);
          docId = spans1DocId;
        } else {
          spans2DocId = spans2.spans.advance(spans1DocId);
          docId = spans2DocId;
        }
      }
      if (docId != NO_MORE_DOCS) {
        if (!goToNextStartPosition()) {
          reset();
          return false;
        }
      }
      return true;
    }
  }

  /**
   * Go to next start position.
   *
   * @return true, if successful
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private boolean goToNextStartPosition() throws IOException {
    int nextSpans1StartPosition, nextSpans1EndPosition;
    int nextSpans2StartPosition, nextSpans2EndPosition;
    while ((nextSpans1StartPosition = spans1.spans
        .nextStartPosition()) != NO_MORE_POSITIONS) {
      nextSpans1EndPosition = spans1.spans.endPosition();
      if (nextSpans1StartPosition == previousSpans2StartPosition) {
        if (previousSpans2EndPositions.contains(nextSpans1EndPosition)) {
          return true;
        }
      } else if (nextSpans1StartPosition == lastSpans2StartPosition) {
        if (nextSpans1EndPosition == lastSpans2EndPosition) {
          return true;
        }
      } else {
        while (lastSpans2StartPosition <= nextSpans1StartPosition) {
          nextSpans2StartPosition = spans2.spans.nextStartPosition();
          if (nextSpans2StartPosition == NO_MORE_POSITIONS) {
            noMorePositions = true;
            return false;
          } else {
            nextSpans2EndPosition = spans2.spans.endPosition();
            if (lastSpans2StartPosition == nextSpans2StartPosition
                && nextSpans1StartPosition == nextSpans2StartPosition) {
              if (previousSpans2StartPosition != lastSpans2StartPosition) {
                previousSpans2StartPosition = lastSpans2StartPosition;
                previousSpans2EndPositions.clear();
              }
              previousSpans2EndPositions.add(lastSpans2EndPosition);
            }
            lastSpans2StartPosition = nextSpans2StartPosition;
            lastSpans2EndPosition = nextSpans2EndPosition;
            if (nextSpans1StartPosition == nextSpans2StartPosition
                && nextSpans1EndPosition == nextSpans2EndPosition) {
              return true;
            }
          }
        }

      }
    }
    noMorePositions = true;
    return false;
  }

  /**
   * Reset.
   */
  private void reset() {
    calledNextStartPosition = false;
    noMorePositions = false;
    lastSpans2StartPosition = -1;
    lastSpans2EndPosition = -1;
    previousSpans2StartPosition = -1;
    previousSpans2EndPositions.clear();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.lucene.search.DocIdSetIterator#cost()
   */
  @Override
  public long cost() {
    return 0;
  }

}
