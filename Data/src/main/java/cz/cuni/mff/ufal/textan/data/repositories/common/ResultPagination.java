package cz.cuni.mff.ufal.textan.data.repositories.common;

import java.util.List;

/**
 * @param <E> result type
 * @author Petr Fanta
 */
public class ResultPagination<E> {

    private final int firstResult;
    private final int maxResults;
    private final List<E> results;
    private final int totalNumberOfResults;

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
