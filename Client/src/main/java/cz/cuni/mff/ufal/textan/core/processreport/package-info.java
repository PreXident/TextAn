/**
 * This package contains classes necessary for report processing.
 * Most importantly there is class ProcessReportPipeline that holds information
 * about processing one report. Virtually all methods of the pipeline object are
 * delegated to its State. Descendants of the class State are singletons.
 * They contain most of the pipeline processing logic and control to which state
 * the pipeline should go next. When this happens, each registered
 * IStateChangedListener is notified.
 */
package cz.cuni.mff.ufal.textan.core.processreport;