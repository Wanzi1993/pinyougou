//注册处理器,名称为brandController,注入#scope上下文服务
app.controller("brandController",function ($scope,$controller,brandService) {

    //继承baseController
    $controller("baseController",{$scope:$scope});
    //查询全部品牌列表
    $scope.findAll=function () {
        brandService.findAll().success(function (response) {
            $scope.list = response;
        });
    };

    //初始化分页参数
    $scope.paginationConf = {
        currentPage:1,//当前页号
        totalItems:10,//总记录数
        itemsPerPage:10,//每页大小
        perPageOptions:[10,20,30,40,50],//可选择的每页大小
        onChange:function () {//当上述的参数发生变化后触发
            $scope.reloadList();

        }

    };

    //加载表格数据
    $scope.reloadList = function() {
        //$scope.findPage($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage)
    };

    //分页查询
    $scope.findPage=function (page,rows) {
        brandService.findPage(page,rows).success(function (response) {
            //更新记录列表
            $scope.list=response.rows;
            //更新总记录数
            $scope.paginationConf.totalItems=response.total;
        });
    };

    //保存品牌数据
    $scope.save = function () {

        var obj;
        if ($scope.entity.id != null) {
            //修改
            obj = brandService.update($scope.entity);
        } else {
            obj = brandService.add($scope.entity);
        }

        obj.success(function (response) {
            if(response.success){
                //刷新列表
                $scope.reloadList();
            } else {
                alert(response.message);
            }
        });

    };

    //修改前回显,根据id查询当前品牌
    $scope.findOne=function (id) {
        brandService.findOne(id).success(function (response) {

            $scope.entity=response;

        })
    }

    //批量删除,先定义一个数组,存储复选框中的id
    $scope.selectedIds = [];

    //往复选框中添加id
    $scope.updateSelection=function ($event,id) {
        //被选中,加入数组
        if ($event.target.checked){
            $scope.selectedIds.push(id);
        }
        //反选,从数组中删除
        else {
            var index = $scope.selectedIds.indexOf(id);
            $scope.selectedIds.splice(index,1);
        }
    };
    //批量删除
    $scope.delete=function () {
        if($scope.selectedIds.length<1){
            alert("请选择要删除的品牌");
            return;
        }
        if (confirm("是否删除")){
            brandService.delete($scope.selectedIds).success(function (response) {
                if (response.success){
                    $scope.reloadList();
                    $scope.selectedIds=[]
                } else {
                    //删除失败,弹出错误信息
                    alert(response.message)
                }
            })
        }
    };
    //搜索
    //定一个空的搜索条件对象
    $scope.searchEntity={};

    $scope.search=function (page,rows) {
        brandService.search($scope.searchEntity,page,rows).success(function (response) {
            $scope.list=response.rows;
            $scope.paginationConf.totalItems=response.total;
        });
    };
});