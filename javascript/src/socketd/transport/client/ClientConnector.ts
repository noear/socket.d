import {Channel} from "../core/Channel";
import {ClientConfig} from "./ClientConfig";

export interface ClientConnector {
    config: ClientConfig

    connect(): Channel
}
