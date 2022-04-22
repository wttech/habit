import React, {Fragment, useEffect, useState, useCallback, useRef} from 'react';
import {Col, Row} from 'react-bootstrap';
import {Field, Form, Formik} from 'formik';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome'
import {faExclamationCircle} from '@fortawesome/free-solid-svg-icons'
import {RouteComponentProps} from "@reach/router";

import useStateFromProp from '@/utils/useStateFromProp';
import RequestGraph from '@/request/RequestGraph';
import EventLog from '@/craft/EventLog';
import ExchangeLogs from '@/craft/ExchangeLogs';
import Environment from '@/environment/Environment';

import './RequestCraft.scss';

const GRAPH = 'graph';
const LOGS = 'logs';

interface Props extends RouteComponentProps {
    environment: Environment
}

const RequestCraft = (props: Props) => {

    const [environment, setEnvironment] = useStateFromProp(props.environment);
    const [lastRequest, setLastRequest] = useState(null);
    const [error, setError] = useState(null);
    const [queryStringCount, setQueryStringCount] = useState(3);
    const [headerCount, setHeaderCount] = useState(3);
    const [visibleTab, setVisibleTab] = useState(GRAPH);

    const craftGrid = useRef<HTMLDivElement>(null);
    const graphContainer = useRef<HTMLDivElement>(null);

    const availableHosts = environment
        ? environment.configuration.servers.flatMap(server => {
            return server.domains;
        })
        : [];

    const calculateQueryString = (values: any) => {
        let queryStringAccumulator = '';
        for (let i = 0; i < queryStringCount; i++) {
            if (values[`qsn${i}`]) {
                if (queryStringAccumulator) {
                    queryStringAccumulator += '&';
                }
                queryStringAccumulator += values[`qsn${i}`];
                if (values[`qsv${i}`]) {
                    queryStringAccumulator += '=' + values[`qsv${i}`];
                }
            }
        }
        return queryStringAccumulator
            ? '?' + queryStringAccumulator
            : '';
    };

    const extractQueryParameters = (values: any) => {
        let queryStringAccumulator = [];
        for (let i = 0; i < queryStringCount; i++) {
            let queryParameterName = values[`qsn${i}`];
            if (queryParameterName) {
                let queryParameterValue = values[`qsv${i}`];
                if (queryParameterValue) {
                    queryStringAccumulator.push({
                        'name': queryParameterName,
                        'value': queryParameterValue
                    });
                } else {
                    queryStringAccumulator.push({
                        'name': queryParameterName
                    });
                }
            }
        }
        return queryStringAccumulator;
    };

    const calculateFullUrl = (values: any) => {
        let queryString = calculateQueryString(values);
        let portString = values.port
            ? `:${values.port}`
            : '';
        return values.protocol + '://' + values.targetHost + portString + values.path + queryString;
    };

    const calculateInitialValues = () => {
        const result: any = {
            'method': 'get',
            'protocol': 'https',
            'targetHost': availableHosts[0],
            'port': '',
            'path': '',
        };
        for (let i = 0; i < queryStringCount; i++) {
            result[`qsn${i}`] = '';
            result[`qsv${i}`] = '';
        }
        for (let i = 0; i < headerCount; i++) {
            result[`hn${i}`] = '';
            result[`hv${i}`] = '';
        }
        return result;
    }

    const extractHeaders = (values: any) => {
        let result: any = {};
        for (let i = 0; i < headerCount; i++) {
            let headerName = values[`hn${i}`];
            if (headerName) {
                let headerValue = values[`hv${i}`] || '';
                result[headerName] = headerValue;
            }
        }
        return result;
    };

    const sendRequest = (values: any) => {
        let method = values.method;
        let targetHost = values.targetHost;
        let queryParameters = extractQueryParameters(values);
        let headers = extractHeaders(values);
        let protocol = values.protocol;
        const requestSpecification = {
            method,
            protocol,
            host: targetHost,
            port: values.port,
            path: values.path,
            queryStringParameters: queryParameters,
            headers,
            body: ''
        }
        return fetch(`/api/v1/environments/${environment.id}/requests`, {
            method: 'POST',
            body: JSON.stringify(requestSpecification),
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
        });
    };

    const resetEnvironment = () => {
        fetch(`/api/v1/environments/${environment.id}/reset`, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
        });
    }

    const isGraphContainerVisible = () => {
        return error || lastRequest;
    };

    const renderQueryStringParameter = (index: number) => {
        return (
            <div key={index} className="form-row">
                <div className="col-md-6">
                    <Field className="form-control form-control-sm" type="text" name={`qsn${index}`}
                           placeholder="Parameter name"/>
                </div>
                <div className="col-md-6">
                    <Field className="form-control form-control-sm" type="text" name={`qsv${index}`}
                           placeholder="Parameter value"/>
                </div>
            </div>
        );
    };

    const renderHeader = (index: number) => {
        return (
            <div key={index} className="form-row">
                <div className="col-md-6">
                    <Field className="form-control form-control-sm" type="text" name={`hn${index}`}
                           placeholder="Header name"/>
                </div>
                <div className="col-md-6">
                    <Field className="form-control form-control-sm" type="text" name={`hv${index}`}
                           placeholder="Header value"/>
                </div>
            </div>
        );
    };

    const handleSubmit = (values: any, {setSubmitting}: any) => {
        setLastRequest(null);
        setError(null);
        return sendRequest(values).then(res => {
            setSubmitting(false);
            if (!res.ok) {
                setLastRequest(null);
                res.json().then(data => setError(data));
            } else {
                setError(null);
                res.json().then(data => setLastRequest(data));
            }
        });
    }

    const renderForm = () => {
        return (
            <Formik initialValues={calculateInitialValues()}
                    onSubmit={handleSubmit}
            >
                {({values, isSubmitting}) => {
                    return (
                        <Form>
                            <div>
                                <p className="small text-secondary">URL: {calculateFullUrl(values)}</p>
                            </div>
                            <div className="form-row">
                                <div className="col-md-2">
                                    <Field className="form-control form-control-sm" as="select" name="method">
                                        <option value="get">GET</option>
                                        <option value="post">POST</option>
                                        <option value="put">PUT</option>
                                        <option value="patch">PATCH</option>
                                        <option value="delete">DELETE</option>
                                        <option value="head">HEAD</option>
                                    </Field>
                                </div>
                                <div className="col-md-2">
                                    <Field className="form-control form-control-sm" as="select" name="protocol">
                                        <option value="https">https</option>
                                        <option value="http">http</option>
                                    </Field>
                                </div>
                                <div className="col-md-3">
                                    <Field className="form-control form-control-sm" as="select" name="targetHost">
                                        {availableHosts.map(host => {
                                            return (
                                                <option key={host} value={host}>{host}</option>
                                            )
                                        })}
                                    </Field>
                                </div>
                                <div className="col-md-1">
                                    <Field className="form-control form-control-sm" type="number" name="port"
                                           placeholder="port"/>
                                </div>
                                <div className="col-md-4">
                                    <Field className="form-control form-control-sm" type="text" name="path"
                                           placeholder="path"/>
                                </div>
                            </div>
                            <div>
                                <p className="small text-secondary">Query string</p>
                                {[...Array(queryStringCount)].map((el, index) => {
                                    return renderQueryStringParameter(index);
                                })}
                            </div>
                            <div>
                                <p className="small text-secondary">Headers</p>
                                {[...Array(queryStringCount)].map((el, index) => {
                                    return renderHeader(index);
                                })}
                            </div>
                            <button type="submit" className="btn btn-sm btn-primary"
                                    disabled={isSubmitting || environment.state !== 'up'}>
                                Submit
                            </button>
                            {isSubmitting &&
                            <Fragment>
                                <span className="spinner-border spinner-border-sm" role="status"
                                      aria-hidden="true"></span>
                                <span className="sr-only">Loading...</span>
                            </Fragment>
                            }
                            {(environment.state !== 'up') &&
                            <Fragment>
                                <span>Start / fix the environment to send the request.</span>
                            </Fragment>
                            }
                        </Form>
                    );
                }
                }

            </Formik>
        )
    };

    const calculateGraphViewButton = (view: string) => {
        if (view === visibleTab) {
            return "btn btn-tab is-active";
        } else {
            return "btn btn-tab";
        }
    };

    const renderActions = (environment: Environment) => {
        const isResetAllowed = environment.configuration.servers.some(server => server.deploy && server.deploy.postDeployCommand);
        const withPost = environment.configuration.servers.filter(server => server.deploy && server.deploy.postDeployCommand);
        console.log(withPost);
        return isResetAllowed ? (
            <div>
                <div className="h5">Actions</div>
                <button className="btn btn-primary btn-sm" onClick={resetEnvironment}>Reset</button>
            </div>
        ) : null;
    }

    const renderContent = () => {
        return (
            <div className="craft-container"
                 ref={craftGrid}>
                <Row>
                    <Col md={6} className="craft-form-container">
                        <div className="card">
                            <div className="card-body">
                                {renderForm()}
                            </div>
                        </div>
                    </Col>
                    <Col md={6} className="craft-event-log-container">
                        <div className="card">
                            <div className="card-body">
                                {renderActions(environment)}
                                <EventLog envId={environment.id}/>
                            </div>
                        </div>
                    </Col>
                </Row>
                {isGraphContainerVisible() &&
                <div className="craft-graph-container"
                     ref={graphContainer}>
                    {error &&
                    <span><FontAwesomeIcon icon={faExclamationCircle} style={{color: 'red'}}/> {error!.message}</span>
                    }
                    {lastRequest &&
                    <Fragment>
                        <div>
                            <button className={`${calculateGraphViewButton(GRAPH)}`}
                                    onClick={e => setVisibleTab(GRAPH)}>Graph
                            </button>
                            <button className={`${calculateGraphViewButton(LOGS)}`}
                                    onClick={e => setVisibleTab(LOGS)}>Logs
                            </button>
                        </div>
                        <div className={'graph-tab' + (visibleTab === GRAPH ? '' : ' hidden')}>
                            <RequestGraph graph={lastRequest!.graph}/>
                        </div>
                        <div className={'graph-tab ' + (visibleTab === LOGS ? '' : ' hidden')}>
                            <ExchangeLogs environment={environment} request={lastRequest}/>
                        </div>
                    </Fragment>
                    }
                </div>
                }
            </div>
        )
    };

    return (
        <Fragment>
            {environment && renderContent()}
        </Fragment>
    );
};

export default RequestCraft;
