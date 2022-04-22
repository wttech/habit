import React, { Fragment, useEffect, useState, useRef } from 'react';
import { throttle } from 'lodash-es';

import useStateFromProp from '@/utils/useStateFromProp';
import RequestLogsContent from '@/request/RequestLogsContent';
import Environment from '@/environment/Environment';
import RequestType from '@/request/RequestType';

interface Props {
    environment: Environment,
    request: RequestType
}

const ExchangeLogs = (props: Props) => {

    const [environment, setEnvironment] = useStateFromProp(props.environment);
    const [request, setRequest] = useStateFromProp(props.request);
    const [logs, setLogs] = useState<any[]>();

    const logCache = useRef([]);

    useEffect(() => {
        setEnvironment(props.environment);
    }, [props.environment]);

    useEffect(() => {
        setRequest(props.request);
    }, [props.request]);

    const setLogsWithLimit = throttle(() => setLogs(logCache.current), 1000, { trailing: true });

    useEffect(() => {
        const logsEndpoint = `/api/v1/environments/${environment.id}/requests/${request.id}/logstream`;
        const source = new EventSource(logsEndpoint);
        logCache.current = [];
        source.onmessage = (e) => {
            const newLog = JSON.parse(e.data);
            const duplicateIndex = logCache.current.findIndex(oldLog => oldLog.id === newLog.id);
            if (duplicateIndex === -1) {
                logCache.current = [...logCache.current];
                logCache.current.push(newLog);
                setLogsWithLimit();
            }
        };
        source.addEventListener('complete', e => {
            source.close();
        });
        return () => { source.close(); setLogsWithLimit.cancel(); }
    }, [environment, request]);

    return (
        <Fragment>
            {request && logs &&
                <RequestLogsContent request={request} logs={logs} />
            }
        </Fragment>
    );
};

export default ExchangeLogs;
