package mtas.parser.function.util;

import java.io.IOException;

import mtas.codec.util.CodecUtil;

public class MtasFunctionParserFunctionDefault extends MtasFunctionParserFunction {

  
  public MtasFunctionParserFunctionDefault(int numberOfArguments) {
    this.dataType = CodecUtil.DATA_TYPE_LONG;
    this.needPositions = false;
    this.sumRule = true;
    for(int i=0; i<numberOfArguments; i++) {
      this.needArgument.add(i);
    }
  }
  
  @Override
  public double getValueDouble(long[] args, long n) throws IOException {
    double value = 0;
    if(args!=null) {
      for(long a : args) {
        value+=a;
      }
    } 
    return value;
  }

  @Override
  public long getValueLong(long[] args, long n) throws IOException {
    long value = 0;
    if(args!=null) {
      for(long a : args) {
        value+=a;
      }
    } 
    return value;
  }

}
