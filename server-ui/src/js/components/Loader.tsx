import React from 'react';

interface Props {
    status: boolean
}

const Loader = (props: Props) => {
    const { status } = props;

    return status ? (
        <div className="d-flex justify-content-center">
            <div className="spinner-border" role="status">
                <span className="sr-only">Loading...</span>
            </div>
        </div>
    ) : null;
};

export default Loader;