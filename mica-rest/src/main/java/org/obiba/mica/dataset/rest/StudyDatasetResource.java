/*
 * Copyright (c) 2014 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.mica.dataset.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.obiba.mica.service.StudyDatasetService;
import org.obiba.opal.web.magma.Dtos;
import org.obiba.opal.web.model.Magma;
import org.obiba.opal.web.model.Math;
import org.obiba.opal.web.model.Search;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;

@Component
@Scope("request")
@Path("/study-dataset/{id}")
@RequiresAuthentication
public class StudyDatasetResource {

  @PathParam("id")
  private String id;

  @Inject
  private StudyDatasetService datasetService;

  @GET
  @Path("/variables")
  public List<Magma.VariableDto> getVariables() {
    ImmutableList.Builder<Magma.VariableDto> builder = ImmutableList.builder();
    datasetService.getVariables(getTableName()).forEach(variable -> builder.add(Dtos.asDto(variable).build()));
    return builder.build();
  }

  @GET
  @Path("/variable/{variable}")
  public Magma.VariableDto getVariable(@PathParam("variable") String variable) {
    return datasetService.getVariable(getTableName(), variable);
  }

  @GET
  @Path("/variable/{variable}/summary")
  public Math.SummaryStatisticsDto getVariableSummary(@PathParam("variable") String variable) {
    return datasetService.getVariableSummary(getTableName(), variable);
  }

  @GET
  @Path("/variable/{variable}/facet")
  public Search.QueryResultDto getVariableFacet(@PathParam("variable") String variable) {
    return datasetService.getVariableFacet(getTableName(), variable);
  }

  @POST
  @Path("/facets")
  public Search.QueryResultDto getFacets(Search.QueryTermsDto query) {
    return datasetService.getFacets(getTableName(), query);
  }


  private String getTableName() {
    return datasetService.findById(id).getStudyTable().getTable();
  }
}