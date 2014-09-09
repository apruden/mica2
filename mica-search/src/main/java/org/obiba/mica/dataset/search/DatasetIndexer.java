/*
 * Copyright (c) 2014 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.mica.dataset.search;

import javax.inject.Inject;

import org.obiba.mica.dataset.domain.Dataset;
import org.obiba.mica.domain.Indexable;
import org.obiba.mica.search.ElasticSearchIndexer;
import org.obiba.mica.study.service.StudyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DatasetIndexer<T extends Dataset> {

  private static final Logger log = LoggerFactory.getLogger(DatasetIndexer.class);

  public static final String DRAFT_DATASET_INDEX = "dataset-draft";

  public static final String PUBLISHED_DATASET_INDEX = "dataset-published";

  public static final String DATASET_TYPE = Dataset.MAPPING_NAME;

  @Inject
  protected StudyService studyService;

  @Inject
  protected ElasticSearchIndexer elasticSearchIndexer;

  protected void reIndexDraft(T dataset) {
    deleteFromDatasetIndex(DRAFT_DATASET_INDEX, dataset);
    elasticSearchIndexer.index(DRAFT_DATASET_INDEX, (Indexable) dataset);
  }

  protected void reIndexPublished(T dataset) {
    deleteFromDatasetIndex(PUBLISHED_DATASET_INDEX, dataset);
    if(dataset.isPublished()) {
      elasticSearchIndexer.index(PUBLISHED_DATASET_INDEX, (Indexable) dataset);
    }
  }

  protected void reIndexDraft(T dataset, String studyId) {

  }

  protected void reIndexPublished(T dataset, String studyId) {

  }

  protected void deleteFromDatasetIndices(T dataset) {
    deleteFromDatasetIndex(DRAFT_DATASET_INDEX, dataset);
    deleteFromDatasetIndex(PUBLISHED_DATASET_INDEX, dataset);
  }

  private void deleteFromDatasetIndex(String indexName, T dataset) {
    elasticSearchIndexer.delete(indexName, (Indexable) dataset);
  }

  protected void reIndexAll() {
    reIndexAll(DRAFT_DATASET_INDEX, findAllDatasets());
    reIndexAll(PUBLISHED_DATASET_INDEX, findAllPublishedDatasets());
  }

  private void reIndexAll(String indexName, Iterable<T> datasets) {
    //if(elasticSearchIndexer.hasIndex(indexName)) elasticSearchIndexer.dropIndex(indexName);
    // TODO delete dataset by their className and delete their children as-well
    elasticSearchIndexer.indexAllIndexables(indexName, datasets);
  }

  protected abstract Iterable<T> findAllDatasets();

  protected abstract Iterable<T> findAllPublishedDatasets();
}
