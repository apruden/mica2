/*
 * Copyright (c) 2016 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.mica.core.repository;

import java.util.List;

import org.obiba.mica.core.domain.EntityState;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface EntityStateRepository<T extends EntityState> extends MongoRepository<T, String> {
  List<T> findByPublishedTagNotNull();
}
