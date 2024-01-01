import { MessageBuilder } from "./Message";
import { EntityDefault } from "./Entity";
import { Frame } from "./Frame";
import { FragmentHolder } from "./FragmentHolder";
import { EntityMetas } from "./Constants";
import { SocketdCodecException } from "../../exception/SocketdException";
/**
 * 分片聚合器
 *
 * @author noear
 * @since 2.0
 */
export class FragmentAggregatorDefault {
    constructor(main) {
        //分片列表
        this._fragmentHolders = new Array();
        this._main = main;
        const dataLengthStr = main.meta(EntityMetas.META_DATA_LENGTH);
        if (!dataLengthStr) {
            throw new SocketdCodecException("Missing '" + EntityMetas.META_DATA_LENGTH + "' meta, event=" + main.event());
        }
        this._dataLength = parseInt(dataLengthStr);
    }
    /**
     * 获取消息流Id（用于消息交互、分片）
     */
    getSid() {
        return this._main.sid();
    }
    /**
     * 数据流大小
     */
    getDataStreamSize() {
        return this._dataStreamSize;
    }
    /**
     * 数据总长度
     */
    getDataLength() {
        return this._dataLength;
    }
    /**
     * 添加帧
     */
    add(index, message) {
        //添加分片
        this._fragmentHolders.push(new FragmentHolder(index, message));
        //添加计数
        this._dataStreamSize = this._dataStreamSize + message.dataSize();
    }
    /**
     * 获取聚合后的帧
     */
    get() {
        //排序
        this._fragmentHolders.sort((f1, f2) => {
            if (f1.getIndex() == f2.getIndex()) {
                return 0;
            }
            else if (f1.getIndex() > f2.getIndex()) {
                return 1;
            }
            else {
                return -1;
            }
        });
        //创建聚合流
        const dataBuffer = new ArrayBuffer(this._dataLength);
        const dataBufferView = new DataView(dataBuffer);
        let dataBufferViewIdx = 0;
        //添加分片数据
        for (const fh of this._fragmentHolders) {
            const tmp = new DataView(fh.getMessage().data().getArray());
            for (let i = 0; i < fh.getMessage().data().size(); i++) {
                dataBufferView.setInt8(dataBufferViewIdx, tmp.getInt8(i));
                dataBufferViewIdx++;
            }
        }
        //返回
        return new Frame(this._main.flag(), new MessageBuilder()
            .flag(this._main.flag())
            .sid(this._main.sid())
            .event(this._main.event())
            .entity(new EntityDefault().metaMapPut(this._main.metaMap()).dataSet(dataBuffer))
            .build());
    }
}
