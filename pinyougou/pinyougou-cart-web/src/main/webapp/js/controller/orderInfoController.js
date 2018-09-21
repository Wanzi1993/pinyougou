app.controller("orderInfoController",function ($scope,cartService,addressService) {
    $scope.getUsername = function () {
        cartService.getUsername().success(function (response) {
            $scope.username = response.username;
        });
    };
    //加载地址列表
    $scope.findAddressList = function () {
        addressService.findAddressList().success(function (response) {

            $scope.addressList = response;

            for (var i = 0; i < response.length; i++) {
                var address = response[i];
                if(address.isDefault=="1"){
                    $scope.address = address;
                    break;
                }
            }

        });

    };
    //选择地址
    $scope.selectAddress=function (address) {
        $scope.address=address;

    };

    //判断当前地址是否为选择的地址
    $scope.isSelecttedAddress=function (address) {
        if ($scope.address == address){
            return true;
        }

        return false;
    }

    //默认支付方式
    $scope.order = {"paymentType":"1"};

    $scope.selectPaymentType=function (type) {
        $scope.order.paymentType=type;
    }

    //查询购物车列表
    $scope.findCartList = function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList = response;
            //计算总数量和价格
            $scope.totalValue = cartService.sumTotalValue(response);
        });

    };

    //提交订单
    $scope.submitOrder = function () {
        $scope.order.receiverAreaName = $scope.address.address;
        $scope.order.receiverMobile = $scope.address.mobile;
        $scope.order.receiver = $scope.address.contact;

        cartService.submitOrder($scope.order).success(function (respons) {
            if (respons.success){

                if ("1"==$scope.order.paymentType){
                    //如果是微信支付,跳转到支付页面,并携带支付日志id
                    alert("下单成功")
                    location.href = "pay.html#?outTradeNo=" + respons.message;
                } else {
                    //如果是货到付款,跳转到支付成功页面
                    location.href = "paysuccess.html";
                }
            }else {
                alert(respons.message);
            }
        })
    }
});