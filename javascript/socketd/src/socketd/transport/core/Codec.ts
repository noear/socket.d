
import {CodecUtils} from "./CodecUtils";
import {Asserts} from "./Asserts";
import {Constants} from "./Constants";
import {Frame} from "./Message";

export interface Codec {
    write(frame: Frame, factory);

    read(buffer): Frame;
}

export class CodecByteBuffer implements Codec {
    write(frame: Frame, factory) {
        if (frame.getMessage()) {
            //sid
            let sidB = CodecUtils.strToBuf(frame.getMessage().sid());
            //event
            let eventB = CodecUtils.strToBuf(frame.getMessage().event());
            //metaString
            let metaStringB = CodecUtils.strToBuf(frame.getMessage().metaString());

            //length (len[int] + flag[int] + sid + event + metaString + data + \n*3)
            let frameSize = 4 + 4 + sidB.length + eventB.length + metaStringB.length + frame.getMessage().dataSize() + 2 * 3;

            Asserts.assertSize("sid", sidB.length, Constants.MAX_SIZE_SID);
            Asserts.assertSize("event", eventB.length, Constants.MAX_SIZE_EVENT);
            Asserts.assertSize("metaString", metaStringB.length, Constants.MAX_SIZE_META_STRING);
            Asserts.assertSize("data", frame.getMessage().dataSize(), Constants.MAX_SIZE_DATA);

            let target = factory.apply(frameSize);

            //长度
            target.putInt(frameSize);

            //flag
            target.putInt(frame.getFlag());

            //sid
            target.putBytes(sidB);
            target.putChar('\n');

            //event
            target.putBytes(eventB);
            target.putChar('\n');

            //metaString
            target.putBytes(metaStringB);
            target.putChar('\n');

            //data
            target.putBytes(frame.getMessage().data());

            target.flush();

            return target;
        } else {
            //length (len[int] + flag[int])
            let frameSize = 4 + 4;
            let target = factory.apply(frameSize);

            //长度
            target.putInt(frameSize);

            //flag
            target.putInt(frame.getFlag());
            target.flush();

            return target;
        }
    }

    read(buffer): Frame { //=>Frame
        return null;
    }
}