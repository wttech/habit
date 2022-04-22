import {useState, useEffect} from 'react';

export default function <T> (propValue: T) {

    const stateTuple = useState<T>(propValue);
    const [state, setState] = stateTuple;
    useEffect(() => {
        setState(propValue);
    }, [propValue])

    return stateTuple;

}