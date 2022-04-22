import React, { useState, useEffect } from 'react';
import { Link } from "@reach/router";
import { Col } from 'react-bootstrap';
import Environment from '@/environment/Environment';
import { faRocket, faLevelUpAlt } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'

import "./Sidebar.scss";

import habitIcon from "../../../assets/habit.png";

interface Props {
    environment: Environment
}

const Sidenav = (props: Props) => {
    const [environment, setEnvironment] = useState(props.environment);

    useEffect(() => {
        setEnvironment(props.environment);
    }, [props.environment]);

    return (
        <Col className="sidebar">
            <Link to="/" className="sidebar-brand"><img src={habitIcon} alt="logo"/></Link>

            <nav className="sidenav">
                <ul>
                    <li className="sidenav-item">
                        <Link className="sidenav-link" to="/environments/"><FontAwesomeIcon icon={faRocket} />Environments</Link>
                        {environment &&
                            <ul>
                                <li className="sidenav-subnav-item">
                                    <Link className="sidenav-subnav-link" to={`/environments/${environment.id}`}><FontAwesomeIcon icon={faLevelUpAlt} rotation={90}/>{environment.id}</Link>
                                </li>
                            </ul>
                        }
                    </li>
                </ul>
            </nav>
        </Col>
    );
};

export default Sidenav;


