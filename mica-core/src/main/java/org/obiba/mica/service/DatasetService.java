/*
 * Copyright (c) 2014 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.mica.service;

import java.util.List;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.obiba.magma.DatasourceFactory;
import org.obiba.magma.Variable;
import org.obiba.magma.support.Initialisables;
import org.obiba.mica.domain.HarmonizedDataset;
import org.obiba.mica.domain.StudyDataset;
import org.obiba.mica.repository.HarmonizedDatasetRepository;
import org.obiba.mica.repository.StudyDatasetRepository;
import org.obiba.opal.rest.client.magma.RestDatasource;
import org.obiba.opal.rest.client.magma.RestDatasourceFactory;
import org.obiba.opal.rest.client.magma.RestValueTable;
import org.obiba.opal.web.model.Magma;
import org.obiba.opal.web.model.Math;
import org.obiba.opal.web.model.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * {@link org.obiba.mica.domain.Dataset} management service.
 */
@Service
@Validated
public class DatasetService implements EnvironmentAware {

  private static final Logger log = LoggerFactory.getLogger(DatasetService.class);

  @Inject
  private StudyDatasetRepository studyDatasetRepository;

  @Inject
  private HarmonizedDatasetRepository harmonizedDatasetRepository;

  private RelaxedPropertyResolver opalPropertyResolver;

  private RestDatasource datasource;

  @Override
  public void setEnvironment(Environment environment) {
    opalPropertyResolver = new RelaxedPropertyResolver(environment, "opal.");
  }

  public void save(@NotNull StudyDataset dataset) {
    studyDatasetRepository.save(dataset);
  }

  public void save(@NotNull HarmonizedDataset dataset) {
    harmonizedDatasetRepository.save(dataset);
  }

  @NotNull
  public StudyDataset findStudyDatasetById(@NotNull String id) throws NoSuchDatasetException {
    StudyDataset dataset = studyDatasetRepository.findOne(id);
    if(dataset == null) throw NoSuchDatasetException.withId(id);
    return dataset;
  }

  @NotNull
  public HarmonizedDataset findHarmonizedDatasetById(@NotNull String id) throws NoSuchDatasetException {
    HarmonizedDataset dataset = harmonizedDatasetRepository.findOne(id);
    if(dataset == null) throw NoSuchDatasetException.withId(id);
    return dataset;
  }

  public List<StudyDataset> getStudyDatasets() {
    return studyDatasetRepository.findAll();
  }

  public List<HarmonizedDataset> getHarmonizedDatasets() {
    return harmonizedDatasetRepository.findAll();
  }

  public Iterable<Variable> getVariables(String tableName) {
    return execute(datasource -> datasource.getValueTable(tableName).getVariables());
  }

  public RestValueTable.RestVariableValueSource getVariableValueSource(String tableName, String variableName) {
    return execute(datasource -> (RestValueTable.RestVariableValueSource) datasource.getValueTable(tableName)
        .getVariableValueSource(variableName));
  }

  public Magma.VariableDto getVariable(String tableName, String variableName) {
    return getVariableValueSource(tableName, variableName).getVariableDto();
  }

  public Math.SummaryStatisticsDto getVariableSummary(String tableName, String variableName) {
    return getVariableValueSource(tableName, variableName).getSummary();
  }

  public Search.QueryResultDto getVariableFacet(String tableName, String variableName) {
    return getVariableValueSource(tableName, variableName).getFacet();
  }

  public Search.QueryResultDto getFacets(String tableName, Search.QueryTermsDto query) {
    return execute(datasource -> (RestValueTable) datasource.getValueTable(tableName)).getFacets(query);
  }

  /**
   * Build the default {@link org.obiba.opal.rest.client.magma.RestDatasource} and execute the callback with it.
   *
   * @param callback
   * @param <T>
   * @return
   */
  public <T> T execute(DatasourceCallback<T> callback) {
    return callback.doWithDatasource(getDatasource());
  }

  /**
   * Callback that can be used to make any operations on a {@link org.obiba.opal.rest.client.magma.RestDatasource}
   *
   * @param <T>
   */
  public interface DatasourceCallback<T> {
    T doWithDatasource(RestDatasource datasource);
  }

  //
  // Private methods
  //

  /**
   * Get a {@link org.obiba.opal.rest.client.magma.RestDatasource} on the default Opal project.
   *
   * @return
   */
  private RestDatasource getDatasource() {
    if(datasource == null) {
      String name = getRemoteDatasource();
      DatasourceFactory factory = new RestDatasourceFactory(name, getOpalUrl(), getOpalUsername(), getOpalPassword(),
          name);
      datasource = (RestDatasource) factory.create();
      Initialisables.initialise(datasource);
    }
    return datasource;
  }

  private String getOpalUrl() {
    return opalPropertyResolver.getProperty("url");
  }

  private String getOpalUsername() {
    return opalPropertyResolver.getProperty("username");
  }

  private String getOpalPassword() {
    return opalPropertyResolver.getProperty("password");
  }

  private String getRemoteDatasource() {
    return opalPropertyResolver.getProperty("project");
  }

}
