# activiti-done
activiti请假案例完整版代码

#### 难点一：流程中的变量取不出来
发起流程的时候，会传入相关的流程变量，这些流程变量在以后的节点可以使用${}这个表达式获取到变量值，而且经过我的测试即便是使用双引号括起来也是可以取到值的，
![image](https://github.com/GitHub-se7en/activiti-done/blob/master/images/start-event.png)


![image](https://github.com/GitHub-se7en/activiti-done/blob/master/images/user-task.png)


但是就是因为这一点，导致后面查询历史变量的时候出现问题，先看下面这个图

![image](https://github.com/GitHub-se7en/activiti-done/blob/master/images/taskid-null.png)

这个是activiti的历史变量表，由于上面的操作，导致历史变量表里面taskid为null，这也导致了，查询某一个流程里面的task的变量是查询不到的，经过查询activiti的资料，activiti中的变量有三种作用域，一种是instance范围的，一种是task范围的，后来我把这个变量限制到task的时候，确实是有taskid了，但是下一个task根本取不到上一个task的值，而这是不能允许的，因为我需要上一个task的变量作为网关的判断条件

#### 解决方式：新建一张表，存储每个task的变量


![image](https://github.com/GitHub-se7en/activiti-done/blob/master/images/activiti-user.png)     

本来一开始我没有想建立中间表，但是权衡在activiti的表里面插入数据和在自己的表里面插入数据，果断选择了新建一张表，这张表里面只存储了同意与否，因为前段需要显示每个节点的审批人的意见，所以只存了这一个变量

#### 难点二：处理数据
下面的整套代码都是清洗数据的代码，大体过程是这样的，首先利用createHistoricActivityInstanceQuery()这个方法查询出这个流程的所有活动，记住活动和任务是不一样的，

由于节点中只有taskid，并没有任务详情，所以我使用这个方法createHistoricTaskInstanceQuery()查询出这个任务的详情是什么，为以后做准备，可能会用到这个


    @GetMapping("/user-get-one-process")
    public List<HistoricActivityInstance> userGetOneInstance(String instanceId) {
        if (StringUtils.isEmpty(instanceId)) {
            return null;
        }

        List<HistoricActivityInstance> historicActivityInstance = processEngine.getHistoryService() // 历史任务Service
                .createHistoricActivityInstanceQuery() // 创建历史活动实例查询
                .processInstanceId(instanceId) // 指定流程实例id
                .finished() // 查询已经完成的任务
                .list();
        List listInstance = new ArrayList();
        //查询出来的所有的节点没法直接返给前端，所以使用map进行处理一遍
        historicActivityInstance.forEach(l -> {
            log.info("判断" + l.getActivityName() + "==" + "Request approved?");
            Map<String, Object> map = new HashMap<>();
            if ("start event".equals(l.getActivityName())) {
                String startEventProcessInstanceId = l.getProcessInstanceId();
                User startUserId = activitiUserService.getStartUserId(startEventProcessInstanceId);
                map.put("actionId", l.getId());
                map.put("instanceId", l.getProcessInstanceId());
                map.put("actionName", l.getActivityName());
                map.put("assignee", startUserId.getId());
                map.put("assigneeRealName", startUserId.getRealName());
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
                        log.info(userMapper.selectByPrimaryKey(Long.valueOf(l.getAssignee())) + "");
                        String realName = userMapper.selectByPrimaryKey(Long.valueOf(l.getAssignee())).getRealName();
                        map.put("assigneeRealName", realName);
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
                    map.put("taskIdDescriptionUserIdRealName", userMapper.selectByPrimaryKey(Long.valueOf((String) (JSON.parseObject(historicTaskInstance.getDescription()).get("userId")))).getRealName());
                    map.put("taskIdDurationInMillis", historicTaskInstance.getDurationInMillis());
                    //根据流程id和任务id锁定这个任务是同意了还是拒绝了
                    ActivitiUser ifApproved = activitiUserService.checkIfApproved(new ActivitiUser(null, l.getAssignee(), instanceId, l.getTaskId(), null));
                    map.put("taskIdIfApproved", ifApproved.getVacationApproved());
                }
            }
            if (map.size()!=0) {
                listInstance.add(map);
            }
        });
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
            instanceFinishedFlag.put("instanceId," + instanceId, "done");
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


#### 待办
