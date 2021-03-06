/*
 * Copyright (c) 2016 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.mica.config.taxonomies;

import org.obiba.opal.core.domain.taxonomy.Taxonomy;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.google.common.collect.Lists;

@ConfigurationProperties(locations = "classpath:/taxonomies/mica-dataset.yml")
public class DatasetTaxonomy extends Taxonomy {

  private static final long serialVersionUID = -8426375064515872649L;

  public DatasetTaxonomy() {
    setVocabularies(Lists.newArrayList()); //explicit initialization for yaml bean factory
  }

}
