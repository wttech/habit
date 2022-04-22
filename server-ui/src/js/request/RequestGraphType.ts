interface ExchangeRequest {
    timestamp: string,
    protocol: string,
    version: string,
    host: string,
    port: number,
    method: string,
    path: string,
    fullPath: string,
    headers: {[key: string]: string},
    query: {[key: string]: string[]},
    body: string
}

interface ExchangeResponse {
    timestamp: string,
    statusCode: number,
    reason: string,
    body: string,
    headers: {[key: string]: string}
}

interface ExchangeError {
    timestamp: string,
    code: number,
    message: string
}

export default interface Graph {
    request?: ExchangeRequest,
    response?: ExchangeResponse,
    error?: ExchangeError,
    subrequests?: Graph[]  
}