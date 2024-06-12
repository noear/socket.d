import {EntityDefault} from "./EntityDefault";
import {Entity} from "../Entity";
import {StrUtils} from "../../../utils/StrUtils";

/**
 * 字符串实体
 *
 * @author noear
 * @since 2.0
 */
export class StringEntity extends EntityDefault implements Entity {
    constructor(data: string) {
        super();
        const dataBuf = StrUtils.strToBuf(data);
        this.dataSet(dataBuf);
    }
}