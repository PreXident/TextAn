/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package cz.cuni.mff.ufal.nametag;

public class NamedEntity {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected NamedEntity(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(NamedEntity obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        nametag_javaJNI.delete_NamedEntity(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setStart(long value) {
    nametag_javaJNI.NamedEntity_start_set(swigCPtr, this, value);
  }

  public long getStart() {
    return nametag_javaJNI.NamedEntity_start_get(swigCPtr, this);
  }

  public void setLength(long value) {
    nametag_javaJNI.NamedEntity_length_set(swigCPtr, this, value);
  }

  public long getLength() {
    return nametag_javaJNI.NamedEntity_length_get(swigCPtr, this);
  }

  public void setType(String value) {
    nametag_javaJNI.NamedEntity_type_set(swigCPtr, this, value);
  }

  public String getType() {
    return nametag_javaJNI.NamedEntity_type_get(swigCPtr, this);
  }

  public NamedEntity() {
    this(nametag_javaJNI.new_NamedEntity__SWIG_0(), true);
  }

  public NamedEntity(long start, long length, String type) {
    this(nametag_javaJNI.new_NamedEntity__SWIG_1(start, length, type), true);
  }

}