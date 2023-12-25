

export interface IdGenerator {
    /**
     * 生成
     */
    generate(): string;
}

export class GuidGenerator implements IdGenerator {
    generate(): string {
        let guid = "";
        for (let i = 1; i <= 32; i++) {
            let n = Math.floor(Math.random() * 16.0).toString(16);
            guid += n;
        }
        return guid;
    }
}