package com.cmiot.gateway.extensions.logging;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.StringJoiner;

import static com.cmiot.gateway.extensions.logging.LoggerFormat.Event.*;

@Component
@SuppressWarnings("Duplicates")
public class FileLogger implements ILogger {

    @Override
    public void logInnerInfo(Logger log, LoggerFormat.Action action, String desc) {
        logInnerInfo(log, action, null, null, desc);
    }

    @Override
    public void logInnerInfo(Logger log, LoggerFormat.Action action, String svcId, String instName, String desc) {
        StringJoiner joiner = new StringJoiner(",");
        if (svcId != null) {
            joiner.add("svcId:" + svcId);
        }
        if (instName != null) {
            joiner.add("instName:" + instName);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logInfo(log, INNER, action, joiner.toString());
    }

    @Override
    public void logInnerWarn(Logger log, LoggerFormat.Action action, String desc) {
        logInnerWarn(log, action, null, null, desc);
    }

    @Override
    public void logInnerWarn(Logger log, LoggerFormat.Action action, String svcId, String instName, String desc) {
        StringJoiner joiner = new StringJoiner(",");
        if (svcId != null) {
            joiner.add("svcId:" + svcId);
        }
        if (instName != null) {
            joiner.add("instName:" + instName);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logWarn(log, INNER, action, joiner.toString());
    }

    @Override
    public void logInnerError(Logger log, LoggerFormat.Action action, String desc, Throwable e) {
        logInnerError(log, action, null, null, desc, e);
    }

    @Override
    public void logInnerError(Logger log, LoggerFormat.Action action, String svcId, String instName, String desc, Throwable e) {
        StringJoiner joiner = new StringJoiner(",");
        if (svcId != null) {
            joiner.add("svcId:" + svcId);
        }
        if (instName != null) {
            joiner.add("instName:" + instName);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logError(log, INNER, action, joiner.toString(), e);
    }

    @Override
    public void logProtocolHubInfo(Logger log, LoggerFormat.Action action, String desc) {
        logProtocolHubInfo(log, action, null, desc);
    }

    @Override
    public void logProtocolHubInfo(Logger log, LoggerFormat.Action action, String extras, String desc) {
        StringJoiner joiner = new StringJoiner(",");
        if (extras != null) {
            joiner.add(extras);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logInfo(log, PROTOCOL_HUB, action, joiner.toString());
    }

    @Override
    public void logProtocolHubWarn(Logger log, LoggerFormat.Action action, String desc) {
        logProtocolHubWarn(log, action, null, desc);
    }

    @Override
    public void logProtocolHubWarn(Logger log, LoggerFormat.Action action, String extras, String desc) {
        StringJoiner joiner = new StringJoiner(",");
        if (extras != null) {
            joiner.add(extras);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logWarn(log, PROTOCOL_HUB, action, joiner.toString());
    }

    @Override
    public void logProtocolHubError(Logger log, LoggerFormat.Action action, String desc, Throwable e) {
        logProtocolHubError(log, action, null, desc, e);
    }

    @Override
    public void logProtocolHubError(Logger log, LoggerFormat.Action action, String extras, String desc, Throwable e) {
        StringJoiner joiner = new StringJoiner(",");
        if (extras != null) {
            joiner.add(extras);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logError(log, PROTOCOL_HUB, action, joiner.toString(), e);
    }

    @Override
    public void logPxyConnInfo(Logger log, LoggerFormat.Action action, String desc, String proxyId) {
        logPxyConnInfo(log, action, desc, proxyId, null);
    }

    @Override
    public void logPxyConnInfo(Logger log, LoggerFormat.Action action, String desc, String proxyId, String extras) {
        StringJoiner joiner = new StringJoiner(",");
        if (proxyId != null) {
            joiner.add("proxyId:" + proxyId);
        }
        if (extras != null) {
            joiner.add(extras);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logInfo(log, GW_PROXY, action, joiner.toString());
    }

    @Override
    public void logPxyConnWarn(Logger log, LoggerFormat.Action action, String desc, String proxyId) {
        logPxyConnWarn(log, action, desc, proxyId, null);
    }

    @Override
    public void logPxyConnWarn(Logger log, LoggerFormat.Action action, String desc, String proxyId, String extras) {
        StringJoiner joiner = new StringJoiner(",");
        if (proxyId != null) {
            joiner.add("proxyId:" + proxyId);
        }
        if (extras != null) {
            joiner.add(extras);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logWarn(log, GW_PROXY, action, joiner.toString());
    }

    @Override
    public void logPxyConnError(Logger log, LoggerFormat.Action action, String desc, String proxyId, Throwable e) {
        logPxyConnError(log, action, desc, proxyId, null, e);
    }

    @Override
    public void logPxyConnError(Logger log, LoggerFormat.Action action, String desc, String proxyId, String extras, Throwable e) {
        StringJoiner joiner = new StringJoiner(",");
        if (proxyId != null) {
            joiner.add("proxyId:" + proxyId);
        }
        if (extras != null) {
            joiner.add(extras);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logError(log, GW_PROXY, action, joiner.toString(), e);
    }

    @Override
    public void logCtrlConnInfo(Logger log, LoggerFormat.Action action, String svcId, String instName, String desc) {
        logCtrlConnInfo(log, action, svcId, instName, desc, null);
    }

    @Override
    public void logCtrlConnInfo(Logger log, LoggerFormat.Action action, String svcId, String instName, String desc, String extras) {
        StringJoiner joiner = new StringJoiner(",");
        if (svcId != null) {
            joiner.add("svcId:" + svcId);
        }
        if (instName != null) {
            joiner.add("instName:" + instName);
        }
        if (extras != null) {
            joiner.add(extras);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logInfo(log, GW_CTRL, action, joiner.toString());
    }

    @Override
    public void logCtrlConnWarn(Logger log, LoggerFormat.Action action, String svcId, String instName, String desc) {
        logCtrlConnWarn(log, action, svcId, instName, desc, null);
    }

    @Override
    public void logCtrlConnWarn(Logger log, LoggerFormat.Action action, String svcId, String instName, String desc, String extras) {
        StringJoiner joiner = new StringJoiner(",");
        if (svcId != null) {
            joiner.add("svcId:" + svcId);
        }
        if (instName != null) {
            joiner.add("instName:" + instName);
        }
        if (extras != null) {
            joiner.add(extras);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logWarn(log, GW_CTRL, action, joiner.toString());
    }

    @Override
    public void logCtrlConnError(Logger log, LoggerFormat.Action action, String svcId, String instName, String desc, Throwable e) {
        logCtrlConnError(log, action, svcId, instName, desc, null, e);
    }

    @Override
    public void logCtrlConnError(Logger log, LoggerFormat.Action action, String svcId, String instName, String desc, String extras, Throwable e) {
        StringJoiner joiner = new StringJoiner(",");
        if (svcId != null) {
            joiner.add("svcId:" + svcId);
        }
        if (instName != null) {
            joiner.add("instName:" + instName);
        }
        if (extras != null) {
            joiner.add(extras);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logError(log, GW_CTRL, action, joiner.toString(), e);
    }

    @Override
    public void logDevInfo(Logger log, LoggerFormat.Action action, Long pid, String did, String desc) {
        logDevInfo(log, action, pid, did, desc, null);
    }

    @Override
    public void logDevInfo(Logger log, LoggerFormat.Action action, Long pid, String did, String desc, String extras) {
        StringJoiner joiner = new StringJoiner(",");
        if (pid != null) {
            joiner.add("pid:" + pid);
        }
        if (did != null) {
            joiner.add("did:" + did);
        }
        if (extras != null) {
            joiner.add(extras);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logInfo(log, DEV, action, joiner.toString());
    }


    @Override
    public void logDevWarn(Logger log, LoggerFormat.Action action, Long pid, String did, String desc) {
        logDevWarn(log, action, pid, did, desc, null);
    }

    @Override
    public void logDevWarn(Logger log, LoggerFormat.Action action, Long pid, String did, String desc, String extras) {
        StringJoiner joiner = new StringJoiner(",");
        if (pid != null) {
            joiner.add("pid:" + pid);
        }
        if (did != null) {
            joiner.add("did:" + did);
        }
        if (extras != null) {
            joiner.add(extras);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logWarn(log, DEV, action, joiner.toString());
    }

    @Override
    public void logDevError(Logger log, LoggerFormat.Action action, Long pid, String did, String desc, Throwable e) {
        logDevError(log, action, pid, did, desc, null, e);
    }

    @Override
    public void logDevError(Logger log, LoggerFormat.Action action, Long pid, String did, String desc, String extras, Throwable e) {
        StringJoiner joiner = new StringJoiner(",");
        if (pid != null) {
            joiner.add("pid:" + pid);
        }
        if (did != null) {
            joiner.add("did:" + did);
        }
        if (extras != null) {
            joiner.add(extras);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logError(log, DEV, action, joiner.toString(), e);
    }


    @Override
    public void logMetricInfo(Logger log, LoggerFormat.Action action, String svcId, String instName, String extras, String desc) {
        StringJoiner joiner = new StringJoiner(",");
        if (svcId != null) {
            joiner.add("svcId:" + svcId);
        }
        if (instName != null) {
            joiner.add("instName:" + instName);
        }
        if (extras != null) {
            joiner.add(extras);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logInfo(log, METRIC, action, joiner.toString());
    }


    private void logInfo(Logger log, LoggerFormat.Event event, LoggerFormat.Action action, String content) {
        log.info("{} {} {}", event.get(), action.get(), content);
    }

    private void logWarn(Logger log, LoggerFormat.Event event, LoggerFormat.Action action, String content) {
        log.warn("{} {} {}", event.get(), action.get(), content);
    }

    private void logError(Logger log, LoggerFormat.Event event, LoggerFormat.Action action, String content, Throwable e) {
        log.error("{} {} {}", event.get(), action.get(), content, e);
    }


}
