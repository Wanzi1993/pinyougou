package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.order.service.PayLogService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/payLog")
@RestController
public class PayLogController {

    @Reference
    private PayLogService payLogService;

    @RequestMapping("/findAll")
    public List<TbPayLog> findAll() {
        return payLogService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return payLogService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody TbPayLog payLog) {
        try {
            payLogService.add(payLog);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    @GetMapping("/findOne")
    public TbPayLog findOne(Long id) {
        return payLogService.findOne(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbPayLog payLog) {
        try {
            payLogService.update(payLog);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            payLogService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     * @param payLog 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbPayLog payLog, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return payLogService.search(page, rows, payLog);
    }

}
