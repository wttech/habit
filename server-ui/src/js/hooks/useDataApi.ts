import React, {useEffect, useState} from 'react';

export const useDataApi = (initialUrl: string) => {
    const [data, setData] = useState({});
    const [url, setUrl] = useState(initialUrl);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        setIsLoading(true);

        fetch(url)
            .then(res => res.json())
            .then(data => {
                setData(data);
                setIsLoading(false);
            })
            .catch(err => setError('Unexpected error while fetching Habit data.'));
    }, [url]);

    return [{ data, isLoading, error }, setUrl];
}