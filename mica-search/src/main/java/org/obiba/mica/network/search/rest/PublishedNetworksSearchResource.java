/*
 * Copyright (c) 2014 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.mica.network.search.rest;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.obiba.mica.network.search.NetworkIndexer;
import org.obiba.mica.search.JoinQueryExecutor;
import org.obiba.mica.search.queries.NetworkQuery;
import org.obiba.mica.search.queries.protobuf.JoinQueryDtoWrapper;
import org.obiba.mica.search.queries.protobuf.QueryDtoHelper;
import org.obiba.mica.search.queries.rql.JoinRQLQueryWrapper;
import org.obiba.mica.web.model.MicaSearch;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Strings;

import static org.obiba.mica.web.model.MicaSearch.JoinQueryDto;
import static org.obiba.mica.web.model.MicaSearch.JoinQueryResultDto;

@Path("/networks")
@RequiresAuthentication
@Scope("request")
@Component
public class PublishedNetworksSearchResource {

  public static final String DEFAULT_SORT = "script";


  @Inject
  JoinQueryExecutor joinQueryExecutor;

  @GET
  @Path("/_search")
  @Timed
  public JoinQueryResultDto list(@QueryParam("from") @DefaultValue("0") int from,
      @QueryParam("limit") @DefaultValue("10") int limit, @QueryParam("sort") @DefaultValue(DEFAULT_SORT)  String sort,
      @QueryParam("order") @DefaultValue("desc") String order, @QueryParam("study") String studyId, @QueryParam("query") String query,
      @QueryParam("locale") @DefaultValue("en") String locale)
      throws IOException {
    String sortScript = "doc['studyIds'].values.size()"; //groovy sort script
    String type = "number";

    MicaSearch.QueryDto queryDto;

    if (!Strings.isNullOrEmpty(sort) && !sort.equals(DEFAULT_SORT)) {
      queryDto = QueryDtoHelper
        .createQueryDto(from, limit, sort, order, query, locale, Stream.of(NetworkIndexer.LOCALIZED_ANALYZED_FIELDS));
    } else {
      queryDto = QueryDtoHelper
        .createQueryDto(from, limit, sortScript, type, order, query, locale, Stream.of(NetworkIndexer.LOCALIZED_ANALYZED_FIELDS));
    }

    if (!Strings.isNullOrEmpty(studyId)) {
      queryDto = QueryDtoHelper.addTermFilters(queryDto,
          Arrays.asList(QueryDtoHelper.createTermFilter(NetworkQuery.JOIN_FIELD, Arrays.asList(studyId))),
          QueryDtoHelper.BoolQueryType.SHOULD);
    }

    JoinQueryResultDto result = joinQueryExecutor.listQuery(JoinQueryExecutor.QueryType.NETWORK, queryDto, locale);
    JoinQueryResultDto.Builder builder = result.toBuilder().clearDatasetResultDto().clearStudyResultDto().clearVariableResultDto();
    builder.setNetworkResultDto(builder.getNetworkResultDto().toBuilder().clearAggs());
    return builder.build();
  }

  @POST
  @Path("/_search")
  @Timed
  public JoinQueryResultDto query(JoinQueryDto joinQueryDto) throws IOException {
    return joinQueryExecutor.query(JoinQueryExecutor.QueryType.NETWORK, new JoinQueryDtoWrapper(joinQueryDto));
  }

  @GET
  @Path("/_rql")
  @Timed
  public JoinQueryResultDto rqlQuery(@QueryParam("query") String query) throws IOException {
    return joinQueryExecutor.query(JoinQueryExecutor.QueryType.NETWORK, new JoinRQLQueryWrapper(query));
  }
}
