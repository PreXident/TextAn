package cz.cuni.mff.ufal.textan.core.processreport;

import cz.cuni.mff.ufal.textan.core.Document;

/**
 * {@link ProcessReportPipeline}'s {@link State} for selecting document from db
 * as source.
 */
public class SelectDocumentState extends State {

    /** Holds the singleton's intance. */
    static private volatile SelectDocumentState instance = null;

    /**
     * Returns singleton's instance.
     * @return singleton's instance
     */
    static SelectDocumentState getInstance() {
        if (instance == null) { //double checking
            synchronized (SelectDocumentState.class) {
                if (instance == null) {
                    instance = new SelectDocumentState();
                }
            }
        }
        return instance;
    }

    /**
     * Only constructor.
     */
    private SelectDocumentState() { }

    @Override
    public void setReport(final ProcessReportPipeline pipeline,
            final Document document) throws DocumentChangedException {
        pipeline.reportId = document.getId();
        ReportEditState.getInstance().setReportText(pipeline, document.getText());
    }

    @Override
    public State.StateType getType() {
        return State.StateType.SELECT_DOCUMENT;
    }
}
