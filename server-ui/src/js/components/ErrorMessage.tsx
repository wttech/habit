import React from 'react'
import $ from 'jquery';

interface Props {
    title: string,
    message: string
}

const ErrorMessage = (props: Props) => {
    const { title, message } = props;

    $('.toast').toast('show');

    return (
        <div className="toast-wrapper">
            <div className="toast" role="alert" aria-live="assertive" aria-atomic="true">
                <div className="toast-header bg-danger text-white">
                    <strong className="mr-auto">{title}</strong>
                    <small>11 mins ago</small>
                    <button type="button" className="ml-2 mb-1 close text-white" data-dismiss="toast" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div className="toast-body">
                    {message}
                </div>
            </div>
        </div>
    );
};

export default ErrorMessage;
