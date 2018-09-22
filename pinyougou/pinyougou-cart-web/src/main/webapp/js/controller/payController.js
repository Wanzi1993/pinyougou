app.controller("payController", function ($scope, $location, cartService, payService) {

    $scope.getUsername = function () {
        cartService.getUsername().success(function (response) {
            $scope.username = response.username;
        });
    };

    //生成二维码
    $scope.createNative = function () {

        //接收地址栏中的支付日志id
        $scope.outTradeNo = $location.search()["outTradeNo"];

        payService.createNative($scope.outTradeNo).success(function (response) {

            if ("SUCCESS" == response.result_code) {//创建支付地址成功
                //显示本次支付总金额
                $scope.money = (response.totalFee / 100).toFixed(2);

                //生成支付地址的二维码
                var qr = new QRious({
                    element: document.getElementById("qrious"),
                    level: "Q",
                    size: 250,
                    value: response.code_url
                });

                //查询订单的支付状态
                queryPayStatus($scope.outTradeNo);

            } else {
                alert("生成二维码失败");
            }
        })


    };
    //查询订单的支付状态
    queryPayStatus = function (outTradeNo) {
        payService.queryPayStatus(outTradeNo).success(function (response) {
            if (response.success) {
                //跳转到支付成功提示页面
                location.href = "paysuccess.html#?money=" + $scope.money;
            } else {
                //如果3分钟未支付则提示支付超时并重新自动生成新的支付二维码
                if ("二维码超时" == response.message) {
                    $scope.createNative();
                } else {
                    //支付失败则跳转到支付失败提示页面
                    location.href = "payfail.html";
                }
            }
        })
    }

    //获取总金额
    $scope.getMoney = function () {
        $scope.money = $location.search()["money"];
    }
});