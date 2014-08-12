/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package cz.cuni.mff.ufal.morphodita;

import cz.cuni.mff.ufal.utils.SystemInfo;

import java.io.File;

public class morphodita_javaJNI {
  public final static native long new_Forms__SWIG_0();
  public final static native long new_Forms__SWIG_1(long jarg1);
  public final static native long Forms_size(long jarg1, Forms jarg1_);
  public final static native long Forms_capacity(long jarg1, Forms jarg1_);
  public final static native void Forms_reserve(long jarg1, Forms jarg1_, long jarg2);
  public final static native boolean Forms_isEmpty(long jarg1, Forms jarg1_);
  public final static native void Forms_clear(long jarg1, Forms jarg1_);
  public final static native void Forms_add(long jarg1, Forms jarg1_, String jarg2);
  public final static native String Forms_get(long jarg1, Forms jarg1_, int jarg2);
  public final static native void Forms_set(long jarg1, Forms jarg1_, int jarg2, String jarg3);
  public final static native void delete_Forms(long jarg1);
  public final static native void TaggedForm_form_set(long jarg1, TaggedForm jarg1_, String jarg2);
  public final static native String TaggedForm_form_get(long jarg1, TaggedForm jarg1_);
  public final static native void TaggedForm_tag_set(long jarg1, TaggedForm jarg1_, String jarg2);
  public final static native String TaggedForm_tag_get(long jarg1, TaggedForm jarg1_);
  public final static native long new_TaggedForm();
  public final static native void delete_TaggedForm(long jarg1);
  public final static native long new_TaggedForms__SWIG_0();
  public final static native long new_TaggedForms__SWIG_1(long jarg1);
  public final static native long TaggedForms_size(long jarg1, TaggedForms jarg1_);
  public final static native long TaggedForms_capacity(long jarg1, TaggedForms jarg1_);
  public final static native void TaggedForms_reserve(long jarg1, TaggedForms jarg1_, long jarg2);
  public final static native boolean TaggedForms_isEmpty(long jarg1, TaggedForms jarg1_);
  public final static native void TaggedForms_clear(long jarg1, TaggedForms jarg1_);
  public final static native void TaggedForms_add(long jarg1, TaggedForms jarg1_, long jarg2, TaggedForm jarg2_);
  public final static native long TaggedForms_get(long jarg1, TaggedForms jarg1_, int jarg2);
  public final static native void TaggedForms_set(long jarg1, TaggedForms jarg1_, int jarg2, long jarg3, TaggedForm jarg3_);
  public final static native void delete_TaggedForms(long jarg1);
  public final static native void TaggedLemma_lemma_set(long jarg1, TaggedLemma jarg1_, String jarg2);
  public final static native String TaggedLemma_lemma_get(long jarg1, TaggedLemma jarg1_);
  public final static native void TaggedLemma_tag_set(long jarg1, TaggedLemma jarg1_, String jarg2);
  public final static native String TaggedLemma_tag_get(long jarg1, TaggedLemma jarg1_);
  public final static native long new_TaggedLemma();
  public final static native void delete_TaggedLemma(long jarg1);
  public final static native long new_TaggedLemmas__SWIG_0();
  public final static native long new_TaggedLemmas__SWIG_1(long jarg1);
  public final static native long TaggedLemmas_size(long jarg1, TaggedLemmas jarg1_);
  public final static native long TaggedLemmas_capacity(long jarg1, TaggedLemmas jarg1_);
  public final static native void TaggedLemmas_reserve(long jarg1, TaggedLemmas jarg1_, long jarg2);
  public final static native boolean TaggedLemmas_isEmpty(long jarg1, TaggedLemmas jarg1_);
  public final static native void TaggedLemmas_clear(long jarg1, TaggedLemmas jarg1_);
  public final static native void TaggedLemmas_add(long jarg1, TaggedLemmas jarg1_, long jarg2, TaggedLemma jarg2_);
  public final static native long TaggedLemmas_get(long jarg1, TaggedLemmas jarg1_, int jarg2);
  public final static native void TaggedLemmas_set(long jarg1, TaggedLemmas jarg1_, int jarg2, long jarg3, TaggedLemma jarg3_);
  public final static native void delete_TaggedLemmas(long jarg1);
  public final static native void TaggedLemmaForms_lemma_set(long jarg1, TaggedLemmaForms jarg1_, String jarg2);
  public final static native String TaggedLemmaForms_lemma_get(long jarg1, TaggedLemmaForms jarg1_);
  public final static native void TaggedLemmaForms_forms_set(long jarg1, TaggedLemmaForms jarg1_, long jarg2, TaggedForms jarg2_);
  public final static native long TaggedLemmaForms_forms_get(long jarg1, TaggedLemmaForms jarg1_);
  public final static native long new_TaggedLemmaForms();
  public final static native void delete_TaggedLemmaForms(long jarg1);
  public final static native long new_TaggedLemmasForms__SWIG_0();
  public final static native long new_TaggedLemmasForms__SWIG_1(long jarg1);
  public final static native long TaggedLemmasForms_size(long jarg1, TaggedLemmasForms jarg1_);
  public final static native long TaggedLemmasForms_capacity(long jarg1, TaggedLemmasForms jarg1_);
  public final static native void TaggedLemmasForms_reserve(long jarg1, TaggedLemmasForms jarg1_, long jarg2);
  public final static native boolean TaggedLemmasForms_isEmpty(long jarg1, TaggedLemmasForms jarg1_);
  public final static native void TaggedLemmasForms_clear(long jarg1, TaggedLemmasForms jarg1_);
  public final static native void TaggedLemmasForms_add(long jarg1, TaggedLemmasForms jarg1_, long jarg2, TaggedLemmaForms jarg2_);
  public final static native long TaggedLemmasForms_get(long jarg1, TaggedLemmasForms jarg1_, int jarg2);
  public final static native void TaggedLemmasForms_set(long jarg1, TaggedLemmasForms jarg1_, int jarg2, long jarg3, TaggedLemmaForms jarg3_);
  public final static native void delete_TaggedLemmasForms(long jarg1);
  public final static native void TokenRange_start_set(long jarg1, TokenRange jarg1_, long jarg2);
  public final static native long TokenRange_start_get(long jarg1, TokenRange jarg1_);
  public final static native void TokenRange_length_set(long jarg1, TokenRange jarg1_, long jarg2);
  public final static native long TokenRange_length_get(long jarg1, TokenRange jarg1_);
  public final static native long new_TokenRange();
  public final static native void delete_TokenRange(long jarg1);
  public final static native long new_TokenRanges__SWIG_0();
  public final static native long new_TokenRanges__SWIG_1(long jarg1);
  public final static native long TokenRanges_size(long jarg1, TokenRanges jarg1_);
  public final static native long TokenRanges_capacity(long jarg1, TokenRanges jarg1_);
  public final static native void TokenRanges_reserve(long jarg1, TokenRanges jarg1_, long jarg2);
  public final static native boolean TokenRanges_isEmpty(long jarg1, TokenRanges jarg1_);
  public final static native void TokenRanges_clear(long jarg1, TokenRanges jarg1_);
  public final static native void TokenRanges_add(long jarg1, TokenRanges jarg1_, long jarg2, TokenRange jarg2_);
  public final static native long TokenRanges_get(long jarg1, TokenRanges jarg1_, int jarg2);
  public final static native void TokenRanges_set(long jarg1, TokenRanges jarg1_, int jarg2, long jarg3, TokenRange jarg3_);
  public final static native void delete_TokenRanges(long jarg1);
  public final static native void Version_major_set(long jarg1, Version jarg1_, long jarg2);
  public final static native long Version_major_get(long jarg1, Version jarg1_);
  public final static native void Version_minor_set(long jarg1, Version jarg1_, long jarg2);
  public final static native long Version_minor_get(long jarg1, Version jarg1_);
  public final static native void Version_patch_set(long jarg1, Version jarg1_, long jarg2);
  public final static native long Version_patch_get(long jarg1, Version jarg1_);
  public final static native long Version_current();
  public final static native long new_Version();
  public final static native void delete_Version(long jarg1);
  public final static native void delete_Tokenizer(long jarg1);
  public final static native void Tokenizer_setText(long jarg1, Tokenizer jarg1_, String jarg2);
  public final static native boolean Tokenizer_nextSentence(long jarg1, Tokenizer jarg1_, long jarg2, Forms jarg2_, long jarg3, TokenRanges jarg3_);
  public final static native long Tokenizer_newVerticalTokenizer();
  public final static native long Tokenizer_newCzechTokenizer();
  public final static native long Tokenizer_newEnglishTokenizer();
  public final static native long Tokenizer_newGenericTokenizer();
  public final static native void delete_Morpho(long jarg1);
  public final static native long Morpho_load(String jarg1);
  public final static native int Morpho_NO_GUESSER_get();
  public final static native int Morpho_GUESSER_get();
  public final static native int Morpho_analyze(long jarg1, Morpho jarg1_, String jarg2, int jarg3, long jarg4, TaggedLemmas jarg4_);
  public final static native int Morpho_generate(long jarg1, Morpho jarg1_, String jarg2, String jarg3, int jarg4, long jarg5, TaggedLemmasForms jarg5_);
  public final static native String Morpho_rawLemma(long jarg1, Morpho jarg1_, String jarg2);
  public final static native String Morpho_lemmaId(long jarg1, Morpho jarg1_, String jarg2);
  public final static native String Morpho_rawForm(long jarg1, Morpho jarg1_, String jarg2);
  public final static native long Morpho_newTokenizer(long jarg1, Morpho jarg1_);
  public final static native void delete_Tagger(long jarg1);
  public final static native long Tagger_load(String jarg1);
  public final static native long Tagger_getMorpho(long jarg1, Tagger jarg1_);
  public final static native void Tagger_tag(long jarg1, Tagger jarg1_, long jarg2, Forms jarg2_, long jarg3, TaggedLemmas jarg3_);
  public final static native long Tagger_newTokenizer(long jarg1, Tagger jarg1_);
  public final static native void delete_TagsetConverter(long jarg1);
  public final static native void TagsetConverter_convert(long jarg1, TagsetConverter jarg1_, long jarg2, TaggedLemma jarg2_);
  public final static native void TagsetConverter_convertAnalyzed(long jarg1, TagsetConverter jarg1_, long jarg2, TaggedLemmas jarg2_);
  public final static native void TagsetConverter_convertGenerated(long jarg1, TagsetConverter jarg1_, long jarg2, TaggedLemmasForms jarg2_);
  public final static native long TagsetConverter_newIdentityConverter();
  public final static native long TagsetConverter_newPdtToConll2009Converter();
  public final static native long TagsetConverter_newStripLemmaCommentConverter(long jarg1, Morpho jarg1_);
  public final static native long TagsetConverter_newStripLemmaIdConverter(long jarg1, Morpho jarg1_);

    static {
        final String libName = "morphodita_java";
        final String libDir = "lib";

        String dir;
        if (SystemInfo.getJVMArch() == SystemInfo.JVMArch.x64) {
            dir = libDir + "/" + "x64";
        } else if (SystemInfo.getJVMArch() == SystemInfo.JVMArch.x86) {
            dir = libDir + "/" + "x86";
        } else {
            dir = libDir;
        }

        File localMorphodita = new File(dir, System.mapLibraryName(libName));

        if (localMorphodita.exists()) {
            System.load(localMorphodita.getAbsolutePath());
        } else {
            File standardLocalMorphodita = new File(libDir, System.mapLibraryName(libName));
            if (standardLocalMorphodita.exists()) {
                System.load(standardLocalMorphodita.getAbsolutePath());
            } else {
                System.loadLibrary(libName);
            }
        }
    }

}
