import React, {Fragment, useEffect, useState} from 'react';

import { IconProp } from '@fortawesome/fontawesome-svg-core';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome'
import {faReply, faShare} from '@fortawesome/free-solid-svg-icons'
import ReactTooltip from 'react-tooltip';

import RequestGraph from '@/request/RequestGraphType';
import {printTime} from '@/utils/DateUtils';

interface Props {
    graph: RequestGraph,
    onExchangeSelection?: any
}

const RequestGraph = (props: Props) => {

    const [graph, setGraph] = useState(props.graph);
    const [exchangeSelectionCallback, setExchangeSelectionCallback] = useState(props.onExchangeSelection);

    useEffect(() => {
        setGraph(props.graph);
    }, [props.graph]);

    useEffect(() => {
        setExchangeSelectionCallback(props.onExchangeSelection);
    }, [props.onExchangeSelection]);

    const handleExchangeSelection = (e: any, exchange: any) => {
        exchangeSelectionCallback(exchange);
    };

    const calculateTooltipContent = (headers: any, body: any, timestamp: any) => {
        return (
            <Fragment>
                Timestamp: {printTime(timestamp)}
                <br /><br />
                Headers
                <div>
                    {Object.keys(headers).sort().map(header => <div key={header}>{header}: {headers[header]}</div>)}
                </div>

                {body &&
                    <Fragment>
                        <br />
                        Body
                        <div>
                            {body}
                        </div>
                    </Fragment>
                }
            </Fragment>
        )
    };

    const renderExchange = (exchange: any, level: any) => {
        const offsetClass = `offset-sm-${level * 3}`;

        return (
            <Fragment key={exchange.request.requestId}>
                <ReactTooltip className="graph-tooltip" id={`${exchange.request.requestId}-request`} place="right" type="dark" effect="solid">
                    {calculateTooltipContent(exchange.request.headers, exchange.request.body, exchange.request.timestamp)}
                </ReactTooltip>
                <div className="row pt-1">
                    <div className={`col-sm-3 ${offsetClass}`}
                        data-tip 
                        data-for={`${exchange.request.requestId}-request`} 
                        onClick={(e) => handleExchangeSelection(e, exchange)}>
                        <div className="card">
                            <div className="card-body">
                                <div className="font-weight-bold text-wrap">
                                    <FontAwesomeIcon icon={faShare} /> {exchange.request.protocol}://{exchange.request.host}:{exchange.request.port}
                                </div>
                                <div className="font-weight-bold text-wrap">
                                    {exchange.request.method} {exchange.request.fullPath}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                {
                    exchange.subrequests && exchange.subrequests.map((subrequest: any) => renderExchange(subrequest, level + 1))
                }
                <ReactTooltip className="graph-tooltip" id={`${exchange.request.requestId}-response`} place="right" type="dark" effect="solid">
                    {calculateTooltipContent(exchange.response.headers, exchange.response.body, exchange.response.timestamp)}
                </ReactTooltip>
                <div className="row pb-1">
                    <div className={`col-sm-3 ${offsetClass}`} data-tip data-for={`${exchange.request.requestId}-response`}>
                        <div className="card">
                            <div className="card-body">
                                <div className="font-weight-bold text-wrap">
                                    <FontAwesomeIcon icon={faReply} /> {exchange.response.statusCode} {exchange.response.reason}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </Fragment>
        );
    }

    return (
        <Fragment>
            <div className="graph-json mb-2">
                {graph.subrequests.map((subrequest: any) => renderExchange(subrequest, 0))}
            </div>
        </Fragment>
    );
};

export default RequestGraph;