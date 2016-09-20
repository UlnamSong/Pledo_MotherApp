angular.module('login', [])
    .controller('LoginController', ['$scope', '$http', '$location', function($scope, $http, $location) {

        this.postForm = function() {
            //console.log('postForm');

            /*var encodedString = 'username=' +
                encodeURIComponent(this.inputData.username) +
                '&password=' +
                encodeURIComponent(this.inputData.password);

            $http({
                method: 'POST',
                url: 'check-login.php',
                data: encodedString,
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            })
            .success(function(data, status, headers, config) {
                console.log(data);
                if ( data.trim() === 'correct') {
                    window.location.href = 'success.html';
                } else {
                    $scope.errorMsg = "Login not correct";
                }
            })
            .error(function(data, status, headers, config) {
                $scope.errorMsg = 'Unable to submit form';
            })*/

            //console.log('this.inputData', this.inputData);
            //console.log('this.inputData.userid', this.inputData.userid);

            if(this.inputData == undefined){
                return;
            }
            if(this.inputData.userid == undefined || this.inputData.password == undefined){
                return;
            }

            /* json post */
            var dataObj = {
                user_id: this.inputData.userid,
                user_pass: this.inputData.password
            };

            var api_url = 'http://61.72.142.86:8118/user/login.do';

            var res = $http.post(api_url, dataObj);
            res.success(function(data, status, headers, config) {
                console.log(data);
                //$scope.message = data;
                //$location.path('/login');
                window.location.href = '../main.html';
            });
            res.error(function(data, status, headers, config) {
                console.log("failure message: " + JSON.stringify({data: data}));
                //alert( "failure message: " + JSON.stringify({data: data}));
            });
        }

    }]);