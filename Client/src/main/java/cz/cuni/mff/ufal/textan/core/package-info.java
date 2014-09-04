/**
 * This package contains core classes needed to communicate with server.
 * It contains mainly client side representation of object, entity, relation,
 * document etc. The most important class is Client which is used for
 * communication with the webservices. It also serves as a factory of
 * ProcessReportPipeline. There are also SynchronizedDataProvider and
 * SynchronizedDocumentProcessor to provide synchronization for
 * IDataProvider and IDocumentProcessor created by CXF.
 */
package cz.cuni.mff.ufal.textan.core;