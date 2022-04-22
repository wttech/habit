const printTime = (isoString: string) => {
    const date = new Date(isoString);
    const hours = `${date.getUTCHours()}`.padStart(2, '0');
    const minutes = `${date.getUTCMinutes()}`.padStart(2, '0');
    const seconds = `${date.getUTCSeconds()}`.padStart(2, '0');
    const millis = `${date.getUTCMilliseconds()}`.padStart(3, '0');
    return `${hours}:${minutes}:${seconds}.${millis}`;
}

export { printTime };