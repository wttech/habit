import React, {Fragment, useEffect, useState} from "react";

import {printTime} from '../utils/DateUtils';

interface Props {
    envId?: string
}

interface Event {
    id: string
    timestamp: string,
    message: string
}

const EventLog = (props: Props) => {

    const [environmentEvents, setEnvironmentEvents] = useState<Event[]>([]);

    useEffect(() => {
        if (props.envId) {
            const eventsEndpoint = `/api/v1/environments/${props.envId}/events`;
            const source = new EventSource(eventsEndpoint);
            let events: Event[] = [];
            source.onmessage = (e) => {
                const newEvent = JSON.parse(e.data);
                const notDuplicate = events.find(existingEvent => existingEvent.id === newEvent.id) === undefined;
                if (notDuplicate) {
                    const newEvents = [...events, newEvent];
                    events = newEvents;
                    setEnvironmentEvents(newEvents);
                }
            };
            return () => { source.close(); }
        }
    }, [props.envId]);

    return (
        <Fragment>
            <div className="h5">Event log</div>
            <pre className="event-log" >
                <output>
                    {environmentEvents.map((event, index) => {
                        return (
                            <Fragment key={index}>
                                {printTime(event.timestamp)}: {event.message}<br />
                            </Fragment>
                        );
                    })}
                </output>
            </pre>
        </Fragment>
    );

}

export default EventLog;