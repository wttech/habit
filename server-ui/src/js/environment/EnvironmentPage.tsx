import React, {Fragment, useEffect, useState, useRef} from 'react';
import {Router, RouteComponentProps} from "@reach/router";
import {Row, Col} from 'react-bootstrap'

import RequestCraft from '@/craft/RequestCraft/RequestCraft';
import ErrorMessage from '@/components/ErrorMessage';
import RequestList from '@/request/RequestList';
import Environment from '@/environment/Environment';
import EnvironmentHeader from '@/components/EnvironmentHeader/EnvironmentHeader';
import EnvironmentDetails from '@/environment/EnvironmentDetails';
import EnvironmentDashboard from '@/environment/EnvironmentDashboard';

interface Props extends RouteComponentProps {
    envId?: string
}

const EnvironmentPage = (props: Props) => {
    const [environment, setEnvironment] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);

    const environmentRef = useRef<Environment>();

    const { envId } = props;

    useEffect(() => {
        setIsLoading(true);

        fetch(`/api/v1/environments/${envId}`)
            .then(res => res.json())
            .then(data => {
                handleEnvironmentUpdate(data);
                setIsLoading(false);
            })
            .catch(err => setError('Unexpected error while fetching Habit environment.'));
    }, []);

    const handleEnvironmentUpdate = (newEnvironmentData: Environment) => {
        setEnvironment(newEnvironmentData);
        environmentRef.current = newEnvironmentData;
    };

    useEffect(() => {
        const stateChangeEndpoint = `/api/v1/environments/${envId}`;
        const source = new EventSource(stateChangeEndpoint);
        source.onmessage = (e) => {
            const newEvent = JSON.parse(e.data);

            if (newEvent.timestamp > environmentRef.current.lastModified) {
                handleEnvironmentUpdate(newEvent.environment);
            }
        };
        return () => { source.close(); }
    }, [envId]);

    return (
        <Fragment>
            {error && <ErrorMessage title="Error" message={error} />}
            <EnvironmentHeader environment={environment} />
            <div className="container-fluid pt-3 pb-3">
                <Row>
                    <Col>
                        <Router primary={false}>
                            <EnvironmentDashboard path="/" environment={environment} isLoading={isLoading} error={error}/>
                            <RequestList path="/requests/*" environment={environment} />
                            <EnvironmentDetails path="/details" environment={environment} isLoading={isLoading} error={error} />
                            <RequestCraft path="/craft" environment={environment} />
                        </Router>
                    </Col>
                </Row>
            </div>
        </Fragment>
    );
};

export default EnvironmentPage;