

export interface IdGenerator {
    /**
     * 生成
     */
    generate(): string;
}

export class GuidGenerator implements IdGenerator {
    generate(): string {
        return 'xxxxxxxxxxxx4xxxyxxxxxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            var r = Math.floor(Math.random() * 16);
            var v = c === 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    }
}