import React, {Fragment, useEffect, useState} from 'react';

import LogLine from '@/request/LogLine';
import Environment from '@/environment/Environment';
import Request from '@/request/RequestType';
import RequestLogsContent from '@/request/RequestLogsContent';

interface Props {
    environment: Environment,
    request: Request
}

const RequestLogs = (props: Props) => {

    const [logs, setLogs] = useState<LogLine[]>();
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string>(null);

    const [environment, setEnvironment] = useState(props.environment);
    const [request, setRequest] = useState(props.request);

    useEffect(() => {
        setEnvironment(props.environment);
    }, [props.environment]);

    useEffect(() => {
        setRequest(props.request);
    }, [props.request]);

    useEffect(() => {
        setIsLoading(true);

        fetch(`/api/v1/environments/${environment.id}/requests/${request.id}/logs`)
            .then(res => res.json())
            .then(data => {
                setLogs(data);
                setIsLoading(false);
            })
            .catch(err => setError('Unexpected error while fetching Habit environment.'));
    }, [environment, request]);

    return (
        <Fragment>
            {request && logs &&
                <RequestLogsContent request={request} logs={logs} />
            }
        </Fragment>
    );
};

export default RequestLogs;