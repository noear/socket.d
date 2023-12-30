import type {Processor} from "./Processor";
import type {ChannelAssistant} from "./ChannelAssistant";
import type {StreamInternal, StreamManger} from "./Stream";
import type {Session} from "./Session";
import type {ChannelSupporter} from "./ChannelSupporter";
import type {Config} from "./Config";
import {Frame, Frames} from "./Frame";
import {MessageBuilder} from "./Message";
import {EntityMetas, Flags} from "./Constants";
import {ChannelBase, ChannelInternal} from "./Channel";
import {SessionDefault} from "./SessionDefault";
import type { IoBiConsumer } from "./Typealias";

export class ChannelDefault<S> extends ChannelBase implements ChannelInternal {
    private _source: S;
    //处理器
    private _processor: Processor;
    //助理
    private _assistant: ChannelAssistant<S>;
    //流管理器
    private _streamManger: StreamManger;
    //会话（懒加载）
    private _session: Session;
    private  _onOpenFuture:IoBiConsumer<boolean, Error>;

    constructor(source: S, supporter: ChannelSupporter<S>) {
        super(supporter.getConfig());
        this._source = source;
        this._processor = supporter.getProcessor();
        this._assistant = supporter.getAssistant();
        this._streamManger = supporter.getConfig().getStreamManger();
    }

    onOpenFuture(future: IoBiConsumer<boolean, Error>) {
        this._onOpenFuture = future;
    }
    doOpenFuture(r:boolean, e:Error) {
        if (this._onOpenFuture) {
            this._onOpenFuture(r, e);
        }
    }

    isValid() {
        return this.isClosed() == 0 && this._assistant.isValid(this._source);
    }

    config(): Config {
        return this._config;
    }

    sendPing() {
        this.send(Frames.pingFrame(), null);
    }

    sendPong() {
        this.send(Frames.pongFrame(), null);
    }

    send(frame: Frame, stream: StreamInternal | null) {

        if (this.getConfig().clientMode()) {
            console.debug("C-SEN:", frame);
        } else {
            console.debug("S-SEN:", frame);
        }


        if (frame.message()) {
            let message = frame.message()!;

            //注册流接收器
            if (stream != null) {
                this._streamManger.addStream(message.sid(), stream);
            }

            //如果有实体（尝试分片）
            if (message.entity() != null) {
                //确保用完自动关闭

                if (message.dataSize() > this.getConfig().getFragmentSize()) {
                    message.putMeta(EntityMetas.META_DATA_LENGTH, message.dataSize().toString());

                    //满足分片条件
                    let fragmentIndex = 0;
                    while (true) {
                        //获取分片
                        fragmentIndex++;
                        let fragmentEntity = this.getConfig().getFragmentHandler().nextFragment(this, fragmentIndex, message);

                        if (fragmentEntity != null) {
                            //主要是 sid 和 entity
                            let fragmentFrame  = new Frame(frame.flag(), new MessageBuilder()
                                .flag(frame.flag())
                                .sid(message.sid())
                                .entity(fragmentEntity)
                                .build());

                            this._assistant.write(this._source, fragmentFrame);
                        } else {
                            //没有分片，说明发完了
                            return;
                        }
                    }
                } else {
                    //不满足分片条件，直接发
                    this._assistant.write(this._source, frame);
                    return;
                }

            }
        }

        this._assistant.write(this._source, frame);
    }

    retrieve(frame: Frame) {
        let stream = this._streamManger.getStream(frame.message()!.sid());

        if (stream != null) {
            if (stream.isSingle() || frame.flag() == Flags.ReplyEnd) {
                //如果是单收或者答复结束，则移除流接收器
                this._streamManger.removeStream(frame.message()!.sid());
            }

            if (stream.isSingle()) {
                //单收时，内部已经是异步机制
                stream.onAccept(frame.message()!, this);
            } else {
                //改为异步处理，避免卡死Io线程
                stream.onAccept(frame.message()!, this);
            }
        } else {
            console.debug(`${this.getConfig().getRoleName()} stream not found, sid=${frame.message()!.sid()}, sessionId=${this.getSession().sessionId()}`);
        }
    }
    reconnect() {
        //由 ClientChannel 实现
    }

    onError(error: Error) {
        this._processor.onError(this, error);
    }

    getSession(): Session {
        if (this._session == null) {
            this._session = new SessionDefault(this);
        }

        return this._session;
    }

    setSession(session: Session) {
        this._session = session;
    }

    close(code) {
        console.debug(`${this.getConfig().getRoleName()} channel will be closed, sessionId=${this.getSession().sessionId()}`);

        try {
            super.close(code);
            this._assistant.close(this._source);
        } catch (e) {
            console.warn(`${this.getConfig().getRoleName()} channel close error, sessionId=${this.getSession().sessionId()}`, e);
        }
    }
}
