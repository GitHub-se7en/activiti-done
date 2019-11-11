package com.java.activiti.controller;

import com.java.activiti.model.TaskRepresentation;
import com.java.activiti.model.TaskRepresentation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName ActivitiController
 * @Description 流程开始与任务完成
 * @Author itw_zhaowg
 * @Date 2019/8/15 14:21
 **/
@RestController
@Slf4j
public class ActivitiController {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;


    @Autowired
    private ProcessEngine processEngine;// = ProcessEngines.getDefaultProcessEngine();


    @PostMapping("/start-process")
    public Map<String, Object> startProcess(String userId,
                                            String departmentId,
                                            String companyCode,
                                            String vacationTypeId,
                                            String startDate,
                                            String endDate,
                                            String durationTime,
                                            String vacationMotivation,
                                            String backupUserId,
                                            String attachmentUrl) {
        Map<String, Object> variables = new HashMap();
        //后期如果涉及到重名，可以单独加一张表，使用taskId和公司下的部门的员工进行关联
        //这样的话，不会出现重复问题
        variables.put("userId", userId);
        variables.put("departmentId", departmentId);
        //这里需要根据userID查询出该人的部门主管是什么，
        //然后作为变量传递到activiti中作为下一步任务的审批人
        Map<String, Object> map = new HashMap<>();
//        Department department;
//        UserRole userRoleProjectManagerId;
//        UserRole userRoleChairmanId;
        try {
//            department = iOrganizationService.selectManagerByDepartmentId(departmentId);
            //1012利用公司编码查询该公司的项目经理和董事长是谁
//            userRoleProjectManagerId = userRoleMapper.selectProjectManagerIdByCompanyCodeAndRole(companyCode);
//            userRoleChairmanId = userRoleMapper.selectChairmanIdByCompanyCodeAndRole(companyCode);
        } catch (Exception e) {
            log.info("==部门主管id为空，流程启动失败==" + e);
            map.put("code", 5000);
            map.put("message", "流程启动失败，部门主管或项目经理或总经理查询失败");
            return map;
        }
//        if (department == null || department.getUserId() == null || userRoleProjectManagerId == null || userRoleChairmanId == null) {
//            log.info("==部门主管id为空或未能查询出项目经理和总经理的userId，流程启动失败==");
//            map.put("code", 5000);
//            map.put("message", "流程启动失败，部门主管id为空或未能查询出项目经理和总经理的userId");
//            return map;
//        }
//        log.info("查询出来该员工的部门主管作为下一级的审批人" + department.getUserId() +
//                "该公司项目经理的userID为：" + userRoleProjectManagerId.getUserId() +
//                "总经理的userId为：" + userRoleChairmanId.getUserId());
//        if (userId.equals(userRoleChairmanId.getUserId().toString())) {
//            map.put("code", 5000);
//            map.put("message", "总经理不能请假");
//            return map;
//        }

//        try {
//            //vacationTypeId
//            Integer result = vacationMapper.updateDeleteFlag(vacationTypeId);
//            if (result == 0) {
//                log.info("==假期类型标志修改失败==");
//                map.put("code", 5000);
//                map.put("message", "流程启动失败，假期类型标志修改失败");
//                return map;
//            }
//        } catch (Exception e) {
//            log.info("==部门主管id为空，流程启动失败==" + e);
//            map.put("code", 5000);
//            map.put("message", "流程启动失败，假期类型标志修改失败");
//            return map;
//        }

//        variables.put("manageId", department.getUserId());
        variables.put("vacationTypeId", vacationTypeId);
        variables.put("startDate", startDate);
        variables.put("endDate", endDate);
        variables.put("durationTime", durationTime);
        variables.put("vacationMotivation", vacationMotivation);
        variables.put("backupUserId", backupUserId);
        variables.put("attachmentUrl", attachmentUrl);
        //1012新增加获取相关的
//        variables.put("projectManageId", userRoleProjectManagerId.getUserId());
//        variables.put("chairmanId", userRoleChairmanId.getUserId());
        try {
            //TODO 由于在controller层使用事务注解失败，所以重新弄一个service把controller的代码放进去，开启事务
            //已完成
//            activitiUserService.startProcessAndSaveInstanceId(userId, variables);
            //TODO 这里就需要区分启动的流程还是经过的流程
        } catch (Exception e) {
            log.info("==异常==" + e);
            map.put("code", 5000);
            map.put("message", "流程启动失败");
            return map;
        }
        map.put("code", 2000);
        map.put("message", "流程启动成功");
        return map;
    }

//    @GetMapping("/manager-get-tasks")
//    public List<TaskRepresentation> managerGetTasks(String userName) {
//        //这里是使用角色进行查询
////        List<Task> tasksUser = taskService.createTaskQuery().taskCandidateUser("kermit").list();
//        //查询这个人下面的所有的代办任务
//        List<Task> managerTasks = taskService.createTaskQuery().taskCandidateUser(userName).list();
//        return managerTasks.stream().map(task -> new TaskRepresentation(task.getId(), task.getName(), task.getProcessInstanceId(), task.getDescription()))
//                .collect(Collectors.toList());
//    }


    @GetMapping("/user-get-tasks")
    public Map<String, Object> userGetTasks(@RequestParam(required = true) String userId,
                                            @RequestParam(required = false) String userIdCondition,
                                            @RequestParam(required = false) String vacationTypeIdCondition) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            List<Task> employeeTasks = taskService.createTaskQuery().taskAssignee(userId).list();
            List<TaskRepresentation> taskRepresentations = employeeTasks.stream()
                    .map(task -> {
                        log.info("任务描述" + task.getDescription());
//                        String userIdToUserName = userMapper.selectByPrimaryKey(Long.valueOf((String) (JSON.parseObject(task.getDescription()).get("userId")))).getRealName();
//                        if (StringUtils.isNotEmpty(userIdCondition)) {
//                            if (!userIdCondition.equals(userIdToUserName)) {
//                                return null;
//                            }
//                        }
//                        String backupUserIdToBackupUserName = userMapper.selectByPrimaryKey(Long.valueOf((String) (JSON.parseObject(task.getDescription()).get("backupUserId")))).getRealName();
//                        String vacationTypeIdToVacationTypeName = vacationMapper.selectVacationNameByVacationId((String) JSON.parseObject(task.getDescription()).get("vacationTypeId")).getVacationName();
//                        if (StringUtils.isNotEmpty(vacationTypeIdCondition)) {
//                            if (!vacationTypeIdCondition.equals((String) JSON.parseObject(task.getDescription()).get("vacationTypeId"))) {
//                                return null;
//                            }
//                        }
//
//                        String departmentIdToDepartmentName = departmentMapper.selectByDepartmentCode((String) JSON.parseObject(task.getDescription()).get("departmentId")).getDepartmentName();

                        //新增下一个节点
                        String assignee = processEngine.getTaskService()
                                .createTaskQuery()
                                .processInstanceId(task.getProcessInstanceId())
                                .active()
                                .singleResult()
                                .getAssignee();
                        String assigneeRealName = null;
                        if ("project manager".equals(assignee)) {
                            assigneeRealName = "项目经理";
                        } else if ("Chairman".equals(assignee)) {
                            assigneeRealName = "总经理";
                        } else {
//                            assigneeRealName = userMapper.selectByPrimaryKey(Long.valueOf(assignee)).getRealName();
                        }


                        return new TaskRepresentation(task.getId(), task.getName(),
                                task.getProcessInstanceId(),
//                                task.getDescription()
//                                        .replace((String) (JSON.parseObject(task.getDescription()).get("userId")), JSON.parseObject(task.getDescription()).get("userId") + "," + userIdToUserName)
//                                        .replace((String) (JSON.parseObject(task.getDescription()).get("backupUserId")), JSON.parseObject(task.getDescription()).get("backupUserId") + "," + backupUserIdToBackupUserName)
//                                        .replace((String) JSON.parseObject(task.getDescription()).get("vacationTypeId"), JSON.parseObject(task.getDescription()).get("vacationTypeId") + "," + vacationTypeIdToVacationTypeName)
//                                        .replace((String) JSON.parseObject(task.getDescription()).get("departmentId"), JSON.parseObject(task.getDescription()).get("departmentId") + "," + departmentIdToDepartmentName),
                                assigneeRealName

                        );
                    })
                    .collect(Collectors.toList());
            resultMap.put("code", 2000);
            resultMap.put("message", "查询成功");
            resultMap.put("content", taskRepresentations);
            return resultMap;
        } catch (Exception e) {
            log.info("查询出现异常" + e);
            resultMap.put("code", 5000);
            resultMap.put("message", "查询失败，出现异常");
            return resultMap;
        }
    }


//    @PostMapping("/complete-task")
//    public Map<String, String> completeTask(String userName, String taskId, String vacationApproved, String managerMotivation) {
//        Map<String, String> map = new HashMap<>();
//        try {
//            Map<String, Object> taskVariables = new HashMap<String, Object>();
//            taskVariables.put("vacationApproved", vacationApproved);
//            taskVariables.put("managerMotivation", managerMotivation);
//            taskService.complete(taskId, taskVariables);
//        } catch (Exception e) {
//            map.put("code", 5000);
//            map.put("message", taskId + "失败");
//            return map;
//        }
//        map.put("code", 2000);
//        map.put("message", taskId + "完成");
//        return map;
//    }


    @PostMapping("/user-complete-task")
    public Map<String, Object> userCompleteTask(String userId, String taskId, String vacationApproved, String managerMotivation) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> taskVariables = new HashMap<String, Object>();
        taskVariables.put("vacationApproved", vacationApproved);
        taskVariables.put("managerMotivation", managerMotivation);
//        Integer code = activitiUserService.completeTask(userId, taskId, vacationApproved, managerMotivation, taskVariables);

//        map.put("code", code);
//        map.put("message", taskId + "完成");
        return map;
    }


    @GetMapping("/user-get-all-process-by-me")
    public List<Object> userGetAllInstance(@RequestParam(required = true) String userId,
                                           @RequestParam(required = false) String userIdCondition,
                                           @RequestParam(required = false) String vacationTypeIdCondition,
                                           @RequestParam(required = false) String vacationStatusCondition) {
        //TODO 获取该用户发起的所有的流程，这里自己之前没有理解到的是流程是由任务构成的，
        //TODO 所谓的审批过程其实是查询出这个流程所有的任务
//        if (StringUtils.isEmpty(userId)) {
//            return null;
//        }
        //这个是根据用户id查询出所有的中间表里面的数据，目的是获取所有的流程id
//        List<ActivitiUser> activitiUsers = activitiUserService.selectAllInstanceByMe(userId);
        List allInstance = new ArrayList();


//        activitiUsers.forEach(activitiUser -> {
//            //这个list其实是一个流程
            List<HistoricActivityInstance> list = processEngine.getHistoryService() // 历史任务Service
                    .createHistoricActivityInstanceQuery() // 创建历史活动实例查询
//                    .processInstanceId(activitiUser.getInstanceId()) // 指定流程实例id
                    .finished() // 查询已经完成的任务
                    .list()
                    .stream().sorted(Comparator.comparing(HistoricActivityInstance::getStartTime))
                    .collect(Collectors.toList());
            List listInstance = new ArrayList();
            //查询出来的所有的节点没法直接返给前端，所以使用map进行处理一遍


            //新增条件查询之后，需要在外层循环打断，一个activitiUser对应于一个流程
//            String startEventProcessInstanceId = activitiUser.getInstanceId();
            //获取发起人是谁
//            User startUserId = activitiUserService.getStartUserId(startEventProcessInstanceId);
//            if (StringUtils.isNotEmpty(userIdCondition)) {
//                if (!userIdCondition.equals(startUserId.getRealName())) {
//                    return;
//                }
//            }
//            String startEventTaskDescription = activitiUserMapper.getStartUserIdByInstanceIdAndTaskId(startEventProcessInstanceId)
//                    .getTaskDescription();
//            log.info("startEventTaskDescription" + startEventTaskDescription);
//            if (StringUtils.isNotEmpty(vacationTypeIdCondition)) {
//                if (!vacationTypeIdCondition.equals((String) JSON.parseObject(startEventTaskDescription).get("vacationTypeId"))) {
//                    return;
//                }
//            }


            list.forEach(l -> {
                log.info("判断" + l.getActivityName() + "==" + "Request approved?");
                if (!"Request approved?".equals(l.getActivityName())) {
                    //一个map就是一个任务节点
                    Map<String, Object> map = new HashMap<>();
                    if ("start event".equals(l.getActivityName())) {

                        map.put("actionId", l.getId());
                        map.put("instanceId", l.getProcessInstanceId());
                        map.put("actionName", l.getActivityName());
//                        map.put("assignee", startUserId.getId());
//                        map.put("assigneeRealName", startUserId.getRealName());
                        map.put("startTime", l.getStartTime());
                        map.put("endTime", l.getEndTime());
                        map.put("taskId", l.getTaskId());

//                        String userIdToUserName = userMapper.selectByPrimaryKey(Long.valueOf((String) (JSON.parseObject(startEventTaskDescription).get("userId")))).getRealName();
//                        log.info("userIdToUserName" + userIdToUserName);
//                        String backupUserIdToBackupUserName = userMapper.selectByPrimaryKey(Long.valueOf((String) (JSON.parseObject(startEventTaskDescription).get("backupUserId")))).getRealName();
//                        log.info("backupUserIdToBackupUserName" + backupUserIdToBackupUserName);
//                        String vacationTypeIdToVacationTypeName = vacationMapper.selectVacationNameByVacationId((String) JSON.parseObject(startEventTaskDescription).get("vacationTypeId")).getVacationName();
//                        log.info("vacationTypeIdToVacationTypeName" + vacationTypeIdToVacationTypeName);
//                        String departmentIdToDepartmentName = departmentMapper.selectByDepartmentCode((String) JSON.parseObject(startEventTaskDescription).get("departmentId")).getDepartmentName();
//                        log.info("departmentIdToDepartmentName" + departmentIdToDepartmentName);

//                        map.put("taskIdDescription", startEventTaskDescription
//                                .replace("\"userId\":\"" + (JSON.parseObject(startEventTaskDescription).get("userId")) + "\"", "\"userId\":\"" + JSON.parseObject(startEventTaskDescription).get("userId") + "," + userIdToUserName + "\"")
//                                .replace("\"backupUserId\":\"" + (JSON.parseObject(startEventTaskDescription).get("backupUserId")) + "\"", "\"backupUserId\":\"" + JSON.parseObject(startEventTaskDescription).get("backupUserId") + "," + backupUserIdToBackupUserName + "\"")
//                                .replace("\"vacationTypeId\":\"" + (JSON.parseObject(startEventTaskDescription).get("vacationTypeId")) + "\"", "\"vacationTypeId\":\"" + JSON.parseObject(startEventTaskDescription).get("vacationTypeId") + "," + vacationTypeIdToVacationTypeName + "\"")
//                                .replace("\"departmentId\":\"" + (JSON.parseObject(startEventTaskDescription).get("departmentId")) + "\"", "\"departmentId\":\"" + JSON.parseObject(startEventTaskDescription).get("departmentId") + "," + departmentIdToDepartmentName + "\"")
//                        );
                    } else {
                        map.put("actionId", l.getId());
                        map.put("instanceId", l.getProcessInstanceId());
                        map.put("actionName", l.getActivityName());
                        map.put("assignee", l.getAssignee());
                        if (l.getAssignee() != null) {
                            if ("project manager".equals(l.getAssignee())) {
                                map.put("assigneeRealName", "项目经理");
                            } else if ("Chairman".equals(l.getAssignee())) {
                                map.put("assigneeRealName", "总经理");
                            } else {
//                                log.info(userMapper.selectByPrimaryKey(Long.valueOf(l.getAssignee())) + "");
//                                String realName = userMapper.selectByPrimaryKey(Long.valueOf(l.getAssignee())).getRealName();
//                                map.put("assigneeRealName", realName);
                            }
                        }
                        map.put("startTime", l.getStartTime());
                        map.put("endTime", l.getEndTime());
                        //由于节点中只有任务id，所以采用了利用任务id查询出任务详情以及任务变量
                        map.put("taskId", l.getTaskId());
                        //查询任务详情
                        if (l.getTaskId() != null) {
                            HistoricTaskInstance historicTaskInstance = processEngine.getHistoryService() // 历史相关Service
                                    .createHistoricTaskInstanceQuery()
//                                    .processInstanceId(activitiUser.getInstanceId())
                                    .finished()
                                    .taskId(l.getTaskId())
                                    .singleResult();
                            map.put("taskIdDescription", historicTaskInstance.getDescription());
//                            map.put("taskIdDescriptionUserIdRealName", userMapper.selectByPrimaryKey(Long.valueOf((String) (JSON.parseObject(historicTaskInstance.getDescription()).get("userId")))).getRealName());
                            map.put("taskIdDurationInMillis", historicTaskInstance.getDurationInMillis());
                            //根据流程id和任务id锁定这个任务是同意了还是拒绝了
//                            ActivitiUser ifApproved = activitiUserService.checkIfApproved(new ActivitiUser(null, l.getAssignee(), activitiUser.getInstanceId(), l.getTaskId(), null));
//                            map.put("taskIdIfApproved", ifApproved.getVacationApproved());
                        }
                    }
                    listInstance.add(map);
                }
            });
            //在流程列表是否完成之前添加一条数据为接下来将要走的任务节点
            List<Task> active = processEngine.getTaskService()
                    .createTaskQuery()
//                    .processInstanceId(activitiUser.getInstanceId())
                    .active()
                    .list();
            log.info("接下来需要走的节点为：" + active.toString());
            Map nextTask = new HashMap();
            if (active.size() == 0) {
                nextTask.put("nextTaskAssigneeRealName", null);

            } else {
                String assignee = active.get(0).getAssignee();
                if ("project manager".equals(assignee)) {
                    nextTask.put("nextTaskAssigneeRealName", "项目经理");
                } else if ("Chairman".equals(assignee)) {
                    nextTask.put("nextTaskAssigneeRealName", "总经理");
                } else {
//                    nextTask.put("nextTaskAssigneeRealName", userMapper.selectByPrimaryKey(Long.valueOf(assignee)).getRealName());
                }
            }
            listInstance.add(nextTask);


            //遍历完成之后，我在列表的最后一个位置在增加一个改流程是否已完成的标志
            Map instanceFinishedFlag = new HashMap();
            String instanceFinishedFlagValue = processEngine.getRuntimeService()
                    .createProcessInstanceQuery()
//                    .processInstanceId(activitiUser.getInstanceId())
                    .singleResult() == null ? "done" : "doing";
            String rejectUserRealName = null;
            if ("done".equals(instanceFinishedFlagValue)) {
//                ActivitiUser checkInstanceFinishedFlag = activitiUserMapper.checkInstanceFinishedFlag(activitiUser.getInstanceId());
//                if (checkInstanceFinishedFlag == null) {
//                    instanceFinishedFlagValue = "vacationApproved";
//                } else {
//                    instanceFinishedFlagValue = "vacationRejected";
//                    rejectUserRealName = userMapper.selectByPrimaryKey(Long.valueOf(checkInstanceFinishedFlag.getUserId())).getRealName();
//                }
            }
//            instanceFinishedFlag.put("instanceId," + activitiUser.getInstanceId(), instanceFinishedFlagValue);
            instanceFinishedFlag.put("rejectUserRealName", rejectUserRealName);
            listInstance.add(instanceFinishedFlag);
            //新增流程发起时间
            Map userStartInstanceTime = new HashMap();
//            userStartInstanceTime.put("userStartInstanceTime", activitiUser.getCreateTime());
            listInstance.add(userStartInstanceTime);

            //1020添加状态的条件查询
            if(StringUtils.isNotEmpty(vacationStatusCondition)){
                if (!vacationStatusCondition.equals(instanceFinishedFlagValue)) {
//                    return;
                }
            }
            allInstance.add(listInstance);
//        });

//        return allInstance;
        return null;
    }



    @GetMapping("/user-get-all-process-by-condition")
    public List<Object> userGetAllInstanceByCondition(@RequestParam(required = true) String userId,
                                                      @RequestParam(required = false) String userIdCondition,
                                                      @RequestParam(required = false) String vacationTypeIdCondition,
                                                      @RequestParam(required = false) String vacationStatusCondition) {
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        //这个是根据用户id查询出所有的中间表里面的数据，目的是获取所有的流程id
//        List<ActivitiUser> activitiUsers = activitiUserService.selectAllInstanceByMeAndPassMe(userId);
        List allInstance = new ArrayList();
//        activitiUsers.forEach(activitiUser -> {
            //这个list其实是一个流程
            List<HistoricActivityInstance> list = processEngine.getHistoryService() // 历史任务Service
                    .createHistoricActivityInstanceQuery() // 创建历史活动实例查询
//                    .processInstanceId(activitiUser.getInstanceId()) // 指定流程实例id
                    .finished() // 查询已经完成的任务
                    .list()
                    .stream().sorted(Comparator.comparing(HistoricActivityInstance::getStartTime))
                    .collect(Collectors.toList());
            List listInstance = new ArrayList();
            //查询出来的所有的节点没法直接返给前端，所以使用map进行处理一遍


            //新增条件查询之后，需要在外层循环打断，一个activitiUser对应于一个流程
//            String startEventProcessInstanceId = activitiUser.getInstanceId();
            //获取发起人是谁
//            User startUserId = activitiUserService.getStartUserId(startEventProcessInstanceId);
//            if (StringUtils.isNotEmpty(userIdCondition)) {
//                if (!userIdCondition.equals(startUserId.getRealName())) {
//                    return;
//                }
//            }
//            String startEventTaskDescription = activitiUserMapper.getStartUserIdByInstanceIdAndTaskId(startEventProcessInstanceId)
//                    .getTaskDescription();
//            if (StringUtils.isNotEmpty(vacationTypeIdCondition)) {
//                if (!vacationTypeIdCondition.equals((String) JSON.parseObject(startEventTaskDescription).get("vacationTypeId"))) {
//                    return;
//                }
//            }
            list.forEach(l -> {
                log.info("判断" + l.getActivityName() + "==" + "Request approved?");
                if (!"Request approved?".equals(l.getActivityName())) {
                    //一个map就是一个任务节点
                    Map<String, Object> map = new HashMap<>();
                    if ("start event".equals(l.getActivityName())) {
                        map.put("actionId", l.getId());
                        map.put("instanceId", l.getProcessInstanceId());
                        map.put("actionName", l.getActivityName());
//                        map.put("assignee", startUserId.getId());
//                        map.put("assigneeRealName", startUserId.getRealName());
                        map.put("startTime", l.getStartTime());
                        map.put("endTime", l.getEndTime());
                        map.put("taskId", l.getTaskId());

//                        log.info("startEventTaskDescription" + startEventTaskDescription);
//                        String userIdToUserName = userMapper.selectByPrimaryKey(Long.valueOf((String) (JSON.parseObject(startEventTaskDescription).get("userId")))).getRealName();
//                        log.info("userIdToUserName" + userIdToUserName);
//                        String backupUserIdToBackupUserName = userMapper.selectByPrimaryKey(Long.valueOf((String) (JSON.parseObject(startEventTaskDescription).get("backupUserId")))).getRealName();
//                        log.info("backupUserIdToBackupUserName" + backupUserIdToBackupUserName);
//                        String vacationTypeIdToVacationTypeName = vacationMapper.selectVacationNameByVacationId((String) JSON.parseObject(startEventTaskDescription).get("vacationTypeId")).getVacationName();
//                        log.info("vacationTypeIdToVacationTypeName" + vacationTypeIdToVacationTypeName);

//                        String departmentIdToDepartmentName = departmentMapper.selectByDepartmentCode((String) JSON.parseObject(startEventTaskDescription).get("departmentId")).getDepartmentName();
//                        log.info("departmentIdToDepartmentName" + departmentIdToDepartmentName);

//                        map.put("taskIdDescription", startEventTaskDescription
//                                .replace("\"userId\":\"" + (JSON.parseObject(startEventTaskDescription).get("userId")) + "\"", "\"userId\":\"" + JSON.parseObject(startEventTaskDescription).get("userId") + "," + userIdToUserName + "\"")
//                                .replace("\"backupUserId\":\"" + (JSON.parseObject(startEventTaskDescription).get("backupUserId")) + "\"", "\"backupUserId\":\"" + JSON.parseObject(startEventTaskDescription).get("backupUserId") + "," + backupUserIdToBackupUserName + "\"")
//                                .replace("\"vacationTypeId\":\"" + (JSON.parseObject(startEventTaskDescription).get("vacationTypeId")) + "\"", "\"vacationTypeId\":\"" + JSON.parseObject(startEventTaskDescription).get("vacationTypeId") + "," + vacationTypeIdToVacationTypeName + "\"")
//                                .replace("\"departmentId\":\"" + (JSON.parseObject(startEventTaskDescription).get("departmentId")) + "\"", "\"departmentId\":\"" + JSON.parseObject(startEventTaskDescription).get("departmentId") + "," + departmentIdToDepartmentName + "\"")
//                        );
                    } else {
                        map.put("actionId", l.getId());
                        map.put("instanceId", l.getProcessInstanceId());
                        map.put("actionName", l.getActivityName());
                        map.put("assignee", l.getAssignee());
                        if (l.getAssignee() != null) {
                            if ("project manager".equals(l.getAssignee())) {
                                map.put("assigneeRealName", "项目经理");
                            } else if ("Chairman".equals(l.getAssignee())) {
                                map.put("assigneeRealName", "总经理");
                            } else {
//                                log.info(userMapper.selectByPrimaryKey(Long.valueOf(l.getAssignee())) + "");
//                                String realName = userMapper.selectByPrimaryKey(Long.valueOf(l.getAssignee())).getRealName();
//                                map.put("assigneeRealName", realName);
                            }
                        }
                        map.put("startTime", l.getStartTime());
                        map.put("endTime", l.getEndTime());
                        //由于节点中只有任务id，所以采用了利用任务id查询出任务详情以及任务变量
                        map.put("taskId", l.getTaskId());
                        //查询任务详情
                        if (l.getTaskId() != null) {
                            HistoricTaskInstance historicTaskInstance = processEngine.getHistoryService() // 历史相关Service
                                    .createHistoricTaskInstanceQuery()
//                                    .processInstanceId(activitiUser.getInstanceId())
                                    .finished()
                                    .taskId(l.getTaskId())
                                    .singleResult();
                            map.put("taskIdDescription", historicTaskInstance.getDescription());
//                            map.put("taskIdDescriptionUserIdRealName", userMapper.selectByPrimaryKey(Long.valueOf((String) (JSON.parseObject(historicTaskInstance.getDescription()).get("userId")))).getRealName());
                            map.put("taskIdDurationInMillis", historicTaskInstance.getDurationInMillis());
                            //根据流程id和任务id锁定这个任务是同意了还是拒绝了
//                            ActivitiUser ifApproved = activitiUserService.checkIfApproved(new ActivitiUser(null, l.getAssignee(), activitiUser.getInstanceId(), l.getTaskId(), null));
//                            map.put("taskIdIfApproved", ifApproved.getVacationApproved());
                        }
                    }
                    listInstance.add(map);
                }
            });

            //在流程列表是否完成之前添加一条数据为接下来将要走的任务节点
            List<Task> active = processEngine.getTaskService()
                    .createTaskQuery()
//                    .processInstanceId(activitiUser.getInstanceId())
                    .active()
                    .list();
            log.info("接下来需要走的节点为：" + active.toString());
            Map nextTask = new HashMap();
            if (active.size() == 0) {
                nextTask.put("nextTaskAssigneeRealName", null);

            } else {
                String assignee = active.get(0).getAssignee();
                if ("project manager".equals(assignee)) {
                    nextTask.put("nextTaskAssigneeRealName", "项目经理");
                } else if ("Chairman".equals(assignee)) {
                    nextTask.put("nextTaskAssigneeRealName", "总经理");
                } else {
//                    nextTask.put("nextTaskAssigneeRealName", userMapper.selectByPrimaryKey(Long.valueOf(assignee)).getRealName());
                }
            }
            listInstance.add(nextTask);


            //遍历完成之后，我在列表的最后一个位置在增加一个改流程是否已完成的标志
            Map instanceFinishedFlag = new HashMap();
            String instanceFinishedFlagValue = processEngine.getRuntimeService()
                    .createProcessInstanceQuery()
//                    .processInstanceId(activitiUser.getInstanceId())
                    .singleResult() == null ? "done" : "doing";
            String rejectUserRealName = null;
//            if ("done".equals(instanceFinishedFlagValue)) {
//                ActivitiUser checkInstanceFinishedFlag = activitiUserMapper.checkInstanceFinishedFlag(activitiUser.getInstanceId());
//                if (checkInstanceFinishedFlag == null) {
                    instanceFinishedFlagValue = "vacationApproved";
//                } else {
                    instanceFinishedFlagValue = "vacationRejected";
//                    rejectUserRealName = userMapper.selectByPrimaryKey(Long.valueOf(checkInstanceFinishedFlag.getUserId())).getRealName();
//                }
//            }
//            instanceFinishedFlag.put("instanceId," + activitiUser.getInstanceId(), instanceFinishedFlagValue);
            instanceFinishedFlag.put("rejectUserRealName", rejectUserRealName);
            listInstance.add(instanceFinishedFlag);
            //新增流程发起时间
            Map userStartInstanceTime = new HashMap();
//            userStartInstanceTime.put("userStartInstanceTime", activitiUser.getCreateTime());
            listInstance.add(userStartInstanceTime);

            //1020添加状态的条件查询
//            if(StringUtils.isNotEmpty(vacationStatusCondition)){
//                if (!vacationStatusCondition.equals(instanceFinishedFlagValue)) {
//                    return;
//                }
//            }
            allInstance.add(listInstance);
//        });

//        return allInstance;
        return null;
    }


//    @ApiOperation(value = "员工上级完成员工请假任务", notes = "员工上级完成员工请假任务")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "userId", value = "员工id，唯一标识，自增", required = true),
//    })
//    @ApiResponses({
//            @ApiResponse(code = 2000, message = "成功"),
//            @ApiResponse(code = 5000, message = "失败,原因未知")
//    })
//    @GetMapping("/user-get-all-process-by-me-tasks")
//    public List<Object> userGetAllInstanceTask(String userId) {
//        if (StringUtils.isEmpty(userId)) {
//            return null;
//        }
//        //这个是根据用户id查询出所有的中间表里面的数据，目的是获取所有的流程id
//        List<ActivitiUser> activitiUsers = activitiUserService.selectAllInstanceByMe(userId);
//        List allInstance = new ArrayList();
//        activitiUsers.forEach(activitiUser -> {
//            List<HistoricTaskInstance> list = processEngine.getHistoryService() // 历史相关Service
//                    .createHistoricTaskInstanceQuery() // 创建历史任务实例查询
//                    .processInstanceId(activitiUser.getInstanceId()) // 用流程实例id查询
//                    .finished() // 查询已经完成的任务
//                    .list();
//            List listInstance = new ArrayList();
//            for (HistoricTaskInstance hti : list) {
//                Map<String, Object> map = new HashMap<>();
//                map.put("taskId", hti.getId());
//                map.put("taskName", hti.getName());
//                map.put("instanceId", hti.getProcessInstanceId());
//                map.put("assignee", hti.getAssignee());
//                map.put("startTime", hti.getStartTime());
//                map.put("endTime", hti.getEndTime());
//                map.put("durationInMillis", hti.getDurationInMillis());
//                map.put("taskDescription", hti.getDescription());
//                map.put("taskClaimTime", hti.getClaimTime());
//                map.put("taskLocalVariables", hti.getTaskLocalVariables());
//                map.put("taskProcessVariables", hti.getProcessVariables());
//                map.put("taskDefinitionKey", hti.getTaskDefinitionKey());
//                listInstance.add(map);
//            }
//            allInstance.add(listInstance);
//        });
//
//        return allInstance;
//    }


    @GetMapping("/user-get-one-process")
    public List<HistoricActivityInstance> userGetOneInstance(String instanceId) {
        //TODO 获取该用户发起的所有的流程，这里自己之前没有理解到的是流程是由任务构成的，
        //TODO 所谓的审批过程其实是查询出这个流程所有的任务
//        if (StringUtils.isEmpty(instanceId)) {
//            return null;
//        }

        List<HistoricActivityInstance> historicActivityInstance = processEngine.getHistoryService() // 历史任务Service
                .createHistoricActivityInstanceQuery() // 创建历史活动实例查询
                .processInstanceId(instanceId) // 指定流程实例id
                .finished() // 查询已经完成的任务
                .list()
                .stream().sorted(Comparator.comparing(HistoricActivityInstance::getStartTime))
                .collect(Collectors.toList());
        List listInstance = new ArrayList();
        //查询出来的所有的节点没法直接返给前端，所以使用map进行处理一遍
        historicActivityInstance.forEach(l -> {
            log.info("判断" + l.getActivityName() + "==" + "Request approved?");
            Map<String, Object> map = new HashMap<>();
            if ("start event".equals(l.getActivityName())) {
                String startEventProcessInstanceId = l.getProcessInstanceId();
//                User startUserId = activitiUserService.getStartUserId(startEventProcessInstanceId);
                map.put("actionId", l.getId());
                map.put("instanceId", l.getProcessInstanceId());
                map.put("actionName", l.getActivityName());
//                map.put("assignee", startUserId.getId());
//                map.put("assigneeRealName", startUserId.getRealName());
                map.put("startTime", l.getStartTime());
                map.put("endTime", l.getEndTime());
                map.put("taskId", l.getTaskId());
            }
            if (!"Request approved?".equals(l.getActivityName()) && !"start event".equals(l.getActivityName())) {
                //一个map就是一个任务节点
                map.put("actionId", l.getId());
                map.put("instanceId", l.getProcessInstanceId());
                map.put("actionName", l.getActivityName());
                map.put("assignee", l.getAssignee());
                if (l.getAssignee() != null) {
                    if ("project manager".equals(l.getAssignee())) {
                        map.put("assigneeRealName", "项目经理");
                    } else if ("Chairman".equals(l.getAssignee())) {
                        map.put("assigneeRealName", "总经理");
                    } else {
//                        log.info(userMapper.selectByPrimaryKey(Long.valueOf(l.getAssignee())) + "");
//                        String realName = userMapper.selectByPrimaryKey(Long.valueOf(l.getAssignee())).getRealName();
//                        map.put("assigneeRealName", realName);
                    }
                }
                map.put("startTime", l.getStartTime());
                map.put("endTime", l.getEndTime());
                //由于节点中只有任务id，所以采用了利用任务id查询出任务详情以及任务变量
                map.put("taskId", l.getTaskId());
                //查询任务详情
                if (l.getTaskId() != null) {
                    HistoricTaskInstance historicTaskInstance = processEngine.getHistoryService() // 历史相关Service
                            .createHistoricTaskInstanceQuery()
                            .processInstanceId(instanceId)
                            .finished()
                            .taskId(l.getTaskId())
                            .singleResult();
                    map.put("taskIdDescription", historicTaskInstance.getDescription());
//                    map.put("taskIdDescriptionUserIdRealName", userMapper.selectByPrimaryKey(Long.valueOf((String) (JSON.parseObject(historicTaskInstance.getDescription()).get("userId")))).getRealName());
//                    map.put("taskIdDurationInMillis", historicTaskInstance.getDurationInMillis());
//                    根据流程id和任务id锁定这个任务是同意了还是拒绝了
//                    ActivitiUser ifApproved = activitiUserService.checkIfApproved(new ActivitiUser(null, l.getAssignee(), instanceId, l.getTaskId(), null));
//                    map.put("taskIdIfApproved", ifApproved.getVacationApproved());
                }
            }
            if (map.size() != 0) {
                listInstance.add(map);
            }
        });
        //在流程列表是否完成之前添加一条数据为接下来将要走的任务节点
        List<Task> active = processEngine.getTaskService()
                .createTaskQuery()
                .processInstanceId(instanceId)
                .active()
                .list();
        log.info("接下来需要走的节点为：" + active.toString());
        Map nextTask = new HashMap();
        if (active.size() == 0) {
            nextTask.put("nextTaskAssigneeRealName", null);

        } else {
            String assignee = active.get(0).getAssignee();
            if ("project manager".equals(assignee)) {
                nextTask.put("nextTaskAssigneeRealName", "项目经理");
            } else if ("Chairman".equals(assignee)) {
                nextTask.put("nextTaskAssigneeRealName", "总经理");
            } else {
//                nextTask.put("nextTaskAssigneeRealName", userMapper.selectByPrimaryKey(Long.valueOf(assignee)).getRealName());
            }
        }
        listInstance.add(nextTask);

        //1.创建运行服务对象
        //2.创建查询流程实例
        //3.获取流程实例ID
        //4.查询是否存在进行中的流程
        ProcessInstance processInstance = processEngine.getRuntimeService()
                .createProcessInstanceQuery()
                .processInstanceId(instanceId)
                .singleResult();


        //5-1.没有说明已经结束
        Map instanceFinishedFlag = new HashMap();
        if (processInstance == null) {
            String rejectUserRealName = null;
            String instanceFinishedFlagValue = null;
//            ActivitiUser checkInstanceFinishedFlag = activitiUserMapper.checkInstanceFinishedFlag(instanceId);
//            if (checkInstanceFinishedFlag == null) {
//                instanceFinishedFlagValue = "vacationApproved";
//            } else {
//                instanceFinishedFlagValue = "vacationRejected";
//                rejectUserRealName = userMapper.selectByPrimaryKey(Long.valueOf(checkInstanceFinishedFlag.getUserId())).getRealName();
//            }
            instanceFinishedFlag.put("instanceId," + instanceId, instanceFinishedFlagValue);
            instanceFinishedFlag.put("rejectUserRealName", rejectUserRealName);
            log.info("instanceId为==" + instanceId + "==的流程已结束");
        }
        //5-2.有说明正在进行
        else {
            instanceFinishedFlag.put("instanceId," + instanceId, "doing");
            log.info("instanceId为==" + instanceId + "==的流程正在运行");
        }


        listInstance.add(instanceFinishedFlag);
        return listInstance;

    }

}
