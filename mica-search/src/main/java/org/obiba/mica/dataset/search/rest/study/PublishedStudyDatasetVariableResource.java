/*
 * Copyright (c) 2014 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.mica.dataset.search.rest.study;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.obiba.mica.dataset.domain.DatasetVariable;
import org.obiba.mica.dataset.domain.StudyDataset;
import org.obiba.mica.dataset.search.rest.AbstractPublishedDatasetVariableResource;
import org.obiba.mica.service.StudyDatasetService;
import org.obiba.mica.web.model.Dtos;
import org.obiba.mica.web.model.Mica;
import org.obiba.opal.web.model.Search;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("request")
@RequiresAuthentication
public class PublishedStudyDatasetVariableResource extends AbstractPublishedDatasetVariableResource<StudyDataset> {

  @PathParam("id")
  private String datasetId;

  @PathParam("name")
  private String variableName;

  @Inject
  private StudyDatasetService datasetService;

  @Inject
  private Dtos dtos;

  @GET
  public Mica.DatasetVariableDto getVariable() {
    return getDatasetVariableDto(StudyDataset.class, datasetId, variableName);
  }

  @GET
  @Path("/summary")
  public org.obiba.opal.web.model.Math.SummaryStatisticsDto getVariableSummary() {
    return datasetService.getVariableSummary(getDataset(StudyDataset.class, datasetId), variableName);
  }

  @GET
  @Path("/facet")
  public Search.QueryResultDto getVariableFacet() {
    return datasetService.getVariableFacet(getDataset(StudyDataset.class, datasetId), variableName);
  }

  @Override
  protected DatasetVariable.Type getDatasetVariableType(String studyId) {
    return DatasetVariable.Type.Study;
  }

  @Override
  public void setDatasetId(String datasetId) {
    this.datasetId = datasetId;
  }

  @Override
  public void setVariableName(String variableName) {
    this.variableName = variableName;
  }
}