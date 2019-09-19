package com.java.activiti.dao;


import com.java.activiti.model.ActivitiUser;

import java.util.List;

public interface ActivitiUserMapper {
    /**
     * @mbg.generated Mon Sep 09 17:50:53 CST 2019
     */
    int deleteByPrimaryKey(String id);

    /**
     * @mbg.generated Mon Sep 09 17:50:53 CST 2019
     */
    int insert(ActivitiUser record);


    /**
     * @Author itw_zhaowg
     * @Description 插入使用的是选择性插入
     * @Date 2019/9/11 14:07
     * @Param [record]
     * @return int
     **/
    int insertSelective(ActivitiUser record);

    /**
     * @mbg.generated Mon Sep 09 17:50:53 CST 2019
     */
    ActivitiUser selectByPrimaryKey(String id);

    /**
     * @mbg.generated Mon Sep 09 17:50:53 CST 2019
     */
    int updateByPrimaryKeySelective(ActivitiUser record);

    /**
     * @mbg.generated Mon Sep 09 17:50:53 CST 2019
     */
    int updateByPrimaryKey(ActivitiUser record);

    /**
     * @return java.util.List<com.oa.system.model.beanDO.ActivitiUser>
     * @Author itw_zhaowg
     * @Description 筛选所有由我发起的流程
     * @Date 2019/9/17 14:38
     * @Param [userId]
     **/
    List<ActivitiUser> selectAllInstanceByMe(String userId);
    /**
     * @Author itw_zhaowg
     * @Description 查看这个任务是同意还是拒绝
     * @Date 2019/9/17 14:39
     * @Param [record]
     * @return com.oa.system.model.beanDO.ActivitiUser
     **/
    ActivitiUser checkIfApproved(ActivitiUser record);

    ActivitiUser getStartUserIdByInstanceIdAndTaskId(String startEventProcessInstanceId);
}
