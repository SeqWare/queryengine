'use strict';

/* Controllers */

angular.module('queryengineApp.controllers', []).
  controller('HomeCtrl', function($scope, $http, APP_CONFIG) {
    var database = {};
    //var url = APP_CONFIG.webservice_url + 'featureset/';
    $http({
        method: 'GET', 
        withCredentials: true,
        url: APP_CONFIG.webservice_url + 'featureset/', 
        transformRequest: angular.identity,
    }).then(function(data, status, headers, config) {
      database["variants"] = data.data.length;
    });
    
    database["samples"] = 0; //Should sent a GET Request to Webservice
    $scope.database = database;
  })
  .controller('UploadCtrl', function($scope, $upload, APP_CONFIG) {
    $scope.data = {};
    $scope.onFileSelect = function($files) {
      for (var i = 0; i < $files.length; i++) {
        var file = $files[i];
        var upload_url = APP_CONFIG.webservice_url + 'featureset/upload';
        $scope.upload = $upload.upload({
          url: upload_url, //upload.php script, node.js route, or servlet url
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
          $scope.variantResponse = data;
          console.log(data);
        }).error(function(data, status, headers, config) {
          $scope.variantResponse = "An error has occurred. Status: " + status;
          console.log($scope.isCompressed);
        });
        //.then(success, error, progress); 
      }
    // $scope.upload = $upload.upload({...}) alternative way of uploading, sends the the file content directly with the same content-type of the file. Could be used to upload files to CouchDB, imgur, etc... for HTML5 FileReader browsers. 
    };

    $scope.onSAMFileSelect = function($files) {
      for (var i = 0; i < $files.length; i++) {
        var file = $files[i];
        var upload_url = APP_CONFIG.webservice_url + 'readset/upload';
        $scope.upload = $upload.upload({
          url: upload_url, //upload.php script, node.js route, or servlet url
          method: 'POST',
          headers: {'Content-Type': undefined},
          // withCredentials: true,
          data: {myObj: $scope.myModelObj},
          file: file
        }).progress(function(evt) {
          console.log('percent: ' + parseInt(100.0 * evt.loaded / evt.total));
        }).success(function(data, status, headers, config) {
          $scope.readResponse = data;
          console.log(data);
        }).error(function(data, status, headers, config) {
          $scope.variantResponse = "An error has occurred. Status: " + status;
        });
        //.then(success, error, progress); 
      }
    // $scope.upload = $upload.upload({...}) alternative way of uploading, sends the the file content directly with the same content-type of the file. Could be used to upload files to CouchDB, imgur, etc... for HTML5 FileReader browsers. 
    };

  })
  .controller('VariantCtrl', function($scope, $http, APP_CONFIG) {
    $scope.master = {};
    $scope.responses = {};
    var url = APP_CONFIG.webservice_url + 'featureset/';
    $http({
      method: 'GET', 
      withCredentials: true,
      url: url, 
      transformRequest: angular.identity
    }).then(function(data, status, headers, config) {
      $scope.responses = data.data;
    });
    $scope.fileUrl = APP_CONFIG.webservice_url + "featureset/download/";
  })
  .controller('ReadCtrl', function($scope, $http, APP_CONFIG) {
    $scope.master = {};
    $scope.responses = {};
    var url = APP_CONFIG.webservice_url + 'readset/';
    $http({
      method: 'GET', 
      withCredentials: true,
      url: url, 
      transformRequest: angular.identity
    }).then(function(data, status, headers, config) {
      $scope.responses = data.data;
    });
  })
  .controller('ReferenceCtrl', function($scope, $http, APP_CONFIG) {
    $scope.master = {};
    $scope.responses = {};
    var url = APP_CONFIG.webservice_url + 'reference/';
    $http({
      method: 'GET', 
      withCredentials: true,
      url: url, 
      transformRequest: angular.identity
    }).then(function(data, status, headers, config) {
      $scope.responses = data.data;
    });
  })
  .controller('ReferenceSetCtrl', function($scope, $http, APP_CONFIG) {
    $scope.master = {};
    $scope.responses = {};
    var url = APP_CONFIG.webservice_url + 'referenceset/';
    $http({
      method: 'GET', 
      withCredentials: true,
      url: url, 
      transformRequest: angular.identity
    }).then(function(data, status, headers, config) {
      $scope.responses = data.data;
    });
  })
  .controller('PluginCtrl', function($scope, $http, APP_CONFIG) {
    $scope.pluginList = {};
    var url = APP_CONFIG.webservice_url + 'plugin/';
    $http({
        method: 'GET', 
        withCredentials: true,
        url: url, 
        transformRequest: angular.identity
      }).then(function(data, status, headers, config) {
        $scope.pluginList = data.data;
      });
    $scope.runPlugin = function() {
      $http({
        method: 'POST', 
        withCredentials: true,
        url: APP_CONFIG.webservice_url + 'plugin/' + $scope.plugin + '/run?reference=' + $scope.reference + '&output=' + $scope.output, 
        transformRequest: angular.identity,
        data: "{}"
      }).then(function(data, status, headers, config) {
        $scope.response = data.data;
      });
    }
  })
  .controller('QueryCtrl', function($scope, $http, APP_CONFIG) {
    $scope.parameters = {};
    $scope.master = {};
    $scope.isDisabled = true; // Disables/Enables download of VCF
    //var file = undefined;
    $scope.runQuery = function() {
      console.log(JSON.stringify($scope.parameters));
      $http({
        method: 'POST',
        withCredentials: true,
        url: APP_CONFIG.webservice_url + 'featureset/run',
        transformRequest: angular.identity,
        data: JSON.stringify($scope.parameters)
      }).then(function(data, status, headers, config) {
        $scope.response = data.data;
        $scope.master = $scope.parameters;
        if (typeof $scope.response != "undefined") {
          $scope.isDisabled = false;
          $scope.fileUrl = APP_CONFIG.webservice_url + "featureset/download/" + $scope.response.sgid;
        } else {
          $scope.isDisabled = true;
        }
      });
    }
    $scope.disable = function() {
      $scope.isDisabled = true;
    }
  });