app.controller("searchController",function ($scope,$location,searchService) {

    //搜索对象
    $scope.searchMap ={"keywords":"","category":"","brand":"","spec":{},"price":"","pageNo":1,"PageSize":20,"sortField":"","sort":""}
    //根据关键字搜索商品
    $scope.search=function () {
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap=response;

            //构建分页导航条
            buildPageInfo();
        });
    };

    //添加过滤条件
    $scope.addSearchItem=function (key,value) {

        if ("brand" == key || "category" == key || "price" == key) {
            //如果点击的是品牌或者分类的话
            $scope.searchMap[key] = value;
        }
        else {
            //其他的都是规格
            $scope.searchMap.spec[key] = value;
        }

        //点击过滤后需要重新搜索
        $scope.search();
        
    }

    //移除过滤条件
    $scope.removeSearchItem=function (key) {
        if ("brand" == key || "category" == key || "price" == key) {
            //如果点击的是品牌,或者分类或者价格的话,为空
            $scope.searchMap[key] = "";
        }
        else {
            //其他都是规格
            delete $scope.searchMap.spec[key];
        };

        $scope.search();

    };

    //构建分页导航条
    buildPageInfo=function () {

        //要分页导航条显示的集合
        $scope.pageNoList=[];

        //要在导航中显示的总页号个数
        var showPageCount = 5;

        //起始页号
        var startPageNo = 1;

        //结束页号
        var endPageNo = $scope.resultMap.totalPages;

        //总页数大于总显示页数时
        if ($scope.resultMap.totalPages > showPageCount){

            //间隔页2页
            var interval = Math.floor((showPageCount/2) );
            //开始页
            startPageNo = parseInt($scope.searchMap.pageNo)-interval;
            //结束页
            endPageNo = parseInt($scope.searchMap.pageNo)+interval;


            if (startPageNo > 0) {
                //当其起始页正常,结束页需要再次判断
                if (endPageNo > $scope.resultMap.totalPages){
                    endPageNo = $scope.resultMap.totalPages;
                    startPageNo = startPageNo-(endPageNo - $scope.resultMap.totalPages);
                }
            }else {
                //起始页小于一
                startPageNo = 1;
                endPageNo = showPageCount;
            }
        }
        //导航条的前面三个点
        $scope.frontDot=false;
        if (startPageNo > 1){
            $scope.frontDot = true;
        }
        //导航条的后面三个点
        $scope.backDot = false;
        if (endPageNo < $scope.resultMap.totalPages){
            $scope.backDot = true;
        }

        for (var i = startPageNo;i <= endPageNo;i++){
            $scope.pageNoList.push(i)
        }

    };

    //当前页构造方法
    $scope.isCurrentPage=function (pageNo) {
        return $scope.searchMap.pageNo == pageNo;
    }

    //根据页号查询
    $scope.queryByPageNo=function (pageNo) {
        if (0 < pageNo && pageNo <= $scope.resultMap.totalPages){
            $scope.searchMap.pageNo = pageNo;
            $scope.search();
        }
    }
    
    //添加排序
    $scope.addSortField=function (sortField,sort) {
        $scope.searchMap.sortField = sortField;
        $scope.searchMap.sort = sort;
        $scope.search();
    }

    //搜索关键字
    $scope.loadKeywords=function () {
        //获取地址栏的关键字
        $scope.searchMap.keywords = $location.search()["keywords"];
        $scope.search();
    }
});

