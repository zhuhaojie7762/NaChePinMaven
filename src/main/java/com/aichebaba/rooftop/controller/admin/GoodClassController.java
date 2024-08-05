package com.aichebaba.rooftop.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.GoodsClass;
import com.aichebaba.rooftop.service.GoodClassService;
import com.aichebaba.rooftop.vo.Json;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;

/**
 * @author yujing 2016年12月8日
 */
@Controller
@Scope("prototype")
public class GoodClassController extends BaseController {
	@Autowired
	private GoodClassService goodClassService;

	/**
	 * 分类管理
	 */
	@ActionKey("manage")
	public void manage() {
		render("newClass.html");
	}

	/**
	 * 获取所有分类
	 */
	@Clear
	@ActionKey("get")
	public void get() {
		Integer id = getParaToInt("id");

		if (null == id) {
			error("获取失败");
			return;
		}
		renderJson(Json.success("", goodClassService.getById(id)));
	}

	/**
	 * 获取所有分类
	 */
	@ActionKey("all")
	public void all() {
		renderJson(Json.success("", goodClassService.getAll()));
	}

	/**
	 * 删除分类
	 */
	@ActionKey("del")
	public void del() {
		Integer id = getParaToInt("id");
		if (null == id) {
			error("处理失败");
			return;
		}
		
		GoodsClass goodsClass = goodClassService.getById(id);
		if (null == goodsClass) {
			error("节点不存在");
			return;
		}

		List<GoodsClass> children = goodClassService.getChildren(id);

		if (!children.isEmpty()) {
			error("子分类不为空，不允许删除");
			return;
		}

		if (goodClassService.isUsed(goodsClass.getId(), goodsClass.getLevel())) {
			error("分类已被使用，不允许删除");
			return;
		}

		goodClassService.delete(id);
		ok("删除成功");
	}

	/**
	 * 编辑分类
	 */
	@ActionKey("edit")
	public void edit() {
		Integer id = getParaToInt("id");
		String name = getPara("name");
		Integer pid = getParaToInt("pid");
		Integer level = getParaToInt("level");

		GoodsClass entity = new GoodsClass();
		if (id != null) {
			entity.setId(id);
		}
		entity.setLevel(level);
		entity.setPid(pid);
		entity.setName(name);

		if (level > 4) {
			error("处理失败");
			return;
		}

		if (id != null) {
			if (goodClassService.edit(entity)) {
				ok("编辑成功", entity);
			} else {
				error("编辑失败");
			}
		} else {
			ok("添加成功", goodClassService.add(entity));
		}
	}
}
