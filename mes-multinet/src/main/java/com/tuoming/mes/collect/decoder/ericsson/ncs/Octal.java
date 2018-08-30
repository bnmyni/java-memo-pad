package com.tuoming.mes.collect.decoder.ericsson.ncs;


public class Octal extends Numeral  {
	  protected Octal(String name, int position, int length, String description) {
	        super(name, position, length, description);
	    }

	  protected Octal( int position, int length, String description) {
	      super(position, length, description);
	  }


	    @Override
	  public String getValue(byte[] bytes) {
	      return Integer.toOctalString(Integer.parseInt(super.getValue(bytes)));
	  }
}
