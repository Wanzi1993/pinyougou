<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>FreeMarker测试</title>
</head>
<body>
<#--这是freemarker的注释,不会输出到文件上-->
<h1>${name};${message}</h1>

<#--assign-->
<#--简单类型-->
<#assign linkman="黑马"/>
${linkman}<br>
<hr><br>


<#--对象-->
<#assign info={"mobile":"1380000000","adress":"广州"}/>
联系电话:${info.mobile},联系地址:${info.adress}<br>

<hr><br>
<#include "header.ftl"/>
<br><hr><br>

<#--if-->
<#assign bool=false/>
<#if bool>
    bool的值为true
<#else>
    bool的值为false
</#if>
<br><hr><br>

<#--遍历list-->
<#list goodsList as goods>
    ${goods_index},名称为:${goods.name},价格为:${goods.price}
</#list>
<br><hr><br>
<#--获取集合总记录数-->
总共${goodsList?size}条记录
<br><hr><br>

<#--将字符串转换为json对象-->
<#assign str="{'id':123,'text':'itcast'}"/>
<#assign jsonObj=str?eval/>
id为:${jsonObj.id},text为:${jsonObj.text}
<br><hr><br>

<#--日期格式处理-->
当前日期:${today?date}<br>
当前时间:${today?time}<br>
当前日期+时间:${today?datetime}<br>
格式化显示当前日期时间:${today?string('yyyy年MM月dd日 HH:mm:ss')}
<br><hr><br>

<#--数值显示处理-->
number=${number}<br>
<#--不显示千位符-->
number?c=${number?c}<br>
</body>
</html>