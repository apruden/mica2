/*
 * Copyright (c) 2014 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.mica.search;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.obiba.mica.core.service.PublishedDocumentService;

public abstract class AbstractPublishedDocumentService<T> implements PublishedDocumentService<T> {

  private static final int MAX_SIZE = 99999;

  @Inject
  private Client client;

  public T findById(String id) {
    List<T> results = findByIds(Arrays.asList(id));
    return (results != null && results.size() > 0) ? results.get(0) : null;
  }

  public List<T> findAll() {
    return executeQuery(QueryBuilders.matchAllQuery(), 0, MAX_SIZE);
  }

  public List<T> findByIds(List<String> ids) {
    return executeQuery(buildFilteredQuery(ids), 0, MAX_SIZE);
  }

  protected abstract List<T> processHits(SearchHits hits);
  protected abstract String getIndexName();
  protected abstract String getType();

  private List<T> executeQuery(QueryBuilder queryBuilder, int from, int size) {
    SearchRequestBuilder requestBuilder = client.prepareSearch(getIndexName()) //
        .setTypes(getType()) //
        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH) //
        .setQuery(queryBuilder) //
        .setFrom(from) //
        .setSize(size);


    SearchResponse response = requestBuilder.execute().actionGet();
    return processHits(response.getHits());
  }

  private QueryBuilder buildFilteredQuery(List<String> ids) {
    IdsQueryBuilder builder = QueryBuilders.idsQuery(getType());
    ids.forEach(builder::addIds);
    return builder;
  }

}