package com.tcl.update.logstatistic;

/**
 * Created by fanyang.sz on 2016/11/30.
 */

public class LogStatisticsKey {

    public final static int LogEventID = 1; // apk的操作日志key为1
    public final static int LogException = 2; // 为日活统计的日志key为2
    public final static int LogEventSelf = 3; // 静默弹窗升级和自动检查升级的日志key为3


    public final static int operType_Download = 1; // 下载
    public final static int operType_NewInstall = 2; // 条件：needInstall=true
    public final static int operType_Update = 3; // 条件：needInstall=false
    public final static int opertype_tips = 4; // 提醒用户升级

    public final static int operResult_Success = 1;// 执行结果成功
    public final static int operResult_Fail = 0;// 执行结果失败

}
