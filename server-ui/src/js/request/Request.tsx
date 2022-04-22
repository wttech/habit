import React, {Fragment, useEffect, useState} from 'react';
import { RouteComponentProps } from "@reach/router";

import Loader from '@/components/Loader';
import RequestGraph from '@/request/RequestGraph';
import RequestLogs from '@/request/RequestLogs';
import Environment from '@/environment/Environment';

interface Props extends RouteComponentProps {
    environment: Environment,
    reqId?: string
}

const Request = (props: Props) => {
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);
    const [request, setRequest] = useState(null);

    const [environment, setEnvironment] = useState(props.environment);
    const [reqId, setReqId] = useState(props.reqId);

    useEffect(() => {
        setEnvironment(props.environment);
    }, [props.environment]);

    useEffect(() => {
        setReqId(props.reqId);
    }, [props.reqId]);

    useEffect(() => {
        if (isValid()) {
            setIsLoading(true);
            setRequest(null);

            fetch(`/api/v1/environments/${environment.id}/requests/${reqId}`)
                .then(res => res.json())
                .then(data => {
                    setRequest(data);
                    setIsLoading(false);
                })
                .catch(err => setError('Unexpected error while fetching Habit environment request details.'));
        }
    }, [environment, reqId]);

    const isValid = () => {
        return environment && reqId;
    };


    return isValid()
        ? <>
            <Loader status={isLoading} />
            {environment && request && <>
                    <h2 className="heading">Graph:</h2>
                    <RequestGraph graph={request.graph} />
                    <h2 className="heading">Logs:</h2>
                    <RequestLogs environment={environment} request={request} />
                </>
            }
        </>
        : <></>;
            

};

export default Request;
