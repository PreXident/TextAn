/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package cz.cuni.mff.ufal.morphodita;

public class TaggedForms {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected TaggedForms(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(TaggedForms obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        morphodita_javaJNI.delete_TaggedForms(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public TaggedForms() {
    this(morphodita_javaJNI.new_TaggedForms__SWIG_0(), true);
  }

  public TaggedForms(long n) {
    this(morphodita_javaJNI.new_TaggedForms__SWIG_1(n), true);
  }

  public long size() {
    return morphodita_javaJNI.TaggedForms_size(swigCPtr, this);
  }

  public long capacity() {
    return morphodita_javaJNI.TaggedForms_capacity(swigCPtr, this);
  }

  public void reserve(long n) {
    morphodita_javaJNI.TaggedForms_reserve(swigCPtr, this, n);
  }

  public boolean isEmpty() {
    return morphodita_javaJNI.TaggedForms_isEmpty(swigCPtr, this);
  }

  public void clear() {
    morphodita_javaJNI.TaggedForms_clear(swigCPtr, this);
  }

  public void add(TaggedForm x) {
    morphodita_javaJNI.TaggedForms_add(swigCPtr, this, TaggedForm.getCPtr(x), x);
  }

  public TaggedForm get(int i) {
    return new TaggedForm(morphodita_javaJNI.TaggedForms_get(swigCPtr, this, i), false);
  }

  public void set(int i, TaggedForm val) {
    morphodita_javaJNI.TaggedForms_set(swigCPtr, this, i, TaggedForm.getCPtr(val), val);
  }

}
