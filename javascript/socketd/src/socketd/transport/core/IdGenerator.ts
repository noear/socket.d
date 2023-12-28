import {StrUtils} from "../../utils/StrUtils";


export interface IdGenerator {
    /**
     * 生成
     */
    generate(): string;
}

export class GuidGenerator implements IdGenerator {
    generate(): string {
        return StrUtils.guid();
    }
}