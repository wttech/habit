import React, { Fragment, useState, useEffect } from 'react';
import { Link, RouteComponentProps, useLocation } from "@reach/router";
import { getBreadcrumbs } from '../../utils/locationUtils';
import State from '../State';
import Environment from '@/environment/Environment';

import "./EnvironmentHeader.scss";

import habitIcon from "../../../assets/habit.png";

interface Props extends RouteComponentProps {
    environment: Environment
}

const EnvironmentHeader = (props: Props) => {

    const [environment, setEnvironment] = useState(props.environment);
    const [breadcrumbs, setBreadcrumbs] = useState([]);
    const location = useLocation();

    useEffect(() => {
        setEnvironment(props.environment);
    }, [props.environment]);

    useEffect(() => {
        if (location && environment) {
            const breadcrumbs = getBreadcrumbs(location.pathname, environment.id);
            setBreadcrumbs(breadcrumbs);
        }
    }, [environment, location]);

    const renderBradcrumbs = (breadcrumbs: string[]) => 
        breadcrumbs.map(bc => <li key={bc} className="nav-item active">
            <span className="nav-link pl-3 pr-3">{bc}</span>
        </li>);

    return (
        <Fragment>
            {environment &&
                <nav className="navbar sticky-top navbar-dark navbar-expand-lg navbar-light bg-dark">
                    <Link to="/" className="navbar-brand">
                        <img src={habitIcon} />
                    </Link>
                    <div className="collapse navbar-collapse" id="navbarSupportedContent">
                        {environment && <State state={environment.state} size="sm"/>}
                        <ul className="navbar-nav mr-auto">
                            <li className="nav-item active">
                                <Link className="nav-link pl-3 pr-3" to={`/environments/${environment.id}`}>{environment.id}</Link>
                            </li>
                            {renderBradcrumbs(breadcrumbs)}
                        </ul>
                    </div>
                </nav>
            }
        </Fragment>
    );
};

export default EnvironmentHeader;
