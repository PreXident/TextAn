/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package cz.cuni.mff.ufal.morphodita;

public class TaggedForm {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected TaggedForm(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(TaggedForm obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        morphodita_javaJNI.delete_TaggedForm(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setForm(String value) {
    morphodita_javaJNI.TaggedForm_form_set(swigCPtr, this, value);
  }

  public String getForm() {
    return morphodita_javaJNI.TaggedForm_form_get(swigCPtr, this);
  }

  public void setTag(String value) {
    morphodita_javaJNI.TaggedForm_tag_set(swigCPtr, this, value);
  }

  public String getTag() {
    return morphodita_javaJNI.TaggedForm_tag_get(swigCPtr, this);
  }

  public TaggedForm() {
    this(morphodita_javaJNI.new_TaggedForm(), true);
  }

}
