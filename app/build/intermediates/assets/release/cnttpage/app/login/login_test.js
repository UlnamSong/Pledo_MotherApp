angular.module('postLogin', [])
    .controller('PostController', ['$scope', '$http', function($scope, $http) {

        this.postForm = function() {

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


            /* json post */
            var dataObj = {
                user_id: this.inputData.username,
                user_pass: this.inputData.password
            };

            var login_url = 'http://61.72.142.86:8118/user/login.do';

            var res = $http.post(login_url, dataObj);
            res.success(function(data, status, headers, config) {
                console.log(data);
                //$scope.message = data;
            });
            res.error(function(data, status, headers, config) {
                console.log("failure message: " + JSON.stringify({data: data}));
                //alert( "failure message: " + JSON.stringify({data: data}));
            });
        }

    }]);