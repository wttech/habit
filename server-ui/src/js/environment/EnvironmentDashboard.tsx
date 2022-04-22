import React, {Fragment, useEffect, useState} from "react";
import {Link, RouteComponentProps} from '@reach/router';

import RequestGraph from '@/request/RequestGraphType';
import Environment from '@/environment/Environment';
import Loader from '@/components/Loader';
import ErrorMessage from '@/components/ErrorMessage';

interface Props extends RouteComponentProps {
    environment: Environment,
    isLoading: boolean,
    error: string
}

const EnvironmentDashboard = (props: Props) => {

    const { environment, isLoading, error } = props;

    const envId = environment ? environment.id : null;

    const [isRequestLoading, setIsRequestLoading] = useState(false);
    const [requestLoadError, setRequestLoadError] = useState(null);
    const [requests, setRequests] = useState([]);

    useEffect(() => {
        if (envId) {
            setIsRequestLoading(true);

            fetch(`/api/v1/environments/${envId}/requests?count=5`)
                .then(res => res.json())
                .then(data => {
                    setRequests(data);
                    setIsRequestLoading(false);
                })
                .catch(err => setRequestLoadError('Could not load requests.'));
        }
    }, [envId]);


    const getFormattedTime = (timestamp: string) => {
        const date = new Date(timestamp);
        return date.toLocaleString();
    };

    const lastResponse = (graph: RequestGraph) => {
        if (graph.subrequests.length > 0) {
            return graph.subrequests[graph.subrequests.length - 1];
        }
        return null;
    };

    return (
        <Fragment>
            <Loader status={isLoading} />
            {error && <ErrorMessage title="Error" message={error} />}
            {environment && 
            <Fragment>
                <div className="row">
                    <div className="col">
                        <div className="card">
                            <div className="card-body">
                                <div className="card-title last-requests-title">
                                    <span className="h5">Last 5 requests</span>
                                    <Link to="requests" className="btn btn-primary">View all</Link>
                                </div>
                                <Loader status={isRequestLoading} />
                                {!isRequestLoading && 
                                    <Fragment>
                                        <div>
                                        {requests.map(({ id, start, graph }) => (
                                            <Link
                                                key={id}
                                                to={`/environments/${environment.id}/requests/${id}`}
                                                className={`list-group-item list-group-item-action`}>
                                                <div className="d-flex w-100 justify-content-between">
                                                    <span>{graph.subrequests[0].request.method} {graph.subrequests[0].request.protocol}://{graph.subrequests[0].request.host}:{graph.subrequests[0].request.port}{graph.subrequests[0].request.fullPath}</span>
                                                </div>
                                                {lastResponse(graph).response &&
                                                    <div className="d-flex w-100 justify-content-between">
                                                        <span>{lastResponse(graph).response.statusCode} {lastResponse(graph).response.reason}</span>    
                                                    </div>
                                                }
                                                {lastResponse(graph).error &&
                                                    <span>Error code: {lastResponse(graph).error.code}</span>
                                                }
                                                <div className="d-flex w-100 justify-content-between">
                                                    <small className="text-muted">{id}</small>
                                                    <small className="text-muted">{getFormattedTime(start)}</small>
                                                </div>
                                            </Link>
                                        ))}
                                        </div>
                                    </Fragment>
                                }
                            </div>
                        </div>
                    </div>
                    <div className="col">
                        <div className="card text-center">
                            <div className="card-body">
                                <Link to="craft" className="btn btn-primary">Craft new request</Link>
                            </div>
                        </div>
                    </div>
                    <div className="col">
                        <div className="card card-dark-mode">
                            <div className="card-body">
                                <h5 className="card-title">Configuration</h5>
                                <pre>
                                    {JSON.stringify(environment.configuration, undefined, 2)}
                                </pre>
                            </div>
                        </div>
                    </div>
                </div>
            </Fragment>
            }
        </Fragment>
    );
};

export default EnvironmentDashboard;
