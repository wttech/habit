import React from 'react';
import {Link} from "@reach/router";

import habitIcon from "../../assets/habit.png";

const Header = () => {
    return (
        <nav className="navbar sticky-top navbar-dark navbar-expand-lg navbar-light bg-dark">
            <Link to="/" className="navbar-brand">
                <img src={habitIcon} />
            </Link>
        </nav>
    );
};

export default Header;
