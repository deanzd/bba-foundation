package com.eking.momp.menu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eking.momp.common.Constant;
import com.eking.momp.db.mapper.MenuMapper;
import com.eking.momp.db.model.Menu;
import com.eking.momp.db.AbstractService;
import com.eking.momp.menu.dto.MenuDto;
import com.eking.momp.menu.service.MenuService;
import com.eking.momp.model.dto.DimensionDto;
import com.eking.momp.model.dto.ModelDto;
import com.eking.momp.model.service.DimensionService;
import com.eking.momp.model.service.LayerService;
import com.eking.momp.model.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class MenuServiceImpl extends AbstractService<MenuMapper, Menu> implements MenuService {
    @Autowired
    private DimensionService dimensionService;
    @Autowired
    private LayerService layerService;
    @Autowired
    private ModelService modelService;

    @Override
    public List<MenuDto> list() {
//		UserContext userContext = UserContextHoder.getUserContext();
//		List<Integer> permissionIds = userContext.getPermissionIds();
        //		checkPermission(menuDtos, permissionIds);
        return genChildMenus(null);
    }

    @SuppressWarnings("unused")
    private void checkPermission(List<MenuDto> menuDtos, List<Integer> permissionIds) {
        menuDtos.forEach(menu -> {
            if (permissionIds.contains(menu.getId())) {
                if (!CollectionUtils.isEmpty(menu.getChildren())) {
                    checkPermission(menu.getChildren(), permissionIds);
                }
            } else {
                menuDtos.remove(menu);
            }
        });
    }

    private List<MenuDto> genChildMenus(Menu parent) {
        List<MenuDto> dtos = new ArrayList<>();
        // 根据parent_id
        QueryWrapper<Menu> qw = new QueryWrapper<>();
        if (parent == null) {
            qw.isNull("parent_id");
        } else {
            qw.eq("parent_id", parent.getId());
        }
        List<Menu> menus = this.listObjs(Constant.FIELD_SHOW_ORDER, true, qw);
        menus.forEach(menu -> {
            MenuDto menuDto = new MenuDto(menu);
            List<MenuDto> children = genChildMenus(menu);
            menuDto.setChildren(children);
            dtos.add(menuDto);
        });
        // dynamic
        if (parent != null && parent.getDynamicChildrenCode() != null) {
            List<MenuDto> dynamicDtos = genDynamicChildMenus(parent.getPath(), parent.getDynamicChildrenCode());
            dtos.addAll(dynamicDtos);
        }
        return dtos;
    }

    private List<MenuDto> genDynamicChildMenus(String path, String code) {
        if ("dimension".equals(code)) {
            List<MenuDto> children = new ArrayList<>();
            List<DimensionDto> dimensions = dimensionService.listSysInit();
            for (DimensionDto dimension : dimensions) {
                children.add(new MenuDto(dimension.getName(), path + "/" + dimension.getId(), null));
            }
            return children;
        } else if ("layer".equals(code)) {
            List<MenuDto> children = new ArrayList<>();
            layerService.list().forEach(layer -> {
                MenuDto layerChild = new MenuDto(layer.getName(), path + "/layer/" + layer.getId(), null);
                List<MenuDto> modelChildren = new ArrayList<>();
                List<ModelDto> models = modelService.listByLayerId(layer.getId());
                if (models.size() > 0) {
                    for (ModelDto model : models) {
                        MenuDto modelChild = new MenuDto(model.getName(),
                                layerChild.getPath() + "/model/" + model.getId() + "/" + model.getCode() + "/0", null);
                        modelChildren.add(modelChild);
                    }
                    layerChild.setChildren(modelChildren);
                    children.add(layerChild);
                }
            });
            return children;
        }
        return Collections.emptyList();
    }
}
