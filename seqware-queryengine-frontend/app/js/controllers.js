'use strict';

/* Controllers */

angular.module('queryengineApp.controllers', []).
  controller('HomeCtrl', function($scope) {
    var database = {};
    database["variants"] = 0; //Should send a GET Request to Webservice with $http or $resource
    database["samples"] = 0; //Should sent a GET Request to Webservice
    $scope.database = database;
  })
  .controller('UploadCtrl', function($scope, $upload) {//$http) {
    //var file = $scope.myFile; 
    $scope.status = {};
    $scope.data = {};
    /*$scope.upload = function(file) {
      var fd = new FormData();
      fd.append('file', file);
      $http({
        method: 'POST',
        url: 'http://10.0.20.188:8889/seqware-queryengine-webservice/api/featureset/upload', 
        data: fd,
        transformRequest: angular.identity, 
        headers: {'Content-Type': undefined}
      }).then(function(data, status, headers, config){
        $scope.data = data || "Request failed";
        $scope.status = status;
      });
    };*/
    $scope.onFileSelect = function($files) {
      for (var i = 0; i < $files.length; i++) {
        var file = $files[i];
        $scope.upload = $upload.upload({
          url: 'http://10.0.20.188:8889/seqware-queryengine-webservice/api/featureset/upload', //upload.php script, node.js route, or servlet url
          method: 'POST',
          headers: {'Content-Type': undefined},
          // withCredentials: true,
          data: {myObj: $scope.myModelObj},
          file: file
          // file: $files, //upload multiple files, this feature only works in HTML5 FromData browsers
          /* set file formData name for 'Content-Desposition' header. Default: 'file' */
          //fileFormDataName: myFile, //OR for HTML5 multiple upload only a list: ['name1', 'name2', ...]
          /* customize how data is added to formData. See #40#issuecomment-28612000 for example */
          //formDataAppender: function(formData, key, val){} //#40#issuecomment-28612000
        }).progress(function(evt) {
          console.log('percent: ' + parseInt(100.0 * evt.loaded / evt.total));
        }).success(function(data, status, headers, config) {
          $scope.data = data;
          console.log(data);
        });
        //.error(...)
        //.then(success, error, progress); 
      }
    // $scope.upload = $upload.upload({...}) alternative way of uploading, sends the the file content directly with the same content-type of the file. Could be used to upload files to CouchDB, imgur, etc... for HTML5 FileReader browsers. 
    };

  })
  .controller('QueryCtrl', function($scope, $http, $q) {
    $scope.master = {};
    $scope.status = {};
    $scope.response = {};
    $scope.update = function(query) {
      $scope.master = angular.copy(query);
      //var deferred_response = $q.defer();
      //var deferred_status = $q.defer();
      $http({
        method: 'GET', 
        withCredentials: true,
        url: 'http://10.0.20.188:8889/seqware-queryengine-webservice/api/featureset/', 
        transformRequest: angular.identity,
      }).then(function(data, status, headers, config) {
        $scope.response = data;
        //deferred_response.resolve(data);
        //deferred_status.resolve(status);
      });
    };
  });