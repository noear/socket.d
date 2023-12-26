import {Utils} from "../../utils/Utils";


export interface IdGenerator {
    /**
     * 生成
     */
    generate(): string;
}

export class GuidGenerator implements IdGenerator {
    generate(): string {
        return Utils.guid();
    }
}