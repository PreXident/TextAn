package cz.cuni.mff.ufal.textan.data.repositories.common;

import java.util.List;

/**
 * @author Petr Fanta
 */
public class ResultPagination<E> {

    private int firstResult;
    private int maxResults;
    private List<E> results;
    private int totalNumberOfResults;

    public ResultPagination(int firstResult, int maxResults, List<E> results, int totalNumberOfResults) {
        this.firstResult = firstResult;
        this.maxResults = maxResults;
        this.results = results;
        this.totalNumberOfResults = totalNumberOfResults;
    }

    public int getFirstResult() {
        return firstResult;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public List<E> getResults() {
        return results;
    }

    public int getTotalNumberOfResults() {
        return totalNumberOfResults;
    }
}
