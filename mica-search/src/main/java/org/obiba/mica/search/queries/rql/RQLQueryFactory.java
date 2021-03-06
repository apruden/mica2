/*
 * Copyright (c) 2016 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.mica.search.queries.rql;

import javax.inject.Inject;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class RQLQueryFactory {

  @Inject
  private ApplicationContext applicationContext;

  public JoinRQLQueryWrapper makeJoinQuery(String query) {
    JoinRQLQueryWrapper joinQuery = applicationContext.getBean(JoinRQLQueryWrapper.class);
    joinQuery.initialize(query);
    return joinQuery;
  }

}
