export class SslContext {
    key?: string | Buffer | Array<string | Buffer > | undefined; // 私钥
    cert?: string | Buffer | Array<string | Buffer> | undefined; // 证书
    ca?: string | Buffer | Array<string | Buffer> | undefined; // 受信任的CA证书（用于验证客户端证书）
    requestCert?: boolean | undefined; // 要求客户端提供证书 //for server
    rejectUnauthorized?: boolean | undefined; // 拒绝未经授权的连接
}