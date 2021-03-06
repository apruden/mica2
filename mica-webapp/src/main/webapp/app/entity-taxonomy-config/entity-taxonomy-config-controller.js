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

mica.entitySfConfig

  .controller('EntityTaxonomyConfigController', [
    '$scope',
    '$q',
    '$filter',
    'MicaConfigResource',
    'EntityTaxonomyConfigResource',
    'ServerErrorUtils',
    'AlertService',
    function ($scope,
              $q,
              $filter,
              MicaConfigResource,
              EntityTaxonomyConfigResource,
              ServerErrorUtils,
              AlertService) {

      var onError = function(response) {
        AlertService.alert({
          id: 'EntityTaxonomyConfigController',
          type: 'danger',
          msg: ServerErrorUtils.buildMessage(response)
        });
      };

      var navigateToVocabulary = function(content) {
        $scope.model.content = content;
        $scope.model.children = content.terms ? content.terms : [];
        $scope.model.type = 'criterion';
      };

      var navigateToTaxonomy = function() {
        $scope.model.content = $scope.taxonomy;
        $scope.model.children = $scope.taxonomy.vocabularies;
        $scope.model.type = 'taxonomy';
        console.log($scope.model);
      };

      var getTaxonomy = function() {
        EntityTaxonomyConfigResource.get({target: $scope.target}).$promise.then(
          function (response) {
            $scope.taxonomy = response;
            navigateToTaxonomy(response);
          },
          onError
        );
      };

      function swapVocabulary(from, to) {
        var tmp = $scope.taxonomy.vocabularies[from];
        $scope.taxonomy.vocabularies[from] = $scope.taxonomy.vocabularies[to];
        $scope.taxonomy.vocabularies[to] = tmp;
      }

      var moveVocabularyUp = function(vocabulary) {
        var from = $scope.taxonomy.vocabularies.indexOf(vocabulary);
        var to = from - 1;
        swapVocabulary(from, to < 0 ? $scope.taxonomy.vocabularies.length - 1 : to);
      };

      var moveVocabularyDown = function(vocabulary) {
        var from = $scope.taxonomy.vocabularies.indexOf(vocabulary);
        var to = from + 1;
        swapVocabulary(from, to > $scope.taxonomy.vocabularies.length ? 1 : to);
      };

      var onSave = function() {
        return EntityTaxonomyConfigResource.save({target: $scope.target}, $scope.taxonomy);
      };

      $scope.model = {
        content: null,
        children: null,
        type: 'taxonomy'
      };

      $scope.onSave = onSave;
      $scope.navigateToVocabulary = navigateToVocabulary;
      $scope.navigateToTaxonomy = navigateToTaxonomy;
      $scope.moveVocabularyUp = moveVocabularyUp;
      $scope.moveVocabularyDown = moveVocabularyDown;

      $scope.state.registerListener($scope);

      getTaxonomy();

    }])

  .controller('entityTaxonomyConfigPropertiesViewController', [
    '$rootScope',
    '$scope',
    '$uibModal',
    '$filter',
    'VocabularyAttributeService',
    'NOTIFICATION_EVENTS',

    function ($rootScope,
              $scope,
              $uibModal,
              $filter,
              VocabularyAttributeService,
              NOTIFICATION_EVENTS) {

      var edit = function() {

          $uibModal.open({
            templateUrl: 'app/entity-taxonomy-config/views/entity-taxonomy-config-properties-modal.html',
            controller: 'entityTaxonomyConfigPropertiesModalController',
            resolve: {
              model: function() {
                return $scope.model;
              }
            }
          });
      };

      function onDelete(event, criterion) {
        $scope.unregisterOnDelete();
        var i = $scope.taxonomy.vocabularies.indexOf(criterion);
        if (i > -1) {
          $scope.taxonomy.vocabularies.splice(i, 1);
          $scope.model.content = $scope.taxonomy;
          $scope.model.children = $scope.taxonomy.vocabularies;
          $scope.model.type = 'taxonomy';
        }
      }

      var deleteCriterion = function() {
        $scope.unregisterOnDelete = $scope.$on(NOTIFICATION_EVENTS.confirmDialogAccepted, onDelete);
        var criterionLabel = $filter('translate')('global.criterion');
        var criterion = $scope.model.content;
        $rootScope.$broadcast(NOTIFICATION_EVENTS.showConfirmDialog,
          {
            titleKey: 'taxonomy-config.delete-dialog.title',
            titleArgs: [criterionLabel],
            messageKey: 'taxonomy-config.delete-dialog.message',
            messageArgs: [criterionLabel.toLowerCase(), criterion.name]
          }, $scope.model.content
        );
      };

      $scope.getField = VocabularyAttributeService.getField;
      $scope.getTypeMap = VocabularyAttributeService.getTypeMap;
      $scope.getLocalized = VocabularyAttributeService.getLocalized;
      $scope.isStatic = VocabularyAttributeService.isStatic;
      $scope.deleteCriterion = deleteCriterion;
      $scope.edit = edit;

    }])

  .controller('entityTaxonomyConfigCriteriaViewController', [
    '$scope',
    '$uibModal',
    'EntityTaxonomyService',
    'VocabularyAttributeService',
    function ($scope,
              $uibModal,
              EntityTaxonomyService,
              VocabularyAttributeService) {

      var addCriteria = function() {

        var model = {
          content: null,
          children: null,
          type: 'criterion'
        };

        $uibModal.open({
          templateUrl: 'app/entity-taxonomy-config/views/entity-taxonomy-config-properties-modal.html',
          controller: 'entityTaxonomyConfigPropertiesModalController',
          resolve: {
            model: function() {
              return model;
            }
          }
        }).result.then(function(){
          $scope.taxonomy.vocabularies = $scope.taxonomy.vocabularies || [];
          $scope.taxonomy.vocabularies.push(model.content);
        });
      };

      $scope.add = addCriteria;
      $scope.getField = VocabularyAttributeService.getField;
      $scope.getTermsCount = EntityTaxonomyService.getTermsCount;

    }])

  .controller('entityTaxonomyConfigTermsViewController', [
    '$rootScope',
    '$scope',
    '$filter',
    '$uibModal',
    'EntityTaxonomyService',
    'VocabularyAttributeService',
    'NOTIFICATION_EVENTS',
    function ($rootScope,
              $scope,
              $filter,
              $uibModal,
              EntityTaxonomyService,
              VocabularyAttributeService,
              NOTIFICATION_EVENTS) {

      var addTerm = function(vocabulary, term) {
        var model = {
          content: term || null,
          children: null,
          type: 'term',
          valueType: VocabularyAttributeService.getType(vocabulary)
        };

        $uibModal.open({
          templateUrl: 'app/entity-taxonomy-config/views/entity-taxonomy-config-properties-modal.html',
          controller: 'entityTaxonomyConfigPropertiesModalController',
          resolve: {
            model: function() {
              return model;
            }
          }
        }).result.then(function(){
          $scope.model.children = vocabulary.terms = vocabulary.terms || [];
          vocabulary.terms.push(model.content);
        });
      };

      function onDelete(event, args) {
        $scope.unregisterOnDelete();
        var i = args.vocabulary.terms.indexOf(args.term);
        if (i > -1) {
          args.vocabulary.terms.splice(i, 1);
        }
      }

      var deleteTerm = function(vocabulary, term) {
        $scope.unregisterOnDelete = $scope.$on(NOTIFICATION_EVENTS.confirmDialogAccepted, onDelete);
        var termLabel = $filter('translate')('global.term');
        $rootScope.$broadcast(NOTIFICATION_EVENTS.showConfirmDialog,
          {
            titleKey: 'taxonomy-config.delete-dialog.title',
            titleArgs: [termLabel],
            messageKey: 'taxonomy-config.delete-dialog.message',
            messageArgs: [termLabel.toLowerCase(), term.name]
          }, {vocabulary: vocabulary, term: term}
        );
      };

      $scope.addTerm = addTerm;
      $scope.deleteTerm = deleteTerm;
      $scope.isStatic = VocabularyAttributeService.isStatic;
      $scope.getTermsCount = EntityTaxonomyService.getTermsCount;
    }])

  .controller('entityTaxonomyConfigPropertiesModalController', [
    '$scope',
    '$uibModalInstance',
    '$filter',
    'EntityTaxonomySchemaFormService',
    'VocabularyAttributeService',
    'MicaConfigResource',
    'model',
    function ($scope,
              $uibModalInstance,
              $filter,
              EntityTaxonomySchemaFormService,
              VocabularyAttributeService,
              MicaConfigResource,
              model) {

      $scope.model = model;
      var apply = function () {
        $scope.$broadcast('schemaFormValidate');

        if($scope.form.$valid) {
          EntityTaxonomySchemaFormService.updateModel($scope.sfForm, $scope.model);
          $uibModalInstance.close($scope.sfForm.model);

        }

        $scope.form.saveAttempted = true;
      };

      var cancel = function () {
        $uibModalInstance.dismiss('cancel');
      };

      var getTitle = function() {
        var key = 'taxonomy-config.' + $scope.model.type + '-dialog.' + ($scope.model.content ? 'edit-' : 'add-');
        switch ($scope.model.type) {
          case 'taxonomy':
            key = 'edit';
            break;
          case 'criterion':
            key += 'criterion';
            break;
          case 'term':
            key += 'term';
            break;
        }

        return $filter('translate')(key);
      };

      MicaConfigResource.get(function (micaConfig) {
        $scope.sfOptions = {
          formDefaults: {
            languages: {}
          }
        };

        micaConfig.languages.forEach(function (lang) {
          $scope.sfOptions.formDefaults.languages[lang] = $filter('translate')('language.' + lang);
        });

        $scope.sfForm = EntityTaxonomySchemaFormService.getFormData($scope.model);
        $scope.isStatic = VocabularyAttributeService.isStatic;
        $scope.apply = apply;
        $scope.cancel = cancel;
        $scope.getTitle = getTitle;
      });

    }]);

