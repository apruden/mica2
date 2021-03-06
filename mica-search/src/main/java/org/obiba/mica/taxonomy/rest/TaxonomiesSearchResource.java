/*
 * Copyright (c) 2016 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.mica.taxonomy.rest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.obiba.mica.micaConfig.service.TaxonomyService;
import org.obiba.mica.taxonomy.TaxonomyResolver;
import org.obiba.mica.core.domain.TaxonomyTarget;
import org.obiba.opal.web.model.Opal;
import org.obiba.opal.web.taxonomy.Dtos;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

@Component
@Scope("request")
@Path("/taxonomies")
@RequiresAuthentication
public class TaxonomiesSearchResource extends AbstractTaxonomySearchResource {

  @Inject
  private TaxonomyService taxonomyService;

  @GET
  @Path("/_filter")
  @Timed
  public List<Opal.TaxonomyDto> filter(@QueryParam("target") @DefaultValue("variable") String target,
    @QueryParam("query") String query, @QueryParam("locale") String locale) {
    TaxonomyTarget taxonomyTarget = getTaxonomyTarget(target);

    if(Strings.isNullOrEmpty(query))
      return getTaxonomies(taxonomyTarget).stream().map(Dtos::asDto).collect(Collectors.toList());

    Map<String, Map<String, List<String>>> taxoNamesMap = TaxonomyResolver
      .asMap(filterVocabularies(taxonomyTarget, query, locale), filterTerms(taxonomyTarget, query, locale));
    List<Opal.TaxonomyDto> results = Lists.newArrayList();
    getTaxonomies(taxonomyTarget).stream().filter(t -> taxoNamesMap.containsKey(t.getName())).forEach(taxo -> {
      Opal.TaxonomyDto.Builder tBuilder = Dtos.asDto(taxo, false).toBuilder();
      populate(tBuilder, taxo, taxoNamesMap);
      results.add(tBuilder.build());
    });

    return results;
  }

  @GET
  @Path("/_search")
  @Timed
  public List<Opal.TaxonomyBundleDto> search(@QueryParam("query") String query, @QueryParam("locale") String locale,
    @Nullable @QueryParam("target") String target) {
    List<Opal.TaxonomyBundleDto> results = Lists.newArrayList();

    List<TaxonomyTarget> targets = target == null
      ? taxonomyService.getTaxonomyTaxonomy().getVocabularies().stream()
      .map(t -> TaxonomyTarget.valueOf(t.getName().toUpperCase())).collect(Collectors.toList())
      : Lists.newArrayList(TaxonomyTarget.valueOf(target.toUpperCase()));

    targets.forEach(t -> filter(t.name(), query, locale).stream()
      .map(taxo -> Opal.TaxonomyBundleDto.newBuilder().setTarget(t.name().toLowerCase()).setTaxonomy(taxo).build())
      .forEach(results::add));
    return results;
  }

}
