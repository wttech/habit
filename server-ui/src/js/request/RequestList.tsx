import React, {Fragment, useEffect, useState} from 'react';
import {Link, Router, RouteComponentProps} from "@reach/router";

import Loader from '@/components/Loader';
import Request from '@/request/Request';

import RequestType from '@/request/RequestType';
import RequestGraph from '@/request/RequestGraphType';
import Environment from '@/environment/Environment';
import ErrorMessage from '@/components/ErrorMessage';

interface Props extends RouteComponentProps {
    environment: Environment
}

const RequestList = (props: Props) => {
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);
    const [requests, setRequests] = useState([]);
    const [selectedRequest, setSelectedRequest] = useState(null);
    const [environment, setEnvironment] = useState(props.environment);

    useEffect(() => {
        setEnvironment(props.environment);
    }, [props.environment]);

    useEffect(() => {
        if (environment) {
            setIsLoading(true);

            fetch(`/api/v1/environments/${environment.id}/requests`)
                .then(res => res.json())
                .then(data => {
                    setRequests(data);
                    setIsLoading(false);
                })
                .catch(err => setError('Unexpected error while fetching Habit environment requests.'));
        }
    }, [environment]);

    const getFormattedTime = (timestamp: string) => {
        const date = new Date(timestamp);
        return date.toLocaleString();
    }

    const lastResponse = (graph: RequestGraph) => {
        if (graph.subrequests.length > 0) {
            return graph.subrequests[graph.subrequests.length - 1];
        }
        return null;
    }

    const handleRequestSelection = (event: any, request: RequestType) => {
        setSelectedRequest(request);
    }

    return (
        <Fragment>
            <Loader status={isLoading} />
            {error && <ErrorMessage title="Error" message={error} />}
            <div className="row fullscreen">
                <div className="col-sm-3 request-list">
                    <div className="list-group">
                        {requests.map((request) => (
                            <Link onClick={(e) => handleRequestSelection(e, request)}
                                to={request.id}
                                key={request.id}
                                className={`list-group-item list-group-item-action`}>
                                <div className="">
                                    <span>{request.graph.subrequests[0].request.protocol}://{request.graph.subrequests[0].request.host}:{request.graph.subrequests[0].request.port}</span>
                                </div>
                                <div>
                                    {request.graph.subrequests[0].request.method} {request.graph.subrequests[0].request.fullPath}
                                </div>

                                {lastResponse(request.graph).response &&
                                    <div>
                                        <span>{lastResponse(request.graph).response.statusCode} {lastResponse(request.graph).response.reason}</span>    
                                    </div>
                                }
                                {lastResponse(request.graph).error &&
                                    <span>Error code: {lastResponse(request.graph).error.code}</span>
                                }
                                <div>
                                    <small className="text-muted">{getFormattedTime(request.start)}</small>
                                </div>
                            </Link>
                        ))}
                    </div>
                </div>
                <div className="col-sm-9 h-100">
                    <div className="request-details">
                        <Router primary={false}>
                            <Request path="/:reqId" environment={environment} />
                        </Router>
                    </div>
                </div>
            </div>
        </Fragment>
    );
}

export default RequestList;