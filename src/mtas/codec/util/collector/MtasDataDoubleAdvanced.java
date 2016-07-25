package mtas.codec.util.collector;

import java.io.IOException;
import java.util.Collections;
import java.util.TreeSet;
import org.apache.commons.lang.ArrayUtils;
import mtas.codec.util.CodecUtil;
import mtas.codec.util.DataCollector.MtasDataCollector;

/**
 * The Class MtasDataDoubleAdvanced.
 */
public class MtasDataDoubleAdvanced
    extends MtasDataAdvanced<Double, Double, MtasDataItemDoubleAdvanced> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new mtas data double advanced.
   *
   * @param collectorType the collector type
   * @param statsItems the stats items
   * @param sortType the sort type
   * @param sortDirection the sort direction
   * @param start the start
   * @param number the number
   * @param subCollectorTypes the sub collector types
   * @param subDataTypes the sub data types
   * @param subStatsTypes the sub stats types
   * @param subStatsItems the sub stats items
   * @param subSortTypes the sub sort types
   * @param subSortDirections the sub sort directions
   * @param subStart the sub start
   * @param subNumber the sub number
   * @param segmentRegistration the segment registration
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public MtasDataDoubleAdvanced(String collectorType,
      TreeSet<String> statsItems, String sortType, String sortDirection,
      Integer start, Integer number, String[] subCollectorTypes,
      String[] subDataTypes, String[] subStatsTypes,
      TreeSet<String>[] subStatsItems, String[] subSortTypes,
      String[] subSortDirections, Integer[] subStart, Integer[] subNumber,
      boolean segmentRegistration) throws IOException {
    super(collectorType, CodecUtil.DATA_TYPE_DOUBLE, statsItems, sortType,
        sortDirection, start, number, subCollectorTypes, subDataTypes,
        subStatsTypes, subStatsItems, subSortTypes, subSortDirections,
        subStart, subNumber, new MtasDataDoubleOperations(),
        segmentRegistration);
  }

  /*
   * (non-Javadoc)
   * 
   * @see mtas.codec.util.DataCollector.MtasDataCollector#getItem(int)
   */
  @Override
  protected final MtasDataItemDoubleAdvanced getItem(int i) {
    return new MtasDataItemDoubleAdvanced(advancedValueSumList[i],
        advancedValueSumOfLogsList[i], advancedValueSumOfSquaresList[i],
        advancedValueMinList[i], advancedValueMaxList[i],
        advancedValueNList[i], hasSub() ? subCollectorListNextLevel[i] : null,
        statsItems, sortType, sortDirection, errorNumber[i], errorList[i]);
  }

  /*
   * (non-Javadoc)
   * 
   * @see mtas.codec.util.DataCollector.MtasDataCollector#add(long, long)
   */
  @Override
  public MtasDataCollector<?, ?> add(long valueSum, long valueN)
      throws IOException {
    throw new IOException("not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see mtas.codec.util.DataCollector.MtasDataCollector#add(long[], int)
   */
  @Override
  public MtasDataCollector<?, ?> add(long[] values, int number)
      throws IOException {
    MtasDataCollector<?, ?> dataCollector = add();
    Double[] newValues = new Double[number];
    for (int i = 0; i < values.length; i++)
      newValues[i] = Long.valueOf(values[i]).doubleValue();
    setValue(newCurrentPosition, newValues, number, newCurrentExisting);
    return dataCollector;
  }

  /*
   * (non-Javadoc)
   * 
   * @see mtas.codec.util.DataCollector.MtasDataCollector#add(double, long)
   */
  @Override
  public MtasDataCollector<?, ?> add(double valueSum, long valueN)
      throws IOException {
    throw new IOException("not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see mtas.codec.util.DataCollector.MtasDataCollector#add(double[], int)
   */
  @Override
  public MtasDataCollector<?, ?> add(double[] values, int number)
      throws IOException {
    MtasDataCollector<?, ?> dataCollector = add();
    setValue(newCurrentPosition, ArrayUtils.toObject(values), number,
        newCurrentExisting);
    return dataCollector;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * mtas.codec.util.DataCollector.MtasDataCollector#add(java.lang.String[],
   * long, long)
   */
  @Override
  public MtasDataCollector<?, ?>[] add(String[] keys, long valueSum,
      long valueN) throws IOException {
    throw new IOException("not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * mtas.codec.util.DataCollector.MtasDataCollector#add(java.lang.String[],
   * long[], int)
   */
  @Override
  public MtasDataCollector<?, ?>[] add(String[] keys, long[] values,
      int number) throws IOException {
    if (keys != null && keys.length > 0) {
      Double[] newValues = new Double[number];
      for (int i = 0; i < values.length; i++)
        newValues[i] = Long.valueOf(values[i]).doubleValue();
      MtasDataCollector<?, ?>[] subCollectors = new MtasDataCollector<?, ?>[keys.length];
      for (int i = 0; i < keys.length; i++) {
        subCollectors[i] = add(keys[i]);
        setValue(newCurrentPosition, newValues, number, newCurrentExisting);
      }
      return subCollectors;
    } else {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * mtas.codec.util.DataCollector.MtasDataCollector#add(java.lang.String[],
   * double, long)
   */
  @Override
  public MtasDataCollector<?, ?>[] add(String[] keys, double valueSum,
      long valueN) throws IOException {
    throw new IOException("not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * mtas.codec.util.DataCollector.MtasDataCollector#add(java.lang.String[],
   * double[], int)
   */
  @Override
  public MtasDataCollector<?, ?>[] add(String[] keys, double[] values,
      int number) throws IOException {
    if (keys != null && keys.length > 0) {
      MtasDataCollector<?, ?>[] subCollectors = new MtasDataCollector<?, ?>[keys.length];
      for (int i = 0; i < keys.length; i++) {
        subCollectors[i] = add(keys[i]);
        setValue(newCurrentPosition, ArrayUtils.toObject(values), number,
            newCurrentExisting);
      }
      return subCollectors;
    } else {
      return null;
    }
  }

  /* (non-Javadoc)
   * @see mtas.codec.util.DataCollector.MtasDataCollector#compareForComputingSegment(java.lang.Number, java.lang.Number)
   */
  @Override
  protected boolean compareForComputingSegment(Double value,
      Double boundary) {
    return value >= boundary;
  }

  /* (non-Javadoc)
   * @see mtas.codec.util.DataCollector.MtasDataCollector#minimumForComputingSegment(java.lang.Number, java.lang.Number)
   */
  @Override
  protected Double minimumForComputingSegment(Double value, Double boundary) {
    return Math.min(value, boundary);
  }

  /* (non-Javadoc)
   * @see mtas.codec.util.DataCollector.MtasDataCollector#minimumForComputingSegment()
   */
  @Override
  protected Double minimumForComputingSegment() {
    return Collections.min(segmentValueMaxList);
  }

  /* (non-Javadoc)
   * @see mtas.codec.util.DataCollector.MtasDataCollector#boundaryForComputingSegment()
   */
  @Override
  protected Double boundaryForComputingSegment() {
    Double boundary = boundaryForSegment();
    double correctionBoundary = 0;
    for (String otherSegmentName : segmentValueMaxListMin.keySet()) {
      if (!otherSegmentName.equals(segmentName)) {
        Double otherBoundary = segmentValueBoundary.get(otherSegmentName);
        if (otherBoundary != null) {
          correctionBoundary += Math.max(0, otherBoundary - boundary);
        }
      }
    }
    return boundary + correctionBoundary;
  }

  /* (non-Javadoc)
   * @see mtas.codec.util.DataCollector.MtasDataCollector#boundaryForSegment()
   */
  @Override
  protected Double boundaryForSegment() {
    return segmentValueMaxListMin.get(segmentName) / segmentNumber;
  }

}
