'use strict';


// Declare app level module which depends on filters, and services
angular.module('queryengineApp', [
  'ngRoute',
  'queryengineApp.filters',
  'queryengineApp.services',
  'queryengineApp.directives',
  'queryengineApp.controllers',
  'angularFileUpload'
]).
config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/', {templateUrl: 'partials/home.html', controller: 'HomeCtrl'});
  $routeProvider.when('/upload', {templateUrl: 'partials/upload.html', controller: 'UploadCtrl'});
  $routeProvider.when('/variants', {templateUrl: 'partials/variants.html', controller: 'VariantCtrl'});
  $routeProvider.when('/plugin', {templateUrl: 'partials/plugin.html', controller: 'PluginCtrl'});
  $routeProvider.when('/query', {templateUrl: 'partials/query.html', controller: 'QueryCtrl'});
  $routeProvider.when('/reads', {templateUrl: 'partials/reads.html', controller: 'ReadCtrl'});
  $routeProvider.when('/references', {templateUrl: 'partials/references.html', controller: 'ReferenceCtrl'});
  $routeProvider.when('/referencesets', {templateUrl: 'partials/referencesets.html', controller: 'ReferenceSetCtrl'});
  $routeProvider.otherwise({redirectTo: '/'});
}]);

// Application Config
angular.module('queryengineApp').constant('APP_CONFIG', {
  'webservice_url':'http://10.0.20.188:8889/seqware-queryengine-webservice/api/'
});
