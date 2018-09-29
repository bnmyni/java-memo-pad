## 数据计算相关的表
#
SELECT * FROM mes_alarminfo_setting;
# 数据预测
SELECT * FROM mes_bigdata_forecast_setting;
# 
SELECT * FROM mes_beforeafter_setting;
# 小区列表刷新配置表
SELECT * FROM mes_celllist_ref;
# 下一时刻数据预测配置
SELECT * FROM mes_fcastnext_setting;
#
SELECT * FROM mes_fcast_setting;
# 关键性指标计算模型
SELECT * FROM mes_kpical_set;
#重叠覆盖度配置表
SELECT * FROM mes_over_setting;
# 
SELECT * FROM mes_performancecal_setting;
# 节能小区监控实体模型
SELECT * FROM mes_montior_setting;
# 休眠小区执行流程
SELECT * FROM mes_sleepexe_setting;

#
SELECT * FROM mes_sleepsel_setting;
#
SELECT * FROM mes_td_off_kpi_setting;
#
SELECT * FROM mes_td_off_setting;
###　采集相关表
# 业务调整指令存储对象 ,对应aos_adjust_command数据表中存储的内容
SELECT * FROM mes_adjust_command;
SELECT * FROM mes_business_log_info;
# 指令字典对象 对应aos_command_map数据表中存储的内容 该对象存储某个指令对应的一些公共配置，
# 如 结束标识、成功标识、失败标识、成功及失败之后需要执行的动作 等
SELECT * FROM mes_command_map;
# 指令模板对象，用于存储系统内需要生成的指令格式，通过通配符配置
SELECT * FROM mes_command_template;
# 数据库查询采集任务配置对象，对应数据表 aos_db_command
SELECT * FROM mes_db_command;
# 配置通过FTP采集数据的任务实体对象
SELECT * FROM mes_ftp_command;
# 所有的ｆｔｐ服务器连接信息
SELECT * FROM mes_ftp_servers;
# cm telnet 命令配置
SELECT * FROM mes_log_command;
# 配置设备生产商\版本等信息
SELECT * FROM mes_manufacturers;
# 
SELECT * FROM mes_oper_log;
# 服务器对象信息
SELECT * FROM mes_servers;
# 服务固化操作配置，主要配置一些针对服务器固化的操作内容，如登录、退出等  对应数据表   aos_server_command
SELECT * FROM mes_server_command;