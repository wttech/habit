import React, {Fragment, useEffect, useState} from "react";
import {Link, RouteComponentProps} from "@reach/router";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome'
import {
  faAngleDoubleUp,
  faPowerOff,
  faQuestion,
  faTimes
} from '@fortawesome/free-solid-svg-icons'

import Environment from '@/environment/Environment';
import Header from '@/components/Header';
import Loader from '@/components/Loader';
import ErrorMessage from '@/components/ErrorMessage';

interface Props extends RouteComponentProps {

}

const Environments = (_: Props) => {
    const [environments, setEnvironments] = useState<Environment[]>([]);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [error, setError] = useState<string>(null);

    useEffect(() => {
        setIsLoading(true);

        fetch('/api/v1/environments')
            .then(res => res.json())
            .then(data => {
                setEnvironments(data);
                setIsLoading(false);
            })
            .catch(err => setError('Unexpected error while fetching Habit environments.'));
    }, []);

    const renderStateIcon = (environment: Environment) => {
        let result;
        switch (environment.state.toLowerCase()) {
            case 'up':
                result = <FontAwesomeIcon style={{color:"green"}} icon={faAngleDoubleUp} />;
                break;
            case 'down':
                result = <FontAwesomeIcon style={{color:"yellow"}} icon={faPowerOff} />;
                break;
            case 'error':
                result = <FontAwesomeIcon style={{color:"red"}} icon={faTimes} />;
                break;
            default:
                result = <FontAwesomeIcon style={{color:"gray"}} icon={faQuestion} />;
                break;
        }
        return result;
    };

    return (
        <Fragment>
            <Header/>
            {error && <ErrorMessage title="Error" message={error} />}
            <div className="container-fluid pt-3 pb-3">
                <h1>Environments</h1>
                <Loader status={isLoading} />
                <div className="list-group">
                    {environments.map(item => <Link
                        key={item.id}
                        to={`/environments/${item.id}`}
                        className="list-group-item list-group-item-action">
                            {renderStateIcon(item)} {item.id}
                    </Link>)}
                </div>
            </div>
        </Fragment>
    );
}

export default Environments;