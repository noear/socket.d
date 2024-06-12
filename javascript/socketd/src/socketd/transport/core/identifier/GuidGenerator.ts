import {StrUtils} from "../../../utils/StrUtils";
import {IdGenerator} from "../IdGenerator";

export class GuidGenerator implements IdGenerator {
    generate(): string {
        return StrUtils.guid();
    }
}