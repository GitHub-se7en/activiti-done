package com.java.activiti.service;


import com.java.activiti.model.ActivitiUser;

import java.util.List;
import java.util.Map;

/**
 * @ClassName ActivitiUserServiceImpl
 * @Description 业务表和activiti的中间表
 * @Author itw_zhaowg
 * @Date 2019/9/10 11:14
 **/
public interface ActivitiUserService {

    Integer insertUserIdInstanceId(ActivitiUser activitiUser);

    Integer startProcessAndSaveInstanceId(String userId, Map<String, Object> variables) throws Exception;

    List<ActivitiUser> selectAllInstanceByMe(String userId);

    ActivitiUser checkIfApproved(ActivitiUser activitiUser);

}
