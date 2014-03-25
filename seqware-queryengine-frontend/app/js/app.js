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
  $routeProvider.when('/query', {templateUrl: 'partials/query.html', controller: 'QueryCtrl'});
  $routeProvider.otherwise({redirectTo: '/'});
}])
.config(['$httpProvider', function($httpProvider) {
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common['X-Requested-With'];
    }
]);

angular.module('queryengineApp').constant('APP_CONFIG', {
  'webservice_url':'http://localhost:8889/seqware-queryengine-webservice/api/'
});