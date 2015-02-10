/** 
 * Author: Filip Sivák <sivakfil@gmail.com>
 */

var main =  angular.module("main", ['ngRoute']);
var HOME = ".";
var WS_FORM_SOCKET = "ws://daisyfetch-fsivak.rhcloud.com/formSocket";

main.config(["$routeProvider", function($routeProvider) {
	$routeProvider.when("/",  {
		templateUrl: "templates/view.html",
		controller: "ListController"
	}).when("/add", {
		templateUrl: "templates/add.html",
		controller: "AddController"
	}).when("/edit/:id", {
		templateUrl: "templates/edit.html",
		controller: "EditController"
	}).otherwise({
		redirectTo: "/"
	});
}]);

// TODO: handle $http errors
main.factory("WebService", ["$http", function($http) {
	var service = {};

	service.list = function() {
		return $http.get(HOME + "/rest/web");
	};

	service.detail = function(id, callback) {
		return $http.get(HOME + "/rest/web/"+id);
	};
	
	service.edit = function(item) {
		return $http.put(HOME + "/rest/web/", item);
	};
	
	service.add = function(item) {
		return $http.post(HOME + "/rest/web", item);
	}
	
	// TODO: change query param to path param
	service.remove = function(item) {
		return $http.delete(HOME + "/rest/web?uuid="+item.name);
	}
	
	service.cachedIdentity = null;
	service.identity = function(callback) {
		if(service.cachedIdentity != null) {
			callback(service.cachedIdentity);
		}else {
			$http.get(HOME + "/rest/identity").success(callback);
		} 
	}
	
	return service;
}]);

main.factory("FormSocketService", function() {
	var service = {};
	
	service.open = function() {
		this.websocket = new WebSocket(WS_FORM_SOCKET);
		this.websocket.onopen = function() {
			console.log("Websocket opened");
		}
		return this.websocket;
	};
	
	return service;
});

main.controller("ListController", ["$scope", "$window", "WebService", function($scope, $win, webService) {
	webService.identity(function(identity) {
		$scope.userEmail = identity.email;
	});
	
	function list() {
		webService.list().success(function(data) {
			$scope.webs = data;
		}).error(function(data) {
			$win.alert(data);
		});
	}
	list();
	
	$scope.btnRemove = function(item) {
		webService.remove(item).success(function() {
			list();
		}).error(function(data) {
			$win.alert(data);
		});
	}
}]);

// source: http://stackoverflow.com/a/18364997/663068
function isValidUrl(url)
{
     return url.match(/^(ht|f)tps?:\/\/[a-z0-9-\.]+\.[a-z]{2,4}\/?([^\s<>\#%"\,\{\}\\|\\\^\[\]`]+)?$/);
}

main.controller("AddController", ["$scope", "$rootScope", "$window", "$location", "WebService", "FormSocketService", 
function($scope, $rootScope, $win, $location, webService, formSocketService) {
	// default values
	$scope.partial = {};
	$scope.partial.item = {
		cache: true,
		cacheLeaseTime: 24,
		reportInterval: 24,
		snippets: []
	};
	
	ws = formSocketService.open();
	
	$rootScope.$on( "$routeChangeStart", function(event, next, current) {
		ws.close();
	});
	
	$scope.partial.addSnippet = function() {
		$scope.partial.item.snippets.push({name: "", selector: ""});
	};
	
	$scope.partial.removeSnippet = function(index) {
		$scope.partial.item.snippets.splice(index, 1);
	};
	
	$scope.btnAdd = function(item) {
		item = $scope.partial.item;
		console.log(item);
		webService.add(item).success(function(data) {
			item = data;	
			$location.path("/");
		}).error(function(data){
			$win.alert(data);
		});
	};
	
	$scope.urlChange = function() {
		if(isValidUrl($scope.partial.item.url)) {
			ws.send("status "+$scope.partial.item.url);
		} 
	};
	
	ws.onmessage = function(event) {
		$scope.partial.item.urlStatus = event.data;
		$scope.$apply();
	};
	
}]);

main.controller("EditController", ["$scope","$window", "$location", "$routeParams", "WebService",
function($scope, $win, $location, $routeParams, webService) {
	$scope.partial = {};
	
	webService.detail($routeParams.id).success(function(item) {
		$scope.partial.item = item;
	}).error(function(data, status, headers){
		if(status == 404) {
			$win.alert("Web does not exist!");
			$location.path("/");
		}else {
			$win.alert(status+": "+data);
		}
	});

	$scope.partial.addSnippet = function() {
		$scope.partial.item.snippets.push({name: "", selector: ""});
	};
	
	$scope.partial.removeSnippet = function(index) {
		$scope.partial.item.snippets.splice(index, 1);
	};
	
	$scope.btnEdit = function(item) {
		item = $scope.partial.item;
		webService.edit(item).success(function() {
			$location.path("/");
		}).error(function(e) {
			$win.alert(e);
		});
	};
}]);

main.controller("identityController", ["$scope","$window","WebService", function(scope,wnd,ws){
	ws.identity(function(data){
		scope.email = data.email;
		scope.type = data.type;
	});
}]);

// source: http://stackoverflow.com/a/18313962/663068
main.directive('ngConfirmClick', [
 function(){
     return {
         link: function (scope, element, attr) {
             var msg = attr.ngConfirmClick || "Are you sure?";
             var clickAction = attr.confirmedClick;
             element.bind('click',function (event) {
                     if ( window.confirm(msg) ) {
                         scope.$eval(clickAction)
                     }
                 });
             }
         };
 }]);
