import React from 'react';
import {Link} from '@reach/router';

interface Props {

}

const RequestDetailsMenu = (props: Props) => {
    return (
        <ul className="nav nav-pills">
            <li className="nav-item">
                <Link to="graph" className={`nav-link active`}>Graph</Link>
            </li>
            <li className="nav-item">
                <Link to="logs" className={`nav-link active`}>Logs</Link>
            </li>
        </ul>
    );
};

export default RequestDetailsMenu;