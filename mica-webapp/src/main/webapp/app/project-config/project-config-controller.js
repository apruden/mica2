/*
 * Copyright (c) 2016 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

'use strict';

mica.projectConfig

  .controller('ProjectConfigController', ['$rootScope', '$location', '$scope', '$log',
    'ProjectFormResource',
    'EntitySchemaFormService',
    'LocalizedSchemaFormService',
    'AlertService',
    'ServerErrorUtils',
    'ProjectFormPermissionsResource',
    'ProjectFormAccessesResource',
    'MicaConfigResource',
    function ($rootScope, $location, $scope, $log,
              ProjectFormResource,
              EntitySchemaFormService,
              LocalizedSchemaFormService,
              AlertService,
              ServerErrorUtils,
              ProjectFormPermissionsResource,
              ProjectFormAccessesResource,
              MicaConfigResource) {

      MicaConfigResource.get(function (micaConfig) {
        $scope.openAccess = micaConfig.openAccess;
      });

      var saveForm = function() {
        switch (EntitySchemaFormService.isFormValid($scope.form)) {
          case EntitySchemaFormService.ParseResult.VALID:
            $scope.projectForm.definition = $scope.form.definition;
            $scope.projectForm.schema = $scope.form.schema;
            ProjectFormResource.save($scope.projectForm,
              function () {
                $location.path('/admin').replace();
              },
              function (response) {
                AlertService.alert({
                  id: 'ProjectConfigController',
                  type: 'danger',
                  msg: ServerErrorUtils.buildMessage(response)
                });
              });
            break;
          case EntitySchemaFormService.ParseResult.SCHEMA:
          AlertService.alert({
            id: 'ProjectConfigController',
            type: 'danger',
            msgKey: 'project-config.syntax-error.schema'
          });
          break;
        case EntitySchemaFormService.ParseResult.DEFINITION:
          AlertService.alert({
            id: 'ProjectConfigController',
            type: 'danger',
            msgKey: 'project-config.syntax-error.definition'
          });
          break;
        }
      };

      $scope.projectForm = {schema: '', definition: ''};

      ProjectFormResource.get(
        function(projectForm){
          $scope.form.definitionJson = EntitySchemaFormService.parseJsonSafely(projectForm.definition, []);
          $scope.form.definition = EntitySchemaFormService.prettifyJson($scope.form.definitionJson);
          $scope.form.schemaJson = EntitySchemaFormService.parseJsonSafely(projectForm.schema, {});
          $scope.form.schema = EntitySchemaFormService.prettifyJson($scope.form.schemaJson);
          $scope.projectForm = projectForm;

          if ($scope.form.definitionJson.length === 0) {
            AlertService.alert({
              id: 'ProjectConfigController',
              type: 'danger',
              msgKey: 'project-config.parse-error.definition'
            });
          }
          if (Object.getOwnPropertyNames($scope.form.schemaJson).length === 0) {
            AlertService.alert({
              id: 'ProjectConfigController',
              type: 'danger',
              msgKey: 'project-config.parse-error.schema'
            });
          }
        },
        function(response) {
          AlertService.alert({
            id: 'ProjectConfigController',
            type: 'danger',
            msg: ServerErrorUtils.buildMessage(response)
          });
        });

      $scope.form = {
        definitionJson: null,
        definition: null,
        schemaJson: null,
        schema: null,
        model: {}
      };

      $scope.loadPermissions = function() {
        $scope.acls = ProjectFormPermissionsResource.get();
        return $scope.acls;
      };

      $scope.addPermission = function (acl) {
        return ProjectFormPermissionsResource.save(acl);
      };

      $scope.deletePermission = function (acl) {
        return ProjectFormPermissionsResource.delete(acl);
      };

      $scope.loadAccesses =function () {
        $scope.accesses = ProjectFormAccessesResource.get();
        return $scope.accesses;
      };

      $scope.addAccess = function (acl) {
        return ProjectFormAccessesResource.save(acl);
      };

      $scope.deleteAccess = function (acl) {
        return ProjectFormAccessesResource.delete(acl);
      };

      $scope.titleInfo = {
        'permissions': 'project-config.permissions.info',
        'accesses': 'project-config.accesses.info'
      };

      $scope.tab = {name: 'form'};
      $scope.saveForm = saveForm;
    }]);
