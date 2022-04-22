import React from 'react';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {SizeProp} from '@fortawesome/fontawesome-svg-core';
import {
  faAngleDoubleUp,
  faPowerOff,
  faQuestion,
} from '@fortawesome/free-solid-svg-icons';

interface Props {
  state: string,
  size?: SizeProp
}

const State = (props: Props) => {
  const renderUpState = () => {
    return (
      <div className="icon-circle icon-circle--success" title="environment up">
        <FontAwesomeIcon icon={faPowerOff} size={props.size} />
      </div>
    );
  };

  const renderDownState = () => {
    return (
      <div className="icon-circle icon-circle--warning" title="environment down">
        <FontAwesomeIcon icon={faPowerOff} size={props.size} />
      </div>
    );
  };

  const renderErrorState = () => {
    return (
      <div className="icon-circle icon-circle--error" title="environment error">
        <FontAwesomeIcon icon={faPowerOff} size={props.size} />
      </div>
    );
  };

  const renderStartingState = () => {
    return (
      <div className="icon-circle icon-circle--warning" title="environment starting">
        <FontAwesomeIcon icon={faAngleDoubleUp} size={props.size} />
      </div>
    );
  };

  const renderUnknownState = (state: string) => {
    return (
      <div className="icon-circle icon-circle--error">
        <FontAwesomeIcon icon={faQuestion} />
      </div>
    );
  };

  const renderState = (state: string) => {
    if (state === 'up') {
      return renderUpState();
    } else if (state === 'down') {
      return renderDownState();
    } else if (state === 'error') {
      return renderErrorState();
    } else if (state === 'starting') {
      return renderStartingState();
    } else {
      return renderUnknownState(state);
    }
  };

  return <div className="state">
    {renderState(props.state)}
  </div>
};

export default State;
