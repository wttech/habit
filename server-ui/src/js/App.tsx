import React, {Fragment} from "react";
import {Router} from "@reach/router";

import Environments from "@/environment/Environments";
import EnvironmentPage from "@/environment/EnvironmentPage";

const App = () => {
    return (
        <Fragment>
            <Router>
                <Environments path="environments" default />
                <EnvironmentPage path="environments/:envId/*" />
            </Router>
        </Fragment>
    );
};

export default App;