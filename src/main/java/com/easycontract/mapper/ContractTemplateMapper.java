package com.easycontract.mapper;

import com.easycontract.entity.po.ContractTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContractTemplateMapper {

    // 动态条件操作

    /**
     * 动态条件查询
     * @param condition 查询条件对象（非空字段作为查询条件）
     * @return 匹配的模板列表
     */
    List<ContractTemplate> selectByCondition(ContractTemplate condition);

    /**
     * 动态条件更新
     * @param entity 包含更新字段的实体
     * @param condition 更新条件对象（非空字段作为条件）
     * @return 影响行数
     */
    int updateByCondition(@Param("entity") ContractTemplate entity,
                          @Param("condition") ContractTemplate condition);

    /**
     * 动态条件删除
     * @param condition 删除条件对象（非空字段作为条件）
     * @return 影响行数
     */
    int deleteByCondition(ContractTemplate condition);

    /**
     * 新增合同模板
     * @param template 模板实体
     * @return 影响行数
     */
    int insert(ContractTemplate template);
}
