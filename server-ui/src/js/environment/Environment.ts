interface DeployConfiguration {
    postDeployCommand?: string
}

interface ServerConfiguration {
    domains: string[],
    deploy?: DeployConfiguration
}

interface Configuration {
    servers: ServerConfiguration[]
}

export default interface Environment {
    id: string,
    configuration: Configuration,
    state: string,
    lastModified: string
}
