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

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.json.JSONException;
import org.obiba.mica.search.JoinQueryExecutor;

import com.codahale.metrics.annotation.Timed;

import static org.obiba.mica.web.model.MicaSearch.JoinQueryDto;
import static org.obiba.mica.web.model.MicaSearch.JoinQueryResultDto;

@Path("/networks/_search")
@RequiresAuthentication
public class PublishedNetworksSearchResource {

  @Inject
  JoinQueryExecutor joinQueryExecutor;

  @GET
  @Timed
  public JoinQueryResultDto list(@QueryParam("from") @DefaultValue("0") int from,
      @QueryParam("size") @DefaultValue("10") int size)
      throws JSONException, IOException {
    return joinQueryExecutor.query(from, size);
  }

  @POST
  @Timed
  public JoinQueryResultDto list(JoinQueryDto joinQueryDto) throws IOException {
    return joinQueryExecutor.query(JoinQueryExecutor.QueryType.NETWORK, joinQueryDto);
  }
}