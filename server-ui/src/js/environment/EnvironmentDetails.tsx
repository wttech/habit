import React, {Fragment} from "react";
import {RouteComponentProps} from "@reach/router";

import Environment from '@/environment/Environment';
import Loader from '@/components/Loader';
import ErrorMessage from '@/components/ErrorMessage';

interface Props extends RouteComponentProps {
    environment: Environment,
    isLoading: boolean,
    error: string
}

const EnvironmentDetails = (props: Props) => {

    const { environment, isLoading, error } = props;

    return (
        <Fragment>
            <Loader status={isLoading} />
            {error && <ErrorMessage title="Error" message={error} />}
            <pre>
                {JSON.stringify(environment.configuration, undefined, 2)}
            </pre>
        </Fragment>
    );
};

export default EnvironmentDetails;