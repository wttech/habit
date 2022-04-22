export default interface LogLine {
    id: string,
    environmentId: string,
    filePath: string,
    epoch: number,
    line: string,
    message?: string,
    data?: {[key: string]: any},
    tags?: string[],
    timestamp?: string
}
