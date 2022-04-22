import React, { Fragment, useEffect, useState } from 'react';

import LogLine from '@/request/LogLine';
import RequestGraphType from '@/request/RequestGraphType';
import Request from '@/request/RequestType';
import { printTime } from '@/utils/DateUtils';

interface Props {
    logs: LogLine[],
    request: Request
}

interface LogsContainer {
    logs: LogFile[]
}

interface LogFile {
    server: string, 
    path: string, 
    lines: LogLine[]
}

interface TimeframeFragment {
    start: Date,
    end: Date
}

interface Timeframe {
    item: RequestGraphType,
    time: TimeframeFragment[]
}

const RequestLogsContent = (props: Props) => {

    const [logs, setLogs] = useState<LogsContainer>(null);
    const [request, setRequest] = useState(props.request);
    const [selectedTimeframes, setSelectedTimeframes] = useState<TimeframeFragment[]>([]);
    const [selectedFile, setSelectedFile] = useState<LogFile>();
    const [orderedTimeframes, setOrderedTimeframes] = useState<Timeframe[]>();

    const addToArray = (result: RequestGraphType[], exchange: RequestGraphType) => {
        result.push(exchange);
        for (let subrequest of exchange.subrequests) {
            addToArray(result, subrequest);
        }
    }

    useEffect(() => {
        if (props.request) {
            const orderedExchanges: RequestGraphType[] = [];
            for (let exchange of props.request.graph.subrequests) {
                addToArray(orderedExchanges, exchange);
            }
            const timeframes = [];
            for (let i = 0; i < orderedExchanges.length; i++) {
                const current = orderedExchanges[i];
                if (current.subrequests.length) {
                    let firstStart = current.request.timestamp;
                    let firstEnd = current.subrequests[0].request.timestamp;
                    let firstTimeframe = { start: new Date(firstStart), end: new Date(firstEnd) };

                    let lastSubrequest = current.subrequests[current.subrequests.length - 1];
                    let secondStart = lastSubrequest.response ? lastSubrequest.response.timestamp : lastSubrequest.error.timestamp;
                    let secondEnd = current.response ? current.response.timestamp : current.error.timestamp;
                    let secondTimeframe = { start: new Date(secondStart), end: new Date(secondEnd) };

                    timeframes.push({ item: current, time: [firstTimeframe, secondTimeframe] });
                } else {
                    let start = current.request.timestamp;
                    let end = current.response ? current.response.timestamp : current.error.timestamp;
                    let exchangeTimeframe = { start: new Date(start), end: new Date(end) };
                    timeframes.push({ item: current, time: [exchangeTimeframe] });
                }
            }
            setOrderedTimeframes(timeframes);
        }
    }, [props.request]);

    useEffect(() => {
        setRequest(props.request);
    }, [props.request]);

    useEffect(() => {
        if (props.logs.length) {
            const structured = structureLogs(props.logs);
            setLogs(structured);
            if (structured.logs) {
                setSelectedFile(structured.logs[0]);
            }
        } else {
            setLogs(null);
            setSelectedFile(null);
        }
    }, [props.logs]);

    const structureLogs = (logs: LogLine[]): LogsContainer => {
        const groupedByFile: {[key: string]: LogFile} = {};
        for (let logLine of logs) {
            const id = logLine.filePath;
            groupedByFile[id] = groupedByFile[id] || { server: '', path: id, lines: [] };
            groupedByFile[id].lines.push(logLine);
        }
        const fileArray: LogFile[] = [];
        Object.values(groupedByFile).forEach(group => fileArray.push(group));
        return { logs: fileArray };
    };

    const isStructured = (line: LogLine) => {
        return line.message;
    };

    const isMatchingTimeframes = (line: LogLine) => {
        return (!selectedTimeframes.length) || selectedTimeframes.some(timeframe => isMatchingTimeframe(line, timeframe));
    };

    const isMatchingTimeframe = (line: LogLine, timeframe: any) => {
        return (line.data['date'] > timeframe.start && line.data['date'] < timeframe.end);
    };

    const getHiddenClass = (line: LogLine) => {
        if (!isMatchingTimeframes(line)) {
            return "hidden";
        } else {
            return "";
        }
    };

    const renderStructuredLine = (line: LogLine) => {
        return (
            <Fragment key={line.id}>
                <div className={`log-line ${getHiddenClass(line)}`}>
                    {printTime(line.timestamp)}
                </div>
                <div className={`log-line log-line-message ${getHiddenClass(line)}`}>
                    {line.message}
                </div>
            </Fragment>
        );
    };

    const renderUnstructuredLine = (line: LogLine) => {
        return (
            <Fragment key={line.id}>
                <div className={`log-line`}>
                </div>
                <div className={`log-line log-line-message`}>
                    {line.line}
                </div>
            </Fragment>
        );
    };

    const renderProxyServerLogSection = (serverLogs: LogFile) => {
        return (
            <div className="log-grid-wrapper">
                <div className="log-grid">
                    {serverLogs.lines.map((line) => {
                        return isStructured(line)
                            ? renderStructuredLine(line)
                            : renderUnstructuredLine(line);
                    })}
                </div>
            </div>
        )
    };

    const getFileItemClass = (logFile: LogFile) => {
        return selectedFile === logFile
            ? 'active'
            : '';
    };

    const getExchangeItemClass = (exchangeTimeframes: TimeframeFragment[]) => {
        return selectedTimeframes === exchangeTimeframes
            ? 'active'
            : '';
    };

    const renderFileList = (logFiles: LogFile[]) => {
        return (
            <div className="list-group">
                <div className="list-group-item">
                    Files
                </div>
                {logFiles.map((logFile) => {
                    return (
                        <Fragment key={`${logFile.server}_${logFile.path}`}>
                            <div className={`logs-file-item item-with-badge list-group-item list-group-item-action ${getFileItemClass(logFile)}`}
                                onClick={e => setSelectedFile(logFile)}>
                                {logFile.path} <span className="badge badge-light">{logFile.lines.length}</span>
                            </div>
                        </Fragment>
                    )
                })}
            </div>
        );
    }

    const calculateMatchingLineCount = (file: LogFile, timeframes: TimeframeFragment[]) => {
        return file.lines.filter(line => (!timeframes.length) || timeframes.some(timeframe => isMatchingTimeframe(line, timeframe))).length;
    };

    const renderTimeframes = (timeframes: Timeframe[], file: LogFile) => {
        return (
            <div className="list-group pb-3">
                <div className="list-group-item">
                    Exchanges
                </div>
                {timeframes.map((timeframe, index: number) => {
                    return (
                        <div key={`${index}`} className={`${getExchangeItemClass(timeframe.time)} item-with-badge logs-section-item list-group-item list-group-item-action`}
                            onClick={e => setSelectedTimeframes(timeframe.time)}>
                                <div>
                                    {timeframe.item.request.protocol}://{timeframe.item.request.host}:{timeframe.item.request.port}<br />
                                    {timeframe.item.request.method} {timeframe.item.request.fullPath}<br />
                                    {timeframe.item.response.statusCode} {timeframe.item.response.reason}
                                </div>
                                <div>
                                    <span className="badge badge-light">{calculateMatchingLineCount(selectedFile, timeframe.time)}</span>
                                </div>
                        </div>
                    )
                })}
                <div className={`${!selectedTimeframes.length && 'active'} logs-section-item item-with-badge list-group-item list-group-item-action`}
                    onClick={e => setSelectedTimeframes([])}>
                    <span>Show all</span><span className="badge badge-light">{file.lines.length}</span>
                </div>
            </div>
        );
    }

    return (
        <Fragment>
            {request && logs && logs.logs &&
                <div>
                    <div className="row">
                        <div className="col-sm-3">
                            <div className="pb-3">
                                {renderFileList(logs.logs)}
                            </div>
                            <div>
                                {renderTimeframes(orderedTimeframes, selectedFile)}
                            </div>
                        </div>
                        <div className="col-sm-9">
                            {selectedFile && renderProxyServerLogSection(selectedFile)}
                        </div>
                    </div>
                </div>
            }
        </Fragment>
    );
};

export default RequestLogsContent;